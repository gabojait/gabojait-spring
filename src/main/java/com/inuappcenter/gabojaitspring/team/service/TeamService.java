package com.inuappcenter.gabojaitspring.team.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.type.Position;
import com.inuappcenter.gabojaitspring.team.domain.Team;
import com.inuappcenter.gabojaitspring.team.repository.TeamRepository;
import com.inuappcenter.gabojaitspring.user.domain.User;
import com.inuappcenter.gabojaitspring.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    public void join(Team team, User user, Position position) {

        switch (position.getType()) {
            case 'D':
                team.addDesigner(user);
                userService.joinTeam(user, team.getId());
                break;
            case 'B':
                team.addBackend(user);
                userService.joinTeam(user, team.getId());
                break;
            case 'F':
                team.addFrontend(user);
                userService.joinTeam(user, team.getId());
                break;
            case 'P':
                team.addProjectManager(user);
                userService.joinTeam(user, team.getId());
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
    public void validatePositionAvailability(Team team, Position position) {

        switch (position.getType()) {
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
     * 팀 다건 조회 |
     * 500(SERVER_ERROR)
     */
    public Page<Team> findMany(Integer pageFrom, Integer pageNum) {
        if (pageNum == null)
            pageNum = 20;

        try {
            return teamRepository.findTeamsByIsPublicIsTrueAndIsDeletedIsFalseOrderByModifiedDateDesc(
                    PageRequest.of(pageFrom, pageNum)
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
}
