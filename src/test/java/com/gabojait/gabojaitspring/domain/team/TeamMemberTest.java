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

import static com.gabojait.gabojaitspring.common.code.ErrorCode.TEAM_LEADER_UNAVAILABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class TeamMemberTest {

    @Test
    @DisplayName("팀원을 생성한다.")
    void builder() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);

        Position position = Position.BACKEND;
        boolean isLeader = true;

        // when
        TeamMember teamMember = createTeamMember(position, isLeader, user, team);

        // then
        assertThat(teamMember)
                .extracting("position", "teamMemberStatus", "isLeader", "isDeleted")
                .containsExactly(position, TeamMemberStatus.PROGRESS, isLeader, false);

        assertEquals((byte) 1, team.getBackendCurrentCnt());
        assertFalse(user.getIsSeekingTeam());
    }

    @Test
    @DisplayName("프로젝트가 완료로 종료된다.")
    void givenComplete_whenUpdateTeamMemberStatus_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);

        TeamMember teamMember = createTeamMember(Position.BACKEND, true, user, team);

        // when
        teamMember.updateTeamMemberStatus(TeamMemberStatus.COMPLETE);

        // then
        assertThat(teamMember)
                .extracting("teamMemberStatus", "isDeleted")
                .containsExactly(TeamMemberStatus.COMPLETE, false);

        assertTrue(user.getIsSeekingTeam());
    }

    @Test
    @DisplayName("프로젝트가 미완료로 종료된다.")
    void givenIncomplete_whenUpdateTeamMemberStatus_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);

        TeamMember teamMember = createTeamMember(Position.BACKEND, true, user, team);

        // when
        teamMember.updateTeamMemberStatus(TeamMemberStatus.INCOMPLETE);

        // then
        assertThat(teamMember)
                .extracting("teamMemberStatus", "isDeleted")
                .containsExactly(TeamMemberStatus.INCOMPLETE, true);

        assertTrue(user.getIsSeekingTeam());
    }

    @Test
    @DisplayName("팀원이 추방된다.")
    void givenFired_whenUpdateTeamMemberStatus_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);

        TeamMember teamMember = createTeamMember(Position.BACKEND, false, user, team);

        // when
        teamMember.updateTeamMemberStatus(TeamMemberStatus.FIRED);

        // then
        assertThat(teamMember)
                .extracting("teamMemberStatus", "isDeleted")
                .containsExactly(TeamMemberStatus.FIRED, true);

        assertTrue(user.getIsSeekingTeam());
        assertEquals((byte) 0, team.getBackendCurrentCnt());
    }

    @Test
    @DisplayName("팀원이 포기한다.")
    void givenQuit_whenUpdateTeamMemberStatus_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);

        TeamMember teamMember = createTeamMember(Position.BACKEND, false, user, team);

        // when
        teamMember.updateTeamMemberStatus(TeamMemberStatus.QUIT);

        // then
        assertThat(teamMember)
                .extracting("teamMemberStatus", "isDeleted")
                .containsExactly(TeamMemberStatus.QUIT, true);

        assertTrue(user.getIsSeekingTeam());
        assertEquals((byte) 0, team.getBackendCurrentCnt());
    }

    @Test
    @DisplayName("팀장이 포기를 하면 예외가 발생한다.")
    void givenLeaderQuit_whenUpdateTeamMemberStatus_thenThrow() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);

        TeamMember teamMember = createTeamMember(Position.BACKEND, true, user, team);

        // when & then
        assertThatThrownBy(() -> teamMember.updateTeamMemberStatus(TeamMemberStatus.QUIT))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TEAM_LEADER_UNAVAILABLE);
    }

    @Test
    @DisplayName("팀장이 추방되면 예외가 발생한다.")
    void givenLeaderFired_whenUpdateTeamMemberStatus_thenThrow() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);

        TeamMember teamMember = createTeamMember(Position.BACKEND, true, user, team);

        // when & then
        assertThatThrownBy(() -> teamMember.updateTeamMemberStatus(TeamMemberStatus.FIRED))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TEAM_LEADER_UNAVAILABLE);
    }

    @Test
    @DisplayName("진행중인 팀원의 회원과의 연관관계를 끊는다.")
    void givenProgress_whenDisconnectUser_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);

        TeamMember teamMember = createTeamMember(Position.BACKEND, true, user, team);

        // when
        teamMember.disconnectUser();

        // then
        assertThat(teamMember)
                .extracting("user", "isDeleted")
                .containsExactly(null, true);

        assertEquals((byte) 0, team.getBackendCurrentCnt());
    }

    @Test
    @DisplayName("진행중이지 않은 팀원의 회원과의 연관관계를 끊는다.")
    void givenNotProgress_whenDisconnectUser_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);
        TeamMember teamMember = createTeamMember(Position.BACKEND, true, user, team);

        teamMember.updateTeamMemberStatus(TeamMemberStatus.COMPLETE);


        // when
        teamMember.disconnectUser();

        // then
        assertThat(teamMember)
                .extracting("user", "isDeleted")
                .containsExactly(null, false);

        assertEquals((byte) 1, team.getBackendCurrentCnt());
    }

    @Test
    @DisplayName("같은 객체인 팀원을 비교하면 동일하다.")
    void givenEqualInstance_whenEquals_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", "가보자잇입니다", "열정적인 사람을 구합니다.", "kakao.com/o/gabojait", (byte) 2, (byte) 2,
                (byte) 2, (byte) 2);
        TeamMember teamMember = createTeamMember(Position.BACKEND, true, user, team);

        // when
        boolean result = teamMember.equals(teamMember);

        // then
        assertThat(result).isTrue();
    }

    private TeamMember createTeamMember(Position position, boolean isLeader, User user, Team team) {
        return TeamMember.builder()
                .position(position)
                .isLeader(isLeader)
                .team(team)
                .user(user)
                .build();
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
}