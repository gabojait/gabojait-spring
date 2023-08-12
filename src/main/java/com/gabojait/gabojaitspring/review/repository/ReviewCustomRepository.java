package com.gabojait.gabojaitspring.review.repository;

import com.gabojait.gabojaitspring.review.domain.Review;
import com.gabojait.gabojaitspring.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewCustomRepository {

    Page<Review> searchOrderByCreatedAt(long id, User reviewee, Pageable pageable);
}
