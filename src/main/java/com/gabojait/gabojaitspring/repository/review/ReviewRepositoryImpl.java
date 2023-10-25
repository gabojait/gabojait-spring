package com.gabojait.gabojaitspring.repository.review;

import com.gabojait.gabojaitspring.domain.review.Review;
import com.gabojait.gabojaitspring.domain.team.TeamMemberStatus;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.gabojait.gabojaitspring.domain.review.QReview.review;
import static com.gabojait.gabojaitspring.domain.team.QTeam.team;
import static com.gabojait.gabojaitspring.domain.team.QTeamMember.teamMember;
import static com.gabojait.gabojaitspring.domain.user.QUser.user;
import static com.querydsl.jpa.JPAExpressions.select;
import static com.querydsl.jpa.JPAExpressions.selectOne;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Review> findPage(long userId, long pageFrom, int pageSize) {
        Long count = queryFactory.select(review.id.count())
                .from(review)
                .where(
                        review.reviewee.id.in(
                                select(teamMember.id)
                                        .from(teamMember)
                                        .leftJoin(teamMember.team, team)
                                        .leftJoin(teamMember.user, user)
                                        .where(
                                                teamMember.user.id.eq(userId),
                                                teamMember.teamMemberStatus.eq(TeamMemberStatus.COMPLETE),
                                                team.completedAt.isNotNull()
                                        ))
                ).fetchOne();

        Pageable pageable = Pageable.ofSize(pageSize);

        if (count == null || count == 0)
            return new PageImpl<>(List.of(), pageable, 0);

        List<Review> reviews = queryFactory.selectFrom(review)
                .where(
                        review.reviewee.id.in(
                                select(teamMember.id)
                                        .from(teamMember)
                                        .leftJoin(teamMember.team, team)
                                        .leftJoin(teamMember.user, user)
                                        .where(
                                                teamMember.user.id.eq(userId),
                                                teamMember.teamMemberStatus.eq(TeamMemberStatus.COMPLETE),
                                                team.completedAt.isNotNull()
                                        )
                        ),
                        review.id.lt(pageFrom)
                ).orderBy(review.createdAt.desc())
                .limit(pageSize)
                .fetch();

        return new PageImpl<>(reviews, pageable, count);
    }

    @Override
    public boolean exists(long userId, long teamId) {
        Integer result = queryFactory
                .selectOne()
                .from(review)
                .leftJoin(review.reviewer, teamMember)
                .where(
                        teamMember.id.in(
                                select(teamMember.id)
                                        .from(teamMember)
                                        .leftJoin(teamMember.user, user)
                                        .leftJoin(teamMember.team, team)
                                        .where(
                                                user.id.eq(userId),
                                                team.id.eq(teamId)
                                        )
                        )
                ).fetchFirst();
        return result != null;
    }

    @Override
    public long countPrevious(long userId, long pageFrom) {
        Long count = queryFactory.select(review.id.count())
                .from(review)
                .where(
                        review.reviewee.id.in(
                                select(teamMember.id)
                                        .from(teamMember)
                                        .leftJoin(teamMember.team, team)
                                        .leftJoin(teamMember.user, user)
                                        .where(
                                                teamMember.user.id.eq(userId),
                                                teamMember.teamMemberStatus.eq(TeamMemberStatus.COMPLETE),
                                                team.completedAt.isNotNull()
                                        )
                        ),
                        review.id.lt(pageFrom)
                ).fetchOne();

        return count != null ? count : 0L;
    }
}
