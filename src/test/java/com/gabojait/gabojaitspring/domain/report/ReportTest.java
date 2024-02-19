package com.gabojait.gabojaitspring.domain.report;

import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class ReportTest {

    @Test
    @DisplayName("신고 생성이 정상 작동한다")
    void givenValid_whenBuilder_thenReturn() {
        // given
        User user = createDefaultUser("tester1", "테스터일", LocalDate.of(1997, 2, 11), LocalDateTime.now());
        User reporter = createDefaultUser("tester2", "테스터이", LocalDate.of(1997, 2, 11), LocalDateTime.now());

        String reason = "신고 내용입니다.";

        // when
        Report report = createReport(reason, user, reporter);

        // then
        assertThat(report)
                .extracting("reason", "user", "reporter")
                .containsExactly(reason, user, reporter);
    }

    private static Report createReport(String reason, User user, User reporter) {
        return Report.builder()
                .reason(reason)
                .user(user)
                .reporter(reporter)
                .build();
    }

    private static User createDefaultUser(String username,
                                          String nickname,
                                          LocalDate birthdate,
                                          LocalDateTime lastRequestAt) {
        Contact contact = Contact.builder()
                .email("tester@gabojait.com")
                .verificationCode("000000")
                .build();
        contact.verified();

        return User.builder()
                .username(username)
                .password("password1!")
                .nickname(nickname)
                .gender(Gender.M)
                .birthdate(birthdate)
                .lastRequestAt(lastRequestAt)
                .contact(contact)
                .build();
    }
}
