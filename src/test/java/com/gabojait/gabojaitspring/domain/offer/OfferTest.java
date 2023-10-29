package com.gabojait.gabojaitspring.domain.offer;

import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class OfferTest {

    @Test
    @DisplayName("팀장의 제안을 생성한다.")
    void givenOfferedByLeader_whenBuilder_thenReturn() {
        // given
        User user1 = createDefaultUser("tester1@gabojait.com", "000000", "tester1", "password1!", "테스터일", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        User user2 = createDefaultUser("tester2@gabojait.com", "000000", "tester2", "password1!", "테스터이", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        Team team = createTeam("가보자잇", (byte) 3);
        TeamMember teamMember = createTeamMember(Position.BACKEND, true, user1, team);
        OfferedBy offeredBy = OfferedBy.LEADER;
        Position position = Position.MANAGER;

        // when
        Offer offer = createOffer(offeredBy, position, user2, team);

        // then
        assertThat(offer)
                .extracting("offeredBy", "position", "isAccepted", "isDeleted")
                .containsExactly(offeredBy, position, null, false);
    }

    @Test
    @DisplayName("회원의 제안을 생성한다.")
    void givenOfferedByUser_whenBuilder_thenReturn() {
        // given
        User user1 = createDefaultUser("tester1@gabojait.com", "000000", "tester1", "password1!", "테스터일", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        User user2 = createDefaultUser("tester2@gabojait.com", "000000", "tester2", "password1!", "테스터이", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        Team team = createTeam("가보자잇", (byte) 3);
        TeamMember teamMember = createTeamMember(Position.BACKEND, true, user1, team);
        OfferedBy offeredBy = OfferedBy.USER;
        Position position = Position.FRONTEND;

        // when
        Offer offer = createOffer(offeredBy, position, user2, team);

        // then
        assertThat(offer)
                .extracting("offeredBy", "position", "isAccepted", "isDeleted")
                .containsExactly(offeredBy, position, null, false);
    }

    @Test
    @DisplayName("제안을 수락한다.")
    void accept() {
        // given
        User user1 = createDefaultUser("tester1@gabojait.com", "000000", "tester1", "password1!", "테스터일", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        User user2 = createDefaultUser("tester2@gabojait.com", "000000", "tester2", "password1!", "테스터이", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", (byte) 2);
        TeamMember teamMember = createTeamMember(Position.BACKEND, true, user1, team);

        Offer offer = createOffer(OfferedBy.LEADER, Position.DESIGNER, user2, team);

        // when
        offer.accept();

        // then
        assertThat(offer)
                .extracting("isAccepted", "isDeleted")
                .containsExactly(true, true);
    }

    @Test
    @DisplayName("제안을 거절한다.")
    void decline() {
        // given
        User user1 = createDefaultUser("tester1@gabojait.com", "000000", "tester1", "password1!", "테스터일", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        User user2 = createDefaultUser("tester2@gabojait.com", "000000", "tester2", "password1!", "테스터이", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", (byte) 2);
        TeamMember teamMember = createTeamMember(Position.BACKEND, true, user1, team);

        Offer offer = createOffer(OfferedBy.LEADER, Position.DESIGNER, user2, team);

        // when
        offer.decline();

        // then
        assertThat(offer)
                .extracting("isAccepted", "isDeleted")
                .containsExactly(false, true);
    }

    @Test
    @DisplayName("제안을 취소한다.")
    void cancel() {
        // given
        User user1 = createDefaultUser("tester1@gabojait.com", "000000", "tester1", "password1!", "테스터일", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        User user2 = createDefaultUser("tester2@gabojait.com", "000000", "tester2", "password1!", "테스터이", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", (byte) 2);
        TeamMember teamMember = createTeamMember(Position.BACKEND, true, user1, team);

        Offer offer = createOffer(OfferedBy.USER, Position.DESIGNER, user2, team);

        // when
        offer.cancel();

        // then
        assertThat(offer)
                .extracting("isAccepted", "isDeleted")
                .containsExactly(null, true);
    }

    private Offer createOffer(OfferedBy offeredBy, Position position, User user, Team team) {
        return Offer.builder()
                .offeredBy(offeredBy)
                .position(position)
                .user(user)
                .team(team)
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

    private Team createTeam(String projectName,
                            byte maxCnt) {
        return Team.builder()
                .projectName(projectName)
                .projectDescription("프로젝트 설명입니다.")
                .expectation("열정적인 팀원을 구합니다.")
                .openChatUrl("kakao.com/o/gabojait")
                .designerMaxCnt(maxCnt)
                .backendMaxCnt(maxCnt)
                .frontendMaxCnt(maxCnt)
                .managerMaxCnt(maxCnt)
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