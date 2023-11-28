package com.gabojait.gabojaitspring.repository.user;

import com.gabojait.gabojaitspring.api.dto.common.response.PageData;
import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.domain.user.User;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.gabojait.gabojaitspring.domain.user.QContact.contact;
import static com.gabojait.gabojaitspring.domain.user.QUser.user;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public PageData<List<User>> findPage(Position position, long pageFrom, int pageSize) {
        Long count = queryFactory.select(user.id.count())
                .from(user)
                .where(
                        positionEq(position),
                        user.isSeekingTeam.isTrue()
                ).fetchOne();

        if (count == null || count == 0)
            return new PageData<>(List.of(), 0);

        List<User> users = queryFactory.selectFrom(user)
                .where(
                        user.id.lt(pageFrom),
                        positionEq(position),
                        user.isSeekingTeam.isTrue()
                ).orderBy(user.createdAt.desc())
                .limit(pageSize)
                .fetch();

        return new PageData<>(users, count);
    }

    @Override
    public Optional<User> findSeekingTeam(long userId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(user)
                        .where(
                                user.id.eq(userId),
                                user.isSeekingTeam.isTrue()
                        ).fetchFirst()
        );
    }

    @Override
    public Optional<User> find(String email) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(user)
                        .leftJoin(user.contact, contact)
                        .where(
                                contact.email.eq(email)
                        ).fetchFirst()
        );
    }

    private Predicate positionEq(Position position) {
        return position != Position.NONE ? user.position.eq(position) : null;
    }
}
