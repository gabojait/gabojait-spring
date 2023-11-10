package com.gabojait.gabojaitspring.domain.team;

import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.domain.user.User;
import com.gabojait.gabojaitspring.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.stream.Stream;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    private static Stream<Arguments> providerJoin() {
        return Stream.of(
                Arguments.of(Position.DESIGNER, (byte) 1, (byte) 0, (byte) 0, (byte) 0),
                Arguments.of(Position.BACKEND, (byte) 0, (byte) 1, (byte) 0, (byte) 0),
                Arguments.of(Position.FRONTEND, (byte) 0, (byte) 0, (byte) 1, (byte) 0),
                Arguments.of(Position.MANAGER, (byte) 0, (byte) 0, (byte) 0, (byte) 1)
        );
    }

    @ParameterizedTest(name = "[{index}] {0}가 팀을 들어간다.")
    @MethodSource("providerJoin")
    @DisplayName("특정 포지션이 팀에 들어간다.")
    void givenProvider_whenJoin_thenReturn(Position position,
                                           byte designerCurrentCnt,
                                           byte backendCurrentCnt,
                                           byte frontendCurrentCnt,
                                           byte managerCurrentCnt) {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);

        // when
        team.join(position);

        // then
        assertThat(team)
                .extracting("designerCurrentCnt", "backendCurrentCnt", "frontendCurrentCnt", "managerCurrentCnt")
                .containsExactly(designerCurrentCnt, backendCurrentCnt, frontendCurrentCnt, managerCurrentCnt);
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

    private static Stream<Arguments> providerLeave() {
        return Stream.of(
                Arguments.of(Position.DESIGNER, (byte) 1, (byte) 0, (byte) 0, (byte) 0),
                Arguments.of(Position.BACKEND, (byte) 0, (byte) 1, (byte) 0, (byte) 0),
                Arguments.of(Position.FRONTEND, (byte) 0, (byte) 0, (byte) 1, (byte) 0),
                Arguments.of(Position.MANAGER, (byte) 0, (byte) 0, (byte) 0, (byte) 1)
        );
    }

    @ParameterizedTest(name = "[{index}] {0}가 팀에서 나간다.")
    @MethodSource("providerLeave")
    @DisplayName("특정 포지션이 팀에서 나간다.")
    void givenProvider_whenLeave_thenReturn(Position position,
                                            byte designerCurrentCnt,
                                            byte backendCurrentCnt,
                                            byte frontendCurrentCnt,
                                            byte managerCurrentCnt) {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);
        team.join(position);
        team.join(position);

        // when
        team.leave(position);

        // then
        assertThat(team)
                .extracting("designerCurrentCnt", "backendCurrentCnt", "frontendCurrentCnt", "managerCurrentCnt",
                        "isRecruiting")
                .containsExactly(designerCurrentCnt, backendCurrentCnt, frontendCurrentCnt, managerCurrentCnt, true);
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

    private static Stream<Arguments> providerIsPositionFull() {
        return Stream.of(
                Arguments.of(Position.DESIGNER),
                Arguments.of(Position.BACKEND),
                Arguments.of(Position.FRONTEND),
                Arguments.of(Position.MANAGER)
        );
    }

    @ParameterizedTest(name = "[{index}] 마감되지 않은 {0} 포지션의 마감 여부를 확인한다.")
    @MethodSource("providerIsPositionFull")
    @DisplayName("마감되지 않은 포지션의 마감 여부를 확인한다.")
    void givenProviderNotFull_whenIsPositionFull_thenReturn(Position position) {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 1, (byte) 1,
                (byte) 1, (byte) 1);

        // when
        boolean result = team.isPositionFull(position);

        // then
        assertThat(result).isFalse();
    }

    @ParameterizedTest(name = "[{index}] 마감된 {0} 포지션의 마감 여부를 확인한다.")
    @MethodSource("providerIsPositionFull")
    @DisplayName("마감되지 않은 포지션의 마감 여부를 확인한다.")
    void givenProviderFull_whenIsPositionFull_thenReturn(Position position) {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 1, (byte) 1,
                (byte) 1, (byte) 1);
        team.join(position);

        // when
        boolean result = team.isPositionFull(position);

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
        assertThat(team.getVisitedCnt()).isEqualTo(visitedCnt);
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

    private static Stream<Arguments> providerEquals() {
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);

        Team projectNameTeam1 = createTeam("가보자잇1", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait",
                (byte) 2, (byte) 2, (byte) 2, (byte) 2);
        Team projectNameTeam2 = createTeam("가보자잇2", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait",
                (byte) 2, (byte) 2, (byte) 2, (byte) 2);

        Team projectDescriptionTeam1 = createTeam("가보자잇", "가보자잇입니다1", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait",
                (byte) 2, (byte) 2, (byte) 2, (byte) 2);
        Team projectDescriptionTeam2 = createTeam("가보자잇", "가보자잇입니다2", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait",
                (byte) 2, (byte) 2, (byte) 2, (byte) 2);

        Team expectationTeam1 = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.1", "kakao.com/o/gabojait",
                (byte) 2, (byte) 2, (byte) 2, (byte) 2);
        Team expectationTeam2 = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.2", "kakao.com/o/gabojait",
                (byte) 2, (byte) 2, (byte) 2, (byte) 2);

        Team openChatTeam1 = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait1",
                (byte) 2, (byte) 2, (byte) 2, (byte) 2);
        Team openChatTeam2 = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait2",
                (byte) 2, (byte) 2, (byte) 2, (byte) 2);

        LocalDateTime completedAt = LocalDateTime.now();
        Team projectUrlTeam1 = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2,
                (byte) 2, (byte) 2, (byte) 2);
        projectUrlTeam1.complete("github.com/gabojait1", completedAt);
        Team projectUrlTeam2 = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2,
                (byte) 2, (byte) 2, (byte) 2);
        projectUrlTeam2.complete("github.com/gabojait2", completedAt);

        Team completedAtTeam1 = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2,
                (byte) 2, (byte) 2, (byte) 2);
        completedAtTeam1.complete("github.com/gabojait", completedAt);
        Team completedAtTeam2 = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2,
                (byte) 2, (byte) 2, (byte) 2);
        completedAtTeam2.complete("github.com/gabojait", completedAt.plusSeconds(1));

        Team designerCurrentCntTeam1 = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait",
                (byte) 2, (byte) 2, (byte) 2, (byte) 2);
        Team designerCurrentCntTeam2 = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait",
                (byte) 2, (byte) 2, (byte) 2, (byte) 2);
        designerCurrentCntTeam2.join(Position.DESIGNER);

        Team backendCurrentCntTeam1 = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait",
                (byte) 2, (byte) 2, (byte) 2, (byte) 2);
        Team backendCurrentCntTeam2 = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait",
                (byte) 2, (byte) 2, (byte) 2, (byte) 2);
        backendCurrentCntTeam2.join(Position.BACKEND);

        Team frontendCurrentCntTeam1 = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait",
                (byte) 2, (byte) 2, (byte) 2, (byte) 2);
        Team frontendCurrentCntTeam2 = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait",
                (byte) 2, (byte) 2, (byte) 2, (byte) 2);
        frontendCurrentCntTeam2.join(Position.FRONTEND);

        Team managerCurrentCntTeam1 = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait",
                (byte) 2, (byte) 2, (byte) 2, (byte) 2);
        Team managerCurrentCntTeam2 = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait",
                (byte) 2, (byte) 2, (byte) 2, (byte) 2);
        managerCurrentCntTeam2.join(Position.MANAGER);

        Team visitCntTeam1 = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait",
                (byte) 2, (byte) 2, (byte) 2, (byte) 2);
        Team visitCntTeam2 = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait",
                (byte) 2, (byte) 2, (byte) 2, (byte) 2);
        visitCntTeam2.visit();

        Team isRecruitingTeam1 = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait",
                (byte) 2, (byte) 2, (byte) 2, (byte) 2);
        Team isRecruitingTeam2 = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait",
                (byte) 2, (byte) 2, (byte) 2, (byte) 2);
        isRecruitingTeam2.updateIsRecruiting(false);

        return Stream.of(
                Arguments.of(team, team, true),
                Arguments.of(team, new Object(), false),
                Arguments.of(
                        createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2,
                                (byte) 2, (byte) 2, (byte) 2),
                        createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2,
                                (byte) 2, (byte) 2, (byte) 2),
                        true
                ),
                Arguments.of(projectNameTeam1, projectNameTeam2, false),
                Arguments.of(projectDescriptionTeam1, projectDescriptionTeam2, false),
                Arguments.of(expectationTeam1, expectationTeam2, false),
                Arguments.of(openChatTeam1, openChatTeam2, false),
                Arguments.of(projectUrlTeam1, projectUrlTeam2, false),
                Arguments.of(completedAtTeam1, completedAtTeam2, false),
                Arguments.of(designerCurrentCntTeam1, designerCurrentCntTeam2, false),
                Arguments.of(backendCurrentCntTeam1, backendCurrentCntTeam2, false),
                Arguments.of(frontendCurrentCntTeam1, frontendCurrentCntTeam2, false),
                Arguments.of(managerCurrentCntTeam1, managerCurrentCntTeam2, false),
                Arguments.of(visitCntTeam1, visitCntTeam2, false),
                Arguments.of(isRecruitingTeam1, isRecruitingTeam2, false),
                Arguments.of(
                        createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2,
                                (byte) 2, (byte) 2, (byte) 2),
                        createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 1,
                                (byte) 2, (byte) 2, (byte) 2),
                        false
                ),
                Arguments.of(
                        createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2,
                                (byte) 2, (byte) 2, (byte) 2),
                        createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2,
                                (byte) 1, (byte) 2, (byte) 2),
                        false
                ),
                Arguments.of(
                        createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2,
                                (byte) 2, (byte) 2, (byte) 2),
                        createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2,
                                (byte) 2, (byte) 1, (byte) 2),
                        false
                ),
                Arguments.of(
                        createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2,
                                (byte) 2, (byte) 2, (byte) 2),
                        createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2,
                                (byte) 2, (byte) 2, (byte) 1),
                        false
                )
        );
    }

    @ParameterizedTest(name = "[{index}] 팀 객체를 비교한다.")
    @MethodSource("providerEquals")
    @DisplayName("팀 객체를 비교한다.")
    void givenProvider_whenEquals_thenReturn(Team team, Object object, boolean result) {
        // when & then
        assertThat(team.equals(object)).isEqualTo(result);
    }

    private static Stream<Arguments> providerHashCode() {
        return Stream.of(
                Arguments.of(
                        createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2,
                                (byte) 2, (byte) 2, (byte) 2),
                        createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2,
                                (byte) 2, (byte) 2, (byte) 2),
                        true
                ),
                Arguments.of(
                        createTeam("가보자잇1", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2,
                                (byte) 2, (byte) 2, (byte) 2),
                        createTeam("가보자잇2", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2,
                                (byte) 2, (byte) 2, (byte) 2),
                        false
                )
        );
    }

    @ParameterizedTest(name = "[{index}] 팀 해시코드를 비교한다.")
    @MethodSource("providerHashCode")
    @DisplayName("팀 해시코드를 비교한다.")
    void givenProvider_whenHashCode_thenReturn(Team team1, Team team2, boolean result) {
        // when
        int hashCode1 = team1.hashCode();
        int hashCode2 = team2.hashCode();

        // then
        assertThat(hashCode1 == hashCode2).isEqualTo(result);
    }

    private static Team createTeam(String projectName,
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

    private static User createDefaultUser(String email,
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

    private static TeamMember createTeamMember(Position position, boolean isLeader, User user, Team team) {
        return TeamMember.builder()
                .position(position)
                .isLeader(isLeader)
                .team(team)
                .user(user)
                .build();
    }
}