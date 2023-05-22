package com.gabojait.gabojaitspring.team.service;

import com.gabojait.gabojaitspring.common.util.UtilityProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.team.domain.type.TeamOrder;
import com.gabojait.gabojaitspring.team.dto.req.TeamDefaultReqDto;
import com.gabojait.gabojaitspring.team.repository.TeamRepository;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final UtilityProvider utilityProvider;

    /**
     * 팀 생성 | main |
     * 409(TEAM_POSITION_UNAVAILABLE)
     * 500(SERVER_ERROR)
     */
    public Team create(Team team, User user) {
        join(team, user, user.getPosition());

        return team;
    }

    /**
     * 팀 정보 수정 | main |
     * 400(ID_CONVERT_INVALID)
     * 403(REQUEST_FORBIDDEN)
     * 404(TEAM_NOT_FOUND)
     * 409(DESIGNER_CNT_UPDATE_UNAVAILABLE / BACKEND_CNT_UPDATE_UNAVAILABLE / FRONTEND_CNT_UPDATE_UNAVAILABLE /
     * MANAGER_CNT_UPDATE_UNAVAILABLE)
     * 500(SERVER_ERROR)
     */
    public Team update(TeamDefaultReqDto request, User user) {
        Team team = findOneById(user.getCurrentTeamId().toString());

        if (!team.isLeader(user.getId().toString()))
            throw new CustomException(null, REQUEST_FORBIDDEN);
        validateAllPositionCnt(team,
                request.getDesignerTotalRecruitCnt(),
                request.getBackendTotalRecruitCnt(),
                request.getFrontendTotalRecruitCnt(),
                request.getManagerTotalRecruitCnt());

        team.updateTeam(request.getProjectName(),
                request.getProjectDescription(),
                request.getDesignerTotalRecruitCnt(),
                request.getBackendTotalRecruitCnt(),
                request.getFrontendTotalRecruitCnt(),
                request.getManagerTotalRecruitCnt(),
                request.getExpectation(),
                request.getOpenChatUrl());
        save(team);

        return team;
    }

    /**
     * 완료한 팀 전체 조회 | main&sub |
     * 400(ID_CONVERT_INVALID)
     * 404(TEAM_NOT_FOUND)
     */
    public List<Team> findAllCompleted(User user) {
        List<Team> teams = new ArrayList<>();
        if (user.getCompletedTeamIds().size() != 0)
            for (ObjectId teamId : user.getCompletedTeamIds())
                teams.add(findOneById(teamId.toString()));

        return teams;
    }

    /**
     * 유저 찜 여부 확인 | main |
     * 400(ID_CONVERT_INVALID)
     * 404(TEAM_NOT_FOUND)
     */
    public Boolean isFavoriteUser(User user, User otherUser) {
        if (!hasCurrentTeam(user))
            return null;

        Team team = findOneById(user.getCurrentTeamId().toString());
        if (!team.isLeader(user.getId().toString()))
            return null;

        for (ObjectId favoriteUserId : team.getFavoriteUserIds())
            if (favoriteUserId.toString().equals(otherUser.getId().toString()))
                return true;

        return false;
    }

    /**
     * 식별자로 단건 조회 | main&sub |
     * 400(ID_CONVERT_INVALID)
     * 404(TEAM_NOT_FOUND)
     */
    public Team findOneById(String teamId) {
        ObjectId id = utilityProvider.toObjectId(teamId);

        return teamRepository.findByIdAndIsDeletedIsFalse(id)
                .orElseThrow(() -> {
                    throw new CustomException(null, TEAM_NOT_FOUND);
                });
    }

    /**
     * 타팀 단건 조회 | main |
     * 400(ID_CONVERT_INVALID)
     * 404(TEAM_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public Team findOther(String teamId, User user) {
        Team team = findOneById(teamId);

        if (!team.isTeamMember(user.getId().toString())) {
            team.incrementVisitedCnt();
            save(team);
        }

        return team;
    }

    /**
     * 포지션과 팀 정렬 기준으로 팀 페이징 다건 조회 | main |
     * 500(SERVER_ERROR)
     */
    public Page<Team> findPagePositionOrder(String position, String teamOrder, Integer pageFrom, Integer pageSize) {
        Position p = Position.fromString(position);
        TeamOrder to = TeamOrder.fromString(teamOrder);
        Pageable pageable = utilityProvider.validatePaging(pageFrom, pageSize, 20);

        Page<Team> teams;

        if (p.equals(Position.NONE)) {
            switch (to.name().toLowerCase()) {
                case "active":
                    teams = findPageByActive(pageable);
                    break;
                case "popularity":
                    teams = findPageByPopularity(pageable);
                    break;
                default:
                    teams = findPageByCreated(pageable);
                    break;
            }
        } else {
            switch (to.name()) {
                case "active":
                    teams = findPagePositionByActive(p, pageable);
                    break;
                case "popularity":
                    teams = findPagePositionByPopularity(p, pageable);
                    break;
                default:
                    teams = findPagePositionByCreated(p, pageable);
                    break;
            }
        }

        return teams;
    }

    /**
     * 팀원 모집 여부 업데이트 | main |
     * 400(ID_CONVERT_INVALID)
     * 403(REQUEST_FORBIDDEN)
     * 404(TEAM_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public void updateIsRecruiting(User user, Boolean isRecruiting) {
        Team team = findOneById(user.getCurrentTeamId().toString());
        if (!team.isLeader(user.getId().toString()))
            throw new CustomException(null, REQUEST_FORBIDDEN);

        team.updateIsRecruiting(isRecruiting);
        save(team);
    }

    /**
     * 팀원 추방 | main |
     * 400(ID_CONVERT_INVALID)
     * 403(REQUEST_FORBIDDEN)
     * 404(TEAM_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public void fire(User leader, User teammate) {
        Team team = findOneById(leader.getCurrentTeamId().toString());
        if (!team.isLeader(leader.getId().toString()))
            throw new CustomException(null, REQUEST_FORBIDDEN);

        team.removeTeammate(teammate, true);
        save(team);
    }

    /**
     * 프로젝트 종료 | main |
     * 400(ID_CONVERT_INVALID)
     * 403(REQUEST_FORBIDDEN)
     * 404(TEAM_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public List<User> quit(User user, String projectUrl) {
        Team team = findOneById(user.getCurrentTeamId().toString());
        if (!team.isLeader(user.getId().toString()))
            throw new CustomException(null, REQUEST_FORBIDDEN);

        List<User> users = team.getAllMembersExceptLeader(team);

        if (projectUrl.isBlank())
            team.delete();
        else
            team.complete(projectUrl);
        save(team);

        return users;
    }

    /**
     * 회원 찜 업데이트 | main |
     * 400(ID_CONVERT_INVALID)
     * 403(REQUEST_FORBIDDEN)
     * 404(TEAM_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public void updateFavoriteUser(User leader, User user, boolean isAddFavorite) {
        Team team = findOneById(leader.getCurrentTeamId().toString());
        if (!team.isLeader(user.getId().toString()))
            throw new CustomException(null, REQUEST_FORBIDDEN);

        team.updateFavoriteUserId(user.getId(), isAddFavorite);

        save(team);
    }

    /**
     * 찜한 회원 식별자 전체 조회 | main |
     * 400(ID_CONVERT_INVALID)
     * 403(REQUEST_FORBIDDEN)
     * 404(TEAM_NOT_FOUND)
     */
    public List<ObjectId> findAllFavorite(User user) {
        Team team = findOneById(user.getCurrentTeamId().toString());
        if (!team.isLeader(user.getId().toString()))
            throw new CustomException(null, REQUEST_FORBIDDEN);

        return team.getFavoriteUserIds();
    }

    /**
     * 식별자로 팀 전체 조회 | main |
     * 500(SERVER_ERROR)
     */
    public List<Team> findAllById(List<ObjectId> teamIds) {
        List<Team> teams = new ArrayList<>();
        try {
            for (ObjectId teamId : teamIds) {
                Optional<Team> team = teamRepository.findByIdAndIsDeletedIsFalse(teamId);
                team.ifPresent(teams::add);
            }
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }

        return teams;
    }

    /**
     * 팀 탈퇴 | main |
     * 400(ID_CONVERT_INVALID)
     * 404(TEAM_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public void leave(User user) {
        Team team = findOneById(user.getCurrentTeamId().toString());

        team.removeTeammate(user, false);

        save(team);
    }

    /**
     * 회원 또는 팀 제안 | main |
     * 400(ID_CONVERT_INVALID)
     * 404(TEAM_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public void offer(String teamId, boolean isOfferedByUser) {
        Team team = findOneById(teamId);

        team.offer(isOfferedByUser);
        save(team);
    }

    /**
     * 회원의 제안 결정 | main |
     * 400(ID_CONVERT_INVALID)
     * 404(TEAM_NOT_FOUND)
     * 409(TEAM_POSITION_UNAVAILABLE)
     * 500(SERVER_ERROR)
     */
    public void decideOfferByUser(Offer offer, User user, boolean isAccepted) {
        if (!isAccepted)
            return;
        Position position = Position.fromChar(offer.getPosition());
        Team team = findOneById(offer.getTeamId().toString());
        join(team, user, position.getType());
    }

    /**
     * 팀의 제안 결정 | main |
     * 400(ID_CONVERT_INVALID)
     * 403(REQUEST_FORBIDDEN)
     * 404(TEAM_NOT_FOUND)
     * 409(TEAM_POSITION_UNAVAILABLE)
     * 500(SERVER_ERROR)
     */
    public void decideOfferByTeam(Offer offer, User user, User otherUser, boolean isAccepted) {
        Team team = findOneById(user.getCurrentTeamId().toString());
        if (!team.isLeader(user.getId().toString()))
            throw new CustomException(null, REQUEST_FORBIDDEN);
        if (!isAccepted)
            return;
        Position position = Position.fromChar(offer.getPosition());
        join(team, otherUser, position.getType());
    }

    /**
     * 리뷰 가능한 팀 전체 조회 | main |
     * 500(SERVER_ERROR)
     */
    public List<Team> findReviewableTeam(List<ObjectId> teamIds) {
        List<Team> teams = findAllById(teamIds);
        List<Team> reviewableTeams = new ArrayList<>();

        for (Team team : teams)
            if (LocalDateTime.now().minusWeeks(4).isBefore(team.getModifiedDate()) && team.getIsComplete())
                reviewableTeams.add(team);

        return reviewableTeams;
    }

    /**
     * 회원이 팀 지원전 검증 | sub |
     * 400(POSITION_TYPE_INVALID / ID_CONVERT_INVALID)
     * 404(TEAM_NOT_FOUND)
     * 409(TEAM_POSITION_UNAVAILABLE)
     * 500(SERVER_ERROR)
     */
    public void validatePreOfferByUser(String teamId, String position) {
        Position p = Position.fromString(position);
        Team team = findOneById(teamId);
        validatePositionAvailability(team, p.getType());
    }

    /**
     * 팀이 회원에게 채용 제안전 검증 | sub |
     * 400(POSITION_TYPE_INVALID / ID_CONVERT_INVALID)
     * 403(REQUEST_FORBIDDEN)
     * 404(TEAM_NOT_FOUND)
     * 409(TEAM_POSITION_UNAVAILABLE)
     * 500(SERVER_ERROR)
     */
    public void validatePreOfferByTeam(String teamId, String leaderId, String userId, String position) {
        Position p = Position.fromString(position);
        Team team = findOneById(teamId);
        validatePositionAvailability(team, p.getType());

        if (!team.isLeader(leaderId))
            throw new CustomException(null, REQUEST_FORBIDDEN);
    }

    /**
     * 팀 합류 |
     * 409(TEAM_POSITION_UNAVAILABLE)
     * 500(SERVER_ERROR)
     */
    private void join(Team team, User user, char position) {
        validatePositionAvailability(team, position);

        team.addTeammate(user, position);

        save(team);
    }

    /**
     * 특정 포지션 합류 여부 검증 |
     * 409(TEAM_POSITION_UNAVAILABLE)
     * 500(SERVER_ERROR)
     */
    private void validatePositionAvailability(Team team, char position) {
        switch (position) {
            case 'D':
                if (team.getDesigners().size() >= team.getDesignerTotalRecruitCnt())
                    throw new CustomException(null, TEAM_POSITION_UNAVAILABLE);
                break;
            case 'B':
                if (team.getBackends().size() >= team.getBackendTotalRecruitCnt())
                    throw new CustomException(null, TEAM_POSITION_UNAVAILABLE);
                break;
            case 'F':
                if (team.getFrontends().size() >= team.getFrontendTotalRecruitCnt())
                    throw new CustomException(null, TEAM_POSITION_UNAVAILABLE);
                break;
            case 'M':
                if (team.getManagers().size() >= team.getManagerTotalRecruitCnt())
                    throw new CustomException(null, TEAM_POSITION_UNAVAILABLE);
                break;
            default:
                throw new CustomException(null, SERVER_ERROR);
        }
    }

    /**
     * 포지션별 총 팀원 수 검증 |
     * 409(DESIGNER_CNT_UPDATE_UNAVAILABLE / BACKEND_CNT_UPDATE_UNAVAILABLE / FRONTEND_CNT_UPDATE_UNAVAILABLE /
     * MANAGER_CNT_UPDATE_UNAVAILABLE)
     */
    private void validateAllPositionCnt(Team team,
                                        short totalDesignerCnt,
                                        short totalBackendCnt,
                                        short totalFrontendCnt,
                                        short totalManagerCnt) {
        if (team.getDesigners().size() > totalDesignerCnt)
            throw new CustomException(null, DESIGNER_CNT_UPDATE_UNAVAILABLE);
        if (team.getBackends().size() > totalBackendCnt)
            throw new CustomException(null, BACKEND_CNT_UPDATE_UNAVAILABLE);
        if (team.getFrontends().size() > totalFrontendCnt)
            throw new CustomException(null, FRONTEND_CNT_UPDATE_UNAVAILABLE);
        if (team.getManagers().size() > totalManagerCnt)
            throw new CustomException(null, MANAGER_CNT_UPDATE_UNAVAILABLE);
    }

    /**
     * 현재 팀 여부 검증
     */
    private boolean hasCurrentTeam(User user) {
        return user.getCurrentTeamId() != null;
    }

    /**
     * 팀 저장 |
     * 500(SERVER_ERROR)
     */
    public Team save(Team team) {
        try {
            return teamRepository.save(team);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 전체 포지션을 활동순으로 팀 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<Team> findPageByActive(Pageable pageable) {
        try {
            return teamRepository.findAllByIsRecruitingIsTrueAndIsDeletedIsFalseOrderByTeamOfferCntDesc(pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 특정 포지션을 활동순으로 팀 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<Team> findPagePositionByActive(Position position, Pageable pageable) {
        try {
            Page<Team> teams;
            switch (position.getType()) {
                case 'D':
                    teams = teamRepository
                            .findAllByIsDesignerFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByTeamOfferCntDesc(pageable);
                    break;
                case 'B':
                    teams = teamRepository
                            .findAllByIsBackendFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByTeamOfferCntDesc(pageable);
                    break;
                case 'F':
                    teams = teamRepository
                            .findAllByIsFrontendFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByTeamOfferCntDesc(pageable);
                    break;
                case 'M':
                    teams = teamRepository
                            .findAllByIsManagerFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByTeamOfferCntDesc(pageable);
                    break;
                default:
                    throw new CustomException(null, SERVER_ERROR);
            }

            return teams;
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 전체 포지션을 인기순으로 팀 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<Team> findPageByPopularity(Pageable pageable) {
        try {
            return teamRepository.findAllByIsRecruitingIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 특정 포지션을 인기순으로 팀 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<Team> findPagePositionByPopularity(Position position, Pageable pageable) {
        try {
            Page<Team> teams;
            switch (position.getType()) {
                case 'D':
                    teams = teamRepository
                            .findAllByIsDesignerFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(pageable);
                    break;
                case 'B':
                    teams = teamRepository
                            .findAllByIsBackendFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(pageable);
                    break;
                case 'F':
                    teams = teamRepository
                            .findAllByIsFrontendFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(pageable);
                    break;
                case 'M':
                    teams = teamRepository
                            .findAllByIsManagerFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(pageable);
                    break;
                default:
                    throw new CustomException(null, SERVER_ERROR);
            }

            return teams;
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 전체 포지션을 생성순으로 팀 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<Team> findPageByCreated(Pageable pageable) {
        try {
            return teamRepository.findAllByIsRecruitingIsTrueAndIsDeletedIsFalseOrderByCreatedDateDesc(pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 특정 포지션을 생성순으로 팀 페이징 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private Page<Team> findPagePositionByCreated(Position position, Pageable pageable) {
        try {
            Page<Team> teams;
            switch (position.getType()) {
                case 'D':
                    teams = teamRepository
                            .findAllByIsDesignerFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByCreatedDateDesc(pageable);
                    break;
                case 'B':
                    teams = teamRepository
                            .findAllByIsBackendFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByCreatedDateDesc(pageable);
                    break;
                case 'F':
                    teams = teamRepository
                            .findAllByIsFrontendFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByCreatedDateDesc(pageable);
                    break;
                case 'M':
                    teams = teamRepository
                            .findAllByIsManagerFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByCreatedDateDesc(pageable);
                    break;
                default:
                    throw new CustomException(null, SERVER_ERROR);
            }

            return teams;
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }
}
