package com.gabojait.gabojaitspring.domain.team;

import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.domain.user.User;
import com.gabojait.gabojaitspring.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class TeamTest {

    @Test
    @DisplayName("팀을 생성한다.")
    void builder() {
        // given
        String projectName = "가보자잇";
        String projectDescription = "가보자잇입니다.";
        String expectation = "열정적인 사람을 구합니다.";
        String openChatUrl = "kakao.com/o/gabojait";
        byte designerMaxCnt = 2;
        byte backendMaxCnt = 2;
        byte frontendMaxCnt = 2;
        byte managerMaxCnt = 2;

        // when
        Team team = createTeam(projectName, projectDescription, expectation, openChatUrl, designerMaxCnt, backendMaxCnt,
                frontendMaxCnt, managerMaxCnt);

        // then
        assertThat(team)
                .extracting("projectName", "projectDescription", "expectation", "openChatUrl", "projectUrl",
                        "completedAt", "designerCurrentCnt", "backendCurrentCnt", "frontendCurrentCnt",
                        "managerCurrentCnt", "designerMaxCnt", "backendMaxCnt", "frontendMaxCnt", "managerMaxCnt",
                        "visitedCnt", "isRecruiting", "isDeleted")
                .containsExactly(projectName, projectDescription, expectation, openChatUrl, null, null, (byte) 0,
                        (byte) 0, (byte) 0, (byte) 0, designerMaxCnt, backendMaxCnt, frontendMaxCnt, managerMaxCnt, 0L,
                        true, false);
    }

    @Test
    @DisplayName("팀을 업데이트한다.")
    void givenValid_whenUpdate_thenReturn() {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);

        String projectName = "가볼까잇";
        String projectDescription = "가보볼까잇입니다.";
        String expectation = "재밋는 사람을 구합니다.";
        byte designerMaxCnt = 3;
        byte backendMaxCnt = 3;
        byte frontendMaxCnt = 3;
        byte managerMaxCnt = 3;

        // when
        team.update(projectName, projectDescription, expectation, designerMaxCnt, backendMaxCnt,
                frontendMaxCnt, managerMaxCnt);

        // then
        assertThat(team)
                .extracting("projectName", "projectDescription", "expectation", "designerMaxCnt", "backendMaxCnt",
                        "frontendMaxCnt", "managerMaxCnt")
                .containsExactly(projectName, projectDescription, expectation, designerMaxCnt, backendMaxCnt,
                        frontendMaxCnt, managerMaxCnt);
    }

    @Test
    @DisplayName("디자이너 현재 수가 업데이트될 최대 수 보다 많으면 예외가 발생한다.")
    void givenLesserDesigner_whenUpdate_thenThrow() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);
        createTeamMember(Position.DESIGNER, true, user, team);

        String projectName = "가볼까잇";
        String projectDescription = "가보볼까잇입니다.";
        String expectation = "재밋는 사람을 구합니다.";
        byte designerMaxCnt = 0;
        byte backendMaxCnt = 1;
        byte frontendMaxCnt = 1;
        byte managerMaxCnt = 1;

        // when & then
        assertThatThrownBy(() -> team.update(projectName, projectDescription, expectation, designerMaxCnt,
                backendMaxCnt, frontendMaxCnt, managerMaxCnt))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(DESIGNER_CNT_UPDATE_UNAVAILABLE);
    }

    @Test
    @DisplayName("백엔드 현재 수가 업데이트될 최대 수 보다 많으면 예외가 발생한다.")
    void givenLesserBackend_whenUpdate_thenThrow() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);
        createTeamMember(Position.BACKEND, true, user, team);

        String projectName = "가볼까잇";
        String projectDescription = "가보볼까잇입니다.";
        String expectation = "재밋는 사람을 구합니다.";
        byte designerMaxCnt = 1;
        byte backendMaxCnt = 0;
        byte frontendMaxCnt = 1;
        byte managerMaxCnt = 1;

        // when & then
        assertThatThrownBy(() -> team.update(projectName, projectDescription, expectation, designerMaxCnt,
                backendMaxCnt, frontendMaxCnt, managerMaxCnt))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(BACKEND_CNT_UPDATE_UNAVAILABLE);
    }

    @Test
    @DisplayName("프런트엔드 현재 수가 업데이트될 최대 수 보다 많으면 예외가 발생한다.")
    void givenLesserFrontend_whenUpdate_thenThrow() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);
        createTeamMember(Position.FRONTEND, true, user, team);

        String projectName = "가볼까잇";
        String projectDescription = "가보볼까잇입니다.";
        String expectation = "재밋는 사람을 구합니다.";
        byte designerMaxCnt = 1;
        byte backendMaxCnt = 1;
        byte frontendMaxCnt = 0;
        byte managerMaxCnt = 1;

        // when & then
        assertThatThrownBy(() -> team.update(projectName, projectDescription, expectation, designerMaxCnt,
                backendMaxCnt, frontendMaxCnt, managerMaxCnt))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(FRONTEND_CNT_UPDATE_UNAVAILABLE);
    }

    @Test
    @DisplayName("매니저 현재 수가 업데이트될 최대 수 보다 많으면 예외가 발생한다.")
    void givenLesserManager_whenUpdate_thenThrow() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);
        createTeamMember(Position.MANAGER, true, user, team);

        String projectName = "가볼까잇";
        String projectDescription = "가보볼까잇입니다.";
        String expectation = "재밋는 사람을 구합니다.";
        byte designerMaxCnt = 1;
        byte backendMaxCnt = 1;
        byte frontendMaxCnt = 1;
        byte managerMaxCnt = 0;

        // when & then
        assertThatThrownBy(() -> team.update(projectName, projectDescription, expectation, designerMaxCnt,
                backendMaxCnt, frontendMaxCnt, managerMaxCnt))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(MANAGER_CNT_UPDATE_UNAVAILABLE);
    }

    @Test
    @DisplayName("디자이너가 팀을 들어간다.")
    void givenDesigner_whenJoin_thenReturn() {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);

        // when
        team.join(Position.DESIGNER);

        // then
        assertThat(team)
                .extracting("designerCurrentCnt", "backendCurrentCnt", "frontendCurrentCnt", "managerCurrentCnt",
                        "isRecruiting")
                .containsExactly((byte) 1, (byte) 0, (byte) 0, (byte) 0, true);
    }

    @Test
    @DisplayName("백엔드 개발자가 팀을 들어간다.")
    void givenBackend_whenJoin_thenReturn() {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);

        // when
        team.join(Position.BACKEND);

        // then
        assertThat(team)
                .extracting("designerCurrentCnt", "backendCurrentCnt", "frontendCurrentCnt", "managerCurrentCnt",
                        "isRecruiting")
                .containsExactly((byte) 0, (byte) 1, (byte) 0, (byte) 0, true);
    }

    @Test
    @DisplayName("프론트엔드 개발자가 팀을 들어간다.")
    void givenFrontend_whenJoin_thenReturn() {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);

        // when
        team.join(Position.FRONTEND);

        // then
        assertThat(team)
                .extracting("designerCurrentCnt", "backendCurrentCnt", "frontendCurrentCnt", "managerCurrentCnt",
                        "isRecruiting")
                .containsExactly((byte) 0, (byte) 0, (byte) 1, (byte) 0, true);
    }

    @Test
    @DisplayName("프로젝트 매니저가 팀을 들어간다.")
    void givenManager_whenJoin_thenReturn() {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);

        // when
        team.join(Position.MANAGER);

        // then
        assertThat(team)
                .extracting("designerCurrentCnt", "backendCurrentCnt", "frontendCurrentCnt", "managerCurrentCnt",
                        "isRecruiting")
                .containsExactly((byte) 0, (byte) 0, (byte) 0, (byte) 1, true);
    }

    @Test
    @DisplayName("모든 포지션 인원이 차면 팀 모집이 마감된다.")
    void givenFull_whenJoin_thenReturn() {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 0, (byte) 0,
                (byte) 0, (byte) 1);

        // when
        team.join(Position.MANAGER);

        // then
        assertThat(team)
                .extracting("designerCurrentCnt", "backendCurrentCnt", "frontendCurrentCnt", "managerCurrentCnt",
                        "isRecruiting")
                .containsExactly((byte) 0, (byte) 0, (byte) 0, (byte) 1, false);
    }

    @Test
    @DisplayName("디자이너가 팀을 나간다.")
    void givenDesigner_whenLeave_thenReturn() {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);
        team.join(Position.DESIGNER);
        team.join(Position.DESIGNER);

        // when
        team.leave(Position.DESIGNER);

        // then
        assertThat(team)
                .extracting("designerCurrentCnt", "isRecruiting")
                .containsExactly((byte) 1, true);
    }

    @Test
    @DisplayName("백엔드 개발자가 팀을 나간다.")
    void givenBackend_whenLeave_thenReturn() {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);
        team.join(Position.BACKEND);
        team.join(Position.BACKEND);

        // when
        team.leave(Position.BACKEND);

        // then
        assertThat(team)
                .extracting("backendCurrentCnt", "isRecruiting")
                .containsExactly((byte) 1, true);
    }

    @Test
    @DisplayName("프런트엔드 개발자가 팀을 나간다.")
    void givenFrontend_whenLeave_thenReturn() {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);
        team.join(Position.FRONTEND);
        team.join(Position.FRONTEND);

        // when
        team.leave(Position.FRONTEND);

        // then
        assertThat(team)
                .extracting("frontendCurrentCnt", "isRecruiting")
                .containsExactly((byte) 1, true);
    }

    @Test
    @DisplayName("프로젝트 매니저가 팀을 나간다.")
    void givenManager_whenLeave_thenReturn() {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);
        team.join(Position.MANAGER);
        team.join(Position.MANAGER);

        // when
        team.leave(Position.MANAGER);

        // then
        assertThat(team)
                .extracting("managerCurrentCnt", "isRecruiting")
                .containsExactly((byte) 1, true);
    }

    @Test
    @DisplayName("팀원 모집 여부를 업데이트한다.")
    void updateIsRecruiting() {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);

        // when
        team.updateIsRecruiting(false);

        // then
        assertThat(team.getIsRecruiting()).isFalse();
    }

    @Test
    @DisplayName("마감되지 않은 디자이너 포지션의 마감 여부를 확인한다.")
    void givenDesignerFull_whenIsPositionFull_thenReturn() {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 1, (byte) 1,
                (byte) 1, (byte) 1);

        // when
        boolean result = team.isPositionFull(Position.DESIGNER);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("마감되지 않은 백엔드 개발자 포지션의 마감 여부를 확인한다.")
    void givenBackendFull_whenIsPositionFull_thenReturn() {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 1, (byte) 1,
                (byte) 1, (byte) 1);

        // when
        boolean result = team.isPositionFull(Position.BACKEND);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("마감되지 않은 프론트엔드 개발자 포지션의 마감 여부를 확인한다.")
    void givenFrontendFull_whenIsPositionFull_thenReturn() {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 1, (byte) 1,
                (byte) 1, (byte) 1);

        // when
        boolean result = team.isPositionFull(Position.FRONTEND);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("마감되지 않은 프로젝트 매니저 포지션의 마감 여부를 확인한다.")
    void givenManagerFull_whenIsPositionFull_thenReturn() {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 1, (byte) 1,
                (byte) 1, (byte) 1);

        // when
        boolean result = team.isPositionFull(Position.MANAGER);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("마감된 디자이너 포지션의 마감 여부를 확인한다.")
    void givenDesignerNotFull_whenIsPositionFull_thenReturn() {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 0, (byte) 0,
                (byte) 0, (byte) 0);

        // when
        boolean result = team.isPositionFull(Position.DESIGNER);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("마감되지 않은 백엔드 개발자 포지션의 마감 여부를 확인한다.")
    void givenBackendNotFull_whenIsPositionFull_thenReturn() {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 0, (byte) 0,
                (byte) 0, (byte) 0);

        // when
        boolean result = team.isPositionFull(Position.BACKEND);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("마감되지 않은 프론트엔드 개발자 포지션의 마감 여부를 확인한다.")
    void givenFrontendNotFull_whenIsPositionFull_thenReturn() {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 0, (byte) 0,
                (byte) 0, (byte) 0);

        // when
        boolean result = team.isPositionFull(Position.FRONTEND);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("마감되지 않은 프로젝트 매니저 포지션의 마감 여부를 확인한다.")
    void givenManagerNotFull_whenIsPositionFull_thenReturn() {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 0, (byte) 0,
                (byte) 0, (byte) 0);

        // when
        boolean result = team.isPositionFull(Position.MANAGER);

        // then
        assertThat(result).isTrue();
    }
    @Test
    @DisplayName("팀 프로필을 방문한다.")
    void visited() {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);

        int visitedCnt = new Random().nextInt(100) + 1;

        // when
        for (int i = 0; i < visitedCnt; i++)
            team.visit();

        // then
        assertEquals(visitedCnt, team.getVisitedCnt());
    }

    @Test
    @DisplayName("프로젝트를 미완료한다.")
    void incomplete() {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);

        // when
        team.incomplete();

        // then
        assertThat(team)
                .extracting("isRecruiting", "isDeleted")
                .containsExactly(false, true);
    }

    @Test
    @DisplayName("프로젝트를 완료한다.")
    void complete() {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);

        String projectUrl = "github.com/gabojait";
        LocalDateTime completedAt = LocalDateTime.now();

        // when
        team.complete(projectUrl, completedAt);

        // then
        assertThat(team)
                .extracting("projectUrl", "completedAt", "isRecruiting")
                .contains(projectUrl, completedAt, false);
    }

    @Test
    @DisplayName("같은 객체인 팀을 비교하면 동일하다.")
    void givenEqualInstance_whenEquals_thenReturn() {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);

        // when
        boolean result = team.equals(team);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("같은 정보인 팀을 비교하면 동일하다.")
    void givenEqualData_whenEquals_thenReturn() {
        // given
        String projectName = "가보자잇";
        String projectDescription = "가보자잇입니다.";
        String expectation = "열정적인 사람을 구합니다.";
        String openChatUrl = "kakao.com/o/gabojait";
        byte maxCnt = 0;
        Team team1 = createTeam(projectName, projectDescription, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);
        Team team2 = createTeam(projectName, projectDescription, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);

        // when
        boolean result = team1.equals(team2);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("다른 객체인 팀을 비교하면 동일하지 않다.")
    void givenUnequalInstance_whenEquals_thenReturn() {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);
        Object object = new Object();

        // when
        boolean result = team.equals(object);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 프로젝트명인 팀을 비교하면 동일하지 않다.")
    void givenUnequalProjectName_whenEquals_thenReturn() {
        // given
        String projectName1 = "가보자잇1";
        String projectName2 = "가보자잇2";

        String projectDescription = "가보자잇입니다.";
        String expectation = "열정적인 사람을 구합니다.";
        String openChatUrl = "kakao.com/o/gabojait";
        byte maxCnt = 0;
        Team team1 = createTeam(projectName1, projectDescription, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);
        Team team2 = createTeam(projectName2, projectDescription, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);

        // when
        boolean result = team1.equals(team2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 프로젝트 설명인 팀을 비교하면 동일하지 않다.")
    void givenUnequalProjectDescription_whenEquals_thenReturn() {
        // given
        String projectDescription1 = "가보자잇입니다.1";
        String projectDescription2 = "가보자잇입니다.2";

        String projectName = "가보자잇";
        String expectation = "열정적인 사람을 구합니다.";
        String openChatUrl = "kakao.com/o/gabojait";
        byte maxCnt = 0;
        Team team1 = createTeam(projectName, projectDescription1, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);
        Team team2 = createTeam(projectName, projectDescription2, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);

        // when
        boolean result = team1.equals(team2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 바라는 점인 팀을 비교하면 동일하지 않다.")
    void givenUnequalExpectation_whenEquals_thenReturn() {
        // given
        String expectation1 = "열정적인 사람을 구합니다.1";
        String expectation2 = "열정적인 사람을 구합니다.2";

        String projectName = "가보자잇";
        String projectDescription = "가보자잇입니다.";
        String openChatUrl = "kakao.com/o/gabojait";
        byte maxCnt = 0;
        Team team1 = createTeam(projectName, projectDescription, expectation1, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);
        Team team2 = createTeam(projectName, projectDescription, expectation2, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);

        // when
        boolean result = team1.equals(team2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 오픈 채팅 링크인 팀을 비교하면 동일하지 않다.")
    void givenUnequalOpenChatUrl_whenEquals_thenReturn() {
        // given
        String openChatUrl1 = "kakao.com/o/gabojait1";
        String openChatUrl2 = "kakao.com/o/gabojait2";

        String projectName = "가보자잇";
        String projectDescription = "가보자잇입니다.";
        String expectation = "열정적인 사람을 구합니다.";
        byte maxCnt = 0;
        Team team1 = createTeam(projectName, projectDescription, expectation, openChatUrl1, maxCnt, maxCnt, maxCnt,
                maxCnt);
        Team team2 = createTeam(projectName, projectDescription, expectation, openChatUrl2, maxCnt, maxCnt, maxCnt,
                maxCnt);

        // when
        boolean result = team1.equals(team2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 프로젝트 URL인 팀을 비교하면 동일하지 않다.")
    void givenUnequalProjectUrl_whenEquals_thenReturn() {
        // given
        String projectUrl1 = "github.com/gabojait1";
        String projectUrl2 = "github.com/gabojait2";

        String projectName = "가보자잇";
        String projectDescription = "가보자잇입니다.";
        String expectation = "열정적인 사람을 구합니다.";
        String openChatUrl = "kakao.com/o/gabojait";
        byte maxCnt = 1;
        LocalDateTime now = LocalDateTime.now();
        Team team1 = createTeam(projectName, projectDescription, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);
        Team team2 = createTeam(projectName, projectDescription, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);

        team1.complete(projectUrl1, now);
        team2.complete(projectUrl2, now);

        // when
        boolean result = team1.equals(team2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 프로젝트 완료일인 팀을 비교하면 동일하지 않다.")
    void givenUnequalCompletedAt_whenEquals_thenReturn() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime completedAt1 = now;
        LocalDateTime completedAt2 = now.minusNanos(1);

        String projectName = "가보자잇";
        String projectDescription = "가보자잇입니다.";
        String expectation = "열정적인 사람을 구합니다.";
        String openChatUrl = "kakao.com/o/gabojait";
        byte maxCnt = 1;
        String projectUrl = "github.com/gabojait";
        Team team1 = createTeam(projectName, projectDescription, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);
        Team team2 = createTeam(projectName, projectDescription, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);

        team1.complete(projectUrl, completedAt1);
        team2.complete(projectUrl, completedAt2);

        // when
        boolean result = team1.equals(team2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 현재 디자이너 수인 팀을 비교하면 동일하지 않다.")
    void givenUnequalDesignerCurrentCnt_whenEquals_thenReturn() {
        // given
        String projectName = "가보자잇";
        String projectDescription = "가보자잇입니다.";
        String expectation = "열정적인 사람을 구합니다.";
        String openChatUrl = "kakao.com/o/gabojait";
        byte maxCnt = 1;
        Team team1 = createTeam(projectName, projectDescription, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);
        Team team2 = createTeam(projectName, projectDescription, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);

        team1.join(Position.DESIGNER);

        // when
        boolean result = team1.equals(team2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 현재 백엔드 개발자 수인 팀을 비교하면 동일하지 않다.")
    void givenUnequalBackendCurrentCnt_whenEquals_thenReturn() {
        // given
        String projectName = "가보자잇";
        String projectDescription = "가보자잇입니다.";
        String expectation = "열정적인 사람을 구합니다.";
        String openChatUrl = "kakao.com/o/gabojait";
        byte maxCnt = 1;
        Team team1 = createTeam(projectName, projectDescription, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);
        Team team2 = createTeam(projectName, projectDescription, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);

        team1.join(Position.BACKEND);

        // when
        boolean result = team1.equals(team2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 현재 프런트엔드 개발자 수인 팀을 비교하면 동일하지 않다.")
    void givenUnequalFrontendCurrentCnt_whenEquals_thenReturn() {
        // given
        String projectName = "가보자잇";
        String projectDescription = "가보자잇입니다.";
        String expectation = "열정적인 사람을 구합니다.";
        String openChatUrl = "kakao.com/o/gabojait";
        byte maxCnt = 1;
        Team team1 = createTeam(projectName, projectDescription, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);
        Team team2 = createTeam(projectName, projectDescription, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);

        team1.join(Position.FRONTEND);

        // when
        boolean result = team1.equals(team2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 현재 프로젝트 매니저 수인 팀을 비교하면 동일하지 않다.")
    void givenUnequalManagerCurrentCnt_whenEquals_thenReturn() {
        // given
        String projectName = "가보자잇";
        String projectDescription = "가보자잇입니다.";
        String expectation = "열정적인 사람을 구합니다.";
        String openChatUrl = "kakao.com/o/gabojait";
        byte maxCnt = 1;
        Team team1 = createTeam(projectName, projectDescription, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);
        Team team2 = createTeam(projectName, projectDescription, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);

        team1.join(Position.MANAGER);

        // when
        boolean result = team1.equals(team2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 최대 디자이너 수인 팀을 비교하면 동일하지 않다.")
    void givenUnequalDesignerMaxCnt_whenEquals_thenReturn() {
        // given
        byte differentMaxCnt = 0;
        byte maxCnt = 1;

        String projectName = "가보자잇";
        String projectDescription = "가보자잇입니다.";
        String expectation = "열정적인 사람을 구합니다.";
        String openChatUrl = "kakao.com/o/gabojait";
        Team team1 = createTeam(projectName, projectDescription, expectation, openChatUrl, differentMaxCnt, maxCnt,
                maxCnt, maxCnt);
        Team team2 = createTeam(projectName, projectDescription, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);

        // when
        boolean result = team1.equals(team2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 최대 백엔드 개발자 수인 팀을 비교하면 동일하지 않다.")
    void givenUnequalBackendMaxCnt_whenEquals_thenReturn() {
        // given
        byte differentMaxCnt = 0;
        byte maxCnt = 1;

        String projectName = "가보자잇";
        String projectDescription = "가보자잇입니다.";
        String expectation = "열정적인 사람을 구합니다.";
        String openChatUrl = "kakao.com/o/gabojait";
        Team team1 = createTeam(projectName, projectDescription, expectation, openChatUrl, maxCnt, differentMaxCnt,
                maxCnt, maxCnt);
        Team team2 = createTeam(projectName, projectDescription, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);

        // when
        boolean result = team1.equals(team2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 최대 프런트엔드 개발자 수인 팀을 비교하면 동일하지 않다.")
    void givenUnequalFrontendMaxCnt_whenEquals_thenReturn() {
        // given
        byte differentMaxCnt = 0;
        byte maxCnt = 1;

        String projectName = "가보자잇";
        String projectDescription = "가보자잇입니다.";
        String expectation = "열정적인 사람을 구합니다.";
        String openChatUrl = "kakao.com/o/gabojait";
        Team team1 = createTeam(projectName, projectDescription, expectation, openChatUrl, maxCnt, maxCnt,
                differentMaxCnt, maxCnt);
        Team team2 = createTeam(projectName, projectDescription, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);

        // when
        boolean result = team1.equals(team2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 최대 프로젝트 매니저 수인 팀을 비교하면 동일하지 않다.")
    void givenUnequalManagerMaxCnt_whenEquals_thenReturn() {
        // given
        byte differentMaxCnt = 0;
        byte maxCnt = 1;

        String projectName = "가보자잇";
        String projectDescription = "가보자잇입니다.";
        String expectation = "열정적인 사람을 구합니다.";
        String openChatUrl = "kakao.com/o/gabojait";
        Team team1 = createTeam(projectName, projectDescription, expectation, openChatUrl, maxCnt, maxCnt,
                maxCnt, differentMaxCnt);
        Team team2 = createTeam(projectName, projectDescription, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);

        // when
        boolean result = team1.equals(team2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 방문자 수인 팀을 비교하면 동일하지 않다.")
    void givenUnequalVisitedCnt_thenEquals_thenReturn() {
        // given
        String projectName = "가보자잇";
        String projectDescription = "가보자잇입니다.";
        String expectation = "열정적인 사람을 구합니다.";
        String openChatUrl = "kakao.com/o/gabojait";
        byte maxCnt = 1;
        Team team1 = createTeam(projectName, projectDescription, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);
        Team team2 = createTeam(projectName, projectDescription, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);

        team1.visit();

        // when
        boolean result = team1.equals(team2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 팀원 찾기 여부인 팀을 비교하면 동일하지 않다.")
    void givenUnequalIsRecruiting_thenEquals_thenReturn() {
        // given
        String projectName = "가보자잇";
        String projectDescription = "가보자잇입니다.";
        String expectation = "열정적인 사람을 구합니다.";
        String openChatUrl = "kakao.com/o/gabojait";
        byte maxCnt = 1;
        Team team1 = createTeam(projectName, projectDescription, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);
        Team team2 = createTeam(projectName, projectDescription, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);

        team1.updateIsRecruiting(false);

        // when
        boolean result = team1.equals(team2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("동일한 팀의 해시코드는 같다.")
    void givenEqual_whenHashCode_thenReturn() {
        // given
        String projectName = "가보자잇";
        String projectDescription = "가보자잇입니다.";
        String expectation = "열정적인 사람을 구합니다.";
        String openChatUrl = "kakao.com/o/gabojait";
        byte maxCnt = 1;
        Team team1 = createTeam(projectName, projectDescription, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);
        Team team2 = createTeam(projectName, projectDescription, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);

        // when
        int hashCode1 = team1.hashCode();
        int hashCode2 = team2.hashCode();

        // then
        assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    @DisplayName("동일하지 않은 팀의 해시코드는 다르다.")
    void givenUnequal_whenHashCode_thenReturn() {
        // given
        String projectName1 = "가보자잇1";
        String projectName2 = "가보자잇2";

        String projectDescription = "가보자잇입니다.";
        String expectation = "열정적인 사람을 구합니다.";
        String openChatUrl = "kakao.com/o/gabojait";
        byte maxCnt = 1;
        Team team1 = createTeam(projectName1, projectDescription, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);
        Team team2 = createTeam(projectName2, projectDescription, expectation, openChatUrl, maxCnt, maxCnt, maxCnt,
                maxCnt);

        // when
        int hashCode1 = team1.hashCode();
        int hashCode2 = team2.hashCode();

        // then
        assertThat(hashCode1).isNotEqualTo(hashCode2);
    }

    private Team createTeam(String projectName,
                            String projectDescription,
                            String expectation,
                            String openChatUrl,
                            byte designerMaxCnt,
                            byte backendMaxCnt,
                            byte frontendMaxCnt,
                            byte managerMaxCnt) {
        return Team.builder()
                .projectName(projectName)
                .projectDescription(projectDescription)
                .expectation(expectation)
                .openChatUrl(openChatUrl)
                .designerMaxCnt(designerMaxCnt)
                .backendMaxCnt(backendMaxCnt)
                .frontendMaxCnt(frontendMaxCnt)
                .managerMaxCnt(managerMaxCnt)
                .build();
    }

    private User createDefaultUser(String email,
                                   String verificationCode,
                                   String username,
                                   String password,
                                   String nickname,
                                   Gender gender,
                                   LocalDate birthdate,
                                   LocalDateTime lastRequestAt) {
        Contact contact = Contact.builder()
                .email(email)
                .verificationCode(verificationCode)
                .build();
        contact.verified();

        return User.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .gender(gender)
                .birthdate(birthdate)
                .lastRequestAt(lastRequestAt)
                .contact(contact)
                .build();
    }

    private TeamMember createTeamMember(Position position, boolean isLeader, User user, Team team) {
        return TeamMember.builder()
                .position(position)
                .isLeader(isLeader)
                .team(team)
                .user(user)
                .build();
    }
}