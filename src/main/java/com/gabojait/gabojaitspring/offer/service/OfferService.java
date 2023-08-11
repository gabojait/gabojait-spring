package com.gabojait.gabojaitspring.offer.service;

import com.gabojait.gabojaitspring.common.util.FcmProvider;
import com.gabojait.gabojaitspring.common.util.GeneralProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.offer.domain.type.OfferedBy;
import com.gabojait.gabojaitspring.offer.dto.req.OfferCreateReqDto;
import com.gabojait.gabojaitspring.offer.dto.res.OfferDefaultResDto;
import com.gabojait.gabojaitspring.offer.repository.OfferRepository;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.team.domain.TeamMember;
import com.gabojait.gabojaitspring.team.repository.TeamMemberRepository;
import com.gabojait.gabojaitspring.team.repository.TeamRepository;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class OfferService {

    private final OfferRepository offerRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final GeneralProvider generalProvider;
    private final FcmProvider fcmProvider;

    /**
     * 회원이 팀에 제안 |
     * 404(TEAM_NOT_FOUND)
     * 409(EXISTING_CURRENT_TEAM / TEAM_POSITION_UNAVAILABLE)
     * 500(SERVER_ERROR)
     */
    public void offerByUser(long userId, Long teamId, OfferCreateReqDto request) {
        User user = findOneUser(userId);
        Team team = findOneTeam(teamId);

        validateHasNoCurrentTeam(user);
        validatePositionAvailability(team, Position.valueOf(request.getPosition()));

        Offer offer = request.toEntity(user, team, OfferedBy.USER);
        saveOffer(offer);

        team.incrementUserOfferCnt();
        user.incrementUserOfferCnt();

        fcmProvider.sendOfferByUser(offer);
    }

    /**
     * 팀이 회원에 제안 |
     * 403(REQUEST_FORBIDDEN)
     * 404(USER_NOT_FOUND)
     * 409(EXISTING_CURRENT_TEAM / TEAM_POSITION_UNAVAILABLE)
     * 500(SERVER_ERROR)
     */
    public void offerByTeam(long leaderId, Long userId, OfferCreateReqDto request) {
        User leader = findOneUser(leaderId);
        User user = findOneUser(userId);

        validateHasNoCurrentTeam(user);

        TeamMember teamMember = findOneCurrentTeamMember(leader);

        validateLeader(teamMember);
        Team team = teamMember.getTeam();

        validatePositionAvailability(team, Position.valueOf(request.getPosition()));


        Offer offer = request.toEntity(user, team, OfferedBy.TEAM);
        saveOffer(offer);

        team.incrementTeamOfferCnt();
        user.incrementTeamOfferCnt();

        fcmProvider.sendOfferByTeam(offer);
    }

    /**
     * 회원이 받은 제안 결정 |
     * 404(USER_NOT_FOUND / OFFER_NOT_FOUND)
     * 409(EXISTING_CURRENT_TEAM / TEAM_POSITION_UNAVAILABLE)
     * 500(SERVER_ERROR)
     */
    public void decideByUser(long userId, Long offerId, Boolean isAccepted) {
        User user = findOneUser(userId);
        Offer offer = findOneOfferByIdAndUser(offerId, user);
        Team team = offer.getTeam();

        validateHasNoCurrentTeam(user);
        validatePositionAvailability(team, offer.getPosition());

        if (isAccepted) {
            offer.accept();
            createTeamMember(user, team, offer.getPosition());

            List<Offer> offers = findAllOfferByUserAndTeam(user, team);
            for (Offer o : offers)
                o.cancel();

            user.incrementJoinTeamCnt();
            team.incrementUserJoinCnt();

            fcmProvider.sendTeamMemberJoin(offer);
        } else {
            offer.decline();
        }
    }

    /**
     * 팀이 받은 제안 결정 |
     * 403(REQUEST_FORBIDDEN)
     * 404(USER_NOT_FOUND / OFFER_NOT_FOUND)
     * 409(EXISTING_CURRENT_TEAM / TEAM_POSITION_UNAVAILABLE)
     * 500(SERVER_ERROR)
     */
    public void decideByTeam(long leaderId, Long offerId, Boolean isAccepted) {
        User leader = findOneUser(leaderId);

        TeamMember leaderTeamMember = findOneCurrentTeamMember(leader);
        validateLeader(leaderTeamMember);
        Team team = leaderTeamMember.getTeam();
        Offer offer = findOneOfferByIdAndTeam(offerId, team);
        validatePositionAvailability(team, offer.getPosition());

        User user = findOneUser(offer.getUser().getId());
        validateHasNoCurrentTeam(user);

        if (isAccepted) {
            offer.accept();
            createTeamMember(user, team, offer.getPosition());

            List<Offer> offers = findAllOfferByUserAndTeam(user, team);
            for (Offer o : offers)
                o.cancel();

            user.incrementJoinTeamCnt();
            team.incrementUserJoinCnt();

            fcmProvider.sendTeamMemberJoin(offer);
        } else {
            offer.decline();
        }
    }

    /**
     * 회원이 보낸 제안 취소 |
     * 404(USER_NOT_FOUND / OFFER_NOT_FOUND)
     */
    public void cancelByUser(long userId, Long offerId) {
        User user = findOneUser(userId);
        Offer offer = findOneOfferByIdAndUserAndOfferedBy(offerId, user);

        offer.cancel();
    }

    /**
     * 회원이 보낸 제안 취소 |
     * 404(USER_NOT_FOUND / OFFER_NOT_FOUND)
     */
    public void cancelByTeam(long leaderId, Long offerId) {
        User leader = findOneUser(leaderId);
        TeamMember leaderTeamMember = findOneCurrentTeamMember(leader);
        validateLeader(leaderTeamMember);
        Team team = leaderTeamMember.getTeam();

        Offer offer = findOneOfferByIdAndTeamAndOfferedBy(offerId, team);

        offer.cancel();
    }

    /**
     * 제안 저장 |
     * 500(SERVER_ERROR)
     */
    private void saveOffer(Offer offer) {
        try {
            offerRepository.save(offer);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 팀원 저장 |
     * 500(SERVER_ERROR)
     */
    private void saveTeamMember(TeamMember teamMember) {
        try {
            teamMemberRepository.save(teamMember);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 팀원 생성 |
     * 500(SERVER_ERROR)
     */
    private void createTeamMember(User user, Team team, Position position) {
        TeamMember teamMember = TeamMember.builder()
                .user(user)
                .team(team)
                .position(position)
                .isLeader(false)
                .build();

        saveTeamMember(teamMember);
    }

    /**
     * 회원으로 받은 제안 페이징 다건 조회 |
     * 404(USER_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public Page<Offer> findManyReceivedOffersByUser(long userId, Integer pageFrom, Integer pageSize) {
        Pageable pageable = generalProvider.validatePaging(pageFrom, pageSize, 20);
        User user = findOneUser(userId);

        try {
            return offerRepository.findAllByUserAndOfferedByAndIsDeletedIsFalse(user, OfferedBy.TEAM, pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 팀으로 받은 제안 페이징 다건 조회 |
     * 403(REQUEST_FORBIDDEN)
     * 404(USER_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public Page<Offer> findManyReceivedOffersByTeam(long userId, Integer pageFrom, Integer pageSize, Position position) {
        Pageable pageable = generalProvider.validatePaging(pageFrom, pageSize, 20);
        User leader = findOneUser(userId);
        TeamMember leaderTeamMember = findOneCurrentTeamMember(leader);
        validateLeader(leaderTeamMember);

        Team team = leaderTeamMember.getTeam();

        try {
            return offerRepository.findAllByTeamAndPositionAndOfferedByAndIsDeletedIsFalse(team, position, OfferedBy.USER, pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 회원으로 보낸 제안 페이징 다건 조회 |
     * 404(USER_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public Page<Offer> findManySentOffersByUser(long userId, Integer pageFrom, Integer pageSize) {
        Pageable pageable = generalProvider.validatePaging(pageFrom, pageSize, 20);
        User user = findOneUser(userId);

        try {
            return offerRepository.findAllByUserAndOfferedByAndIsDeletedIsFalse(user, OfferedBy.USER, pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 식별자와 회원으로 제안 단건 조회 |
     * 404(OFFER_NOT_FOUND)
     */
    private Offer findOneOfferByIdAndUser(long offerId, User user) {
        return offerRepository.findByIdAndUserAndIsDeletedIsFalse(offerId, user)
                .orElseThrow(() -> {
                    throw new CustomException(OFFER_NOT_FOUND);
                });
    }

    /**
     * 식별자와 팀으로 제안 단건 조회 |
     * 404(OFFER_NOT_FOUND)
     */
    private Offer findOneOfferByIdAndTeam(long offerId, Team team) {
        return offerRepository.findByIdAndTeamAndIsDeletedIsFalse(offerId, team)
                .orElseThrow(() -> {
                    throw new CustomException(OFFER_NOT_FOUND);
                });
    }

    /**
     * 식별자, 회원, 제안자로 제안 단건 조회 |
     * 404(OFFER_NOT_FOUND)
     */
    private Offer findOneOfferByIdAndUserAndOfferedBy(long offerId, User user) {
        return offerRepository.findByIdAndUserAndOfferedByAndIsDeletedIsFalse(offerId, user, OfferedBy.USER)
                .orElseThrow(() -> {
                    throw new CustomException(OFFER_NOT_FOUND);
                });
    }

    /**
     * 식별자, 회원, 제안자로 제안 단건 조회 |
     * 404(OFFER_NOT_FOUND)
     */
    private Offer findOneOfferByIdAndTeamAndOfferedBy(long offerId, Team team) {
        return offerRepository.findByIdAndTeamAndOfferedByAndIsDeletedIsFalse(offerId, team, OfferedBy.TEAM)
                .orElseThrow(() -> {
                    throw new CustomException(OFFER_NOT_FOUND);
                });
    }

    /**
     * 회원와 팀으로 회원이 특정 팀에게 보낸 전체 제안 조회 |
     * 500(SERVER_ERROR)
     */
    private List<Offer> findAllOfferByUserAndTeam(User user, Team team) {
        try {
            return offerRepository.findAllByUserAndTeamAndIsAcceptedIsNullAndIsDeletedIsFalse(user, team);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 식별자로 팀 단건 조회 |
     * 404(TEAM_NOT_FOUND)
     */
    private Team findOneTeam(long teamId) {
        return teamRepository.findByIdAndIsDeletedIsFalse(teamId)
                .orElseThrow(() -> {
                    throw new CustomException(TEAM_NOT_FOUND);
                });
    }

    /**
     * 식별자로 회원 단건 조회 |
     * 404(USER_NOT_FOUND)
     */
    private User findOneUser(long userId) {
        return userRepository.findByIdAndIsDeletedIsFalse(userId)
                .orElseThrow(() -> {
                    throw new CustomException(USER_NOT_FOUND);
                });
    }

    /**
     * 식별자로 현재 팀원 단건 조회 |
     * 404(CURRENT_TEAM_NOT_FOUND)
     */
    private TeamMember findOneCurrentTeamMember(User user) {
        return teamMemberRepository.findByUserAndIsDeletedIsFalse(user)
                .orElseThrow(() -> {
                    throw new CustomException(CURRENT_TEAM_NOT_FOUND);
                });
    }

    /**
     * 회원으로 현재 팀 미존재 검증 |
     * 409(EXISTING_CURRENT_TEAM)
     */
    private void validateHasNoCurrentTeam(User user) {
        Optional<TeamMember> foundTeamMember =  teamMemberRepository.findByUserAndIsDeletedIsFalse(user);

        if (foundTeamMember.isPresent())
            throw new CustomException(EXISTING_CURRENT_TEAM);

    }

    /**
     * 포지션 여부 검증 |
     * 409(TEAM_POSITION_UNAVAILABLE)
     */
    private void validatePositionAvailability(Team team, Position position) {
        boolean isFull = team.isPositionFull(position);

        if (isFull)
            throw new CustomException(TEAM_POSITION_UNAVAILABLE);
    }

    /**
     * 리더 검증 |
     * 403(REQUEST_FORBIDDEN)
     */
    private void validateLeader(TeamMember teamMember) {
        if (!teamMember.getIsLeader())
            throw new CustomException(REQUEST_FORBIDDEN);
    }
}
