package com.inuappcenter.gabojaitspring.review.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.review.domain.Question;
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

    /**
     * 리뷰 이전 작성 여부 확인 |
     * 409(EXISTING_REVIEW)
     * 500(SERVER_ERROR)
     */
    public void isExistingReview(ObjectId reviewerUserId,
                                 ObjectId revieweeUserId,
                                 ObjectId teamId,
                                 List<Question> questions) {

        try {
            for (Question question : questions)
                reviewRepository.findByReviewerUserIdAndRevieweeUserIdAndTeamIdAndQuestionAndIsDeletedIsFalse(
                        reviewerUserId,
                        revieweeUserId,
                        teamId,
                        question)
                        .ifPresent((r) -> {
                            throw new CustomException(EXISTING_REVIEW);
                        });

        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }
}
