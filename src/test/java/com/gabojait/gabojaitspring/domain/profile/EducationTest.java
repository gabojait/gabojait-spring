package com.gabojait.gabojaitspring.domain.profile;

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

    private static Stream<Arguments> providerEquals() {
        LocalDateTime now = LocalDateTime.now();

        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), now);
        Education education = createEducation("가보자잇대", LocalDate.of(2019, 3, 1), LocalDate.of(2023, 8, 1), false,
                user);

        User user1 = createDefaultUser("tester@gabojait.com", "000000", "tester1", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), now);
        User user2 = createDefaultUser("tester@gabojait.com", "000000", "tester2", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), now);
        Education userEducation1 = createEducation("가보자잇대", LocalDate.of(2019, 3, 1), LocalDate.of(2023, 8, 1), false,
                user1);
        Education userEducation2 = createEducation("가보자잇대", LocalDate.of(2019, 3, 1), LocalDate.of(2023, 8, 1), false,
                user2);

        return Stream.of(
                Arguments.of(education, education, true),
                Arguments.of(education, new Object(), false),
                Arguments.of(
                        createEducation("가보자잇대", LocalDate.of(2019, 3, 1), LocalDate.of(2023, 8, 1), false, user),
                        createEducation("가보자잇대", LocalDate.of(2019, 3, 1), LocalDate.of(2023, 8, 1), false, user),
                        true
                ),
                Arguments.of(userEducation1, userEducation2, false),
                Arguments.of(
                        createEducation("가보자잇대1", LocalDate.of(2019, 3, 1), LocalDate.of(2023, 8, 1), false, user),
                        createEducation("가보자잇대2", LocalDate.of(2019, 3, 1), LocalDate.of(2023, 8, 1), false, user),
                        false
                ),
                Arguments.of(
                        createEducation("가보자잇대", LocalDate.of(2019, 3, 1), LocalDate.of(2023, 8, 1), false, user),
                        createEducation("가보자잇대", LocalDate.of(2019, 3, 2), LocalDate.of(2023, 8, 1), false, user),
                        false
                ),
                Arguments.of(
                        createEducation("가보자잇대", LocalDate.of(2019, 3, 1), LocalDate.of(2023, 8, 1), false, user),
                        createEducation("가보자잇대", LocalDate.of(2019, 3, 1), LocalDate.of(2023, 8, 2), false, user),
                        false
                ),
                Arguments.of(
                        createEducation("가보자잇대", LocalDate.of(2019, 3, 1), LocalDate.of(2023, 8, 1), false, user),
                        createEducation("가보자잇대", LocalDate.of(2019, 3, 1), LocalDate.of(2023, 8, 1), true, user),
                        false
                )
        );
    }

    @ParameterizedTest(name = "[{index}] 학력 객체를 비교한다.")
    @MethodSource("providerEquals")
    @DisplayName("학력 객체를 비교한다.")
    void givenProvider_whenEquals_thenReturn(Education education, Object object, boolean result) {
        // when & then
        assertThat(education.equals(object)).isEqualTo(result);
    }

    private static Stream<Arguments> providerHashCode() {
        LocalDateTime now = LocalDateTime.now();

        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), now);

        return Stream.of(
                Arguments.of(
                        createEducation("가보자잇대", LocalDate.of(2019, 3, 1), LocalDate.of(2023, 8, 1), false, user),
                        createEducation("가보자잇대", LocalDate.of(2019, 3, 1), LocalDate.of(2023, 8, 1), false, user),
                        true
                ),
                Arguments.of(
                        createEducation("가보자잇대1", LocalDate.of(2019, 3, 1), LocalDate.of(2023, 8, 1), false, user),
                        createEducation("가보자잇대2", LocalDate.of(2019, 3, 1), LocalDate.of(2023, 8, 1), false, user),
                        false
                )
        );
    }

    @ParameterizedTest(name = "[{index}] 학력 해시코드를 비교한다.")
    @MethodSource("providerHashCode")
    @DisplayName("학력 해시코드를 비교한다.")
    void givenProvider_whenHashCode_thenReturn(Education education1, Education education2, boolean result) {
        // when
        int hashCode1 = education1.hashCode();
        int hashCode2 = education2.hashCode();

        // then
        assertThat(hashCode1 == hashCode2).isEqualTo(result);
    }

    private static Education createEducation(String institutionName,
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