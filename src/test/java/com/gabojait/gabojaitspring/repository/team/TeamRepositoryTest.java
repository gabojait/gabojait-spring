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
import static org.junit.jupiter.api.Assertions.assertAll;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class TeamRepositoryTest {

    @Autowired private TeamRepository teamRepository;

    @Test
    @DisplayName("전체 포지션으로 팀 페이징 조회가 정상 작동한다")
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
                        .containsExactlyInAnyOrder(team4, team3),
                () -> assertThat(teams.getData().size()).isEqualTo(pageSize),
                () -> assertThat(teams.getTotal()).isEqualTo(4)
        );
    }

    @Test
    @DisplayName("특정 포지션으로 팀 페이징 조회가 정상 작동한다")
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
                        .containsExactly(team3, team2),
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