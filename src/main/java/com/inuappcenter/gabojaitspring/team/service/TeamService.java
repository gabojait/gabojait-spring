package com.inuappcenter.gabojaitspring.team.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.type.Position;
import com.inuappcenter.gabojaitspring.profile.domain.type.TeamMemberStatus;
import com.inuappcenter.gabojaitspring.team.domain.Team;
import com.inuappcenter.gabojaitspring.team.dto.req.TeamDefaultReqDto;
import com.inuappcenter.gabojaitspring.team.repository.TeamRepository;
import com.inuappcenter.gabojaitspring.user.domain.User;
import com.inuappcenter.gabojaitspring.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserService userService;

    /**
     * 저장 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public Team save(Team team) {

        try {
            return teamRepository.save(team);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 팀 합류 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void join(Team team, User user, char position, TeamMemberStatus teamMemberStatus) {

        switch (position) {
            case 'D':
                team.addDesigner(user);
                userService.joinTeam(user, team.getId(), teamMemberStatus);
                break;
            case 'B':
                team.addBackend(user);
                userService.joinTeam(user, team.getId(), teamMemberStatus);
                break;
            case 'F':
                team.addFrontend(user);
                userService.joinTeam(user, team.getId(), teamMemberStatus);
                break;
            case 'P':
                team.addProjectManager(user);
                userService.joinTeam(user, team.getId(), teamMemberStatus);
                break;
            default:
                throw new CustomException(SERVER_ERROR);
        }

        save(team);
    }

    /**
     * 팀 리더 검증 |
     * 403(ROLE_NOT_ALLOWED)
     */
    public void validateLeader(Team team, User leader) {
        if (!team.getLeaderUserId().equals(leader.getId()))
            throw new CustomException(ROLE_NOT_ALLOWED);
    }

    /**
     * 팀원 검증 |
     * 409(TEAM_LEADER_CONFLICT)
     */
    public void validateNonLeader(Team team, User user) {
        if (team.getLeaderUserId().equals(user.getId()))
            throw new CustomException(TEAM_LEADER_CONFLICT);
    }

    /**
     * 단건 조회 |
     * 404(TEAM_NOT_FOUND)
     */
    public Team findOne(String teamId) {

        return teamRepository.findById(new ObjectId(teamId))
                .orElseThrow(() -> {
                    throw new CustomException(TEAM_NOT_FOUND);
                });
    }

    /**
     * 완료한 팀 전체 조회 |
     * 404(TEAM_NOT_FOUND)
     */
    public List<Team> findAllPrevious(User user) {

        if (user.getCompletedTeamIds().size() == 0)
            return null;
        else {
            List<Team> teams = new ArrayList<>();

            for (ObjectId teamId : user.getCompletedTeamIds())
                teams.add(teamRepository.findById(teamId)
                        .orElseThrow(() -> {
                            throw new CustomException(TEAM_NOT_FOUND);
                        }));

            return teams;
        }
    }

    /**
     * 포지션 여유 검증 |
     * 409(DESIGNER_POSITION_UNAVAILABLE)
     * 409(BACKEND_POSITION_UNAVAILABLE)
     * 409(FRONTEND_POSITION_UNAVAILABLE)
     * 409(PROJECT_MANAGER_POSITION_UNAVAILABLE)
     * 500(SERVER_ERROR)
     */
    public void validatePositionAvailability(Team team, char position) {

        switch (position) {
            case 'D':
                if (team.getDesignerTotalRecruitCnt() <= team.getDesigners().size())
                    throw new CustomException(DESIGNER_POSITION_UNAVAILABLE);
                break;
            case 'B':
                if (team.getBackendTotalRecruitCnt() <= team.getBackends().size())
                    throw new CustomException(BACKEND_POSITION_UNAVAILABLE);
                break;
            case 'F':
                if (team.getFrontendTotalRecruitCnt() <= team.getFrontends().size())
                    throw new CustomException(FRONTEND_POSITION_UNAVAILABLE);
                break;
            case 'P':
                if (team.getProjectManagerTotalRecruitCnt() <= team.getProjectManagers().size())
                    throw new CustomException(PROJECT_MANAGER_POSITION_UNAVAILABLE);
                break;
            default:
                throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 유저가 찜한 팀 다건 조회 |
     * 삭제된 팀일 경우 찜한 목록에서 삭제 |
     * 500(SERVER_ERROR)
     */
    public List<Team> findManyUserFavoriteTeamsAndRemoveIfDeleted(User user, Integer pageFrom, Integer pageSize) {

        if (pageSize == null)
            pageSize = 20;

        List<ObjectId> favoriteTeamIds = user.getFavoriteTeamIdsByPaging(pageFrom, pageSize);
        List<Team> teams = new ArrayList<>();

        if (favoriteTeamIds.isEmpty()) {
            return teams;
        } else {
            try {
                for (ObjectId favoriteTeamId : favoriteTeamIds) {
                    Optional<Team> team = teamRepository.findByIdAndIsDeletedIsFalse(favoriteTeamId);

                    if (team.isEmpty()) {
                        userService.removeFavoriteTeam(user, favoriteTeamId);
                    } else {
                        teams.add(team.get());
                    }
                }
            } catch (RuntimeException e) {
                throw new CustomException(SERVER_ERROR);
            }
        }

        return teams;
    }

    /**
     * 팀 다건 조회 |
     * 500(SERVER_ERROR)
     */
    public Page<Team> findMany(Integer pageFrom, Integer pageSize) {
        if (pageSize == null)
            pageSize = 20;

        try {
            return teamRepository.findTeamsByIsPublicIsTrueAndIsDeletedIsFalseOrderByModifiedDateDesc(
                    PageRequest.of(pageFrom, pageSize)
            );
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 팀 공개 여부 수정 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void updateIsPublic(Team team, boolean isPublic) {

        try {
            team.updateIsPublic(isPublic);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        save(team);
    }

    /**
     * 지원자 제안 추가 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void addApplication(Team team, ObjectId offerId) {

        try {
            team.addApplicationId(offerId);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        save(team);
    }

    /**
     * 지원자 제안 제거 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void removeApplication(Team team, ObjectId offerId) {

        try {
            team.removeApplicationId(offerId);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        save(team);
    }

    /**
     * 팀 제안 추가 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void addRecruit(Team team, ObjectId offerId) {

        try {
            team.addRecruitId(offerId);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        save(team);
    }

    /**
     * 팀 제안 제거 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void removeRecruit(Team team, ObjectId offerId) {

        try {
            team.removeRecruitId(offerId);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        save(team);
    }

    /**
     * 유저 찜 추가 |
     * 500(SERVER_ERROR)
     */
    public void addFavoriteUser(Team team, ObjectId userId) {

        try {
            team.addFavoriteUserId(userId);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        save(team);
    }

    /**
     * 유저 찜 제거 |
     * 500(SERVER_ERROR)
     */
    public void removeFavoriteUser(Team team, ObjectId userId) {

        try {
            team.removeFavoriteUserId(userId);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        save(team);
    }

    /**
     * 현재 소속 팀의 포지션 검증 |
     * 500(SERVER_ERROR)
     */
    public Position getPositionInCurrentTeam(Team team, User user) {

        for (User designer : team.getDesigners())
            if (designer.getId().equals(user.getId()))
                return Position.DESIGNER;

        for (User backend : team.getBackends())
            if (backend.getId().equals(user.getId()))
                return Position.BACKEND;

        for (User frontend : team.getFrontends())
            if (frontend.getId().equals(user.getId()))
                return Position.FRONTEND;

        for (User projectManager : team.getProjectManagers())
            if (projectManager.getId().equals(user.getId()))
                return Position.PM;

        throw new CustomException(SERVER_ERROR);
    }

    /**
     * 업데이트될 포지션 여유 검증 |
     * 400(DESIGNER_LIMIT_INVALID)
     * 400(BACKEND_LIMIT_INVALID)
     * 400(FRONTEND_LIMIT_INVALID)
     * 400(PROJECT_MANAGER_LIMIT_INVALID)
     */
    public void validateUpdatePositionAvailability(Team team, TeamDefaultReqDto request, Position position) {

        Map<Character, Short> totalRecruitCnt = new HashMap<>();
        totalRecruitCnt.put(Position.DESIGNER.getType(), request.getDesignerTotalRecruitCnt());
        totalRecruitCnt.put(Position.BACKEND.getType(), request.getBackendTotalRecruitCnt());
        totalRecruitCnt.put(Position.FRONTEND.getType(), request.getFrontendTotalRecruitCnt());
        totalRecruitCnt.put(Position.PM.getType(), request.getProjectManagerTotalRecruitCnt());

        incrementLeaderPosition(position, totalRecruitCnt);

        if (team.getDesigners().size() > totalRecruitCnt.get(Position.DESIGNER.getType()))
            throw new CustomException(DESIGNER_LIMIT_INVALID);
        if (team.getBackends().size() > totalRecruitCnt.get(Position.BACKEND.getType()))
            throw new CustomException(BACKEND_LIMIT_INVALID);
        if (team.getFrontends().size() > totalRecruitCnt.get(Position.FRONTEND.getType()))
            throw new CustomException(FRONTEND_LIMIT_INVALID);
        if (team.getProjectManagers().size() > totalRecruitCnt.get(Position.PM.getType()))
            throw new CustomException(PROJECT_MANAGER_LIMIT_INVALID);
    }

    /**
     * 리더 포지션 추가 |
     * 500(SERVER_ERROR)
     */
    private void incrementLeaderPosition(Position position, Map<Character, Short> totalRecruitCnt) {

        switch (position.getType()) {
            case 'D':
                totalRecruitCnt.replace(Position.DESIGNER.getType(),
                        (short) (totalRecruitCnt.get(Position.DESIGNER.getType()) + 1));
                break;
            case 'B':
                totalRecruitCnt.replace(Position.BACKEND.getType(),
                        (short) (totalRecruitCnt.get(Position.BACKEND.getType()) + 1));
                break;
            case 'F':
                totalRecruitCnt.replace(Position.FRONTEND.getType(),
                        (short) (totalRecruitCnt.get(Position.FRONTEND.getType()) + 1));
                break;
            case 'P':
                totalRecruitCnt.replace(Position.PM.getType(),
                        (short) (totalRecruitCnt.get(Position.PM.getType()) + 1));
                break;
            default:
                throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 팀 정보 수정 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void update(Team team, TeamDefaultReqDto request, Position position) {

        Map<Character, Short> totalRecruitCnt = new HashMap<>();
        totalRecruitCnt.put(Position.DESIGNER.getType(), request.getDesignerTotalRecruitCnt());
        totalRecruitCnt.put(Position.BACKEND.getType(), request.getBackendTotalRecruitCnt());
        totalRecruitCnt.put(Position.FRONTEND.getType(), request.getFrontendTotalRecruitCnt());
        totalRecruitCnt.put(Position.PM.getType(), request.getProjectManagerTotalRecruitCnt());

        incrementLeaderPosition(position, totalRecruitCnt);

        try {
            team.updateInfo(request.getProjectName(),
                    request.getProjectDescription(),
                    totalRecruitCnt.get(Position.DESIGNER.getType()),
                    totalRecruitCnt.get(Position.BACKEND.getType()),
                    totalRecruitCnt.get(Position.FRONTEND.getType()),
                    totalRecruitCnt.get(Position.PM.getType()),
                    request.getExpectation(),
                    request.getOpenChatUrl());
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        save(team);
    }

    /**
     * 팀 탈퇴 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void leaveTeam(Team team, User user, Position position) {

        try {
            switch (position.getType()) {
                case 'D':
                    userService.quitTeam(user);
                    team.removeDesigner(user);
                    break;
                case 'B':
                    userService.quitTeam(user);
                    team.removeBackend(user);
                    break;
                case 'F':
                    userService.quitTeam(user);
                    team.removeFrontend(user);
                    break;
                case 'P':
                    userService.quitTeam(user);
                    team.removeProjectManager(user);
                    break;
                default:
                    throw new CustomException(SERVER_ERROR);
            }
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        save(team);
    }

    /**
     * 프로젝트 완료 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void projectComplete(Team team, String projectUrl) {

        for (User designer : team.getDesigners())
            userService.completeTeam(designer);
        for (User backend : team.getBackends())
            userService.completeTeam(backend);
        for (User frontend : team.getFrontends())
            userService.completeTeam(frontend);
        for (User projectManager : team.getProjectManagers())
            userService.completeTeam(projectManager);

        try {
            team.complete(projectUrl);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        save(team);
    }

    /**
     * 프로젝트 완료 여부 검증 |
     * 409(INCOMPLETE_PROJECT)
     * 500(SERVER_ERROR)
     */
    public void validateProjectComplete(Team team) {

        try {
            if (!team.getIsComplete())
                throw new CustomException(INCOMPLETE_PROJECT);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 과거 팀원 여부 검증 |
     * 500(SERVER_ERROR)
     */
    public String validatePreviouslyTeammates(User userOne, User userTwo) {

        try {
            List<ObjectId> userOneTeamId = userOne.getCompletedTeamIds();

            for (ObjectId teamId : userOneTeamId)
                if (userTwo.getCompletedTeamIds().contains(teamId))
                    return teamId.toString();

            throw new CustomException(SERVER_ERROR);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 팀 삭제 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void delete(Team team) {

        for (User designer : team.getDesigners())
            userService.quitTeam(designer);
        for (User backend : team.getBackends())
            userService.quitTeam(backend);
        for (User frontend : team.getFrontends())
            userService.quitTeam(frontend);
        for (User projectManager : team.getProjectManagers())
            userService.quitTeam(projectManager);

        try {
            team.delete();
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }

        save(team);
    }
}
