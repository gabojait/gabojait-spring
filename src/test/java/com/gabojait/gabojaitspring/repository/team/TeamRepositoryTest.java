package com.gabojait.gabojaitspring.repository.team;

import com.gabojait.gabojaitspring.common.dto.response.PageData;
import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.user.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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

    @ParameterizedTest(name = "[{index}] {0} 포지션으로 팀 페이징 조회한다")
    @EnumSource(Position.class)
    @DisplayName("팀 페이징 조회가 정상 작동한다")
    void givenValid_whenFindPage_thenReturn(Position position) {
        // given
        Team team1 = createTeam("프로젝트1", (byte) 3);
        Team team2 = createTeam("프로젝트2", (byte) 2);
        Team team3 = createTeam("프로젝트3", (byte) 1);
        teamRepository.saveAll(List.of(team1, team2, team3));

        long pageFrom = Long.MAX_VALUE;
        int pageSize = 2;

        // when
        PageData<List<Team>> teams = teamRepository.findPage(position, pageFrom, pageSize);

        // then
        assertAll(
                () -> assertThat(teams.getData()).containsExactly(team3, team2),
                () -> assertThat(teams.getData().size()).isEqualTo(pageSize),
                () -> assertThat(teams.getTotal()).isEqualTo(3L)
        );
    }

    @Test
    @DisplayName("존재 하지 않은 팀 페이징 조회가 정상 작동한다")
    void givenNoneExistingTeam_whenFindPage_thenReturn() {
        // given
        long pageFrom = Long.MAX_VALUE;
        int pageSize = 1;
        Position position = Position.NONE;

        // when

        PageData<List<Team>> teams = teamRepository.findPage(position, pageFrom, pageSize);

        assertAll(
                () -> assertThat(teams.getData()).isEmpty(),
                () -> assertThat(teams.getTotal()).isEqualTo(0L)
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