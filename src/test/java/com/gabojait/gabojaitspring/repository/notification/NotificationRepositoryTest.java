package com.gabojait.gabojaitspring.repository.notification;

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
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.not;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class NotificationRepositoryTest {

    @Autowired private NotificationRepository notificationRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ContactRepository contactRepository;

    @Test
    @DisplayName("알림 페이징 조회를 한다.")
    void findPage() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Notification notification1 = createNotification(user, NotificationType.NONE, "알림 제목1", "알림1이 왔습니다.");
        Notification notification2 = createNotification(user, NotificationType.USER_OFFER, "알림 제목2", "알림2이 왔습니다.");
        Notification notification3 = createNotification(user, NotificationType.TEAM_OFFER, "알림 제목3", "알림3이 왔습니다.");
        notificationRepository.saveAll(List.of(notification1, notification2, notification3));

        long pageFrom = notification3.getId();
        int pageSize = 1;

        // when
        Page<Notification> notifications = notificationRepository.findPage(user.getUsername(), pageFrom, pageSize);

        // then
        assertThat(notifications)
                .extracting("id", "notificationType", "title", "body", "isRead", "isDeleted", "createdAt", "updatedAt")
                .containsExactly(
                        tuple(notification2.getId(), notification2.getNotificationType(), notification2.getTitle(),
                                notification2.getBody(), notification2.getIsRead(), notification2.getIsDeleted(),
                                notification2.getCreatedAt(), notification2.getUpdatedAt())
                );

        assertEquals(pageSize, notifications.getSize());
        assertEquals(3L, notifications.getTotalElements());
    }

    @Test
    @DisplayName("읽지 않은 알림을 조회한다.")
    void givenUnread_whenFindUnread_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Notification notification = createNotification(user, NotificationType.NONE, "알림 제목", "알림이 왔습니다.");
        notificationRepository.save(notification);

        // when
        Optional<Notification> foundNotification = notificationRepository.findUnread(user.getUsername(), notification.getId());

        // then
        assertThat(foundNotification.get())
                .extracting("id", "notificationType", "title", "body", "isRead", "isDeleted", "createdAt", "updatedAt")
                .containsExactly(notification.getId(), notification.getNotificationType(), notification.getTitle(),
                        notification.getBody(), notification.getIsRead(), notification.getIsDeleted(),
                        notification.getCreatedAt(), notification.getUpdatedAt());
    }

    @Test
    @DisplayName("알림은 읽은 후 읽은 알림을 조회한다.")
    void givenRead_whenFindUnread_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Notification notification = createNotification(user, NotificationType.NONE, "알림 제목", "알림이 왔습니다.");
        notification.read();
        notificationRepository.save(notification);

        // when
        Optional<Notification> foundNotification = notificationRepository.findUnread(user.getUsername(), notification.getId());

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