package com.gabojait.gabojaitspring.repository.notification;

import com.gabojait.gabojaitspring.domain.notification.Notification;
import com.gabojait.gabojaitspring.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationCustomRepository {

    List<Notification> findAllByUser(User user);
}
