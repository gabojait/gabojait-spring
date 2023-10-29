package com.gabojait.gabojaitspring.repository.user;

import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.domain.user.User;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.gabojait.gabojaitspring.domain.user.QUser.user;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<User> findPage(Position position, long pageFrom, int pageSize) {
        Long count = queryFactory.select(user.id.count())
                .from(user)
                .where(
                        positionEq(position),
                        user.isSeekingTeam.isTrue()
                ).fetchOne();

        Pageable pageable = Pageable.ofSize(pageSize);

        if (count == null || count == 0)
            return new PageImpl<>(List.of(), pageable, 0);

        List<User> users = queryFactory.selectFrom(user)
                .where(
                        user.id.lt(pageFrom),
                        positionEq(position),
                        user.isSeekingTeam.isTrue()
                ).orderBy(user.createdAt.desc())
                .limit(pageSize)
                .fetch();

        return new PageImpl<>(users, pageable, count);
    }

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

    private Predicate positionEq(Position position) {
        return position != Position.NONE ? user.position.eq(position) : null;
    }
}
