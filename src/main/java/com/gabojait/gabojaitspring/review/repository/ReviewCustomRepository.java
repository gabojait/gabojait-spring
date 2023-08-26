package com.gabojait.gabojaitspring.review.repository;

import com.gabojait.gabojaitspring.review.domain.Review;
import com.gabojait.gabojaitspring.user.domain.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewCustomRepository {

    List<Review> searchOrderByCreatedAt(long id, User reviewee, Pageable pageable);
}
