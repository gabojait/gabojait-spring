package com.gabojait.gabojaitspring.repository.review;

import com.gabojait.gabojaitspring.domain.review.Review;
import org.springframework.data.domain.Page;

public interface ReviewCustomRepository {

    Page<Review> findPage(long userId, long pageFrom, int pageSize);

    boolean exists(long userId, long teamId);

    long countPrevious(long userId, long pageFrom);
}
