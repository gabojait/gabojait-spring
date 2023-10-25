package com.gabojait.gabojaitspring.repository.favorite;

import com.gabojait.gabojaitspring.domain.favorite.Favorite;
import com.gabojait.gabojaitspring.domain.user.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.gabojait.gabojaitspring.domain.favorite.QFavorite.favorite;
import static com.gabojait.gabojaitspring.domain.team.QTeam.team;
import static com.gabojait.gabojaitspring.domain.user.QUser.user;

@RequiredArgsConstructor
public class FavoriteRepositoryImpl implements FavoriteCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsUser(long userId, long targetUserId) {
        QUser targetUser = new QUser("targetUser");

        Integer result = queryFactory
                .selectOne()
                .from(favorite)
                .leftJoin(favorite.user, user)
                .leftJoin(favorite.favoriteUser, targetUser)
                .where(
                        favorite.user.id.eq(userId),
                        targetUser.id.eq(targetUserId)
                ).fetchFirst();

        return result != null;
    }

    @Override
    public boolean existsTeam(long userId, long teamId) {
        Integer result = queryFactory
                .selectOne()
                .from(favorite)
                .leftJoin(favorite.user, user)
                .leftJoin(favorite.favoriteTeam, team)
                .where(
                        favorite.user.id.eq(userId),
                        favorite.favoriteTeam.id.eq(teamId)
                ).fetchFirst();

        return result != null;
    }

    @Override
    public Optional<Favorite> findUser(long userId, long targetUserId) {
        QUser targetUser = new QUser("targetUser");

        return Optional.ofNullable(
                queryFactory.select(favorite)
                        .from(favorite)
                        .leftJoin(favorite.user, user)
                        .leftJoin(favorite.favoriteUser, targetUser)
                        .where(
                                favorite.user.id.eq(userId),
                                targetUser.id.eq(targetUserId)
                        ).fetchFirst()
        );
    }

    @Override
    public Optional<Favorite> findTeam(long userId, long teamId) {
        return Optional.ofNullable(
                queryFactory.select(favorite)
                        .from(favorite)
                        .leftJoin(favorite.user, user)
                        .leftJoin(favorite.favoriteTeam, team)
                        .where(
                                favorite.user.id.eq(userId),
                                favorite.favoriteTeam.id.eq(teamId)
                        ).fetchFirst()
        );
    }

    @Override
    public Page<Favorite> findPageUser(long userId, long pageFrom, int pageSize) {
        Long count = queryFactory.select(favorite.count())
                .from(favorite)
                .leftJoin(favorite.user, user)
                .where(
                        favorite.user.id.eq(userId),
                        favorite.favoriteUser.isNotNull()
                ).fetchOne();

        Pageable pageable = Pageable.ofSize(pageSize);

        if (count == null || count == 0)
            return new PageImpl<>(List.of(), pageable, 0);

        QUser targetUser = new QUser("targetUser");
        List<Favorite> favorites = queryFactory.select(favorite)
                .from(favorite)
                .leftJoin(favorite.favoriteUser, targetUser)
                .fetchJoin()
                .leftJoin(favorite.user, user)
                .where(
                        favorite.id.lt(pageFrom),
                        favorite.favoriteUser.isNotNull(),
                        favorite.user.id.eq(userId)
                ).orderBy(favorite.createdAt.desc())
                .limit(pageSize)
                .fetch();

        return new PageImpl<>(favorites, pageable, count);
    }

    @Override
    public Page<Favorite> findPageTeam(long userId, long pageFrom, int pageSize) {
        Long count = queryFactory.select(favorite.count())
                .from(favorite)
                .leftJoin(favorite.user, user)
                .where(
                        favorite.user.id.eq(userId),
                        favorite.favoriteTeam.isNotNull()
                ).fetchOne();

        Pageable pageable = Pageable.ofSize(pageSize);

        if (count == null || count == 0)
            return new PageImpl<>(List.of(), pageable, 0);

        List<Favorite> favorites = queryFactory.select(favorite)
                .from(favorite)
                .leftJoin(favorite.favoriteTeam, team)
                .fetchJoin()
                .leftJoin(favorite.user, user)
                .where(
                        favorite.id.lt(pageFrom),
                        favorite.favoriteTeam.isNotNull(),
                        favorite.user.id.eq(userId)
                ).orderBy(favorite.createdAt.desc())
                .limit(pageSize)
                .fetch();

        return new PageImpl<>(favorites, pageable, count);
    }
}
