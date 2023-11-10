package com.gabojait.gabojaitspring.domain.offer;

import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class OfferTest {

    private static Stream<Arguments> providerBuilder() {
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터일", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", (byte) 3);

        return Stream.of(
                Arguments.of(OfferedBy.LEADER, Position.BACKEND, user, team),
                Arguments.of(OfferedBy.USER, Position.BACKEND, user, team)
        );
    }

    @ParameterizedTest(name = "[{index}] {0}가 제안을 생성한다.")
    @MethodSource("providerBuilder")
    @DisplayName("제안을 생성한다.")
    void givenProvider_whenBuilder_thenReturn(OfferedBy offeredBy, Position position, User user, Team team) {
        // when
        Offer offer = createOffer(offeredBy, position, user, team);

        // then
        assertThat(offer)
                .extracting("offeredBy", "position", "isAccepted", "isDeleted")
                .containsExactly(offeredBy, position, null, false);
    }

    private static Stream<Arguments> providerAccept() {
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터일", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", (byte) 3);

        return Stream.of(
                Arguments.of(OfferedBy.LEADER, Position.BACKEND, user, team),
                Arguments.of(OfferedBy.USER, Position.BACKEND, user, team)
        );
    }

    @ParameterizedTest(name = "[{index}] {0}가 제안을 수락한다.")
    @EnumSource(OfferedBy.class)
    @DisplayName("제안을 수락한다.")
    void givenEnum_whenAccept_thenReturn(OfferedBy offeredBy) {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터일", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", (byte) 3);
        Position position = Position.BACKEND;

        Offer offer = createOffer(offeredBy, position, user, team);

        // when
        offer.accept();

        // then
        assertThat(offer)
                .extracting("offeredBy", "position", "isAccepted", "isDeleted")
                .containsExactly(offeredBy, position, true, true);
    }

    @ParameterizedTest(name = "[{index}] {0}가 제안을 거절한다.")
    @EnumSource(OfferedBy.class)
    @DisplayName("제안을 거절한다.")
    void givenEnum_whenDecline_thenReturn(OfferedBy offeredBy) {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터일", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", (byte) 3);
        Position position = Position.BACKEND;

        Offer offer = createOffer(offeredBy, position, user, team);

        // when
        offer.decline();

        // then
        assertThat(offer)
                .extracting("offeredBy", "position", "isAccepted", "isDeleted")
                .containsExactly(offeredBy, position, false, true);
    }

    @ParameterizedTest(name = "[{index}] {0}가 제안을 취소한다.")
    @EnumSource(OfferedBy.class)
    @DisplayName("제안을 취소한다.")
    void givenEnum_whenCancel_thenReturn(OfferedBy offeredBy) {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터일", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", (byte) 3);
        Position position = Position.BACKEND;

        Offer offer = createOffer(offeredBy, position, user, team);

        // when
        offer.cancel();

        // then
        assertThat(offer)
                .extracting("offeredBy", "position", "isAccepted", "isDeleted")
                .containsExactly(offeredBy, position, null, true);
    }

    private static Stream<Arguments> providerEquals() {
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", (byte) 3);
        Offer offer = createOffer(OfferedBy.USER, Position.BACKEND, user, team);

        User user1 = createDefaultUser("tester@gabojait.com", "000000", "tester1", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Offer userOffer1 = createOffer(OfferedBy.USER, Position.BACKEND, user1, team);
        User user2 = createDefaultUser("tester@gabojait.com", "000000", "tester2", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Offer userOffer2 = createOffer(OfferedBy.USER, Position.BACKEND, user2, team);

        Team team1 = createTeam("가보자잇1", (byte) 3);
        Offer teamOffer1 = createOffer(OfferedBy.USER, Position.BACKEND, user, team1);
        Team team2 = createTeam("가보자잇2", (byte) 3);
        Offer teamOffer2 = createOffer(OfferedBy.USER, Position.BACKEND, user, team2);

        Offer isAccepted1 = createOffer(OfferedBy.USER, Position.BACKEND, user, team);
        Offer isAccepted2 = createOffer(OfferedBy.USER, Position.BACKEND, user, team);
        isAccepted2.accept();

        return Stream.of(
                Arguments.of(offer, offer, true),
                Arguments.of(offer, new Object(), false),
                Arguments.of(
                        createOffer(OfferedBy.USER, Position.BACKEND, user, team),
                        createOffer(OfferedBy.USER, Position.BACKEND, user, team),
                        true
                ),
                Arguments.of(userOffer1, userOffer2, false),
                Arguments.of(teamOffer1, teamOffer2, false),
                Arguments.of(isAccepted1, isAccepted2, false),
                Arguments.of(
                        createOffer(OfferedBy.USER, Position.BACKEND, user, team),
                        createOffer(OfferedBy.LEADER, Position.BACKEND, user, team),
                        false
                ),
                Arguments.of(
                        createOffer(OfferedBy.USER, Position.BACKEND, user, team),
                        createOffer(OfferedBy.USER, Position.FRONTEND, user, team),
                        false
                )
        );
    }

    @ParameterizedTest(name = "[{index}] 제안 객체를 비교한다.")
    @MethodSource("providerEquals")
    @DisplayName("제안 객체를 비교한다.")
    void givenProvider_whenEquals_thenReturn(Offer offer, Object object, boolean result) {
        // when & then
        assertThat(offer.equals(object)).isEqualTo(result);
    }

    private static Stream<Arguments> providerHashCode() {
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", (byte) 3);

        return Stream.of(
                Arguments.of(
                        createOffer(OfferedBy.USER, Position.BACKEND, user, team),
                        createOffer(OfferedBy.USER, Position.BACKEND, user, team),
                        true
                ),
                Arguments.of(
                        createOffer(OfferedBy.USER, Position.BACKEND, user, team),
                        createOffer(OfferedBy.LEADER, Position.BACKEND, user, team),
                        false
                )
        );
    }

    @ParameterizedTest(name = "[{index}] 제안 해시코드를 비교한다.")
    @MethodSource("providerHashCode")
    @DisplayName("제안 해시코드를 비교한다.")
    void givenProvider_whenHashCode_thenReturn(Offer offer1, Offer offer2, boolean result) {
        // when
        int hashCode1 = offer1.hashCode();
        int hashCode2 = offer2.hashCode();

        // then
        assertThat(hashCode1 == hashCode2).isEqualTo(result);
    }

    private static Offer createOffer(OfferedBy offeredBy, Position position, User user, Team team) {
        return Offer.builder()
                .offeredBy(offeredBy)
                .position(position)
                .user(user)
                .team(team)
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

    private static Team createTeam(String projectName, byte maxCnt) {
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
}