package com.gabojait.gabojaitspring.domain.notification;

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

    private static Stream<Arguments> providerEquals() {
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.F,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Notification notification = createNotification(user, NotificationType.NONE, "알림 제목", "알림이 왔습니다.");

        User user1 = createDefaultUser("tester1@gabojait.com", "000000", "tester1", "password1!", "테스터일", Gender.F,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Notification userNotification1 = createNotification(user1, NotificationType.NONE, "알림 제목", "알림이 왔습니다.");
        User user2 = createDefaultUser("tester2@gabojait.com", "000000", "tester2", "password1!", "테스터이", Gender.F,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Notification userNotification2 = createNotification(user2, NotificationType.NONE, "알림 제목", "알림이 왔습니다.");

        Notification isReadNotification1 = createNotification(user, NotificationType.NONE, "알림 제목", "알림이 왔습니다.");
        Notification isReadNotification2 = createNotification(user, NotificationType.NONE, "알림 제목", "알림이 왔습니다.");
        isReadNotification2.read();

        return Stream.of(
                Arguments.of(notification, notification, true),
                Arguments.of(notification, new Object(), false),
                Arguments.of(
                        createNotification(user, NotificationType.NONE, "알림 제목", "알림이 왔습니다."),
                        createNotification(user, NotificationType.NONE, "알림 제목", "알림이 왔습니다."),
                        true
                ),
                Arguments.of(
                        createNotification(user, NotificationType.NONE, "알림 제목", "알림이 왔습니다."),
                        createNotification(user, NotificationType.USER_OFFER, "알림 제목", "알림이 왔습니다."),
                        false
                ),
                Arguments.of(
                        createNotification(user, NotificationType.NONE, "알림 제목1", "알림이 왔습니다."),
                        createNotification(user, NotificationType.NONE, "알림 제목2", "알림이 왔습니다."),
                        false
                ),
                Arguments.of(
                        createNotification(user, NotificationType.NONE, "알림 제목", "알림이 왔습니다.1"),
                        createNotification(user, NotificationType.NONE, "알림 제목", "알림이 왔습니다.2"),
                        false
                ),
                Arguments.of(userNotification1, userNotification2, false),
                Arguments.of(isReadNotification1, isReadNotification2, false)
        );
    }

    @ParameterizedTest(name = "[{index}] 알림 객체를 비교한다.")
    @MethodSource("providerEquals")
    @DisplayName("알림 객체를 비교한다.")
    void givenProvider_whenEquals_thenReturn(Notification notification, Object object, boolean result) {
        // when & then
        assertThat(notification.equals(object)).isEqualTo(result);
    }

    private static Stream<Arguments> providerHashCode() {
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.F,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        return Stream.of(
                Arguments.of(
                        createNotification(user, NotificationType.NONE, "알림 제목", "알림이 왔습니다."),
                        createNotification(user, NotificationType.NONE, "알림 제목", "알림이 왔습니다."),
                        true
                ),
                Arguments.of(
                        createNotification(user, NotificationType.NONE, "알림 제목1", "알림이 왔습니다."),
                        createNotification(user, NotificationType.NONE, "알림 제목2", "알림이 왔습니다."),
                        false
                )
        );
    }

    @ParameterizedTest(name = "[{index}] 알림 해시코드를 비교한다.")
    @MethodSource("providerHashCode")
    @DisplayName("알림 해시코드를 비교한다.")
    void givenProvider_whenHashCode_thenReturn(Notification notification1, Notification notification2, boolean result) {
        // when
        int hashCode1 = notification1.hashCode();
        int hashCode2 = notification2.hashCode();

        // then
        assertThat(hashCode1 == hashCode2).isEqualTo(result);
    }

    private static Notification createNotification(User user, NotificationType notificationType, String title, String body) {
        return Notification.builder()
                .user(user)
                .notificationType(notificationType)
                .title(title)
                .body(body)
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