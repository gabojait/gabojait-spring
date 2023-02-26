package com.inuappcenter.gabojaitspring.team.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.team.domain.Team;
import com.inuappcenter.gabojaitspring.team.repository.TeamRepository;
import com.inuappcenter.gabojaitspring.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;

    /**
     * 저장 |
     * 500(SERVER_ERROR)
     */
    public Team save(Team team) {

        try {
            return teamRepository.save(team);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    public void joinTeam(Team team, User leader) {


    }

    /**
     * 포지션 여유 검증 |
     * 409(DESIGNER_POSITION_UNAVAILABLE)
     * 409(BACKEND_POSITION_UNAVAILABLE)
     * 409(FRONTEND_POSITION_UNAVAILABLE)
     * 409(PROJECT_MANAGER_POSITION_UNAVAILABLE)
     * 500(SERVER_ERROR)
     */
    public void validatePositionAvailability (Team team, User user) {

        switch (user.getPosition()) {
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
}
