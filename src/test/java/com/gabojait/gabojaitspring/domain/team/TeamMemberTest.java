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
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.TEAM_LEADER_UNAVAILABLE;
import static com.gabojait.gabojaitspring.common.code.ErrorCode.UNREGISTER_UNAVAILABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class TeamMemberTest {

    @Test
    @DisplayName("팀원 생성이 정상 작동한다")
    void givenValid_whenBuilder_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", (byte) 2, (byte) 2, (byte) 2, (byte) 2);

        Position position = Position.BACKEND;
        boolean isLeader = true;

        // when
        TeamMember teamMember = createTeamMember(position, isLeader, user, team);

        // then
        assertAll(
                () -> assertThat(teamMember)
                        .extracting("position", "teamMemberStatus", "isLeader", "isDeleted", "user", "team")
                        .containsExactly(position, TeamMemberStatus.PROGRESS, isLeader, false, user, team),
                () -> assertThat(teamMember.getUser().getIsSeekingTeam()).isFalse(),
                () -> assertThat(teamMember.getTeam())
                        .extracting("backendCurrentCnt", "isRecruiting")
                        .containsExactly((byte)1, true)
        );
    }

    @Test
    @DisplayName("팀장이 프로젝트 완료를 하면 정상 작동한다")
    void givenLeader_whenComplete_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", (byte) 2, (byte) 2, (byte) 2, (byte) 2);

        Position position = Position.BACKEND;
        boolean isLeader = true;
        TeamMember teamMember = createTeamMember(position, isLeader, user, team);

        String projectUrl = "github.com/gabojait";
        LocalDateTime completedAt = LocalDateTime.now();

        // when
        teamMember.complete(projectUrl, completedAt);

        // then
        assertAll(
                () -> assertThat(teamMember)
                        .extracting("position", "isLeader", "isDeleted")
                        .containsExactly(position, isLeader, false),
                () -> assertThat(teamMember.getTeam())
                        .extracting("projectUrl", "completedAt", "isRecruiting")
                        .containsExactly(projectUrl, completedAt, false),
                () -> assertThat(teamMember.getUser().getIsSeekingTeam()).isTrue()
        );
    }

    @Test
    @DisplayName("팀원이 프로젝트 완료를 하면 정상 작동한다")
    void givenMember_whenComplete_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", (byte) 2, (byte) 2, (byte) 2, (byte) 2);

        Position position = Position.BACKEND;
        boolean isLeader = false;
        TeamMember teamMember = createTeamMember(position, isLeader, user, team);

        String projectUrl = "github.com/gabojait";
        LocalDateTime completedAt = LocalDateTime.now();

        // when
        teamMember.complete(projectUrl, completedAt);

        // then
        assertAll(
                () -> assertThat(teamMember)
                        .extracting("position", "isLeader", "isDeleted")
                        .containsExactly(position, isLeader, false),
                () -> assertThat(teamMember.getTeam())
                        .extracting("projectUrl", "completedAt", "isRecruiting")
                        .containsExactly(null, null, true),
                () -> assertThat(teamMember.getUser().getIsSeekingTeam()).isTrue()
        );
    }

    @ParameterizedTest(name = "[{index}] 팀장 여부가 {0}가 프로젝트를 미완료한다")
    @ValueSource(booleans = {true, false})
    @DisplayName("프로젝트 미완료를 한다")
    void givenValid_whenIncomplete_thenReturn(boolean isLeader) {
        // given
        User user = createDefaultUser("tester@gabojait.com", LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", (byte) 2, (byte) 2, (byte) 2, (byte) 2);

        Position position = Position.BACKEND;
        TeamMember teamMember = createTeamMember(position, isLeader, user, team);

        // when
        teamMember.incomplete();

        // then
        assertAll(
                () -> assertThat(teamMember)
                        .extracting("position", "isLeader", "isDeleted")
                        .containsExactly(position, isLeader, true),
                () -> assertThat(teamMember.getUser().getIsSeekingTeam()).isTrue()
        );
    }

    @Test
    @DisplayName("팀원이 프로젝트에서 추방 당하면 정상 작동한다")
    void givenMember_whenFire_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", (byte) 2, (byte) 2, (byte) 2, (byte) 2);

        Position position = Position.BACKEND;
        boolean isLeader = false;
        TeamMember teamMember = createTeamMember(position, isLeader, user, team);

        // when
        teamMember.fire();

        // then
        assertAll(
                () -> assertThat(teamMember)
                        .extracting("position", "isLeader", "isDeleted")
                        .containsExactly(position, isLeader, true),
                () -> assertThat(teamMember.getUser().getIsSeekingTeam()).isTrue()
        );
    }

    @Test
    @DisplayName("팀장이 프로젝트에서 추방 당하면 예외가 발생한다")
    void givenLeader_whenFire_thenThrow() {
        // given
        User user = createDefaultUser("tester@gabojait.com", LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", (byte) 2, (byte) 2, (byte) 2, (byte) 2);

        Position position = Position.BACKEND;
        boolean isLeader = true;
        TeamMember teamMember = createTeamMember(position, isLeader, user, team);

        // when & then
        assertThatThrownBy(teamMember::fire)
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TEAM_LEADER_UNAVAILABLE);
    }

    @Test
    @DisplayName("팀원이 프로젝트를 탈퇴하면 정상 작동한다")
    void givenMember_whenQuit_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", (byte) 2, (byte) 2, (byte) 2, (byte) 2);

        Position position = Position.BACKEND;
        boolean isLeader = false;
        TeamMember teamMember = createTeamMember(position, isLeader, user, team);

        // when
        teamMember.quit();

        // then
        assertAll(
                () -> assertThat(teamMember)
                        .extracting("position", "isLeader", "isDeleted")
                        .containsExactly(position, isLeader, true),
                () -> assertThat(teamMember.getTeam())
                        .extracting("designerCurrentCnt", "backendCurrentCnt", "frontendCurrentCnt", "managerCurrentCnt")
                        .containsExactly((byte) 0, (byte) 0, (byte) 0, (byte) 0)
        );
    }

    @Test
    @DisplayName("팀장이 프로젝트를 탈퇴하면 예외가 발생한다")
    void givenLeader_whenQuit_thenThrow() {
        // given
        User user = createDefaultUser("tester@gabojait.com", LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", (byte) 2, (byte) 2, (byte) 2, (byte) 2);

        Position position = Position.BACKEND;
        boolean isLeader = true;
        TeamMember teamMember = createTeamMember(position, isLeader, user, team);

        // when & then
        assertThatThrownBy(teamMember::quit)
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TEAM_LEADER_UNAVAILABLE);
    }

    @Test
    @DisplayName("진행 상태인 팀원의 회원 연관관계를 끊으면 정상 작동한다")
    void givenProgressMember_whenDisconnectUser_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", (byte) 2, (byte) 2, (byte) 2, (byte) 2);

        boolean isLeader = false;
        Position position = Position.BACKEND;
        TeamMember teamMember = createTeamMember(position, isLeader, user, team);

        // when
        teamMember.disconnectUser();

        // then
        assertAll(
                () -> assertThat(teamMember)
                        .extracting("position", "teamMemberStatus", "isLeader", "isDeleted")
                        .containsExactly(position, TeamMemberStatus.QUIT, isLeader, true),
                () -> assertThat(teamMember.getTeam().getBackendCurrentCnt()).isEqualTo((byte) 0),
                () -> assertThat(teamMember.getUser()).isNull()
        );
    }

    @Test
    @DisplayName("진행 상태인 팀장의 회원 연관관계를 끊으면 예외가 발생한다")
    void givenProgressLeader_whenDisconnectUser_thenThrow() {
        // given
        User user = createDefaultUser("tester@gabojait.com", LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", (byte) 2, (byte) 2, (byte) 2, (byte) 2);

        boolean isLeader = true;
        Position position = Position.BACKEND;
        TeamMember teamMember = createTeamMember(position, isLeader, user, team);

        // when & then
        assertThatThrownBy(teamMember::disconnectUser)
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(UNREGISTER_UNAVAILABLE);
    }

    @ParameterizedTest(name = "[{index}] 완료 상태이며 팀장 여부가 {0}인 회원과의 연관관계를 끊는다")
    @ValueSource(booleans = {true, false})
    @DisplayName("완료 상태인 회원 연관관계를 끊으면 정상 작동한다")
    void givenComplete_whenDisconnectUser_thenReturn(boolean isLeader) {
        // given
        User user = createDefaultUser("tester@gabojait.com", LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", (byte) 2, (byte) 2, (byte) 2, (byte) 2);

        Position position = Position.BACKEND;
        String projectUrl = "github.com/gabojait";
        LocalDateTime completedAt = LocalDateTime.now();

        TeamMember teamMember = createTeamMember(position, isLeader, user, team);
        teamMember.complete(projectUrl, completedAt);

        // when
        teamMember.disconnectUser();

        // then
        assertAll(
                () -> assertThat(teamMember)
                        .extracting("position", "teamMemberStatus", "isLeader", "isDeleted")
                        .containsExactly(position, TeamMemberStatus.COMPLETE, isLeader, false),
                () -> assertThat(teamMember.getTeam().getBackendCurrentCnt()).isEqualTo((byte) 1),
                () -> assertThat(teamMember.getUser()).isNull()
        );
    }

    private static Stream<Arguments> providerEquals() {
        User user = createDefaultUser("tester@gabojait.com", LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", (byte) 10, (byte) 10, (byte) 10, (byte) 10);
        TeamMember teamMember = createTeamMember(Position.BACKEND, false, user, team);

        TeamMember teamMemberStatusTeamMember1 = createTeamMember(Position.BACKEND, false, user, team);
        TeamMember teamMemberStatusTeamMember2 = createTeamMember(Position.BACKEND, false, user, team);
        teamMemberStatusTeamMember2.quit();

        User user1 = createDefaultUser("tester1@gabojait.com", LocalDate.of(1997, 2, 11), LocalDateTime.now());
        TeamMember userTeamMember1 = createTeamMember(Position.BACKEND, false, user1, team);
        User user2 = createDefaultUser("tester2@gabojait.com", LocalDate.of(1997, 2, 11), LocalDateTime.now());
        TeamMember userTeamMember2 = createTeamMember(Position.BACKEND, false, user2, team);

        Team team1 = createTeam("가보자잇1", (byte) 2, (byte) 2, (byte) 2, (byte) 2);
        TeamMember teamTeamMember1 = createTeamMember(Position.BACKEND, false, user, team1);
        Team team2 = createTeam("가보자잇2", (byte) 2, (byte) 2, (byte) 2, (byte) 2);
        TeamMember teamTeamMember2 = createTeamMember(Position.BACKEND, false, user, team2);

        return Stream.of(
                Arguments.of(teamMember, teamMember, true),
                Arguments.of(teamMember, new Object(), false),
                Arguments.of(
                        createTeamMember(Position.BACKEND, false, user, team),
                        createTeamMember(Position.BACKEND, false, user, team),
                        true
                ),
                Arguments.of(
                        createTeamMember(Position.BACKEND, true, user, team),
                        createTeamMember(Position.BACKEND, false, user, team),
                        false
                ),
                Arguments.of(
                        createTeamMember(Position.BACKEND, false, user, team),
                        createTeamMember(Position.FRONTEND, false, user, team),
                        false
                ),
                Arguments.of(teamMemberStatusTeamMember1,teamMemberStatusTeamMember2, false),
                Arguments.of(userTeamMember1,userTeamMember2, false),
                Arguments.of(teamTeamMember1,teamTeamMember2, false)
        );
    }

    @ParameterizedTest(name = "[{index}] 팀원 객체를 비교한다")
    @MethodSource("providerEquals")
    @DisplayName("팀원 객체 비교가 정상 작동한다")
    void givenProvider_whenEquals_thenReturn(TeamMember teamMember, Object object, boolean result) {
        // when & then
        assertThat(teamMember.equals(object)).isEqualTo(result);
    }

    private static Stream<Arguments> providerHashCode() {
        User user = createDefaultUser("tester@gabojait.com", LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", (byte) 5, (byte) 5, (byte) 5, (byte) 5);

        return Stream.of(
                Arguments.of(
                        createTeamMember(Position.BACKEND, false, user, team),
                        createTeamMember(Position.BACKEND, false, user, team),
                        true
                ),
                Arguments.of(
                        createTeamMember(Position.BACKEND, false, user, team),
                        createTeamMember(Position.FRONTEND, false, user, team),
                        false
                )
        );
    }

    @ParameterizedTest(name = "[{index}] 팀원 해시코드를 비교한다")
    @MethodSource("providerHashCode")
    @DisplayName("팀원 해시코드 비교가 정상 작동한다")
    void givenProvider_whenHashCode_thenReturn(TeamMember teamMember1, TeamMember teamMember2, boolean result) {
        // when
        int hashCode1 = teamMember1.hashCode();
        int hashCode2 = teamMember2.hashCode();

        // then
        assertThat(hashCode1 == hashCode2).isEqualTo(result);
    }

    private static TeamMember createTeamMember(Position position, boolean isLeader, User user, Team team) {
        return TeamMember.builder()
                .position(position)
                .isLeader(isLeader)
                .team(team)
                .user(user)
                .build();
    }

    private static Team createTeam(String projectName,
                                   byte designerMaxCnt,
                                   byte backendMaxCnt,
                                   byte frontendMaxCnt,
                                   byte managerMaxCnt) {
        return Team.builder()
                .projectName(projectName)
                .projectDescription("가보자잇입니다")
                .expectation("열정적인 사람을 구합니다.")
                .openChatUrl("kakao.com/o/gabojait")
                .designerMaxCnt(designerMaxCnt)
                .backendMaxCnt(backendMaxCnt)
                .frontendMaxCnt(frontendMaxCnt)
                .managerMaxCnt(managerMaxCnt)
                .build();
    }

    private static User createDefaultUser(String email, LocalDate birthdate, LocalDateTime lastRequestAt) {
        Contact contact = Contact.builder()
                .email(email)
                .verificationCode("000000")
                .build();
        contact.verified();

        return User.builder()
                .username("tester")
                .password("password1!")
                .nickname("테스터")
                .gender(Gender.M)
                .birthdate(birthdate)
                .lastRequestAt(lastRequestAt)
                .contact(contact)
                .build();
    }
}