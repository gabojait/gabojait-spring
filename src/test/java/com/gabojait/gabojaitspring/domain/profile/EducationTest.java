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
        String institutionName = "가보자잇사";
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

        Education education = createEducation("가보자잇사", LocalDate.of(2019, 3, 1), LocalDate.of(2023, 8, 1), false,
                user);

        String institutionName = "앱센터사";
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