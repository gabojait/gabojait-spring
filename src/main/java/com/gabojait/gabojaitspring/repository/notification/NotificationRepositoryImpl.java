package com.gabojait.gabojaitspring.repository.notification;

import com.gabojait.gabojaitspring.domain.notification.Notification;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.gabojait.gabojaitspring.domain.notification.QNotification.notification;
import static com.gabojait.gabojaitspring.domain.user.QUser.user;

@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Notification> findPage(String username, long pageFrom, int pageSize) {
        Long count = queryFactory
                .select(notification.id.count())
                .from(notification)
                .where(
                        notification.user.username.eq(username)
                ).fetchOne();

        Pageable pageable = Pageable.ofSize(pageSize);

        if (count == null || count == 0)
            return new PageImpl<>(List.of(), pageable, 0);

        List<Notification> notifications = queryFactory
                .select(notification)
                .from(notification)
                .leftJoin(notification.user, user)
                .where(
                        notification.id.lt(pageFrom),
                        notification.user.username.eq(username)
                ).orderBy(notification.createdAt.desc())
                .limit(pageSize)
                .fetch();

        return new PageImpl<>(notifications, pageable, count);
    }

    @Override
    public Optional<Notification> findUnread(String username, long notificationId) {
        return Optional.ofNullable(
                queryFactory
                        .select(notification)
                        .from(notification)
                        .leftJoin(notification.user, user)
                        .where(
                                notification.id.eq(notificationId),
                                notification.user.username.eq(username),
                                notification.isRead.isFalse()
                        ).fetchFirst()
        );
    }
}
