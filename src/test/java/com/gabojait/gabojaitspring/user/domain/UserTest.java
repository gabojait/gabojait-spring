package com.gabojait.gabojaitspring.user.domain;

import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.user.domain.type.Gender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.*;

class UserTest {

    @Test
    @DisplayName("회원 | 빌더")
    void builder() {
        // given
        String username = "tester";
        String password = "password123!";
        Gender gender = Gender.N;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        String nickname = "테스터";

        // when
        User user = createUser(username, password, gender, birthdate, nickname);

        // then
        assertThat(user)
                .extracting("username", "password", "gender", "birthdate", "nickname", "imageUrl",
                        "profileDescription", "isSeekingTeam", "rating", "visitedCnt", "isTemporaryPassword",
                        "isNotified", "isDeleted")
                .containsExactly(username, password, gender, birthdate, nickname, null,
                        null, true, 0F, 0L, false,
                        true, false);
    }

    @Test
    @DisplayName("회원 | 비밀번호 업데이트")
    void updatePassword() {
        // given
        User user = createUser("tester", "password123!", Gender.N,
                LocalDate.of(1997, 2, 11), "테스터");
        String newPassword = "new123!";
        boolean isTemporaryPassword = false;

        // when
        user.updatePassword(newPassword, isTemporaryPassword);

        // then
        assertThat(user)
                .extracting("password", "isTemporaryPassword")
                .containsExactly(newPassword, isTemporaryPassword);
    }

    @Test
    @DisplayName("회원 | 비밀번호 업데이트 | 임시")
    void updatePassword_temporary() {
        // given
        User user = createUser("tester", "password123!", Gender.N,
                LocalDate.of(1997, 2, 11), "테스터");
        String newPassword = "new123!";
        boolean isTemporaryPassword = true;

        // when
        user.updatePassword(newPassword, isTemporaryPassword);

        // then
        assertThat(user)
                .extracting("password", "isTemporaryPassword")
                .containsExactly(newPassword, isTemporaryPassword);
    }

    @Test
    @DisplayName("회원 | 닉네임 업데이트")
    void updateNickname() {
        // given
        User user = createUser("tester", "password123!", Gender.N,
                LocalDate.of(1997, 2, 11), "테스터");
        String newNickname = "새닉네임";

        // when
        user.updateNickname(newNickname);

        // then
        assertThat(user.getNickname()).isEqualTo(newNickname);
    }

    @Test
    @DisplayName("회원 | 알림 여부 업데이트")
    void updateIsNotified() {
        // given
        User user = createUser("tester", "password123!", Gender.N,
                LocalDate.of(1997, 2, 11), "테스터");

        // when
        user.updateIsNotified(false);

        // then
        assertThat(user.getIsNotified()).isFalse();
    }

    @Test
    @DisplayName("회원 | 마지막 요청일 업데이트")
    void updateLastRequestAt() {
        // given
        User user = createUser("tester", "password123!", Gender.N,
                LocalDate.of(1997, 2, 11), "테스터");

        // when
        user.updateLastRequestAt();

        // then
        assertThat(user.getLastRequestAt()).isEqualToIgnoringSeconds(LocalDateTime.now());
    }

    @Test
    @DisplayName("회원 | 회원 탈퇴")
    void delete() {
        // given
        User user = createUser("tester", "password123!", Gender.N,
                LocalDate.of(1997, 2, 11), "테스터");

        // when
        user.delete();

        // then
        assertThat(user).extracting("username", "isDeleted").containsExactly(null, true);
        assertThat(user.getContact())
                .extracting("email", "isDeleted")
                .containsExactly(null, true);
    }

    @Test
    @DisplayName("회원 | 포지션 업데이트")
    void updatePosition() {
        // given
        User user = createUser("tester", "password123!", Gender.N,
                LocalDate.of(1997, 2, 11), "테스터");

        // when
        user.updatePosition(Position.BACKEND);

        // then
        assertThat(user.getPosition()).isEqualTo(Position.BACKEND);
    }

    @Test
    @DisplayName("회원 | 포지션 여부 | 없음")
    void hasPosition_nonExist() {
        // given
        User user = createUser("tester", "password123!", Gender.N,
                LocalDate.of(1997, 2, 11), "테스터");

        // when
        boolean hasPosition = user.hasPosition();

        // then
        assertThat(hasPosition).isFalse();
    }

    @Test
    @DisplayName("회원 | 포지션 여부 | 있음")
    void hasPosition_exist() {
        // given
        User user = createUser("tester", "password123!", Gender.N,
                LocalDate.of(1997, 2, 11), "테스터");
        user.updatePosition(Position.BACKEND);

        // when
        boolean hasPosition = user.hasPosition();

        // then
        assertThat(hasPosition).isTrue();
    }

    @Test
    @DisplayName("회원 | 프로필 소개 업데이트")
    void updateProfileDescription() {
        // given
        User user = createUser("tester", "password123!", Gender.N,
                LocalDate.of(1997, 2, 11), "테스터");
        String profileDescription = "안녕하세요.";

        // when
        user.updateProfileDescription(profileDescription);

        // then
        assertThat(user.getProfileDescription()).isEqualTo(profileDescription);
    }

    @Test
    @DisplayName("회원 | 프로필 사진 URL 업데이트")
    void updateImageUrl() {
        // given
        User user = createUser("tester", "password123!", Gender.N,
                LocalDate.of(1997, 2, 11), "테스터");
        String imageUrl = "https://google.com/";

        // when
        user.updateImageUrl(imageUrl);

        // then
        assertThat(user.getImageUrl()).isEqualTo(imageUrl);
    }

    @Test
    @DisplayName("회원 | 팀 찾기 여부 수정")
    void updateIsSeekingTeam() {
        // given
        User user = createUser("tester", "password123!", Gender.N,
                LocalDate.of(1997, 2, 11), "테스터");
        boolean isSeekingTeam = false;

        // when
        user.updateIsSeekingTeam(isSeekingTeam);

        // then
        assertThat(user.getIsSeekingTeam()).isFalse();
    }

    @Test
    @DisplayName("회원 | 방문 수 증가")
    void incrementVisitedCnt() {
        // given
        User user = createUser("tester", "password123!", Gender.N,
                LocalDate.of(1997, 2, 11), "테스터");

        // when
        user.incrementVisitedCnt();

        // then
        assertThat(user.getVisitedCnt()).isEqualTo(1L);
    }

    @Test
    @DisplayName("회원 | 평가")
    void rate() {
        // given
        User user = createUser("tester", "password123!", Gender.N,
                LocalDate.of(1997, 2, 11), "테스터");
        float rating1 = 1F;
        float rating2 = 2F;
        float rating3 = 3F;
        float averageRating = (rating1 + rating2 + rating3) / 3;

        // when
        user.rate(rating1);
        user.rate(rating2);
        user.rate(rating3);

        // then
        assertThat(user).extracting("rating", "reviewCnt").containsExactly(averageRating, 3);
    }

    private User createUser(String username,
                            String password,
                            Gender gender,
                            LocalDate birthdate,
                            String nickname) {

        Contact contact = Contact.builder()
                .email("test@gabojait.com")
                .verificationCode("000000")
                .build();

        return User.builder()
                .username(username)
                .password(password)
                .gender(gender)
                .birthdate(birthdate)
                .nickname(nickname)
                .contact(contact)
                .build();
    }
}