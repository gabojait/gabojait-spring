package com.gabojait.gabojaitspring.repository.team;

import com.gabojait.gabojaitspring.api.dto.common.response.PageData;
import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.user.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class TeamRepositoryTest {

    @Autowired private TeamRepository teamRepository;

    @Test
    @DisplayName("전체 포지션으로 팀 페이징 조회를 한다.")
    void givenNonePosition_whenFindPage_thenReturn() {
        // given
        Team team1 = createTeam("프로젝트1", (byte) 2);
        Team team2 = createTeam("프로젝트2", (byte) 2);
        Team team3 = createTeam("프로젝트3", (byte) 1);
        Team team4 = createTeam("프로젝트4", (byte) 0);
        teamRepository.saveAll(List.of(team1, team2, team3, team4));

        long pageFrom = Long.MAX_VALUE;
        int pageSize = 2;
        Position position = Position.NONE;

        // when
        PageData<List<Team>> teams = teamRepository.findPage(position, pageFrom, pageSize);

        // then
        assertAll(
                () -> assertThat(teams.getData())
                        .extracting("id", "projectName", "projectDescription", "expectation", "openChatUrl", "projectUrl",
                                "designerCurrentCnt", "backendCurrentCnt", "frontendCurrentCnt", "managerCurrentCnt",
                                "designerMaxCnt", "backendMaxCnt", "frontendMaxCnt", "managerMaxCnt", "visitedCnt",
                                "isRecruiting", "isDeleted")
                        .containsExactly(
                                tuple(team4.getId(), team4.getProjectName(), team4.getProjectDescription(),
                                        team4.getExpectation(), team4.getOpenChatUrl(), team4.getProjectUrl(),
                                        team4.getDesignerCurrentCnt(), team4.getBackendCurrentCnt(),
                                        team4.getFrontendCurrentCnt(), team4.getManagerCurrentCnt(), team4.getDesignerMaxCnt(),
                                        team4.getBackendMaxCnt(), team4.getFrontendMaxCnt(), team4.getManagerMaxCnt(),
                                        team4.getVisitedCnt(), team4.getIsRecruiting(), team4.getIsDeleted()),
                                tuple(team3.getId(), team3.getProjectName(), team3.getProjectDescription(),
                                        team3.getExpectation(), team3.getOpenChatUrl(), team3.getProjectUrl(),
                                        team3.getDesignerCurrentCnt(), team3.getBackendCurrentCnt(),
                                        team3.getFrontendCurrentCnt(), team3.getManagerCurrentCnt(), team3.getDesignerMaxCnt(),
                                        team3.getBackendMaxCnt(), team3.getFrontendMaxCnt(), team3.getManagerMaxCnt(),
                                        team3.getVisitedCnt(), team3.getIsRecruiting(), team3.getIsDeleted())
                        ),
                () -> assertThat(teams.getData().size()).isEqualTo(pageSize),
                () -> assertThat(teams.getTotal()).isEqualTo(4)
        );
    }

    @Test
    @DisplayName("특정 포지션으로 팀 페이징 조회를 한다.")
    void givenPosition_whenFindPage_thenReturn() {
        // given
        Team team1 = createTeam("프로젝트1", (byte) 2);
        Team team2 = createTeam("프로젝트2", (byte) 2);
        Team team3 = createTeam("프로젝트3", (byte) 1);
        Team team4 = createTeam("프로젝트4", (byte) 0);
        teamRepository.saveAll(List.of(team1, team2, team3, team4));

        long pageFrom = Long.MAX_VALUE;
        int pageSize = 2;
        Position position = Position.BACKEND;

        // when
        PageData<List<Team>> teams = teamRepository.findPage(position, pageFrom, pageSize);

        // then
        assertAll(
                () -> assertThat(teams.getData())
                        .extracting("id", "projectName", "projectDescription", "expectation", "openChatUrl", "projectUrl",
                                "designerCurrentCnt", "backendCurrentCnt", "frontendCurrentCnt", "managerCurrentCnt",
                                "designerMaxCnt", "backendMaxCnt", "frontendMaxCnt", "managerMaxCnt", "visitedCnt",
                                "isRecruiting", "isDeleted")
                        .containsExactly(
                                tuple(team3.getId(), team3.getProjectName(), team3.getProjectDescription(),
                                        team3.getExpectation(), team3.getOpenChatUrl(), team3.getProjectUrl(),
                                        team3.getDesignerCurrentCnt(), team3.getBackendCurrentCnt(),
                                        team3.getFrontendCurrentCnt(), team3.getManagerCurrentCnt(), team3.getDesignerMaxCnt(),
                                        team3.getBackendMaxCnt(), team3.getFrontendMaxCnt(), team3.getManagerMaxCnt(),
                                        team3.getVisitedCnt(), team3.getIsRecruiting(), team3.getIsDeleted()),
                                tuple(team2.getId(), team2.getProjectName(), team2.getProjectDescription(),
                                        team2.getExpectation(), team2.getOpenChatUrl(), team2.getProjectUrl(),
                                        team2.getDesignerCurrentCnt(), team2.getBackendCurrentCnt(),
                                        team2.getFrontendCurrentCnt(), team2.getManagerCurrentCnt(), team2.getDesignerMaxCnt(),
                                        team2.getBackendMaxCnt(), team2.getFrontendMaxCnt(), team2.getManagerMaxCnt(),
                                        team2.getVisitedCnt(), team2.getIsRecruiting(), team2.getIsDeleted())
                        ),
                () -> assertThat(teams.getData().size()).isEqualTo(pageSize),
                () -> assertThat(teams.getTotal()).isEqualTo(3)
        );
    }

    private Team createTeam(String projectName, byte maxCnt) {
        return Team.builder()
                .projectName(projectName)
                .projectDescription("설명입니다.")
                .expectation("바라는 점입니다.")
                .openChatUrl("kakao.com/o/gabojait.com")
                .designerMaxCnt(maxCnt)
                .backendMaxCnt(maxCnt)
                .frontendMaxCnt(maxCnt)
                .managerMaxCnt(maxCnt)
                .build();
    }

}