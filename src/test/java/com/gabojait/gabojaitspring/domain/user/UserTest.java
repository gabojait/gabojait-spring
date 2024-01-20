package com.gabojait.gabojaitspring.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class UserTest {

    @Test
    @DisplayName("회원 생성이 정상 작동한다")
    void givenValid_whenBuilder_thenReturn() {
        // given
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime lastRequestAt = LocalDateTime.now();
        Contact contact = createDefaultContact("tester@gabojait.com");

        // when
        User user = createUser(username, password, nickname, gender, birthdate, lastRequestAt, contact);

        // then
        assertAll(
                () -> assertThat(user)
                        .extracting("username", "password", "nickname", "profileDescription", "imageUrl", "birthdate",
                                "lastRequestAt", "gender", "position", "rating", "visitedCnt", "reviewCnt", "isSeekingTeam",
                                "isTemporaryPassword", "isNotified")
                        .containsExactly(username, password, nickname, null, null, birthdate, lastRequestAt, gender,
                                Position.NONE, 0F, 0L, 0, true, false, true),
                () -> assertThat(user.getContact()).isEqualTo(contact)
        );
    }

    @Test
    @DisplayName("새 비밀번호로 회원 비밀번호 업데이트가 정상 작동한다")
    void givenNewPassword_whenUpdatePassword_thenReturn() {
        // given
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime lastRequestAt = LocalDateTime.now();
        Contact contact = createDefaultContact("tester@gabojait.com");
        User user = createUser(username, password, nickname, gender, birthdate, lastRequestAt, contact);

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
    @DisplayName("임시 비밀본호로 회원 비밀번호 업데이트가 정상 작동한다")
    void givenTemporaryPassword_whenUpdatePassword_thenReturn() {
        // given
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime lastRequestAt = LocalDateTime.now();
        Contact contact = createDefaultContact("tester@gabojait.com");
        User user = createUser(username, password, nickname, gender, birthdate, lastRequestAt, contact);

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
    @DisplayName("회원 닉네임 업데이트가 정상 작동한다")
    void givenValid_whenUpdateNickname_thenReturn() {
        // given
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime lastRequestAt = LocalDateTime.now();
        Contact contact = createDefaultContact("tester@gabojait.com");
        User user = createUser(username, password, nickname, gender, birthdate, lastRequestAt, contact);

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

    @ParameterizedTest(name = "[{index}] {0}로 회원 알림 여부를 업데이트 한다")
    @ValueSource(booleans = {true, false})
    @DisplayName("회원 알림 여부 업데이트가 정상 작동한다")
    void givenValid_whenUpdateIsNotified_thenReturn(boolean isNotified) {
        // given
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime lastRequestAt = LocalDateTime.now();
        Contact contact = createDefaultContact("tester@gabojait.com");
        User user = createUser(username, password, nickname, gender, birthdate, lastRequestAt, contact);

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
    @DisplayName("회원 마지막 요청일 업데이트가 정상 작동한다")
    void givenValid_whenUpdateLastRequestAt_thenReturn() {
        // given
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime lastRequestAt = LocalDateTime.now();
        Contact contact = createDefaultContact("tester@gabojait.com");
        User user = createUser(username, password, nickname, gender, birthdate, lastRequestAt, contact);

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

    @ParameterizedTest(name = "[{index}] {0}로 포지션을 업데이트한다")
    @EnumSource(Position.class)
    @DisplayName("회원 포지션 업데이트가 정상 작동한다")
    void givenValid_whenUpdatePosition_thenReturn(Position position) {
        // given
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime lastRequestAt = LocalDateTime.now();
        Contact contact = createDefaultContact("tester@gabojait.com");
        User user = createUser(username, password, nickname, gender, birthdate, lastRequestAt, contact);

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

    @ParameterizedTest(name = "[{index}] 포지션 존재 여부가 {0}일때 포지션 존재 여부를 확인한다")
    @MethodSource("providerHasPosition")
    @DisplayName("회원 포지션 존재 여부 확인이 정상 작동한다")
    void givenProvider_whenHasPosition_thenReturn(Position position, boolean result) {
        // given
        Contact contact = createDefaultContact("tester@gabojait.com");
        User user = createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11),
                LocalDateTime.now(), contact);

        user.updatePosition(position);

        // when & then
        assertThat(user.hasPosition()).isEqualTo(result);
    }

    @Test
    @DisplayName("회원 프로필 소개 업데이트가 정상 작동한다")
    void givenValid_whenUpdateProfileDescription_thenReturn() {
        // given
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime lastRequestAt = LocalDateTime.now();
        Contact contact = createDefaultContact("tester@gabojait.com");
        User user = createUser(username, password, nickname, gender, birthdate, lastRequestAt, contact);

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
    @DisplayName("회원 프로필 사진 URL 업데이트가 정상 작동한다")
    void givenValid_whenUpdateImageUrl_thenReturn() {
        // given
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime lastRequestAt = LocalDateTime.now();
        Contact contact = createDefaultContact("tester@gabojait.com");
        User user = createUser(username, password, nickname, gender, birthdate, lastRequestAt, contact);

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

    @ParameterizedTest(name = "[{index}] {0}로 팀 찾기 여부를 업데이트한다")
    @ValueSource(booleans = {true, false})
    @DisplayName("회원 팀 찾기 여부 업데이트가 정상 작동한다")
    void givenValid_whenUpdateIsSeekingTeam_thenReturn(boolean isSeekingTeam) {
        // given
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime lastRequestAt = LocalDateTime.now();
        Contact contact = createDefaultContact("tester@gabojait.com");
        User user = createUser(username, password, nickname, gender, birthdate, lastRequestAt, contact);

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

    @ParameterizedTest(name = "[{index}] {0}번 회원 프로필을 방문한다")
    @ValueSource(longs = {1, 3, 5, 7, 10})
    @DisplayName("회원 프로필 방문이 정상 작동한다")
    void givenValid_whenVisit_thenReturn(long visitCnt) {
        // given
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime lastRequestAt = LocalDateTime.now();
        Contact contact = createDefaultContact("tester@gabojait.com");
        User user = createUser(username, password, nickname, gender, birthdate, lastRequestAt, contact);

        // when
        LongStream.range(0, visitCnt)
                .forEach(i -> user.visit());

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

    @ParameterizedTest(name = "[{index}] {1}로 회원 평점을 업데이트한다")
    @MethodSource("providerRate")
    @DisplayName("회원 평점 업데이트가 정상 작동한다")
    void givenProvider_whenRate_thenReturn(float[] ratings, float averageRating) {
        // given
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime lastRequestAt = LocalDateTime.now();
        Contact contact = createDefaultContact("tester@gabojait.com");
        User user = createUser(username, password, nickname, gender, birthdate, lastRequestAt, contact);

        // when
        for (float r : ratings)
            user.rate(r);

        // then
        assertThat(user)
                .extracting("username", "password", "nickname", "profileDescription", "imageUrl", "birthdate",
                        "lastRequestAt", "gender", "position", "rating", "visitedCnt", "reviewCnt", "isSeekingTeam",
                        "isTemporaryPassword", "isNotified")
                .containsExactly(username, password, nickname, null, null, birthdate, lastRequestAt, gender,
                        Position.NONE, averageRating, 0L, ratings.length, true, false, true);
    }

    private static Stream<Arguments> providerEquals() {
        LocalDateTime now = LocalDateTime.now();

        Contact contact = createDefaultContact("tester@gabojait.com");
        User user = createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11),
                LocalDateTime.now(), contact);

        Contact contact1 = createDefaultContact("tester1@gabojait.com");
        User contactUser1 = createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11),
                LocalDateTime.now(), contact1);
        Contact contact2 = createDefaultContact("tester2@gabojait.com");
        User contactUser2 = createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11),
                LocalDateTime.now(), contact2);

        User profileDescriptionUser1 = createUser("tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), now, contact);
        profileDescriptionUser1.updateProfileDescription("1");
        User profileDescriptionUser2 = createUser("tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), now, contact);
        profileDescriptionUser2.updateProfileDescription("2");

        User imageUrlUser1 = createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11), now,
                contact);
        imageUrlUser1.updateImageUrl("1");
        User imageUrlUser2 = createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11), now,
                contact);
        imageUrlUser2.updateImageUrl("2");

        User positionUser1 = createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11), now,
                contact);
        positionUser1.updatePosition(Position.DESIGNER);
        User positionUser2 = createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11), now,
                contact);
        positionUser2.updatePosition(Position.BACKEND);

        User ratingUser1 = createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11), now,
                contact);
        User ratingUser2 = createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11), now,
                contact);
        ratingUser2.rate(5F);

        User visitUser1 = createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11), now,
                contact);
        User visitUser2 = createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11), now,
                contact);
        visitUser2.visit();

        User reviewCntUser1 = createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11),
                now, contact);
        reviewCntUser1.rate(1F);
        User reviewCntUser2 = createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11),
                now, contact);
        reviewCntUser2.rate(1F);
        reviewCntUser2.rate(1F);

        User isSeekingTeamUser1 = createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11),
                now, contact);
        isSeekingTeamUser1.updateIsSeekingTeam(true);
        User isSeekingTeamUser2 = createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11),
                now, contact);
        isSeekingTeamUser2.updateIsSeekingTeam(false);

        User isTemporaryPasswordUser1 = createUser("tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), now, contact);
        isTemporaryPasswordUser1.updatePassword("password1!", false);
        User isTemporaryPasswordUser2 = createUser("tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), now, contact);
        isTemporaryPasswordUser2.updatePassword("password1!", true);

        User isNotifiedUser1 = createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11),
                now, contact);
        isNotifiedUser1.updateIsNotified(true);
        User isNotifiedUser2 = createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11),
                now, contact);
        isNotifiedUser2.updateIsNotified(false);

        return Stream.of(
                Arguments.of(user, user, true),
                Arguments.of(user, new Object(), false),
                Arguments.of(
                        createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11), now,
                                contact),
                        createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11), now,
                                contact),
                        true
                ),
                Arguments.of(contactUser1, contactUser2, false),
                Arguments.of(
                        createUser("tester1", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11), now,
                                contact),
                        createUser("tester2", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11), now,
                                contact),
                        false
                ),
                Arguments.of(
                        createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11), now,
                                contact),
                        createUser("tester", "password2!", "테스터", Gender.M, LocalDate.of(1997, 2, 11), now,
                                contact),
                        false
                ),
                Arguments.of(
                        createUser("tester", "password1!", "테스터일", Gender.M, LocalDate.of(1997, 2, 11), now,
                                contact),
                        createUser("tester", "password1!", "테스터이", Gender.M, LocalDate.of(1997, 2, 11), now,
                                contact),
                        false
                ),
                Arguments.of(
                        createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11), now,
                                contact),
                        createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 12), now,
                                contact),
                        false
                ),
                Arguments.of(
                        createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11), now,
                                contact),
                        createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11),
                                LocalDateTime.now(), contact),
                        false
                ),
                Arguments.of(
                        createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11), now,
                                contact),
                        createUser("tester", "password1!", "테스터", Gender.F, LocalDate.of(1997, 2, 11), now,
                                contact),
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

    @ParameterizedTest(name = "[{index}] 회원 객체를 비교한다")
    @MethodSource("providerEquals")
    @DisplayName("회원 객체 비교가 정상 작동한다")
    void givenProvider_whenEquals_thenReturn(User user, Object object, boolean result) {
        // when & then
        assertThat(user.equals(object)).isEqualTo(result);
    }

    private static Stream<Arguments> providerHashCode() {
        LocalDateTime now = LocalDateTime.now();
        Contact contact = createDefaultContact("tester@gabojait.com");

        return Stream.of(
                Arguments.of(
                        createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11), now,
                                contact),
                        createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11), now,
                                contact),
                        true
                ),
                Arguments.of(
                        createUser("tester1", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11), now,
                                contact),
                        createUser("tester2", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11), now,
                                contact),
                        false
                )
        );
    }

    @ParameterizedTest(name = "[{index}] 회원 해시코드를 비교한다")
    @MethodSource("providerHashCode")
    @DisplayName("회원 해시코드 비교가 정상 적당한다")
    void givenProvider_whenHashCode_thenReturn(User user1, User user2, boolean result) {
        // when
        int hashCode1 = user1.hashCode();
        int hashCode2 = user2.hashCode();

        // then
        assertThat(hashCode1 == hashCode2).isEqualTo(result);
    }

    @Test
    @DisplayName("회원 아이디 반환이 정상 적당한다")
    void givenValid_whenGetUsername_thenReturn() {
        // given
        String username = "tester";
        Contact contact = createDefaultContact("tester@gabojait.com");
        User user = createUser(username, "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11),
                LocalDateTime.now(), contact);

        // when & then
        assertThat(user.getUsername()).isEqualTo(username);
    }

    @Test
    @DisplayName("회원 비밀번호 반환이 정상 작동한다")
    void givenValid_whenGetPassword_thenReturn() {
        // given
        String password = "password1!";
        Contact contact = createDefaultContact("tester@gabojait.com");
        User user = createUser("tester", password, "테스터", Gender.M, LocalDate.of(1997, 2, 11),
                LocalDateTime.now(), contact);

        // when & then
        assertThat(user.getPassword()).isEqualTo(password);
    }

    @Test
    @DisplayName("회원 권한들 반환이 정상 작동한다")
    void givenValid_whenGetAuthorities_thenReturn() {
        // given
        Contact contact = createDefaultContact("tester@gabojait.com");
        User user = createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11),
                LocalDateTime.now(), contact);

        // when & then
        assertThat(user.getAuthorities()).isNull();
    }

    @Test
    @DisplayName("회원 계정 만료 여부 반환이 정상 작동한다")
    void givenValid_whenIssAccountNonExpired_thenReturn() {
        // given
        Contact contact = createDefaultContact("tester@gabojait.com");
        User user = createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11),
                LocalDateTime.now(), contact);

        // when & then
        assertThat(user.isAccountNonExpired()).isTrue();
    }

    @Test
    @DisplayName("회원 계정 잠금 여부 반환이 정상 작동한다")
    void givenValid_whenIsAccountNonLocked_thenReturn() {
        // given
        Contact contact = createDefaultContact("tester@gabojait.com");
        User user = createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11),
                LocalDateTime.now(), contact);

        // when & then
        assertThat(user.isAccountNonLocked()).isTrue();
    }

    @Test
    @DisplayName("회원 계정 자격 만료 여부 반환이 정상 작동한다")
    void givenValid_whenIsCredentialsNonExpired_thenReturn() {
        // given
        Contact contact = createDefaultContact("tester@gabojait.com");
        User user = createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11),
                LocalDateTime.now(), contact);

        // when & then
        assertThat(user.isCredentialsNonExpired()).isTrue();
    }

    @Test
    @DisplayName("회원 가능 여부 반환이 정상 작동한다")
    void givenValid_whenIsEnabled_thenReturn() {
        // given
        Contact contact = createDefaultContact("tester@gabojait.com");
        User user = createUser("tester", "password1!", "테스터", Gender.M, LocalDate.of(1997, 2, 11),
                LocalDateTime.now(), contact);

        // when
        assertThat(user.isEnabled()).isTrue();
    }

    private static User createUser(String username,
                                   String password,
                                   String nickname,
                                   Gender gender,
                                   LocalDate birthdate,
                                   LocalDateTime lastRequestAt,
                                   Contact contact) {
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
    
    private static Contact createDefaultContact(String email) {
        Contact contact = Contact.builder()
                .email(email)
                .verificationCode("000000")
                .build();
        
        contact.verified();
        
        return contact;
    }
}