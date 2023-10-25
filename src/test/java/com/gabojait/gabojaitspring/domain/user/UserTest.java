package com.gabojait.gabojaitspring.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
    @DisplayName("비밀번호를 업데이트한다.")
    void givenNonTemporaryPassword_whenUpdatePassword() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        String password = "password2!";

        // when
        user.updatePassword(password, false);

        // then
        assertThat(user)
                .extracting("password", "isTemporaryPassword")
                .containsExactly(password, false);
    }

    @Test
    @DisplayName("임시 비밀번호를 업데이트한다.")
    void givenTemporaryPassword_whenUpdatePassword() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        String password = "password2!";

        // when
        user.updatePassword(password, true);

        // then
        assertThat(user)
                .extracting("password", "isTemporaryPassword")
                .containsExactly(password, true);
    }

    @Test
    @DisplayName("닉네임을 업데이트한다.")
    void updateNickname() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        String nickname = "새테스터";

        // when
        user.updateNickname(nickname);

        // then
        assertEquals(nickname, user.getNickname());
    }

    @Test
    @DisplayName("알림 여부를 업데이트한다.")
    void updateIsNotified() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        // when
        user.updateIsNotified(false);

        // then
        assertFalse(user.getIsNotified());
    }

    @Test
    @DisplayName("마지막 요청일을 업데이트한다.")
    void updateLastRequestAt() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        LocalDateTime lastRequestAt = LocalDateTime.now();

        // when
        user.updateLastRequestAt(lastRequestAt);

        // then
        assertEquals(lastRequestAt, user.getLastRequestAt());
    }

    @Test
    @DisplayName("포지션을 업데이트한다.")
    void updatePosition() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        Position position = Position.BACKEND;

        // when
        user.updatePosition(position);

        // then
        assertEquals(position, user.getPosition());
    }

    @Test
    @DisplayName("포지션이 없는 경우 존재 여부를 확인한다.")
    void givenNoPosition_whenHasPosition() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        // when
        boolean result = user.hasPosition();

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("포지션이 있는 경우 존재 여부를 확인한다.")
    void hasPosition() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        user.updatePosition(Position.BACKEND);

        // when
        boolean result = user.hasPosition();

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("프로필 소개를 업데이트한다.")
    void updateProfileDescription() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        String profileDescription = "안녕하세요! 신입 백엔드 개발자 테스터입니다.";

        // when
        user.updateProfileDescription(profileDescription);

        // then
        assertEquals(profileDescription, user.getProfileDescription());
    }

    @Test
    @DisplayName("프로필 사진 URL을 업데이트한다.")
    void updateImageUrl() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        String imageUrl = "https://google.com";

        // when
        user.updateImageUrl(imageUrl);

        // then
        assertEquals(imageUrl, user.getImageUrl());
    }

    @Test
    @DisplayName("팀 찾기 여부를 업데이트한다.")
    void updateIsSeekingTeam() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        // when
        user.updateIsSeekingTeam(false);

        // then
        assertFalse(user.getIsSeekingTeam());
    }

    @Test
    @DisplayName("회원 프로필을 방문한다.")
    void visit() {
        // given
        User user = createDefaultUser("test@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        int visitedCnt = new Random().nextInt(100) + 1;

        // when
        for (int i = 0; i < visitedCnt; i++)
            user.visit();

        // then
        assertEquals(visitedCnt, user.getVisitedCnt());
    }

    @Test
    @DisplayName("회원 평점을 업데이트한다.")
    void rate() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        float[] rating = {1F, 2F, 3F, 4F, 5F};
        float averageRating = 3F;

        // when
        for (float r : rating)
            user.rate(r);

        // then
        assertEquals(averageRating, user.getRating());
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