package com.inuappcenter.gabojaitspring.review.service;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.review.domain.Question;
import com.inuappcenter.gabojaitspring.review.domain.Review;
import com.inuappcenter.gabojaitspring.review.repository.ReviewRepository;
import com.inuappcenter.gabojaitspring.team.domain.Team;
import com.inuappcenter.gabojaitspring.team.dto.res.TeamAbstractResDto;
import com.inuappcenter.gabojaitspring.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
                                 String revieweeUserId,
                                 ObjectId teamId,
                                 Question question) {

        reviewRepository.findByReviewerUserIdAndRevieweeUserIdAndTeamIdAndQuestionAndIsDeletedIsFalse(
                reviewerUserId,
                new ObjectId(revieweeUserId),
                teamId,
                question).ifPresent((r) -> {
                    throw new CustomException(EXISTING_REVIEW);
                });
    }

    /**
     * 리뷰 대상자 검증 |
     * REVIEWEE_NOT_FOUND
     */
    public void validateReviewee(List<ObjectId> revieweesUserId, String revieweeUserId) {

        if (!revieweesUserId.contains(new ObjectId(revieweeUserId)))
            throw new CustomException(REVIEWEE_NOT_FOUND);
    }

    /**
     * 리뷰 작성 가능한 팀 조회 |
     * 프로젝트 종료 기준 4주 동안 리뷰 작성이 가능하다.
     */
    public List<Team> findUndoneTeam(List<Team> completedTeams) {

        List<Team> teams = new ArrayList<>();
        for (Team team: completedTeams) {
            if (team.getCompletedDate().isAfter(LocalDateTime.now().minusWeeks(4)))
                teams.add(team);
        }

        return teams;
    }
}
