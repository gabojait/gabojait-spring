package com.gabojait.gabojaitspring.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

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
    void givenNonTemporaryPassword_whenUpdatePassword_thenReturn() {
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
    void givenTemporaryPassword_whenUpdatePassword_thenReturn() {
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
    @DisplayName("참으로 알림 여부를 업데이트한다.")
    void givenTrue_whenUpdateIsNotified_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        // when
        user.updateIsNotified(true);

        // then
        assertThat(user.getIsNotified()).isTrue();
    }

    @Test
    @DisplayName("거짓으로 알림 여부를 업데이트한다.")
    void givenFalse_whenUpdateIsNotified_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        // when
        user.updateIsNotified(false);

        // then
        assertThat(user.getIsNotified()).isFalse();
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
    @DisplayName("디자이너로 포지션을 업데이트한다.")
    void givenDesigner_whenUpdatePosition_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        Position position = Position.DESIGNER;

        // when
        user.updatePosition(position);

        // then
        assertThat(user.getPosition()).isEqualTo(position);
    }

    @Test
    @DisplayName("백엔드 개발자로 포지션을 업데이트한다.")
    void givenBackend_whenUpdatePosition_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        Position position = Position.BACKEND;

        // when
        user.updatePosition(position);

        // then
        assertThat(user.getPosition()).isEqualTo(position);
    }

    @Test
    @DisplayName("프런트엔드 개발자로 포지션을 업데이트한다.")
    void givenFrontend_whenUpdatePosition_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        Position position = Position.FRONTEND;

        // when
        user.updatePosition(position);

        // then
        assertThat(user.getPosition()).isEqualTo(position);
    }

    @Test
    @DisplayName("프로젝트 매니저로 포지션을 업데이트한다.")
    void givenManager_whenUpdatePosition_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        Position position = Position.MANAGER;

        // when
        user.updatePosition(position);

        // then
        assertThat(user.getPosition()).isEqualTo(position);
    }

    @Test
    @DisplayName("선택 안함으로 포지션을 업데이트한다.")
    void givenNone_whenUpdatePosition_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        Position position = Position.NONE;

        // when
        user.updatePosition(position);

        // then
        assertThat(user.getPosition()).isEqualTo(position);
    }

    @Test
    @DisplayName("포지션이 없는 경우 존재 여부를 확인한다.")
    void givenNoPosition_whenHasPosition_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        // when
        boolean result = user.hasPosition();

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("포지션이 있는 경우 존재 여부를 확인한다.")
    void givenExistingPosition_whenHasPosition_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        user.updatePosition(Position.BACKEND);

        // when
        boolean result = user.hasPosition();

        // then
        assertThat(result).isTrue();
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
        assertThat(user.getProfileDescription()).isEqualTo(profileDescription);
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
        assertThat(user.getImageUrl()).isEqualTo(imageUrl);
    }

    @Test
    @DisplayName("참으로 팀 찾기 여부를 업데이트한다.")
    void givenTrue_whenUpdateIsSeekingTeam_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        // when
        user.updateIsSeekingTeam(true);

        // then
        assertThat(user.getIsSeekingTeam()).isTrue();
    }

    @Test
    @DisplayName("거짓으로 팀 찾기 여부를 업데이트한다.")
    void givenFalse_whenUpdateIsSeekingTeam_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        // when
        user.updateIsSeekingTeam(false);

        // then
        assertThat(user.getIsSeekingTeam()).isFalse();
    }

    @Test
    @DisplayName("회원 프로필을 방문한다.")
    void visit() {
        // given
        User user = createDefaultUser("test@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        // when
        user.visit();

        // then
        assertThat(user.getVisitedCnt()).isEqualTo(1L);
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
        assertThat(user.getRating()).isEqualTo(averageRating);
    }

    @Test
    @DisplayName("같은 객체인 회원을 비교하면 동일하다.")
    void givenEqualInstance_whenEquals_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        // when
        boolean result = user.equals(user);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("같은 정보인 회원을 비교하면 동일하다.")
    void givenEqualData_whenEquals_thenReturn() {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.N;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime now = LocalDateTime.now();
        User user1 = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate, now);
        User user2 = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate, now);

        // when
        boolean result1 = user1.equals(user2);

        // then
        assertThat(result1).isTrue();
    }

    @Test
    @DisplayName("다른 객체로 회원을 비교하면 동일하지 않다.")
    void givenUnequalInstance_whenEquals_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Object object = new Object();

        // when
        boolean result = user.equals(object);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 연락처인 회원을 비교하면 동일하지 않다.")
    void givenUnequalContact_whenEquals_thenReturn() {
        // given
        String email1 = "tester1@gabojait.com";
        String email2 = "tester2@gabojait.com";

        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.N;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime now = LocalDateTime.now();
        User user1 = createDefaultUser(email1, verificationCode, username, password, nickname, gender, birthdate, now);
        User user2 = createDefaultUser(email2, verificationCode, username, password, nickname, gender, birthdate, now);

        // when
        boolean result = user1.equals(user2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 아이디인 회원을 비교하면 동일하지 않다.")
    void givenUnequalUsername_whenEquals_thenReturn() {
        // given
        String username1 = "tester1";
        String username2 = "tester2";

        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.N;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime now = LocalDateTime.now();
        User user1 = createDefaultUser(email, verificationCode, username1, password, nickname, gender, birthdate, now);
        User user2 = createDefaultUser(email, verificationCode, username2, password, nickname, gender, birthdate, now);

        // when
        boolean result = user1.equals(user2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 비밀번호인 회원을 비교하면 동일하지 않다.")
    void givenUnequalPassword_whenEquals_thenReturn() {
        // given
        String password1 = "password1!";
        String password2 = "password2!";

        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String nickname = "테스터";
        Gender gender = Gender.N;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime now = LocalDateTime.now();
        User user1 = createDefaultUser(email, verificationCode, username, password1, nickname, gender, birthdate, now);
        User user2 = createDefaultUser(email, verificationCode, username, password2, nickname, gender, birthdate, now);

        // when
        boolean result = user1.equals(user2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 닉네임인 회원을 비교하면 동일하지 않다.")
    void givenUnequalNickname_whenEquals_thenReturn() {
        // given
        String nickname1 = "테스터일";
        String nickname2 = "테스터이";

        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        Gender gender = Gender.N;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime now = LocalDateTime.now();
        User user1 = createDefaultUser(email, verificationCode, username, password, nickname1, gender, birthdate, now);
        User user2 = createDefaultUser(email, verificationCode, username, password, nickname2, gender, birthdate, now);

        // when
        boolean result = user1.equals(user2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 프로필 설명인 회원을 비교하면 동일하지 않다.")
    void givenUnequalProfileDescription_whenEquals_thenReturn() {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.N;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime now = LocalDateTime.now();
        User user1 = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate, now);
        User user2 = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate, now);

        user1.updateProfileDescription("안녕하세요1");
        user2.updateProfileDescription("안녕하세요2");

        // when
        boolean result = user1.equals(user2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 프로필 사진인 회원을 비교하면 동일하지 않다.")
    void givenUnequalImageUrl_whenEquals_thenReturn() {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.N;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime now = LocalDateTime.now();
        User user1 = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate, now);
        User user2 = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate, now);

        user1.updateImageUrl("gabojait1");
        user2.updateProfileDescription("gabojait2");

        // when
        boolean result = user1.equals(user2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 생일인 회원을 비교하면 동일하지 않다.")
    void givenUnequalBirthdate_whenEquals_thenReturn() {
        // given
        LocalDate birthdate1 = LocalDate.of(1997, 2, 11);
        LocalDate birthdate2 = LocalDate.of(1997, 2, 12);

        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.N;
        LocalDateTime now = LocalDateTime.now();
        User user1 = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate1, now);
        User user2 = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate2, now);

        // when
        boolean result = user1.equals(user2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 마지막 요청일인 회원을 비교하면 동일하지 않다.")
    void givenUnequalLastRequestAt_whenEquals_thenReturn() {
        // given
        LocalDateTime now1 = LocalDateTime.now();
        LocalDateTime now2 = now1.plusNanos(1);

        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.N;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        User user1 = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate, now1);
        User user2 = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate, now2);

        // when
        boolean result = user1.equals(user2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 성별인 회원을 비교하면 동일하지 않다.")
    void givenUnequalGender_whenEquals_thenReturn() {
        // given
        Gender gender1 = Gender.M;
        Gender gender2 = Gender.N;

        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime now = LocalDateTime.now();
        User user1 = createDefaultUser(email, verificationCode, username, password, nickname, gender1, birthdate, now);
        User user2 = createDefaultUser(email, verificationCode, username, password, nickname, gender2, birthdate, now);

        // when
        boolean result = user1.equals(user2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 포지션인 회원을 비교하면 동일하지 않다.")
    void givenUnequalPosition_whenEquals_thenReturn() {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.N;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime now = LocalDateTime.now();
        User user1 = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate, now);
        User user2 = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate, now);

        user1.updatePosition(Position.MANAGER);
        user2.updatePosition(Position.BACKEND);

        // when
        boolean result = user1.equals(user2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 방문 수인 회원을 비교하면 동일하지 않다.")
    void givenUnequalVisitedCnt_whenEquals_thenReturn() {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.N;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime now = LocalDateTime.now();
        User user1 = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate, now);
        User user2 = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate, now);

        user1.visit();

        // when
        boolean result = user1.equals(user2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 평점인 회원을 비교하면 동일하지 않다.")
    void givenUnequalRating_whenEquals_thenReturn() {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.N;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime now = LocalDateTime.now();
        User user1 = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate, now);
        User user2 = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate, now);

        user1.rate(1F);
        user2.rate(2F);

        // when
        boolean result = user1.equals(user2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 팀 찾기 여부인 회원을 비교하면 동일하지 않다.")
    void givenUnequalIsSeekingTeam_whenEquals_thenReturn() {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.N;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime now = LocalDateTime.now();
        User user1 = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate, now);
        User user2 = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate, now);

        user1.updateIsSeekingTeam(false);

        // when
        boolean result = user1.equals(user2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 임시 비밀번호 여부인 회원을 비교하면 동일하지 않다.")
    void givenUnequalIsTemporaryPassword_whenEquals_thenReturn() {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.N;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime now = LocalDateTime.now();
        User user1 = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate, now);
        User user2 = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate, now);

        user1.updatePassword(password, true);

        // when
        boolean result = user1.equals(user2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 알림 여부인 회원을 비교하면 동일하지 않다.")
    void givenUnequalIsNotified_whenEquals_thenReturn() {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.N;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime now = LocalDateTime.now();
        User user1 = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate, now);
        User user2 = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate, now);

        user1.updateIsNotified(false);

        // when
        boolean result = user1.equals(user2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("동일한 회원의 해시코드는 같다.")
    void givenEqual_whenHashCode_thenReturn() {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.N;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime now = LocalDateTime.now();
        User user1 = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate, now);
        User user2 = createDefaultUser(email, verificationCode, username, password, nickname, gender, birthdate, now);

        // when
        int hashCode1 = user1.hashCode();
        int hashCode2 = user2.hashCode();

        // then
        assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    @DisplayName("동일하지 않은 회원의 해시코드는 다르다.")
    void givenUnequal_whenHashCode_thenReturn() {
        // given
        String username1 = "tester1";
        String username2 = "tester2";

        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.N;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime now = LocalDateTime.now();
        User user1 = createDefaultUser(email, verificationCode, username1, password, nickname, gender, birthdate, now);
        User user2 = createDefaultUser(email, verificationCode, username2, password, nickname, gender, birthdate, now);

        // when
        int hashCode1 = user1.hashCode();
        int hashCode2 = user2.hashCode();

        // then
        assertThat(hashCode1).isNotEqualTo(hashCode2);
    }

    @Test
    @DisplayName("회원의 아이디를 반환한다.")
    void getUsername() {
        // given
        String username = "tester";
        User user = createDefaultUser("tester@gabojait.com", "000000", username, "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        // when
        String gotUsername = user.getUsername();

        // then
        assertThat(gotUsername).isEqualTo(username);
    }

    @Test
    @DisplayName("회원의 비밀번호를 반환한다.")
    void getPassword() {
        // given
        String password = "password1!";
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", password, "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        // when
        String gotPassword = user.getPassword();

        // when & then
        assertThat(gotPassword).isEqualTo(password);
    }

    @Test
    @DisplayName("회원의 권한들을 반환한다.")
    void getAuthorities() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        // when
        Collection<? extends GrantedAuthority> result = user.getAuthorities();

        // then
        assertThat(result).isEqualTo(null);
    }

    @Test
    @DisplayName("회원의 계정 만료 여부를 반환한다.")
    void isAccountNonExpired() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        // when
        boolean result = user.isAccountNonExpired();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("회원의 계정 잠금 여부를 반환한다.")
    void isAccountNonLocked() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        // when
        boolean result = user.isAccountNonLocked();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("회원의 계정 자격 만료 여부를 반환한다.")
    void isCredentialsNonExpired() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        // when
        boolean result = user.isCredentialsNonExpired();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("회원의 가능 여부를 반환한다.")
    void isEnabled() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        // when
        boolean result = user.isEnabled();

        // then
        assertThat(result).isTrue();
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