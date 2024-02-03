package com.gabojait.gabojaitspring.repository.notification;

import com.gabojait.gabojaitspring.common.response.PageData;
import com.gabojait.gabojaitspring.domain.notification.Notification;
import com.gabojait.gabojaitspring.domain.notification.NotificationType;
import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.User;
import com.gabojait.gabojaitspring.repository.user.ContactRepository;
import com.gabojait.gabojaitspring.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class NotificationRepositoryTest {

    @Autowired private NotificationRepository notificationRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ContactRepository contactRepository;

    @Test
    @DisplayName("알림이 있을시 알림 페이징 조회가 정상 작동한다")
    void givenValid_whenFindPage_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Notification notification1 = createNotification(user, NotificationType.NONE, "알림 제목1", "알림1이 왔습니다.");
        Notification notification2 = createNotification(user, NotificationType.USER_OFFER, "알림 제목2", "알림2이 왔습니다.");
        Notification notification3 = createNotification(user, NotificationType.TEAM_OFFER, "알림 제목3", "알림3이 왔습니다.");
        notificationRepository.saveAll(List.of(notification1, notification2, notification3));

        long pageFrom = notification3.getId();
        int pageSize = 1;

        // when
        PageData<List<Notification>> notifications = notificationRepository.findPage(user.getId(), pageFrom, pageSize);

        // then
        assertAll(
                () -> assertThat(notifications.getData()).containsExactly(notification2),
                () -> assertThat(notifications.getData().size()).isEqualTo(pageSize),
                () -> assertThat(notifications.getTotal()).isEqualTo(3L)
        );
    }

    @Test
    @DisplayName("알림이 없을시 알림 페이징 조회가 정상 작동한다")
    void givenNoneExisting_whenFindPage_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        long pageFrom = Long.MAX_VALUE;
        int pageSize = 1;

        // when
        PageData<List<Notification>> notifications = notificationRepository.findPage(user.getId(), pageFrom, pageSize);

        // then
        assertAll(
                () -> assertThat(notifications.getData()).isEmpty(),
                () -> assertThat(notifications.getTotal()).isEqualTo(0L)
        );
    }

    @Test
    @DisplayName("읽지 않은 알림을 조회가 정상 작동한다")
    void givenRead_whenFindUnread_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Notification notification = createNotification(user, NotificationType.NONE, "알림 제목", "알림이 왔습니다.");
        notificationRepository.save(notification);

        // when
        Notification foundNotification = notificationRepository.findUnread(user.getId(), notification.getId()).get();

        // then
        assertThat(foundNotification).isEqualTo(notification);
    }

    @Test
    @DisplayName("알림은 읽은 후 읽은 알림을 조회가 정상 작동한다")
    void givenUnread_whenFindUnread_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Notification notification = createNotification(user, NotificationType.NONE, "알림 제목", "알림이 왔습니다.");
        notification.read();
        notificationRepository.save(notification);

        // when
        Optional<Notification> foundNotification = notificationRepository.findUnread(user.getId(), notification.getId());

        // then
        assertThat(foundNotification).isEmpty();
    }

    private Notification createNotification(User user, NotificationType notificationType, String title, String body) {
        return Notification.builder()
                .user(user)
                .notificationType(notificationType)
                .title(title)
                .body(body)
                .build();
    }

    private User createSavedDefaultUser(String email, String username, String nickname) {
        Contact contact = Contact.builder()
                .email(email)
                .verificationCode("000000")
                .build();
        contact.verified();
        contactRepository.save(contact);

        User user = User.builder()
                .username(username)
                .password("password1!")
                .nickname(nickname)
                .gender(Gender.M)
                .birthdate(LocalDate.of(1997, 2, 11))
                .lastRequestAt(LocalDateTime.now())
                .contact(contact)
                .build();

        return userRepository.save(user);
    }
}