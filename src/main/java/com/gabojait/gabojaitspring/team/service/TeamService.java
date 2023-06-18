package com.gabojait.gabojaitspring.team.service;

import com.gabojait.gabojaitspring.common.util.FcmProvider;
import com.gabojait.gabojaitspring.common.util.GeneralProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.favorite.domain.FavoriteTeam;
import com.gabojait.gabojaitspring.favorite.repository.FavoriteTeamRepository;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.profile.domain.type.TeamMemberStatus;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.team.domain.TeamMember;
import com.gabojait.gabojaitspring.team.domain.type.TeamOrder;
import com.gabojait.gabojaitspring.team.dto.req.TeamDefaultReqDto;
import com.gabojait.gabojaitspring.team.dto.res.TeamDetailResDto;
import com.gabojait.gabojaitspring.team.repository.TeamMemberRepository;
import com.gabojait.gabojaitspring.team.repository.TeamRepository;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final FavoriteTeamRepository favoriteTeamRepository;
    private final GeneralProvider generalProvider;
    private final FcmProvider fcmProvider;

    /**
     * 팀 생성 |
     * 409(EXISTING_CURRENT_TEAM / NON_EXISTING_POSITION)
     * 500(SERVER_ERROR)
     */
    public Team create(TeamDefaultReqDto request, User user) {
        validateHasNoCurrentTeam(user);
        validateCurrentPosition(user);

        Team team = request.toEntity(user);
        saveTeam(team);
        joinTeam(user, team, TeamMemberStatus.LEADER, Position.fromChar(user.getPosition()));

        return team;
    }

    /**
     * 팀 수정 |
     * 403(REQUEST_FORBIDDEN)
     * 404(CURRENT_TEAM_NOT_FOUND)
     * 409(DESIGNER_CNT_UPDATE_UNAVAILABLE / BACKEND_CNT_UPDATE_UNAVAILABLE / FRONTEND_CNT_UPDATE_UNAVAILABLE /
     * MANAGER_CNT_UPDATE_UNAVAILABLE)
     */
    public Team update(TeamDefaultReqDto request, User user) {
        TeamMember teamMember = findOneCurrentTeamMember(user);
        validateIsLeader(teamMember);
        validateAllPositionCnt(teamMember.getTeam(),
                request.getDesignerTotalRecruitCnt(),
                request.getBackendTotalRecruitCnt(),
                request.getFrontendTotalRecruitCnt(),
                request.getManagerTotalRecruitCnt());

        Team team = teamMember.getTeam();
        team.update(request.getProjectName(),
                request.getProjectDescription(),
                request.getDesignerTotalRecruitCnt(),
                request.getBackendTotalRecruitCnt(),
                request.getFrontendTotalRecruitCnt(),
                request.getManagerTotalRecruitCnt(),
                request.getExpectation(),
                request.getOpenChatUrl());

        fcmProvider.sendTeamProfileUpdated(team);

        return team;
    }

    /**
     * 팀원 모집 여부 업데이트 |
     * 403(REQUEST_FORBIDDEN)
     * 404(CURRENT_TEAM_NOT_FOUND)
     */
    public void updateIsRecruiting(User user, boolean isRecruiting) {
        TeamMember teamMember = findOneCurrentTeamMember(user);

        validateIsLeader(teamMember);

        teamMember.getTeam().updateIsRecruiting(isRecruiting);
    }

    /**
     * 프로젝트 종료 |
     * 403(REQUEST_FORBIDDEN)
     * 404(CURRENT_TEAM_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public void quit(User user, String projectUrl) {
        TeamMember teamMember = findOneCurrentTeamMember(user);

        validateIsLeader(teamMember);

        if (projectUrl.isBlank())
            incomplete(teamMember.getTeam());
        else
            complete(teamMember.getTeam(), projectUrl);
    }

    /**
     * 팀원 추방 |
     * 403(REQUEST_FORBIDDEN)
     * 404(CURRENT_TEAM_NOT_FOUND)
     */
    public void fire(User leader, Long userId) {
        TeamMember leaderTeamMember = findOneCurrentTeamMember(leader);

        validateIsLeader(leaderTeamMember);

        Team team = leaderTeamMember.getTeam();
        for(TeamMember teamMember : team.getTeamMembers())
            if (teamMember.getUser().getId().equals(userId)) {
                hardDeleteTeamMember(teamMember);

                team.incrementUserFiredCnt();
                teamMember.getUser().incrementFiredTeamCnt();

                fcmProvider.sendTeamMemberFired(teamMember.getUser(), team);
                return;
            }
    }

    /**
     * 팀 합류 |
     * 409(TEAM_POSITION_UNAVAILABLE)
     * 500(SERVER_ERROR)
     */
    public void joinTeam(User user, Team team, TeamMemberStatus teamMemberStatus, Position position) {
        validatePositionAvailability(team, position);

        TeamMember teamMember = TeamMember.builder()
                .user(user)
                .team(team)
                .position(position)
                .teamMemberStatus(teamMemberStatus)
                .build();

        saveTeamMember(teamMember);

        team.updateIsPositionFull(position);

        if (teamMemberStatus.equals(TeamMemberStatus.LEADER))
            user.incrementCreateTeamCnt();
        else
            user.incrementJoinTeamCnt();

    }

    /**
     * 팀 나가기 |
     * 404(CURRENT_TEAM_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public void leaveTeam(User user) {
        TeamMember teamMember = findOneCurrentTeamMember(user);

        Team team = teamMember.getTeam();

        team.incrementUserLeftCnt();
        user.incrementQuitTeamByUserCnt();

        hardDeleteTeamMember(teamMember);

        fcmProvider.sendTeamMemberQuit(user, team);
    }

    /**
     * 팀 저장 |
     * 500(SERVER_ERROR)
     */
    private void saveTeam(Team team) {
        try {
            teamRepository.save(team);
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
     * 팀 멤버 하드 삭제 |
     * 500(SERVER_ERROR)
     */
    public void hardDeleteTeamMember(TeamMember teamMember) {
        try {
            teamMemberRepository.delete(teamMember);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 회원으로 현재 팀 단건 조회 |
     * 404(CURRENT_TEAM_NOT_FOUND)
     */
    public Team findOneCurrentTeam(User user) {
        TeamMember teamMember = findOneCurrentTeamMember(user);

        return teamMember.getTeam();
    }

    /**
     * 식별자로 팀 단건 조회 |
     * 404(TEAM_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public TeamDetailResDto findOneOtherTeam(Long teamId, User user) {
        Team team = findOneTeam(teamId);

        boolean isTeamMember = user.isTeamMember(team);

        if (!isTeamMember)
            team.incrementVisitedCnt();

        Boolean isFavorite = isFavoriteTeam(user, team);

        return new TeamDetailResDto(team, isFavorite);
    }

    /**
     * 포지션과 팀 정렬 기준으로 팀 페이징 다건 조회 | main |
     * 500(SERVER_ERROR)
     */
    public Page<Team> findManyTeamByPositionOrder(String position, String teamOrder,Integer pageFrom, Integer pageSize) {
        Position p = Position.fromString(position);
        TeamOrder to = TeamOrder.fromString(teamOrder);
        Pageable pageable = generalProvider.validatePaging(pageFrom, pageSize, 20);

        Page<Team> teams;

        if (p.equals(Position.NONE)) {
            switch (to.name().toLowerCase()) {
                case "active":
                    teams = findManyTeamOrderByActive(pageable);
                    break;
                case "popularity":
                    teams = findManyTeamOrderByPopularity(pageable);
                    break;
                default:
                    teams = findManyTeamOrderByCreated(pageable);
                    break;
            }
        } else {
            switch (to.name()) {
                case "active":
                    teams = findManyTeamByPositionOrderByActive(p, pageable);
                    break;
                case "popularity":
                    teams = findManyTeamByPositionOrderByPopularity(p, pageable);
                    break;
                default:
                    teams = findManyTeamByPositionOrderByCreated(p, pageable);
                    break;
            }
        }

        return teams;
    }

    /**
     * 식별자로 팀 단건 조회 |
     * 404(TEAM_NOT_FOUND)
     */
    public Team findOneTeam(Long teamId) {
        return teamRepository.findByIdAndIsDeletedIsFalse(teamId)
                .orElseThrow(() -> {
                    throw new CustomException(TEAM_NOT_FOUND);
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
            switch (position.getType()) {
                case 'D':
                    teams = teamRepository
                            .findAllByIsDesignerFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByTeamOfferCntDesc(
                                    pageable
                            );
                    break;
                case 'B':
                    teams = teamRepository
                            .findAllByIsBackendFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByTeamOfferCntDesc(
                                    pageable
                            );
                    break;
                case 'F':
                    teams = teamRepository
                            .findAllByIsFrontendFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByTeamOfferCntDesc(
                                    pageable
                            );
                    break;
                case 'M':
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
            switch (position.getType()) {
                case 'D':
                    teams = teamRepository
                            .findAllByIsDesignerFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(
                                    pageable
                            );
                    break;
                case 'B':
                    teams = teamRepository
                            .findAllByIsBackendFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(
                                    pageable
                            );
                    break;
                case 'F':
                    teams = teamRepository
                            .findAllByIsFrontendFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(
                                    pageable
                            );
                    break;
                case 'M':
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
            switch (position.getType()) {
                case 'D':
                    teams = teamRepository
                            .findAllByIsDesignerFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByCreatedAtDesc(
                                    pageable
                            );
                    break;
                case 'B':
                    teams = teamRepository
                            .findAllByIsBackendFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByCreatedAtDesc(
                                    pageable
                            );
                    break;
                case 'F':
                    teams = teamRepository
                            .findAllByIsFrontendFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByCreatedAtDesc(
                                    pageable
                            );
                    break;
                case 'M':
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
     * 현재 팀 멤버 단건 조회 |
     * 404(CURRENT_TEAM_NOT_FOUND)
     */
    public TeamMember findOneCurrentTeamMember(User user) {
        return teamMemberRepository.findByUserAndIsDeletedIsFalse(user)
                .orElseThrow(() -> {
                    throw new CustomException(CURRENT_TEAM_NOT_FOUND);
                });
    }

    /**
     * 리더 검증 |
     * 403(REQUEST_FORBIDDEN)
     */
    public void validateIsLeader(TeamMember teamMember) {
        if (!teamMember.isLeader())
            throw new CustomException(REQUEST_FORBIDDEN);
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
                                        byte designerTotalCnt,
                                        byte backendTotalCnt,
                                        byte frontendTotalCnt,
                                        byte managerTotalCnt) {
        byte designerCurrentCnt = 0;
        byte backendCurrentCnt = 0;
        byte frontendCurrentCnt = 0;
        byte managerCurrentCnt = 0;

        for (TeamMember teamMember : team.getTeamMembers())
            switch (teamMember.getPosition()) {
                case 'D':
                    designerCurrentCnt++;
                    break;
                case 'B':
                    backendCurrentCnt++;
                    break;
                case 'F':
                    frontendCurrentCnt++;
                    break;
                case 'M':
                    managerCurrentCnt++;
                    break;
            }

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
     * 프로젝트 미완료 |
     * 500(SERVER_ERROR)
     */
    private void incomplete(Team team) {
        for(TeamMember teamMember : team.getTeamMembers()) {
            hardDeleteTeamMember(teamMember);

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
            teamMember.complete();

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
