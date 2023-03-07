package com.inuappcenter.gabojaitspring.review.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.review.domain.Review;
import com.inuappcenter.gabojaitspring.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;

    /**
     * 리뷰 저장 |
     * 500(SERVER_ERROR)
     */
    public Review save(Review review) {

        try {
            return reviewRepository.save(review);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 리뷰 전체 조회 |
     * 500(SERVER_ERROR)
     */
    public List<Review> findAll(String revieweeUserId) {

        try {
            return reviewRepository
                    .findReviewsByRevieweeUserIdAndIsDeletedIsFalseOrderByCreatedDate(new ObjectId(revieweeUserId));
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }
}
