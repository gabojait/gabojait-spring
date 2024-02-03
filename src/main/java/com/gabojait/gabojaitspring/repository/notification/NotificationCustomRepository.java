package com.gabojait.gabojaitspring.repository.notification;

import com.gabojait.gabojaitspring.common.dto.response.PageData;
import com.gabojait.gabojaitspring.domain.notification.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationCustomRepository {

    PageData<List<Notification>> findPage(long userId, long pageFrom, int pageSize);

    Optional<Notification> findUnread(long userId, long notificationId);
}
