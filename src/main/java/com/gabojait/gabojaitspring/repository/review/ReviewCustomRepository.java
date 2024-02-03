package com.gabojait.gabojaitspring.repository.review;

import com.gabojait.gabojaitspring.common.response.PageData;
import com.gabojait.gabojaitspring.domain.review.Review;

import java.util.List;

public interface ReviewCustomRepository {

    PageData<List<Review>> findPage(long userId, long pageFrom, int pageSize);

    boolean exists(long userId, long teamId);

    long countPrevious(long userId, long pageFrom);
}
