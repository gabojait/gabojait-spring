package com.gabojait.gabojaitspring.domain.favorite;

import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class FavoriteTest {

    @Test
    @DisplayName("회원 찜 생성이 정상 작동한다")
    void givenFavoriteUser_whenBuilder_thenReturn() {
        // given
        User user1 = createDefaultUser("tester1@gabojait.com", "000000", "tester1", "password1!", "테스터일", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        User user2 = createDefaultUser("tester2@gabojait.com", "000000", "tester2", "password1!", "테스터이", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        // when
        Favorite favorite = createFavorite(user1, null, user2);

        // then
        assertThat(favorite)
                .extracting("user", "favoriteTeam", "favoriteUser")
                .containsExactly(user1, null, user2);
    }

    @Test
    @DisplayName("팀 찜 생성이 정상 작동한다")
    void givenFavoriteTeam_whenBuilder_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", (byte) 2);

        // when
        Favorite favorite = createFavorite(user, team, null);

        // then
        assertThat(favorite)
                .extracting("user", "favoriteTeam", "favoriteUser")
                .containsExactly(user, team, null);
    }

    private static Stream<Arguments> providerEquals() {
        User user = createDefaultUser("tester1@gabojait.com", "000000", "tester1", "password1!", "테스터일", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        User favoriteUser = createDefaultUser("tester2@gabojait.com", "000000", "tester2", "password1!", "테스터이", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team favoriteTeam = createTeam("가보자잇", (byte) 2);
        Favorite favorite = createFavorite(user, favoriteTeam, null);

        User user1 = createDefaultUser("tester1@gabojait.com", "000000", "tester1", "password1!", "테스터일",
                Gender.M, LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Favorite user1Favorite = createFavorite(user1, null, null);
        User user2 = createDefaultUser("tester2@gabojait.com", "000000", "tester2", "password1!", "테스터이",
                Gender.M, LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Favorite user2Favorite = createFavorite(user2, null, null);

        Team team1 = createTeam("가보자잇1", (byte) 2);
        Favorite favoriteTeamFavorite1 = createFavorite(user, team1, null);
        Team team2 = createTeam("가보자잇2", (byte) 2);
        Favorite favoriteTeamFavorite2 = createFavorite(user, team2, null);

        User favoriteUser1 = createDefaultUser("tester1@gabojait.com", "000000", "tester1", "password1!", "테스터일",
                Gender.M, LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Favorite favoriteUserFavorite1 = createFavorite(user, null, favoriteUser1);
        User favoriteUser2 = createDefaultUser("tester2@gabojait.com", "000000", "tester2", "password1!", "테스터이",
                Gender.M, LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Favorite favoriteUserFavorite2 = createFavorite(user, null, favoriteUser2);

        return Stream.of(
                Arguments.of(favorite, favorite, true),
                Arguments.of(favorite, new Object(), false),
                Arguments.of(createFavorite(user, null, favoriteUser), createFavorite(user, null, favoriteUser), true),
                Arguments.of(user1Favorite, user2Favorite, false),
                Arguments.of(favoriteTeamFavorite1, favoriteTeamFavorite2, false),
                Arguments.of(favoriteUserFavorite1, favoriteUserFavorite2, false)
        );
    }

    @ParameterizedTest(name = "[{index}] 찜 객체를 비교한다")
    @MethodSource("providerEquals")
    @DisplayName("찜 객체를 비교한다")
    void givenProvider_whenEquals_thenReturn(Favorite favorite, Object object, boolean result) {
        // when & then
        assertThat(favorite.equals(object)).isEqualTo(result);
    }

    private static Stream<Arguments> providerHashCode() {
        User user = createDefaultUser("tester1@gabojait.com", "000000", "tester1", "password1!", "테스터일", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team favoriteTeam = createTeam("가보자잇", (byte) 2);
        User favoriteUser = createDefaultUser("tester2@gabojait.com", "000000", "tester2", "password1!", "테스터이",
                Gender.M, LocalDate.of(1997, 2, 11), LocalDateTime.now());

        return Stream.of(
                Arguments.of(
                        createFavorite(user, favoriteTeam, null),
                        createFavorite(user, favoriteTeam, null),
                        true
                ),
                Arguments.of(
                        createFavorite(user, favoriteTeam, null),
                        createFavorite(user, null, favoriteUser),
                        false
                )
        );
    }

    @ParameterizedTest(name = "[{index}] 찜 해시코드를 비교한다")
    @MethodSource("providerHashCode")
    @DisplayName("찜 해시코드를 비교한다")
    void givenProvider_whenHashCode_thenReturn(Favorite favorite1, Favorite favorite2, boolean result) {
        // when
        int hashCode1 = favorite1.hashCode();
        int hashCode2 = favorite2.hashCode();

        // then
        assertThat(hashCode1 == hashCode2).isEqualTo(result);
    }

    private static Favorite createFavorite(User user, Team favoriteTeam, User favoriteUser) {
        return Favorite.builder()
                .user(user)
                .favoriteTeam(favoriteTeam)
                .favoriteUser(favoriteUser)
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