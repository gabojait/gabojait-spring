package com.gabojait.gabojaitspring.favorite.repository;

import com.gabojait.gabojaitspring.common.util.PageProvider;
import com.gabojait.gabojaitspring.favorite.domain.FavoriteTeam;
import com.gabojait.gabojaitspring.favorite.domain.FavoriteUser;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.gabojait.gabojaitspring.favorite.domain.QFavoriteUser.favoriteUser;

@Repository
@RequiredArgsConstructor
public class FavoriteUserRepositoryImpl implements FavoriteUserCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final PageProvider pageProvider;

    @Override
    public Page<FavoriteUser> searchByTeamOrderByCreatedAt(long id, Team team, Pageable pageable) {
        List<FavoriteUser> favoriteUsers = queryFactory
                .selectFrom(favoriteUser)
                .where(
                        favoriteUser.id.lt(id),
                        favoriteUser.team.eq(team),
                        favoriteUser.isDeleted.isFalse()
                )
                .orderBy(favoriteUser.createdAt.desc())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(favoriteUser.count())
                .from(favoriteUser)
                .where(
                        favoriteUser.team.eq(team),
                        favoriteUser.isDeleted.isFalse()
                )
                .fetchOne();

        count = pageProvider.validateCount(count);

        return new PageImpl<>(favoriteUsers, pageable, count);
    }
}
