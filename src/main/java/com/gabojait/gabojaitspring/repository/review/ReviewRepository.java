package com.gabojait.gabojaitspring.repository.review;

import com.gabojait.gabojaitspring.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewCustomRepository {
}
