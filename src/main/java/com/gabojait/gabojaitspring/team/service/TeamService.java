package com.gabojait.gabojaitspring.team.service;

import com.gabojait.gabojaitspring.common.util.FcmProvider;
import com.gabojait.gabojaitspring.common.util.GeneralProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.favorite.domain.FavoriteTeam;
import com.gabojait.gabojaitspring.favorite.repository.FavoriteTeamRepository;
import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.offer.repository.OfferRepository;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.team.domain.TeamMember;
import com.gabojait.gabojaitspring.team.domain.type.TeamOrder;
import com.gabojait.gabojaitspring.team.dto.req.TeamDefaultReqDto;
import com.gabojait.gabojaitspring.team.dto.req.TeamMemberRecruitCntReqDto;
import com.gabojait.gabojaitspring.team.dto.res.TeamOfferAndFavoriteResDto;
import com.gabojait.gabojaitspring.team.repository.TeamMemberRepository;
import com.gabojait.gabojaitspring.team.repository.TeamRepository;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final FavoriteTeamRepository favoriteTeamRepository;
    private final OfferRepository offerRepository;
    private final UserRepository userRepository;
    private final GeneralProvider generalProvider;
    private final FcmProvider fcmProvider;

    /**
     * 팀 생성 |
     * 404(USER_NOT_FOUND)
     * 409(EXISTING_CURRENT_TEAM / NON_EXISTING_POSITION)
     * 500(SERVER_ERROR)
     */
    public Team create(long userId, TeamDefaultReqDto request) {
        User user = findOneUser(userId);
        Optional<TeamMember> teamMember = findOneCurrentTeamMember(user);

        validateHasNoCurrentTeam(teamMember);
        validateCurrentPosition(user);

        Team team = request.toEntity(user);
        saveTeam(team);
        joinTeam(user, team, true, user.getPosition());

        return team;
    }

    /**
     * 팀 수정 |
     * 403(REQUEST_FORBIDDEN)
     * 404(USER_NOT_FOUND)
     * 404(CURRENT_TEAM_NOT_FOUND)
     * 409(DESIGNER_CNT_UPDATE_UNAVAILABLE / BACKEND_CNT_UPDATE_UNAVAILABLE / FRONTEND_CNT_UPDATE_UNAVAILABLE /
     * MANAGER_CNT_UPDATE_UNAVAILABLE)
     */
    public Team update(long userId, TeamDefaultReqDto request) {
        User user = findOneUser(userId);

        Optional<TeamMember> foundTeamMember = findOneCurrentTeamMember(user);
        TeamMember teamMember = validateHasCurrentTeam(foundTeamMember);
        validateIsLeader(teamMember);

        Map<Position, Byte> teamMemberRecruits = new HashMap<>();
        for(TeamMemberRecruitCntReqDto teamMemberRecruitCnt : request.getTeamMemberRecruitCnts()) {
            Position position = Position.valueOf(teamMemberRecruitCnt.getPosition());
            
            teamMemberRecruits.put(position, teamMemberRecruitCnt.getTotalRecruitCnt());
        }

        validateAllPositionCnt(teamMember.getTeam(), teamMemberRecruits);

        Team team = teamMember.getTeam();
        team.update(request.getProjectName(),
                request.getProjectDescription(),
                request.getExpectation(),
                request.getOpenChatUrl(),
                teamMemberRecruits);

        fcmProvider.sendTeamProfileUpdated(team);

        return team;
    }

    /**
     * 팀원 모집 여부 업데이트 |
     * 403(REQUEST_FORBIDDEN)
     * 404(USER_NOT_FOUND / CURRENT_TEAM_NOT_FOUND)
     */
    public void updateIsRecruiting(long userId, boolean isRecruiting) {
        User user = findOneUser(userId);

        Optional<TeamMember> foundTeamMember = findOneCurrentTeamMember(user);
        TeamMember teamMember = validateHasCurrentTeam(foundTeamMember);
        validateIsLeader(teamMember);

        teamMember.getTeam().updateIsRecruiting(isRecruiting);
    }

    /**
     * 프로젝트 종료 |
     * 403(REQUEST_FORBIDDEN)
     * 404(USER_NOT_FOUND / CURRENT_TEAM_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public void quit(long userId, String projectUrl) {
        User user = findOneUser(userId);

        Optional<TeamMember> foundTeamMember = findOneCurrentTeamMember(user);
        TeamMember teamMember = validateHasCurrentTeam(foundTeamMember);
        validateIsLeader(teamMember);

        if (projectUrl.isBlank())
            incomplete(teamMember.getTeam());
        else
            complete(teamMember.getTeam(), projectUrl);
    }

    /**
     * 팀원 추방 |
     * 403(REQUEST_FORBIDDEN)
     * 404(USER_NOT_FOUND / CURRENT_TEAM_NOT_FOUND)
     * 409(SELF_FIRE_UNAVAILABLE)
     */
    public void fire(long leaderUserId, Long userId) {
        validateSelfFire(leaderUserId, userId);
        User leader = findOneUser(leaderUserId);

        Optional<TeamMember> foundLeaderTeamMember = findOneCurrentTeamMember(leader);
        TeamMember leaderTeamMember = validateHasCurrentTeam(foundLeaderTeamMember);
        validateIsLeader(leaderTeamMember);

        User user = findOneUser(userId);
        TeamMember teamMember = findOneTeamMember(user, leaderTeamMember.getTeam());

        teamMember.delete();
        teamMember.getUser().updateIsSeekingTeam(true);
        fcmProvider.sendTeamMemberFired(user, teamMember.getTeam());
    }

    /**
     * 팀 합류 |
     * 409(TEAM_POSITION_UNAVAILABLE)
     * 500(SERVER_ERROR)
     */
    public void joinTeam(User user, Team team, Boolean isLeader, Position position) {
        validatePositionAvailability(team, position);

        TeamMember teamMember = TeamMember.builder()
                .user(user)
                .team(team)
                .position(position)
                .isLeader(isLeader)
                .build();

        saveTeamMember(teamMember);
        user.updateIsSeekingTeam(false);

        team.updateIsPositionFull(position);

        if (isLeader)
            user.incrementCreateTeamCnt();
        else
            user.incrementJoinTeamCnt();
    }

    /**
     * 팀 나가기 |
     * 404(CURRENT_TEAM_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public void leaveTeam(long userId) {
        User user = findOneUser(userId);

        Optional<TeamMember> foundTeamMember = findOneCurrentTeamMember(user);
        TeamMember teamMember = validateHasCurrentTeam(foundTeamMember);
        Team team = teamMember.getTeam();

        user.updateIsSeekingTeam(true);
        team.incrementUserLeftCnt();
        user.incrementQuitTeamByUserCnt();

        teamMember.delete();

        fcmProvider.sendTeamMemberQuit(user, team);
    }

    /**
     * 팀 저장 |
     * 500(SERVER_ERROR)
     */
    private Team saveTeam(Team team) {
        try {
            return teamRepository.save(team);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 팀 멤버 저장 |
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
     * 회원으로 현재 팀 단건 조회 |
     * 404(USER_NOT_FOUND / CURRENT_TEAM_NOT_FOUND)
     */
    public Team findOneCurrentTeam(long userId) {
        User user = findOneUser(userId);
        Optional<TeamMember> foundTeamMember = findOneCurrentTeamMember(user);
        TeamMember teamMember = validateHasCurrentTeam(foundTeamMember);

        return teamMember.getTeam();
    }

    /**
     * 팀 식별자와 회원으로 타 팀 단건 조회 |
     * 404(USER_NOT_FOUND / TEAM_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public TeamOfferAndFavoriteResDto findOneOtherTeam(long userId, Long teamId) {
        User user = findOneUser(userId);
        Team team = findOneTeam(teamId);

        Optional<TeamMember> foundTeamMember = teamMemberRepository.findByUserAndTeamAndIsDeletedIsFalse(user, team);
        if (foundTeamMember.isEmpty())
            team.incrementVisitedCnt();

        List<Offer> offers = findAllOffersToTeam(user, team);
        Boolean isFavorite = isFavoriteTeam(user, team);

        return new TeamOfferAndFavoriteResDto(team, offers, isFavorite);
    }

    /**
     * 포지션과 팀 정렬 기준으로 팀 페이징 다건 조회 | main |
     * 500(SERVER_ERROR)
     */
    public Page<Team> findManyTeamByPositionOrder(Position position,
                                                  TeamOrder teamOrder,
                                                  Integer pageFrom,
                                                  Integer pageSize) {
        Pageable pageable = generalProvider.validatePaging(pageFrom, pageSize, 20);

        Page<Team> teams;

        if (position.equals(Position.NONE)) {
            switch (teamOrder.name()) {
                case "ACTIVE":
                    teams = findManyTeamOrderByActive(pageable);
                    break;
                case "POPULARITY":
                    teams = findManyTeamOrderByPopularity(pageable);
                    break;
                default:
                    teams = findManyTeamOrderByCreated(pageable);
                    break;
            }
        } else {
            switch (teamOrder.name()) {
                case "ACTIVE":
                    teams = findManyTeamByPositionOrderByActive(position, pageable);
                    break;
                case "POPULARITY":
                    teams = findManyTeamByPositionOrderByPopularity(position, pageable);
                    break;
                default:
                    teams = findManyTeamByPositionOrderByCreated(position, pageable);
                    break;
            }
        }

        return teams;
    }

    /**
     * 식별자로 팀 단건 조회 |
     * 404(TEAM_NOT_FOUND)
     */
    private Team findOneTeam(Long teamId) {
        return teamRepository.findByIdAndIsDeletedIsFalse(teamId)
                .orElseThrow(() -> {
                    throw new CustomException(TEAM_NOT_FOUND);
                });
    }

    /**
     * 식별자로 회원 단건 조회 |
     * 404(USER_NOT_FOUND)
     */
    private User findOneUser(Long userId) {
        return userRepository.findByIdAndIsDeletedIsFalse(userId)
                .orElseThrow(() -> {
                    throw new CustomException(USER_NOT_FOUND);
                });
    }

    /**
     * 회원 식별자와 팀 식별자로 팀멤버 단건 조회 |
     * 404(TEAM_MEMBER_NOT_FOUND)
     */
    private TeamMember findOneTeamMember(User user, Team team) {
       return teamMemberRepository.findByUserAndTeamAndIsDeletedIsFalse(user, team)
               .orElseThrow(() -> {
                   throw new CustomException(TEAM_MEMBER_NOT_FOUND);
               });
    }

    /**
     * 전체 포지션을 활동순으로 팀 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<Team> findManyTeamOrderByActive(Pageable pageable) {
        try {
            return teamRepository.findAllByIsRecruitingIsTrueAndIsDeletedIsFalseOrderByTeamOfferCntDesc(pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 전체 포지션을 인기순으로 팀 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<Team> findManyTeamOrderByPopularity(Pageable pageable) {
        try {
            return teamRepository.findAllByIsRecruitingIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 전체 포지션을 생성순으로 팀 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<Team> findManyTeamOrderByCreated(Pageable pageable) {
        try {
            return teamRepository.findAllByIsRecruitingIsTrueAndIsDeletedIsFalseOrderByCreatedAtDesc(pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 특정 포지션을 활동순으로 팀 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<Team> findManyTeamByPositionOrderByActive(Position position, Pageable pageable) {
        try {
            Page<Team> teams;
            switch (position.name()) {
                case "DESIGNER":
                    teams = teamRepository
                            .findAllByIsDesignerFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByTeamOfferCntDesc(
                                    pageable
                            );
                    break;
                case "BACKEND":
                    teams = teamRepository
                            .findAllByIsBackendFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByTeamOfferCntDesc(
                                    pageable
                            );
                    break;
                case "FRONTEND":
                    teams = teamRepository
                            .findAllByIsFrontendFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByTeamOfferCntDesc(
                                    pageable
                            );
                    break;
                case "MANAGER":
                    teams = teamRepository
                            .findAllByIsManagerFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByTeamOfferCntDesc(
                                    pageable
                            );
                    break;
                default:
                    throw new CustomException(SERVER_ERROR);
            }

            return teams;
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 특정 포지션을 인기순으로 팀 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<Team> findManyTeamByPositionOrderByPopularity(Position position, Pageable pageable) {
        try {
            Page<Team> teams;
            switch (position.name()) {
                case "DESIGNER":
                    teams = teamRepository
                            .findAllByIsDesignerFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(
                                    pageable
                            );
                    break;
                case "BACKEND":
                    teams = teamRepository
                            .findAllByIsBackendFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(
                                    pageable
                            );
                    break;
                case "FRONTEND":
                    teams = teamRepository
                            .findAllByIsFrontendFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(
                                    pageable
                            );
                    break;
                case "MANAGER":
                    teams = teamRepository
                            .findAllByIsManagerFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(
                                    pageable
                            );
                    break;
                default:
                    throw new CustomException(SERVER_ERROR);
            }

            return teams;
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 특정 포지션을 생성순으로 팀 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<Team> findManyTeamByPositionOrderByCreated(Position position, Pageable pageable) {
        try {
            Page<Team> teams;
            switch (position.name()) {
                case "DESIGNER":
                    teams = teamRepository
                            .findAllByIsDesignerFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByCreatedAtDesc(
                                    pageable
                            );
                    break;
                case "BACKEND":
                    teams = teamRepository
                            .findAllByIsBackendFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByCreatedAtDesc(
                                    pageable
                            );
                    break;
                case "FRONTEND":
                    teams = teamRepository
                            .findAllByIsFrontendFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByCreatedAtDesc(
                                    pageable
                            );
                    break;
                case "MANAGER":
                    teams = teamRepository
                            .findAllByIsManagerFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByCreatedAtDesc(
                                    pageable
                            );
                    break;
                default:
                    throw new CustomException(SERVER_ERROR);
            }

            return teams;
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 현재 팀 멤버 단건 조회
     */
    public Optional<TeamMember> findOneCurrentTeamMember(User user) {
        return teamMemberRepository.findByUserAndIsDeletedIsFalse(user);
    }

    /**
     * 회원와 팀으로 회원이 특정 팀에게 보낸 전체 제안 조회 |
     * 500(SERVER_ERROR)
     */
    public List<Offer> findAllOffersToTeam(User user, Team team) {
        try {
            return offerRepository.findAllByUserAndTeamAndIsAcceptedIsNullAndIsDeletedIsFalse(user, team);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 리더 검증 |
     * 403(REQUEST_FORBIDDEN)
     */
    public void validateIsLeader(TeamMember teamMember) {
        if (!teamMember.getIsLeader())
            throw new CustomException(REQUEST_FORBIDDEN);
    }

    /**
     * 회원으로 현재 팀 존재 검증 |
     * 404(CURRENT_TEAM_NOT_FOUND)
     */
    private TeamMember validateHasCurrentTeam(Optional<TeamMember> teamMember) {
        if (teamMember.isEmpty())
            throw new CustomException(CURRENT_TEAM_NOT_FOUND);

        return teamMember.get();
    }

    /**
     * 회원으로 현재 팀 미존재 검증 |
     * 409(EXISTING_CURRENT_TEAM)
     */
    private void validateHasNoCurrentTeam(Optional<TeamMember> teamMember) {
        if (teamMember.isPresent())
            throw new CustomException(EXISTING_CURRENT_TEAM);
    }

    /**
     * 회원으로 현재 포지션 존재 검증 |
     * 409(NON_EXISTING_POSITION)
     */
    private void validateCurrentPosition(User user) {
        if (!user.hasPosition())
            throw new CustomException(NON_EXISTING_POSITION);
    }

    /**
     * 포지션별 총 팀원 수 검증 |
     * 409(DESIGNER_CNT_UPDATE_UNAVAILABLE / BACKEND_CNT_UPDATE_UNAVAILABLE / FRONTEND_CNT_UPDATE_UNAVAILABLE /
     * MANAGER_CNT_UPDATE_UNAVAILABLE)
     */
    private void validateAllPositionCnt(Team team,
                                        Map<Position, Byte> teamMemberRecruits) {
        byte designerCurrentCnt = 0;
        byte backendCurrentCnt = 0;
        byte frontendCurrentCnt = 0;
        byte managerCurrentCnt = 0;

        for (TeamMember teamMember : team.getTeamMembers())
            switch (teamMember.getPosition().name()) {
                case "DESIGNER":
                    designerCurrentCnt++;
                    break;
                case "BACKEND":
                    backendCurrentCnt++;
                    break;
                case "FRONTEND":
                    frontendCurrentCnt++;
                    break;
                case "MANAGER":
                    managerCurrentCnt++;
                    break;
            }

        byte designerTotalCnt = 0;
        byte backendTotalCnt = 0;
        byte frontendTotalCnt = 0;
        byte managerTotalCnt = 0;

        if (teamMemberRecruits.containsKey(Position.DESIGNER))
            designerTotalCnt += teamMemberRecruits.get(Position.DESIGNER);
        if (teamMemberRecruits.containsKey(Position.BACKEND))
            backendTotalCnt += teamMemberRecruits.get(Position.BACKEND);
        if (teamMemberRecruits.containsKey(Position.FRONTEND))
            frontendTotalCnt += teamMemberRecruits.get(Position.FRONTEND);
        if (teamMemberRecruits.containsKey(Position.MANAGER))
            managerTotalCnt += teamMemberRecruits.get(Position.MANAGER);


        if (designerTotalCnt < designerCurrentCnt)
            throw new CustomException(DESIGNER_CNT_UPDATE_UNAVAILABLE);
        if (backendTotalCnt < backendCurrentCnt)
            throw new CustomException(BACKEND_CNT_UPDATE_UNAVAILABLE);
        if (frontendTotalCnt < frontendCurrentCnt)
            throw new CustomException(FRONTEND_CNT_UPDATE_UNAVAILABLE);
        if (managerTotalCnt < managerCurrentCnt)
            throw new CustomException(MANAGER_CNT_UPDATE_UNAVAILABLE);
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
     * 본인 추방 검증 |
     * 409(SELF_FIRE_UNAVAILABLE)
     */
    private void validateSelfFire(long userId, long otherUserId) {
        if (userId == otherUserId)
            throw new CustomException(SELF_FIRE_UNAVAILABLE);
    }

    /**
     * 프로젝트 미완료 |
     * 500(SERVER_ERROR)
     */
    private void incomplete(Team team) {
        for(TeamMember teamMember : team.getTeamMembers()) {
            teamMember.delete();
            teamMember.getUser().updateIsSeekingTeam(true);

            teamMember.getUser().incrementQuitTeamByLeaderCnt();
        }

        team.incomplete();

        fcmProvider.sendTeamIncomplete(team);
    }

    /**
     * 프로젝트 완료
     */
    private void complete(Team team, String projectUrl) {
        for(TeamMember teamMember : team.getTeamMembers()) {
            teamMember.delete();
            teamMember.getUser().updateIsSeekingTeam(true);

            teamMember.getUser().incrementCompleteTeamCnt();
        }

        team.complete(projectUrl);

        fcmProvider.sendTeamComplete(team);
    }

    /**
     * 찜한 팀 여부 확인 |
     * 500(SERVER_ERROR)
     */
    private boolean isFavoriteTeam(User user, Team team) {
        try {
            Optional<FavoriteTeam> favoriteTeam =
                    favoriteTeamRepository.findByUserAndTeamAndIsDeletedIsFalse(user, team);

            return favoriteTeam.isPresent();
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }
}
