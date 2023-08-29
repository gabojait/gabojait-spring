package com.gabojait.gabojaitspring.favorite.repository;

import com.gabojait.gabojaitspring.common.util.PageProvider;
import com.gabojait.gabojaitspring.favorite.domain.FavoriteTeam;
import com.gabojait.gabojaitspring.user.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.gabojait.gabojaitspring.favorite.domain.QFavoriteTeam.favoriteTeam;

@Repository
@RequiredArgsConstructor
public class FavoriteTeamRepositoryImpl implements FavoriteTeamCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final PageProvider pageProvider;

    @Override
    public Page<FavoriteTeam> searchByUserOrderByCreatedAt(long id, User user, Pageable pageable) {
        List<FavoriteTeam> favoriteTeams = queryFactory
                .selectFrom(favoriteTeam)
                .where(
                        favoriteTeam.id.lt(id),
                        favoriteTeam.user.eq(user),
                        favoriteTeam.isDeleted.isFalse()
                )
                .orderBy(favoriteTeam.createdAt.desc())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(favoriteTeam.count())
                .from(favoriteTeam)
                .where(
                        favoriteTeam.user.eq(user),
                        favoriteTeam.isDeleted.isFalse()
                )
                .fetchOne();

        count = pageProvider.validateCount(count);

        return new PageImpl<>(favoriteTeams, pageable, count);
    }
}
