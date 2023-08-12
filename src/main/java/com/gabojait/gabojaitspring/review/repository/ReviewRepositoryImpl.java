package com.gabojait.gabojaitspring.review.repository;

import com.gabojait.gabojaitspring.common.util.PageProvider;
import com.gabojait.gabojaitspring.review.domain.Review;
import com.gabojait.gabojaitspring.user.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.gabojait.gabojaitspring.review.domain.QReview.review;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final PageProvider pageProvider;


    @Override
    public Page<Review> searchOrderByCreatedAt(long id, User reviewee, Pageable pageable) {
        List<Review> reviews = queryFactory
                .selectFrom(review)
                .where(
                        review.id.lt(id),
                        review.reviewee.eq(reviewee),
                        review.isDeleted.eq(false)
                )
                .orderBy(review.createdAt.desc())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(review.count())
                .from(review)
                .where(
                        review.reviewee.eq(reviewee),
                        review.isDeleted.eq(false)
                )
                .fetchOne();

        count = pageProvider.validateCount(count);

        return new PageImpl<>(reviews, pageable, count);
    }
}
