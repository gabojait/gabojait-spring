package com.gabojait.gabojaitspring.domain.report;

import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class SuspendTest {

    @Test
    @DisplayName("정지 생성이 정상 작동한다")
    void givenValid_whenBuilder_thenReturn() {
        // given
        User admin = createDefaultUser("tester1", "테스터일", LocalDate.of(1997, 2, 11), LocalDateTime.now());
        User user = createDefaultUser("tester2", "테스터이", LocalDate.of(1997, 2, 11), LocalDateTime.now());
        User reporter = createDefaultUser("tester3", "테스터삼", LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Report report = createReport(user, reporter);

        String reason = "정지 사유입니다.";
        LocalDate startAt = LocalDate.of(2000, 1, 1);
        LocalDate endAt = LocalDate.now().plusDays(1);

        // when
        Suspend suspend = createSuspend(reason, startAt, endAt, user, admin, report);

        // then
        assertThat(suspend)
                .extracting("reason", "startAt", "endAt", "user", "admin", "report")
                .containsExactly(reason, startAt, endAt, user, admin, report);
    }

    private static Suspend createSuspend(String reason,
                                         LocalDate startAt,
                                         LocalDate endAt,
                                         User user,
                                         User admin,
                                         Report report) {
        return Suspend.builder()
                .reason(reason)
                .startAt(startAt)
                .endAt(endAt)
                .user(user)
                .admin(admin)
                .report(report)
                .build();
    }

    private static Report createReport(User user, User reporter) {
        return Report.builder()
                .reason("신고 내용입니다.")
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
