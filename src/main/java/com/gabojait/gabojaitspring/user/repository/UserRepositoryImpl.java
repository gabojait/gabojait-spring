package com.gabojait.gabojaitspring.user.repository;

import com.gabojait.gabojaitspring.common.util.PageProvider;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.user.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.gabojait.gabojaitspring.user.domain.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final PageProvider pageProvider;

    @Override
    public Page<User> searchOrderByCreatedAt(long id, Pageable pageable) {
        List<User> users = queryFactory
                .selectFrom(user)
                .where(
                        user.id.lt(id),
                        user.isSeekingTeam.eq(true),
                        user.isDeleted.eq(false)
                )
                .orderBy(user.createdAt.desc())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(user.count())
                .from(user)
                .where(
                        user.isSeekingTeam.eq(true),
                        user.isDeleted.eq(false)
                )
                .fetchOne();

        count = pageProvider.validateCount(count);

        return new PageImpl<>(users, pageable, count);
    }

    @Override
    public Page<User> searchByPositionOrderByCreatedAt(long id, Position position, Pageable pageable) {
        List<User> users = queryFactory
                .selectFrom(user)
                .where(
                        user.id.lt(id),
                        user.position.eq(position),
                        user.isSeekingTeam.eq(true),
                        user.isDeleted.eq(false)
                )
                .orderBy(user.createdAt.desc())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(user.count())
                .from(user)
                .where(
                        user.position.eq(position),
                        user.isSeekingTeam.eq(true),
                        user.isDeleted.eq(false)
                )
                .fetchOne();

        count = pageProvider.validateCount(count);

        return new PageImpl<>(users, pageable, count);
    }

    @Override
    public Page<User> searchAdmin(long id, String username, Pageable pageable) {
        List<User> users = queryFactory
                .selectFrom(user)
                .where(
                        user.id.lt(id),
                        user.username.endsWith(username),
                        user.isDeleted.eq(true)
                )
                .orderBy(user.createdAt.desc())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(user.count())
                .from(user)
                .where(
                        user.id.lt(id),
                        user.username.endsWith(username),
                        user.isDeleted.eq(false)
                )
                .fetchOne();

        count = pageProvider.validateCount(count);

        return new PageImpl<>(users, pageable, count);
    }
}
