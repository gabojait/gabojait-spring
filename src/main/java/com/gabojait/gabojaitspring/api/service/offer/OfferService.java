package com.gabojait.gabojaitspring.api.service.offer;

import com.gabojait.gabojaitspring.common.dto.response.PageData;
import com.gabojait.gabojaitspring.api.dto.offer.request.OfferCreateRequest;
import com.gabojait.gabojaitspring.api.dto.offer.request.OfferDecideRequest;
import com.gabojait.gabojaitspring.api.dto.offer.response.OfferPageResponse;
import com.gabojait.gabojaitspring.api.service.notification.NotificationService;
import com.gabojait.gabojaitspring.domain.offer.Offer;
import com.gabojait.gabojaitspring.domain.offer.OfferedBy;
import com.gabojait.gabojaitspring.domain.profile.Skill;
import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.domain.user.User;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.repository.offer.OfferRepository;
import com.gabojait.gabojaitspring.repository.profile.SkillRepository;
import com.gabojait.gabojaitspring.repository.team.TeamMemberRepository;
import com.gabojait.gabojaitspring.repository.team.TeamRepository;
import com.gabojait.gabojaitspring.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OfferService {

    private final OfferRepository offerRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final SkillRepository skillRepository;
    private final NotificationService notificationService;

    /**
     * 회원이 팀에 제안 |
     * 404(USER_NOT_FOUND / TEAM_NOT_FOUND)
     * 409(TEAM_POSITION_UNAVAILABLE)
     * @param userId 회원 식별자
     * @param teamId 팀 식별자
     * @param request 제안 생성 요청
     */
    @Transactional
    public void offerByUser(long userId, long teamId, OfferCreateRequest request) {
        User user = findUser(userId);
        Team team = findTeam(teamId);

        validatePositionAvailability(team, Position.valueOf(request.getOfferPosition()));

        Offer offer = request.toEntity(user, team, OfferedBy.USER);
        offerRepository.save(offer);

        notificationService.sendOfferByUser(offer);
    }

    /**
     * 팀이 회원에 제안 |
     * 403(REQUEST_FORBIDDEN)
     * 404(CURRENT_TEAM_NOT_FOUND)
     * 409(TEAM_POSITION_UNAVAILABLE)
     * @param leaderUserId 리더 회원 식별자
     * @param otherUserId 다른 회원 식별자
     * @param request 제안 생성 요청
     */
    @Transactional
    public void offerByTeam(long leaderUserId, long otherUserId, OfferCreateRequest request) {
        TeamMember leaderTeamMember = findCurrentTeamMemberFetchTeam(leaderUserId);

        validateLeader(leaderTeamMember);
        validatePositionAvailability(leaderTeamMember.getTeam(), Position.valueOf(request.getOfferPosition()));

        User user = findUserSeekingTeam(otherUserId);

        Offer offer = request.toEntity(user, leaderTeamMember.getTeam(), OfferedBy.LEADER);
        offerRepository.save(offer);

        notificationService.sendOfferByTeam(offer);
    }

    /**
     * 회원 관련 제안 페이징 조회 |
     * @param userId 회원 식별자
     * @param offeredBy 제안자
     * @param pageFrom 페이지 시작점
     * @param pageSize 페이지 크기
     * @return 제안 페이지 응답들
     */
    public PageData<List<OfferPageResponse>> findPageUserOffer(long userId,
                                                               OfferedBy offeredBy,
                                                               long pageFrom,
                                                               int pageSize) {
        PageData<List<Offer>> offers = offerRepository.findPageFetchUser(userId, offeredBy, pageFrom, pageSize);

        List<Long> userIds = offers.getData().stream()
                .map(o -> o.getUser().getId())
                .collect(Collectors.toList());

        List<Skill> skills = skillRepository.findAllInFetchUser(userIds);

        List<OfferPageResponse> responses = offers.getData().stream()
                .map(offer -> {
                    Long uId = offer.getUser().getId();
                    List<Skill> userSkills = skills.stream()
                            .filter(skill -> uId.equals(skill.getUser().getId()))
                            .collect(Collectors.toList());
                    return new OfferPageResponse(offer, userSkills);
                }).collect(Collectors.toList());

        return new PageData<>(responses, offers.getTotal());
    }

    /**
     * 팀 관련 제안 페이징 조회 |
     * 403(REQUEST_FORBIDDEN)
     * 404(USER_NOT_FOUND / CURRENT_TEAM_NOT_FOUND)
     * @param userId 회원 식별자
     * @param position 포지션
     * @param offeredBy 제안자
     * @param pageFrom 페이지 시작점
     * @param pageSize 페이지 크기
     * @return 제안 페이지 응답들
     */
    public PageData<List<OfferPageResponse>> findPageTeamOffer(long userId,
                                                               Position position,
                                                               OfferedBy offeredBy,
                                                               long pageFrom,
                                                               int pageSize) {
        User user = findUser(userId);
        TeamMember teamMember = findCurrentTeamMemberFetchTeam(user.getId());

        validateLeader(teamMember);

        PageData<List<Offer>> offers = offerRepository.findPageFetchTeam(teamMember.getTeam().getId(), position,
                offeredBy, pageFrom, pageSize);

        List<Skill> skills = skillRepository.findAllInFetchUser(offers.getData().stream()
                .map(o -> o.getUser().getId())
                .collect(Collectors.toList()));
        Map<Long, List<Skill>> sMap = skills.stream()
                .collect(Collectors.groupingBy(s -> s.getUser().getId()));

        List<OfferPageResponse> responses = offers.getData().stream()
                .map(o -> new OfferPageResponse(o, sMap.getOrDefault(o.getUser().getId(), Collections.emptyList())))
                .collect(Collectors.toList());

        return new PageData<>(responses, offers.getTotal());
    }

    /**
     * 회원이 받은 제안 결정
     * 404(OFFER_NOT_FOUND)
     * @param userId 회원 식별자
     * @param offerId 제안 식별자
     * @param request 제안 결정 요청
     */
    @Transactional
    public void userDecideOffer(long userId, long offerId, OfferDecideRequest request) {
        Offer offer = findOfferFetchTeam(userId, offerId, OfferedBy.LEADER);

        if (request.getIsAccepted()) {
            offer.accept();
            TeamMember teamMember = request.toTeamMemberEntity(offer);
            teamMemberRepository.save(teamMember);

            offerRepository.findAllByTeamId(offer.getUser().getId(), offer.getTeam().getId())
                    .forEach(Offer::cancel);

            notificationService.sendTeamMemberJoin(offer);
        } else {
            offer.decline();
        }
    }

    /**
     * 팀이 받은 제안 결정 |
     * 403(REQUEST_FORBIDDEN)
     * 404(CURRENT_TEAM_NOT_FOUND / OFFER_NOT_FOUND)
     * @param userId 회원 식별자
     * @param offerId 제안 식별자
     * @param request 제안 결정 요청
     */
    @Transactional
    public void teamDecideOffer(long userId, long offerId, OfferDecideRequest request) {
        TeamMember teamLeader = findCurrentTeamMemberFetchTeam(userId);

        validateLeader(teamLeader);

        Offer offer = findOfferFetchUser(teamLeader.getTeam().getId(), offerId, OfferedBy.USER);

        if (request.getIsAccepted()) {
            offer.accept();
            TeamMember teamMember = request.toTeamMemberEntity(offer);
            teamMemberRepository.save(teamMember);

            offerRepository.findAllByTeamId(offer.getUser().getId(), offer.getTeam().getId())
                    .forEach(Offer::cancel);

            notificationService.sendTeamMemberJoin(offer);
        } else {
            offer.decline();
        }
    }

    /**
     * 회원이 보낸 제안 취소 |
     * 404(OFFER_NOT_FOUND)
     * @param userId 회원 식별자
     * @param offerId 제안 식별자
     */
    @Transactional
    public void cancelByUser(long userId, long offerId) {
        Offer offer = findOfferFetchTeam(userId, offerId, OfferedBy.USER);

        offer.cancel();
    }

    /**
     * 팀이 보낸 제안 취소 |
     * 403(REQUEST_FORBIDDEN)
     * 404(CURRENT_TEAM_NOT_FOUND / OFFER_NOT_FOUND)
     * @param userId 회원 식별자
     * @param offerId 제안 식별자
     */
    @Transactional
    public void cancelByTeam(long userId, long offerId) {
        TeamMember teamMember = findCurrentTeamMemberFetchTeam(userId);

        validateLeader(teamMember);

        Offer offer = findOfferFetchUser(teamMember.getTeam().getId(), offerId, OfferedBy.LEADER);

        offer.cancel();
    }

    /**
     * 제안 단건 조회 |
     * 404(OFFER_NOT_FOUND)
     * @param userId 회원 식별자
     * @param offerId 제안 식별자
     * @param offeredBy 제안자
     * @return 제안
     */
    private Offer findOfferFetchTeam(long userId, long offerId, OfferedBy offeredBy) {
        return offerRepository.findFetchTeam(userId, offerId, offeredBy)
                .orElseThrow(() -> {
                    throw new CustomException(OFFER_NOT_FOUND);
                });
    }

    /**
     * 제안 단건 조회 |
     * 404(OFFER_NOT_FOUND)
     * @param teamId 팀 식별자
     * @param offerId 제안 식별자
     * @param offeredBy 제안자
     * @return 제안
     */
    private Offer findOfferFetchUser(long teamId, long offerId, OfferedBy offeredBy) {
        return offerRepository.findFetchUser(teamId, offerId, offeredBy)
                .orElseThrow(() -> {
                    throw new CustomException(OFFER_NOT_FOUND);
                });
    }

    /**
     * 현재 팀원 단건 조회 |
     * 404(CURRENT_TEAM_NOT_FOUND)
     * @param userId 회원 식별자
     * @return 팀원
     */
    private TeamMember findCurrentTeamMemberFetchTeam(long userId) {
        return teamMemberRepository.findCurrentFetchTeam(userId)
                .orElseThrow(() -> {
                    throw new CustomException(CURRENT_TEAM_NOT_FOUND);
                });
    }

    /**
     * 회원 단건 조회 |
     * 404(USER_NOT_FOUND)
     * @param username 회원 아이디
     * @return 회원
     */
    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    throw new CustomException(USER_NOT_FOUND);
                });
    }

    /**
     * 회원 단건 조회 |
     * 404(USER_NOT_FOUND)
     * @param userId 회원 식별자
     * @return 회원
     */
    private User findUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    throw new CustomException(USER_NOT_FOUND);
                });
    }

    /**
     * 팀을 찾는 회원 단건 조회 |
     * 404(USER_NOT_FOUND)
     * @param userId 회원 식별자
     * @return 회원
     */
    private User findUserSeekingTeam(long userId) {
        return userRepository.findSeekingTeam(userId)
                .orElseThrow(() -> {
                    throw new CustomException(USER_NOT_FOUND);
                });
    }

    /**
     * 팀 단건 조회 |
     * 404(TEAM_NOT_FOUND)
     * @param teamId 팀 식별자
     * @return 팀
     */
    private Team findTeam(long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> {
                    throw new CustomException(TEAM_NOT_FOUND);
                });
    }

    /**
     * 팀장 여부 검증 |
     * 403(REQUEST_FORBIDDEN)
     * @param teamMember 팀원
     */
    private void validateLeader(TeamMember teamMember) {
        if (!teamMember.getIsLeader())
            throw new CustomException(REQUEST_FORBIDDEN);
    }

    /**
     * 포지션 여부 검증 |
     * 409(TEAM_POSITION_UNAVAILABLE)
     * @param team 팀
     * @param position 포지션
     */
    private void validatePositionAvailability(Team team, Position position) {
        boolean isPositionFull = team.isPositionFull(position);

        if (isPositionFull)
            throw new CustomException(TEAM_POSITION_UNAVAILABLE);
    }
}
