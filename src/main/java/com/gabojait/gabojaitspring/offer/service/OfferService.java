package com.gabojait.gabojaitspring.offer.service;

import com.gabojait.gabojaitspring.common.util.FcmProvider;
import com.gabojait.gabojaitspring.common.util.GeneralProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.offer.domain.type.OfferedBy;
import com.gabojait.gabojaitspring.offer.dto.req.OfferCreateReqDto;
import com.gabojait.gabojaitspring.offer.repository.OfferRepository;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.team.repository.TeamRepository;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class OfferService {

    private final OfferRepository offerRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final GeneralProvider generalProvider;
    private final FcmProvider fcmProvider;

    /**
     * 회원이 팀에 제안 |
     * 404(TEAM_NOT_FOUND)
     * 409(EXISTING_CURRENT_TEAM / TEAM_POSITION_UNAVAILABLE)
     * 500(SERVER_ERROR)
     */
    public void offerByUser(User user, Long teamId, OfferCreateReqDto request) {
        Team team = findOneTeam(teamId);

        validateHasNoCurrentTeam(user);
        validatePositionAvailability(team, Position.fromString(request.getPosition()));

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
    public void offerByTeam(User leader, Long userId, OfferCreateReqDto request) {
        User user = findOneUser(userId);

        validateLeader(leader);
        validateHasNoCurrentTeam(user);

        Team team = leader.getTeamMembers().get(leader.getTeamMembers().size() - 1).getTeam();

        validatePositionAvailability(team, Position.fromString(request.getPosition()));

        Offer offer = request.toEntity(user, team, OfferedBy.TEAM);
        saveOffer(offer);

        team.incrementTeamOfferCnt();
        user.incrementTeamOfferCnt();

        fcmProvider.sendOfferByTeam(offer);
    }

    /**
     * 회원이 받은 제안 결정 |
     * 404(OFFER_NOT_FOUND)
     * 409(EXISTING_CURRENT_TEAM / TEAM_POSITION_UNAVAILABLE)
     */
    public void decideByUser(User user, Long offerId, Boolean isAccepted) {
        Offer offer = findOneOfferByIdAndUser(offerId, user);
        Team team = offer.getTeam();
        Position position = Position.fromChar(offer.getPosition());

        validateHasNoCurrentTeam(user);
        validatePositionAvailability(team, position);

        if (isAccepted) {
            offer.accept();

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
     * 404(OFFER_NOT_FOUND / USER_NOT_FOUND)
     * 409(EXISTING_CURRENT_TEAM / TEAM_POSITION_UNAVAILABLE)
     */
    public void decideByTeam(User leader, Long offerId, Boolean isAccepted) {
        Team team = leader.getTeamMembers().get(leader.getTeamMembers().size() - 1).getTeam();
        Offer offer = findOneOfferByIdAndTeam(offerId, team);
        User user = findOneUser(offer.getUser().getId());
        Position position = Position.fromChar(offer.getPosition());

        validateLeader(leader);
        validateHasNoCurrentTeam(user);
        validatePositionAvailability(team, position);

        if (isAccepted) {
            offer.accept();

            user.incrementJoinTeamCnt();
            team.incrementUserJoinCnt();

            fcmProvider.sendTeamMemberJoin(offer);
        } else {
            offer.decline();
        }
    }

    /**
     * 회원이 보낸 제안 취소 |
     * 404(OFFER_NOT_FOUND)
     */
    public void cancelByUser(User user, Long offerId) {
        Offer offer = findOneOfferByIdAndUserAndOfferedBy(offerId, user);

        offer.cancel();
    }

    /**
     * 회원이 보낸 제안 취소 |
     * 404(OFFER_NOT_FOUND)
     */
    public void cancelByTeam(User leader, Long offerId) {
        validateLeader(leader);

        Team team = leader.getTeamMembers().get(leader.getTeamMembers().size() - 1).getTeam();
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
     * 회원으로 제안 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    public Page<Offer> findManyOffersByUser(User user, Integer pageFrom, Integer pageSize) {
        Pageable pageable = generalProvider.validatePaging(pageFrom, pageSize, 20);

        try {
            return offerRepository.findAllByUserAndIsDeletedIsFalse(user, pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 팀으로 제안 페이징 다건 조회 |
     * 403(REQUEST_FORBIDDEN)
     * 500(SERVER_ERROR)
     */
    public Page<Offer> findManyOffersByTeam(User user, Integer pageFrom, Integer pageSize) {
        Pageable pageable = generalProvider.validatePaging(pageFrom, pageSize, 20);
        validateLeader(user);

        Team team = user.getTeamMembers().get(user.getTeamMembers().size() - 1).getTeam();

        try {
            return offerRepository.findAllByTeamAndIsDeletedIsFalse(team, pageable);
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
        return offerRepository.findByIdAndUserAndOfferedByAndIsDeletedIsFalse(offerId, user, OfferedBy.USER.getType())
                .orElseThrow(() -> {
                    throw new CustomException(OFFER_NOT_FOUND);
                });
    }

    /**
     * 식별자, 회원, 제안자로 제안 단건 조회 |
     * 404(OFFER_NOT_FOUND)
     */
    private Offer findOneOfferByIdAndTeamAndOfferedBy(long offerId, Team team) {
        return offerRepository.findByIdAndTeamAndOfferedByAndIsDeletedIsFalse(offerId, team, OfferedBy.TEAM.getType())
                .orElseThrow(() -> {
                    throw new CustomException(OFFER_NOT_FOUND);
                });
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
     * 회원으로 현재 팀 미존재 검증 |
     * 409(EXISTING_CURRENT_TEAM)
     */
    private void validateHasNoCurrentTeam(User user) {
        if (user.getTeamMembers().isEmpty())
            return;

        if (!user.getTeamMembers().get(user.getTeamMembers().size() - 1).getIsDeleted())
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
    private void validateLeader(User user) {
        if (!user.isLeader())
            throw new CustomException(REQUEST_FORBIDDEN);
    }
}
