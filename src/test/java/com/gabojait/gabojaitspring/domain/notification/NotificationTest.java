package com.gabojait.gabojaitspring.domain.notification;

import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Test
    @DisplayName("알림을 생성한다.")
    void builder() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.F,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        NotificationType notificationType = NotificationType.NONE;
        String title = "알림 제목";
        String body = "알림이 왔습니다.";

        // when
        Notification notification = createNotification(user, notificationType, title, body);

        // then
        assertThat(notification)
                .extracting("notificationType", "title", "body", "isRead", "isDeleted")
                .containsExactly(notificationType, title, body, false, false);
    }

    @Test
    @DisplayName("알림을 읽는다.")
    void read() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.F,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Notification notification = createNotification(user, NotificationType.TEAM_OFFER, "알림 제목", "알림이 왔습니다.");

        // when
        notification.read();

        // then
        assertTrue(notification.getIsRead());
    }

    private Notification createNotification(User user, NotificationType notificationType, String title, String body) {
        return Notification.builder()
                .user(user)
                .notificationType(notificationType)
                .title(title)
                .body(body)
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