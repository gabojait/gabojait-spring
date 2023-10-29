package com.gabojait.gabojaitspring.repository.profile;

import com.gabojait.gabojaitspring.domain.profile.Portfolio;
import com.gabojait.gabojaitspring.domain.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.gabojait.gabojaitspring.domain.profile.QPortfolio.portfolio;

@Repository
@RequiredArgsConstructor
public class PortfolioRepositoryImpl implements PortfolioCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Portfolio> findAll(long userId) {
        return queryFactory
                .selectFrom(portfolio)
                .where(portfolio.user.id.eq(userId))
                .orderBy(portfolio.createdAt.desc())
                .fetch();
    }
}
