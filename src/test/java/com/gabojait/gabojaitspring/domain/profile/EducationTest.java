package com.gabojait.gabojaitspring.domain.profile;

import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class EducationTest {

    @Test
    @DisplayName("학력을 생성한다.")
    void builder() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        String institutionName = "가보자잇대";
        LocalDate startedAt = LocalDate.of(2019, 3, 1);
        LocalDate endedAt = LocalDate.of(2023, 8, 1);
        boolean isCurrent = false;

        // when
        Education education = createEducation(institutionName, startedAt, endedAt, isCurrent, user);

        // then
        assertThat(education)
                .extracting("institutionName", "startedAt", "endedAt", "isCurrent")
                .containsExactly(institutionName, startedAt, endedAt, isCurrent);

    }

    @Test
    @DisplayName("학력을 업데이트한다.")
    void update() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        Education education = createEducation("가보자잇대", LocalDate.of(2019, 3, 1), LocalDate.of(2023, 8, 1), false,
                user);

        String institutionName = "앱센터대";
        LocalDate startedAt = LocalDate.of(2020, 3, 1);
        LocalDate endedAt = null;
        boolean isCurrent = false;

        // when
        education.update(institutionName, startedAt, endedAt, isCurrent);

        // then
        assertThat(education)
                .extracting("institutionName", "startedAt", "endedAt", "isCurrent")
                .containsExactly(institutionName, startedAt, endedAt, isCurrent);
    }

    @Test
    @DisplayName("같은 객체인 학력을 비교하면 동일하다.")
    void givenEqualInstance_whenEquals_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Education education = createEducation("가보자잇대", LocalDate.of(2019, 3, 1), LocalDate.of(2023, 8, 1), false,
                user);

        // when
        boolean result = education.equals(education);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("같은 정보인 학력을 비교하면 동일하다.")
    void givenEqualData_whenEqausl_thenReturn() {
        // given
        String institutionName = "가보자잇대";
        LocalDate startedAt = LocalDate.of(2019, 3, 1);
        LocalDate endedAt = LocalDate.of(2023, 8, 1);
        boolean isCurrent = false;
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Education education1 = createEducation(institutionName, startedAt, endedAt, isCurrent, user);
        Education education2 = createEducation(institutionName, startedAt, endedAt, isCurrent, user);

        // when
        boolean result = education1.equals(education2);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("다른 객체인 학력을 비교하면 동일하지 않다.")
    void givenUnequalInstance_whenEquals_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Education education = createEducation("가보자잇대", LocalDate.of(2019, 3, 1), LocalDate.of(2023, 8, 1), false,
                user);
        Object object = new Object();

        // when
        boolean result = education.equals(object);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 회원인 학력을 비교하면 동일하지 않다.")
    void givenUnequalUser_whenEquals_thenReturn() {
        // given
        String username1 = "tester1";
        String username2 = "tester2";

        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime now = LocalDateTime.now();
        User user1 = createDefaultUser(email, verificationCode, username1, password, nickname, gender, birthdate, now);
        User user2 = createDefaultUser(email, verificationCode, username2, password, nickname, gender, birthdate, now);

        String institutionName = "가보자잇대";
        LocalDate startedAt = LocalDate.of(2019, 3, 1);
        LocalDate endedAt = LocalDate.of(2023, 8, 1);
        boolean isCurrent = false;
        Education education1 = createEducation(institutionName, startedAt, endedAt, isCurrent, user1);
        Education education2 = createEducation(institutionName, startedAt, endedAt, isCurrent, user2);

        // when
        boolean result = education1.equals(education2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 학교명인 학력을 비교하면 동일하지 않다.")
    void givenUnequalInstitutionName_whenEquals_thenReturn() {
        // given
        String institutionName1 = "가보자잇대";
        String institutionName2 = "가볼까잇대";

        LocalDate startedAt = LocalDate.of(2019, 3, 1);
        LocalDate endedAt = LocalDate.of(2023, 8, 1);
        boolean isCurrent = false;
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Education education1 = createEducation(institutionName1, startedAt, endedAt, isCurrent, user);
        Education education2 = createEducation(institutionName2, startedAt, endedAt, isCurrent, user);

        // when
        boolean result = education1.equals(education2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 시작일인 학력을 비교하면 동일하지 않다.")
    void givenUnequalStartedAt_whenEquals_thenReturn() {
        // given
        LocalDate startedAt1 = LocalDate.of(2019, 3, 1);
        LocalDate startedAt2 = LocalDate.of(2019, 3, 2);

        String institutionName = "가보자잇대";
        LocalDate endedAt = LocalDate.of(2023, 8, 1);
        boolean isCurrent = false;
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Education education1 = createEducation(institutionName, startedAt1, endedAt, isCurrent, user);
        Education education2 = createEducation(institutionName, startedAt2, endedAt, isCurrent, user);

        // when
        boolean result = education1.equals(education2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 종료일인 학력을 비교하면 동일하지 않다.")
    void givenUnequalEndedAt_whenEquals_thenReturn() {
        // given
        LocalDate endedAt1 = LocalDate.of(2023, 8, 1);
        LocalDate endedAt2 = LocalDate.of(2023, 8, 2);

        String institutionName = "가보자잇대";
        LocalDate startedAt = LocalDate.of(2019, 3, 1);
        boolean isCurrent = false;
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Education education1 = createEducation(institutionName, startedAt, endedAt1, isCurrent, user);
        Education education2 = createEducation(institutionName, startedAt, endedAt2, isCurrent, user);

        // when
        boolean result = education1.equals(education2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 현재 여부인 학력을 비교하면 동일하지 않다.")
    void givenUnequalIsCurrent_whenEquals_thenReturn() {
        // given
        boolean isCurrent1 = false;
        boolean isCurrent2 = true;

        String institutionName = "가보자잇대";
        LocalDate startedAt = LocalDate.of(2019, 3, 1);
        LocalDate endedAt = LocalDate.of(2023, 8, 1);
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Education education1 = createEducation(institutionName, startedAt, endedAt, isCurrent1, user);
        Education education2 = createEducation(institutionName, startedAt, endedAt, isCurrent2, user);

        // when
        boolean result = education1.equals(education2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("동일한 학력의 해시코드는 같다.")
    void givenEqual_whenHashCode_thenReturn() {
        // given
        String institutionName = "가보자잇대";
        LocalDate startedAt = LocalDate.of(2019, 3, 1);
        LocalDate endedAt = LocalDate.of(2023, 8, 1);
        boolean isCurrent = false;
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Education education1 = createEducation(institutionName, startedAt, endedAt, isCurrent, user);
        Education education2 = createEducation(institutionName, startedAt, endedAt, isCurrent, user);

        // when
        int hashCode1 = education1.hashCode();
        int hashCode2 = education2.hashCode();

        // then
        assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    @DisplayName("동일하지 않은 학력의 해시코드는 다르다.")
    void givenUnequal_whenHashCode_thenReturn() {
        // given
        String institutionName1 = "가보자잇대";
        String institutionName2 = "가볼까잇대";

        LocalDate startedAt = LocalDate.of(2019, 3, 1);
        LocalDate endedAt = LocalDate.of(2023, 8, 1);
        boolean isCurrent = false;
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Education education1 = createEducation(institutionName1, startedAt, endedAt, isCurrent, user);
        Education education2 = createEducation(institutionName2, startedAt, endedAt, isCurrent, user);

        // when
        int hashCode1 = education1.hashCode();
        int hashCode2 = education2.hashCode();

        // then
        assertThat(hashCode1).isNotEqualTo(hashCode2);
    }

    private Education createEducation(String institutionName,
                                      LocalDate startedAt,
                                      LocalDate endedAt,
                                      boolean isCurrent,
                                      User user) {
        return Education.builder()
                .institutionName(institutionName)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .isCurrent(isCurrent)
                .user(user)
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