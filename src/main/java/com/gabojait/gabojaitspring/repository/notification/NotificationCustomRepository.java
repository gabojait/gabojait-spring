package com.gabojait.gabojaitspring.repository.notification;

import com.gabojait.gabojaitspring.domain.notification.Notification;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface NotificationCustomRepository {

    Page<Notification> findPage(long userId, long pageFrom, int pageSize);

    Optional<Notification> findUnread(long userId, long notificationId);
}
