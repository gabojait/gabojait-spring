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
    @DisplayName("팀을 들어간다.")
    void join() {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);

        // when
        team.join(Position.BACKEND);

        // then
        assertEquals((byte) 1, team.getBackendCurrentCnt());
    }

    @Test
    @DisplayName("팀을 나간다.")
    void leave() {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);
        team.join(Position.BACKEND);
        team.join(Position.BACKEND);

        // when
        team.leave(Position.BACKEND);

        // then
        assertEquals((byte) 1, team.getBackendCurrentCnt());
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
        assertFalse(team.getIsRecruiting());
    }

    @Test
    @DisplayName("포지션의 마감 여부를 확인한다.")
    void isPositionFull() {
        // given
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 0, (byte) 0,
                (byte) 0, (byte) 0);

        // when
        boolean result = team.isPositionFull(Position.BACKEND);

        // then
        assertTrue(result);
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