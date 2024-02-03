package com.gabojait.gabojaitspring.repository.notification;

import com.gabojait.gabojaitspring.common.dto.response.PageData;
import com.gabojait.gabojaitspring.domain.notification.Notification;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.gabojait.gabojaitspring.domain.notification.QNotification.notification;
import static com.gabojait.gabojaitspring.domain.user.QUser.user;

@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public PageData<List<Notification>> findPage(long userId, long pageFrom, int pageSize) {
        Long count = queryFactory
                .select(notification.id.count())
                .from(notification)
                .where(
                        notification.user.id.eq(userId)
                ).fetchOne();

        if (count == null || count == 0)
            return new PageData<>(List.of(), 0);

        List<Notification> notifications = queryFactory
                .select(notification)
                .from(notification)
                .leftJoin(notification.user, user)
                .where(
                        notification.id.lt(pageFrom),
                        notification.user.id.eq(userId)
                ).orderBy(notification.createdAt.desc())
                .limit(pageSize)
                .fetch();

        return new PageData<>(notifications, count);
    }

    @Override
    public Optional<Notification> findUnread(long userId, long notificationId) {
        return Optional.ofNullable(
                queryFactory
                        .select(notification)
                        .from(notification)
                        .leftJoin(notification.user, user)
                        .where(
                                notification.id.eq(notificationId),
                                notification.user.id.eq(userId),
                                notification.isRead.isFalse()
                        ).fetchFirst()
        );
    }
}
