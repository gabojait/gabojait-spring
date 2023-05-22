package com.gabojait.gabojaitspring.review.service;

import com.gabojait.gabojaitspring.common.util.UtilityProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.review.domain.Review;
import com.gabojait.gabojaitspring.review.dto.req.ReviewCreateReqDto;
import com.gabojait.gabojaitspring.review.dto.req.ReviewDefaultReqDto;
import com.gabojait.gabojaitspring.review.repository.ReviewRepository;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UtilityProvider utilityProvider;

    /**
     * 리뷰 생성 | main |
     * 400(TEAM_MEMBER_INVALID / ID_CONVERT_INVALID)
     * 409(REVIEW_UNAVAILABLE)
     * 500(SERVER_ERROR)
     */
    public List<Review> create(User user, Team team, ReviewCreateReqDto request) {
        List<Review> reviews = new ArrayList<>();
        validateCompletedTeam(user, team);

        for (ReviewDefaultReqDto r : request.getReviews()) {
            ObjectId revieweeId = utilityProvider.toObjectId(r.getRevieweeId());
            validateTeamMember(r.getRevieweeId(), team);
            Optional<Review> review = findOneByReviewerIdRevieweeIdTeamId(user.getId(), revieweeId, team.getId());

            if (review.isEmpty() && !revieweeId.toString().equals(user.getId().toString())) {
                Review newReview = r.toEntity(user.getId(), revieweeId, team.getId());
                save(newReview);
                reviews.add(newReview);
            }
        }

        return reviews;
    }

    /**
     * 리뷰 가능한 팀 식별자 전체 조회 | main |
     * 500(SERVER_ERROR)
     */
    public List<ObjectId> findReviewableTeamIds(User user) {
        List<ObjectId> teamIds = new ArrayList<>();

        for (ObjectId teamId : user.getCompletedTeamIds()) {
            List<Review> reviews = findManyByReviewerIdTeamId(user.getId(), teamId);

            if (reviews.isEmpty())
                teamIds.add(teamId);
        }

        return teamIds;
    }

    /**
     * 리뷰 대상자 식별자로 리뷰 페이징 조회 | main |
     * 400(ID_CONVERT_INVALID)
     * 500(SERVER_ERROR)
     */
    public Page<Review> findPageByRevieweeId(String revieweeId, Integer pageFrom, Integer pageSize) {
        Pageable pageable = utilityProvider.validatePaging(pageFrom, pageSize, 20);
        ObjectId id = utilityProvider.toObjectId(revieweeId);

        try {
            return reviewRepository.findAllByRevieweeIdAndIsDeletedIsFalse(id, pageable);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 리뷰 저장 |
     * 500(SERVER_ERROR)
     */
    private Review save(Review review) {
        try {
            return reviewRepository.save(review);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 리뷰 작성자 식별자와 팀 식별자로 리뷰 다건 조회 |
     * 500(SERVER_ERROR)
     */
    private List<Review> findManyByReviewerIdTeamId(ObjectId reviewerId, ObjectId teamId) {
        try {
            return reviewRepository.findAllByReviewerIdAndTeamIdAndIsDeletedIsFalse(reviewerId, teamId);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 리뷰 작성자 식별자, 리뷰 대상자 식별자, 팀 식별자로 리뷰 단건 조회 |
     * 500(SERVER_ERROR)
     */
    private Optional<Review> findOneByReviewerIdRevieweeIdTeamId(ObjectId reviewerId,
                                                                 ObjectId revieweeId,
                                                                 ObjectId teamId) {
        try {
            return reviewRepository.findByReviewerIdAndRevieweeIdAndTeamIdAndIsDeletedIsFalse(reviewerId,
                    revieweeId,
                    teamId);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 완료한 팀 검증 |
     * 409(REVIEW_UNAVAILABLE)
     */
    private void validateCompletedTeam(User user, Team team) {
        if (!user.isCompletedTeam(team.getId()) || !team.getIsComplete())
            throw new CustomException(null, REVIEW_UNAVAILABLE);
    }

    /**
     * 팀 소속 검증 |
     * 400(TEAM_MEMBER_INVALID)
     */
    private void validateTeamMember(String userId, Team team) {
        if (!team.isTeamMember(userId))
            throw new CustomException(null, TEAM_MEMBER_INVALID);
    }
}
