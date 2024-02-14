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
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationTest {

    @Test
    @DisplayName("알림 생성이 정상 작동한다")
    void builder() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "tester", "테스터",
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        String title = "알림 제목";
        String body = "알림이 왔습니다.";

        // when
        Notification notification = createNotification(user, title, body, DeepLinkType.HOME_PAGE);

        // then
        assertThat(notification)
                .extracting("title", "body", "isRead", "deepLinkType", "isDeleted", "user")
                .containsExactly(title, body, false, DeepLinkType.HOME_PAGE, false, user);
    }

    @Test
    @DisplayName("알림 읽기가 정상 작동한다")
    void read() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "tester", "테스터",
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Notification notification = createNotification(user, "알림 제목", "알림이 왔습니다.", DeepLinkType.HOME_PAGE);

        // when
        notification.read();

        // then
        assertTrue(notification.getIsRead());
    }

    private static Stream<Arguments> providerEquals() {
        User user = createDefaultUser("tester@gabojait.com", "tester", "테스터",
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Notification notification = createNotification(user, "알림 제목", "알림이 왔습니다.", DeepLinkType.HOME_PAGE);

        User user1 = createDefaultUser("tester1@gabojait.com", "tester1", "테스터일",
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Notification userNotification1 = createNotification(user1, "알림 제목", "알림이 왔습니다.", DeepLinkType.HOME_PAGE);
        User user2 = createDefaultUser("tester2@gabojait.com", "tester2", "테스터이",
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Notification userNotification2 = createNotification(user2, "알림 제목", "알림이 왔습니다.", DeepLinkType.HOME_PAGE);

        Notification isReadNotification1 = createNotification(user, "알림 제목", "알림이 왔습니다.", DeepLinkType.HOME_PAGE);
        Notification isReadNotification2 = createNotification(user, "알림 제목", "알림이 왔습니다.", DeepLinkType.HOME_PAGE);
        isReadNotification2.read();

        return Stream.of(
                Arguments.of(notification, notification, true),
                Arguments.of(notification, new Object(), false),
                Arguments.of(
                        createNotification(user, "알림 제목", "알림이 왔습니다.", DeepLinkType.HOME_PAGE),
                        createNotification(user, "알림 제목", "알림이 왔습니다.", DeepLinkType.HOME_PAGE),
                        true
                ),
                Arguments.of(
                        createNotification(user, "알림 제목", "알림이 왔습니다.", DeepLinkType.HOME_PAGE),
                        createNotification(user, "알림 제목", "알림이 왔습니다.", DeepLinkType.TEAM_PAGE),
                        false
                ),
                Arguments.of(
                        createNotification(user, "알림 제목1", "알림이 왔습니다.", DeepLinkType.HOME_PAGE),
                        createNotification(user, "알림 제목2", "알림이 왔습니다.", DeepLinkType.HOME_PAGE),
                        false
                ),
                Arguments.of(
                        createNotification(user, "알림 제목", "알림이 왔습니다.1", DeepLinkType.HOME_PAGE),
                        createNotification(user, "알림 제목", "알림이 왔습니다.2", DeepLinkType.HOME_PAGE),
                        false
                ),
                Arguments.of(userNotification1, userNotification2, false),
                Arguments.of(isReadNotification1, isReadNotification2, false)
        );
    }

    @ParameterizedTest(name = "[{index}] 알림 객체를 비교한다")
    @MethodSource("providerEquals")
    @DisplayName("알림 객체를 비교한다")
    void givenProvider_whenEquals_thenReturn(Notification notification, Object object, boolean result) {
        // when & then
        assertThat(notification.equals(object)).isEqualTo(result);
    }

    private static Stream<Arguments> providerHashCode() {
        User user = createDefaultUser("tester@gabojait.com", "tester", "테스터",
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        return Stream.of(
                Arguments.of(
                        createNotification(user, "알림 제목", "알림이 왔습니다.", DeepLinkType.HOME_PAGE),
                        createNotification(user, "알림 제목", "알림이 왔습니다.", DeepLinkType.HOME_PAGE),
                        true
                ),
                Arguments.of(
                        createNotification(user, "알림 제목1", "알림이 왔습니다.", DeepLinkType.HOME_PAGE),
                        createNotification(user, "알림 제목2", "알림이 왔습니다.", DeepLinkType.HOME_PAGE),
                        false
                )
        );
    }

    @ParameterizedTest(name = "[{index}] 알림 해시코드를 비교한다")
    @MethodSource("providerHashCode")
    @DisplayName("알림 해시코드를 비교한다")
    void givenProvider_whenHashCode_thenReturn(Notification notification1, Notification notification2, boolean result) {
        // when
        int hashCode1 = notification1.hashCode();
        int hashCode2 = notification2.hashCode();

        // then
        assertThat(hashCode1 == hashCode2).isEqualTo(result);
    }

    private static Notification createNotification(User user, String title, String body, DeepLinkType deepLinkType) {
        return Notification.builder()
                .user(user)
                .title(title)
                .body(body)
                .deepLinkType(deepLinkType)
                .build();

    }

    private static User createDefaultUser(String email,
                                          String username,
                                          String nickname,
                                          LocalDate birthdate,
                                          LocalDateTime lastRequestAt) {
        Contact contact = Contact.builder()
                .email(email)
                .verificationCode("000000")
                .build();
        contact.verified();

        return User.builder()
                .username(username)
                .password("password1!")
                .nickname(nickname)
                .gender(Gender.F)
                .birthdate(birthdate)
                .lastRequestAt(lastRequestAt)
                .contact(contact)
                .build();
    }
}