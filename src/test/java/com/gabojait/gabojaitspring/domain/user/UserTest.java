package com.gabojait.gabojaitspring.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    @DisplayName("회원을 생성한다.")
    void builder() {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime lastRequestAt = LocalDateTime.now();

        // when
        User user = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate,
                lastRequestAt);

        // then
        assertThat(user)
                .extracting("username", "password", "nickname", "profileDescription", "imageUrl", "birthdate",
                        "lastRequestAt", "gender", "position", "rating", "visitedCnt", "reviewCnt", "isSeekingTeam",
                        "isTemporaryPassword", "isNotified")
                .containsExactly(username, password, nickname, null, null, birthdate, lastRequestAt, gender,
                        Position.NONE, 0F, 0L, 0, true, false, true);
    }

    @Test
    @DisplayName("회원 비밀번호를 업데이트한다.")
    void givenNonTemporaryPassword_whenUpdatePassword_thenReturn() {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime lastRequestAt = LocalDateTime.now();
        User user = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate,
                lastRequestAt);

        String updatePassword = "password2!";
        boolean isTemporaryPassword = false;

        // when
        user.updatePassword(updatePassword, isTemporaryPassword);

        // then
        assertThat(user)
                .extracting("username", "password", "nickname", "profileDescription", "imageUrl", "birthdate",
                        "lastRequestAt", "gender", "position", "rating", "visitedCnt", "reviewCnt", "isSeekingTeam",
                        "isTemporaryPassword", "isNotified")
                .containsExactly(username, updatePassword, nickname, null, null, birthdate, lastRequestAt, gender,
                        Position.NONE, 0F, 0L, 0, true, isTemporaryPassword, true);
    }

    @Test
    @DisplayName("회원 임시 비밀번호를 업데이트한다.")
    void givenTemporaryPassword_whenUpdatePassword_thenReturn() {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime lastRequestAt = LocalDateTime.now();
        User user = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate,
                lastRequestAt);

        String updatePassword = "password2!";
        boolean isTemporaryPassword = true;


        // when
        user.updatePassword(updatePassword, true);

        // then
        assertThat(user)
                .extracting("username", "password", "nickname", "profileDescription", "imageUrl", "birthdate",
                        "lastRequestAt", "gender", "position", "rating", "visitedCnt", "reviewCnt", "isSeekingTeam",
                        "isTemporaryPassword", "isNotified")
                .containsExactly(username, updatePassword, nickname, null, null, birthdate, lastRequestAt, gender,
                        Position.NONE, 0F, 0L, 0, true, isTemporaryPassword, true);
    }

    @Test
    @DisplayName("회원 닉네임을 업데이트한다.")
    void updateNickname() {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime lastRequestAt = LocalDateTime.now();
        User user = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate,
                lastRequestAt);

        String updateNickname = "새테스터";

        // when
        user.updateNickname(updateNickname);

        // then
        assertThat(user)
                .extracting("username", "password", "nickname", "profileDescription", "imageUrl", "birthdate",
                        "lastRequestAt", "gender", "position", "rating", "visitedCnt", "reviewCnt", "isSeekingTeam",
                        "isTemporaryPassword", "isNotified")
                .containsExactly(username, password, updateNickname, null, null, birthdate, lastRequestAt, gender,
                        Position.NONE, 0F, 0L, 0, true, false, true);
    }

    private static Stream<Arguments> providerUpdateIsNotified() {
        return Stream.of(
                Arguments.of(true),
                Arguments.of(false)
        );
    }

    @ParameterizedTest(name = "[{index}] {0}로 회원 알림 여부를 업데이트한다.")
    @MethodSource("providerUpdateIsNotified")
    @DisplayName("회원 알림 여부를 업데이트한다.")
    void givenProvider_whenUpdateIsNotified_thenReturn(boolean isNotified) {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime lastRequestAt = LocalDateTime.now();
        User user = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate,
                lastRequestAt);

        // when
        user.updateIsNotified(isNotified);

        // then
        assertThat(user)
                .extracting("username", "password", "nickname", "profileDescription", "imageUrl", "birthdate",
                        "lastRequestAt", "gender", "position", "rating", "visitedCnt", "reviewCnt", "isSeekingTeam",
                        "isTemporaryPassword", "isNotified")
                .containsExactly(username, password, nickname, null, null, birthdate, lastRequestAt, gender,
                        Position.NONE, 0F, 0L, 0, true, false, isNotified);
    }

    @Test
    @DisplayName("회원 마지막 요청일을 업데이트한다.")
    void updateLastRequestAt() {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime lastRequestAt = LocalDateTime.now();
        User user = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate,
                lastRequestAt);

        LocalDateTime updateLastRequestAt = LocalDateTime.now();

        // when
        user.updateLastRequestAt(updateLastRequestAt);

        // then
        assertThat(user)
                .extracting("username", "password", "nickname", "profileDescription", "imageUrl", "birthdate",
                        "lastRequestAt", "gender", "position", "rating", "visitedCnt", "reviewCnt", "isSeekingTeam",
                        "isTemporaryPassword", "isNotified")
                .containsExactly(username, password, nickname, null, null, birthdate, updateLastRequestAt, gender,
                        Position.NONE, 0F, 0L, 0, true, false, true);
    }

    private static Stream<Arguments> providerUpdatePosition() {
        return Stream.of(
                Arguments.of(Position.DESIGNER),
                Arguments.of(Position.BACKEND),
                Arguments.of(Position.FRONTEND),
                Arguments.of(Position.MANAGER),
                Arguments.of(Position.NONE)
        );
    }

    @ParameterizedTest(name = "[{index}] {0}로 포지션을 업데이트한다.")
    @MethodSource("providerUpdatePosition")
    @DisplayName("회원 포지션을 업데이트 한다.")
    void givenProvider_whenUpdatePosition_thenReturn(Position position) {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime lastRequestAt = LocalDateTime.now();
        User user = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate,
                lastRequestAt);

        // when
        user.updatePosition(position);

        // then
        assertThat(user)
                .extracting("username", "password", "nickname", "profileDescription", "imageUrl", "birthdate",
                        "lastRequestAt", "gender", "position", "rating", "visitedCnt", "reviewCnt", "isSeekingTeam",
                        "isTemporaryPassword", "isNotified")
                .containsExactly(username, password, nickname, null, null, birthdate, lastRequestAt, gender, position,
                        0F, 0L, 0, true, false, true);
    }

    private static Stream<Arguments> providerHasPosition() {
        return Stream.of(
                Arguments.of(Position.NONE, false),
                Arguments.of(Position.DESIGNER, true),
                Arguments.of(Position.BACKEND, true),
                Arguments.of(Position.FRONTEND, true),
                Arguments.of(Position.MANAGER, true)
        );
    }

    @ParameterizedTest(name = "[{index}] 포지션 존재 여부가 {0}일때 포지션 존재 여부를 확인한다.")
    @MethodSource("providerHasPosition")
    @DisplayName("회원 포지션 존재 여부를 확인한다.")
    void givenProvider_whenHasPosition_thenReturn(Position position, boolean result) {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        user.updatePosition(position);

        // when & then
        assertThat(user.hasPosition()).isEqualTo(result);
    }

    @Test
    @DisplayName("회원 프로필 소개를 업데이트한다.")
    void updateProfileDescription() {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime lastRequestAt = LocalDateTime.now();
        User user = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate,
                lastRequestAt);

        String profileDescription = "안녕하세요! 신입 백엔드 개발자 테스터입니다.";

        // when
        user.updateProfileDescription(profileDescription);

        // then
        assertThat(user)
                .extracting("username", "password", "nickname", "profileDescription", "imageUrl", "birthdate",
                        "lastRequestAt", "gender", "position", "rating", "visitedCnt", "reviewCnt", "isSeekingTeam",
                        "isTemporaryPassword", "isNotified")
                .containsExactly(username, password, nickname, profileDescription, null, birthdate, lastRequestAt,
                        gender, Position.NONE, 0F, 0L, 0, true, false, true);
    }

    @Test
    @DisplayName("회원 프로필 사진 URL을 업데이트한다.")
    void updateImageUrl() {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime lastRequestAt = LocalDateTime.now();
        User user = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate,
                lastRequestAt);

        String imageUrl = "https://google.com";

        // when
        user.updateImageUrl(imageUrl);

        // then
        assertThat(user)
                .extracting("username", "password", "nickname", "profileDescription", "imageUrl", "birthdate",
                        "lastRequestAt", "gender", "position", "rating", "visitedCnt", "reviewCnt", "isSeekingTeam",
                        "isTemporaryPassword", "isNotified")
                .containsExactly(username, password, nickname, null, imageUrl, birthdate, lastRequestAt, gender,
                        Position.NONE, 0F, 0L, 0, true, false, true);
    }

    @ParameterizedTest(name = "[{index}] {0}로 팀 찾기 여부를 업데이트한다.")
    @ValueSource(booleans = {true, false})
    @DisplayName("회원 팀 찾기 여부를 업데이트한다.")
    void givenProvider_whenUpdateIsSeekingTeam_thenReturn(boolean isSeekingTeam) {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime lastRequestAt = LocalDateTime.now();
        User user = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate,
                lastRequestAt);

        // when
        user.updateIsSeekingTeam(isSeekingTeam);

        // then
        assertThat(user)
                .extracting("username", "password", "nickname", "profileDescription", "imageUrl", "birthdate",
                        "lastRequestAt", "gender", "position", "rating", "visitedCnt", "reviewCnt", "isSeekingTeam",
                        "isTemporaryPassword", "isNotified")
                .containsExactly(username, password, nickname, null, null, birthdate, lastRequestAt, gender,
                        Position.NONE, 0F, 0L, 0, isSeekingTeam, false, true);
    }

    @ParameterizedTest(name = "[{index}] {0}번 회원 프로필을 방문한다.")
    @ValueSource(longs = {1, 3, 5, 7, 10})
    @DisplayName("회원 프로필을 방문한다.")
    void givenProvider_whenVisit_thenReturn(long visitCnt) {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime lastRequestAt = LocalDateTime.now();
        User user = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate,
                lastRequestAt);

        // when
        for (long i = 0; i < visitCnt; i++)
            user.visit();

        // then
        assertThat(user)
                .extracting("username", "password", "nickname", "profileDescription", "imageUrl", "birthdate",
                        "lastRequestAt", "gender", "position", "rating", "visitedCnt", "reviewCnt", "isSeekingTeam",
                        "isTemporaryPassword", "isNotified")
                .containsExactly(username, password, nickname, null, null, birthdate, lastRequestAt, gender,
                        Position.NONE, 0F, visitCnt, 0, true, false, true);
    }

    private static Stream<Arguments> providerRate() {
        return Stream.of(
                Arguments.of(new float[] {1, 1, 1}, 1F),
                Arguments.of(new float[] {1, 3, 2}, 2F),
                Arguments.of(new float[] {1, 2, 3, 4, 5}, 3F),
                Arguments.of(new float[] {3, 4, 5}, 4F),
                Arguments.of(new float[] {5, 5, 5}, 5F)
        );
    }

    @ParameterizedTest(name = "[{index}] {1}로 회원 평점을 업데이트한다.")
    @MethodSource("providerRate")
    @DisplayName("회원 평점을 업데이트한다.")
    void givenProvider_whenRate_thenReturn(float[] ratings, float averageRating) {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime lastRequestAt = LocalDateTime.now();
        User user = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate,
                lastRequestAt);

        // when
        for (float r : ratings)
            user.rate(r);

        // then
        assertThat(user.getRating()).isEqualTo(averageRating);
    }

    private static Stream<Arguments> providerEquals() {
        LocalDateTime now = LocalDateTime.now();

        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), now);

        User profileDescriptionUser1 = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터",
                Gender.M, LocalDate.of(1997, 2, 11), now);
        profileDescriptionUser1.updateProfileDescription("1");
        User profileDescriptionUser2 = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터",
                Gender.M, LocalDate.of(1997, 2, 11), now);
        profileDescriptionUser2.updateProfileDescription("2");

        User imageUrlUser1 = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터",
                Gender.M, LocalDate.of(1997, 2, 11), now);
        imageUrlUser1.updateImageUrl("1");
        User imageUrlUser2 = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터",
                Gender.M, LocalDate.of(1997, 2, 11), now);
        imageUrlUser2.updateImageUrl("2");

        User positionUser1 = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터",
                Gender.M, LocalDate.of(1997, 2, 11), now);
        positionUser1.updatePosition(Position.DESIGNER);
        User positionUser2 = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터",
                Gender.M, LocalDate.of(1997, 2, 11), now);
        positionUser2.updatePosition(Position.BACKEND);

        User ratingUser1 = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터",
                Gender.M, LocalDate.of(1997, 2, 11), now);
        User ratingUser2 = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터",
                Gender.M, LocalDate.of(1997, 2, 11), now);
        ratingUser2.rate(5F);

        User visitUser1 = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터",
                Gender.M, LocalDate.of(1997, 2, 11), now);
        User visitUser2 = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터",
                Gender.M, LocalDate.of(1997, 2, 11), now);
        visitUser2.visit();

        User reviewCntUser1 = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터",
                Gender.M, LocalDate.of(1997, 2, 11), now);
        reviewCntUser1.rate(1F);
        User reviewCntUser2 = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터",
                Gender.M, LocalDate.of(1997, 2, 11), now);
        reviewCntUser2.rate(1F);
        reviewCntUser2.rate(1F);

        User isSeekingTeamUser1 = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터",
                Gender.M, LocalDate.of(1997, 2, 11), now);
        isSeekingTeamUser1.updateIsSeekingTeam(true);
        User isSeekingTeamUser2 = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터",
                Gender.M, LocalDate.of(1997, 2, 11), now);
        isSeekingTeamUser2.updateIsSeekingTeam(false);

        User isTemporaryPasswordUser1 = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!",
                "테스터", Gender.M, LocalDate.of(1997, 2, 11), now);
        isTemporaryPasswordUser1.updatePassword("password1!", false);
        User isTemporaryPasswordUser2 = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!",
                "테스터", Gender.M, LocalDate.of(1997, 2, 11), now);
        isTemporaryPasswordUser2.updatePassword("password1!", true);

        User isNotifiedUser1 = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!",
                "테스터", Gender.M, LocalDate.of(1997, 2, 11), now);
        isNotifiedUser1.updateIsNotified(true);
        User isNotifiedUser2 = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!",
                "테스터", Gender.M, LocalDate.of(1997, 2, 11), now);
        isNotifiedUser2.updateIsNotified(false);

        return Stream.of(
                Arguments.of(user, user, true),
                Arguments.of(user, new Object(), false),
                Arguments.of(
                        createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                                LocalDate.of(1997, 2, 11), now),
                        createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                                LocalDate.of(1997, 2, 11), now),
                        true
                ),
                Arguments.of(
                        createDefaultUser("tester1@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                                LocalDate.of(1997, 2, 11), now),
                        createDefaultUser("tester2@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                                LocalDate.of(1997, 2, 11), now),
                        false
                ),
                Arguments.of(
                        createDefaultUser("tester@gabojait.com", "000000", "tester1", "password1!", "테스터", Gender.M,
                                LocalDate.of(1997, 2, 11), now),
                        createDefaultUser("tester@gabojait.com", "000000", "tester2", "password1!", "테스터", Gender.M,
                                LocalDate.of(1997, 2, 11), now),
                        false
                ),
                Arguments.of(
                        createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                                LocalDate.of(1997, 2, 11), now),
                        createDefaultUser("tester@gabojait.com", "000000", "tester", "password2!", "테스터", Gender.M,
                                LocalDate.of(1997, 2, 11), now),
                        false
                ),
                Arguments.of(
                        createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                                LocalDate.of(1997, 2, 11), now),
                        createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                                LocalDate.of(1997, 2, 12), now),
                        false
                ),
                Arguments.of(
                        createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                                LocalDate.of(1997, 2, 11), now),
                        createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                                LocalDate.of(1997, 2, 11), LocalDateTime.now()),
                        false
                ),
                Arguments.of(
                        createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                                LocalDate.of(1997, 2, 11), now),
                        createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.F,
                                LocalDate.of(1997, 2, 11), now),
                        false
                ),
                Arguments.of(profileDescriptionUser1, profileDescriptionUser2 , false),
                Arguments.of(imageUrlUser1, imageUrlUser2, false),
                Arguments.of(positionUser1, positionUser2, false),
                Arguments.of(ratingUser1, ratingUser2, false),
                Arguments.of(visitUser1, visitUser2, false),
                Arguments.of(reviewCntUser1, reviewCntUser2, false),
                Arguments.of(isSeekingTeamUser1, isSeekingTeamUser2, false),
                Arguments.of(isTemporaryPasswordUser1, isTemporaryPasswordUser2, false),
                Arguments.of(isNotifiedUser1, isNotifiedUser2, false)
        );
    }

    @ParameterizedTest(name = "[{index}] 회원 객체를 비교한다.")
    @MethodSource("providerEquals")
    @DisplayName("회원 객체를 비교한다.")
    void givenProvider_whenEquals_thenReturn(User user, Object object, boolean result) {
        // when & then
        assertThat(user.equals(object)).isEqualTo(result);
    }

    private static Stream<Arguments> providerHashCode() {
        LocalDateTime now = LocalDateTime.now();

        return Stream.of(
                Arguments.of(
                        createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                                LocalDate.of(1997, 2, 11), now),
                        createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                                LocalDate.of(1997, 2, 11), now),
                        true
                ),
                Arguments.of(
                        createDefaultUser("tester@gabojait.com", "000000", "tester1", "password1!", "테스터", Gender.M,
                                LocalDate.of(1997, 2, 11), now),
                        createDefaultUser("tester@gabojait.com", "000000", "tester2", "password1!", "테스터", Gender.M,
                                LocalDate.of(1997, 2, 11), now),
                        false
                )
        );
    }

    @ParameterizedTest(name = "[{index}] 회원 해시코드를 비교한다.")
    @MethodSource("providerHashCode")
    @DisplayName("회원 해시코드를 비교한다.")
    void givenProvider_whenHashCode_thenReturn(User user1, User user2, boolean result) {
        // when
        int hashCode1 = user1.hashCode();
        int hashCode2 = user2.hashCode();

        // then
        assertThat(hashCode1 == hashCode2).isEqualTo(result);
    }

    @Test
    @DisplayName("회원의 아이디를 반환한다.")
    void getUsername() {
        // given
        String username = "tester";
        User user = createDefaultUser("tester@gabojait.com", "000000", username, "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        // when & then
        assertThat(user.getUsername()).isEqualTo(username);
    }

    @Test
    @DisplayName("회원의 비밀번호를 반환한다.")
    void getPassword() {
        // given
        String password = "password1!";
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", password, "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        // when & then
        assertThat(user.getPassword()).isEqualTo(password);
    }

    @Test
    @DisplayName("회원의 권한들을 반환한다.")
    void getAuthorities() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        // when & then
        assertThat(user.getAuthorities()).isNull();
    }

    @Test
    @DisplayName("회원의 계정 만료 여부를 반환한다.")
    void isAccountNonExpired() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        // when & then
        assertThat(user.isAccountNonExpired()).isTrue();
    }

    @Test
    @DisplayName("회원의 계정 잠금 여부를 반환한다.")
    void isAccountNonLocked() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        // when & then
        assertThat(user.isAccountNonLocked()).isTrue();
    }

    @Test
    @DisplayName("회원의 계정 자격 만료 여부를 반환한다.")
    void isCredentialsNonExpired() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        // when & then
        assertThat(user.isCredentialsNonExpired()).isTrue();
    }

    @Test
    @DisplayName("회원의 가능 여부를 반환한다.")
    void isEnabled() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        // when
        assertThat(user.isEnabled()).isTrue();
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