package com.gabojait.gabojaitspring.review.repository;

import com.gabojait.gabojaitspring.review.domain.Review;
import com.gabojait.gabojaitspring.user.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.gabojait.gabojaitspring.review.domain.QReview.review;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewCustomRepository {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<Review> searchOrderByCreatedAt(long id, User reviewee, Pageable pageable) {
        return queryFactory
                .selectFrom(review)
                .where(
                        review.id.lt(id),
                        review.reviewee.eq(reviewee),
                        review.isDeleted.isFalse()
                )
                .orderBy(review.createdAt.desc())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
