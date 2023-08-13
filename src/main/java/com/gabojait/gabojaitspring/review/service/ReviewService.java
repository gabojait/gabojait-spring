package com.gabojait.gabojaitspring.review.service;

import com.gabojait.gabojaitspring.common.util.PageProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.review.domain.Review;
import com.gabojait.gabojaitspring.review.dto.req.ReviewCreateReqDto;
import com.gabojait.gabojaitspring.review.dto.req.ReviewDefaultReqDto;
import com.gabojait.gabojaitspring.review.repository.ReviewRepository;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.team.domain.TeamMember;
import com.gabojait.gabojaitspring.team.repository.TeamMemberRepository;
import com.gabojait.gabojaitspring.team.repository.TeamRepository;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final PageProvider pageProvider;

    /**
     * 리뷰 생성 |
     * 404(USER_NOT_FOUND / TEAM_NOT_FOUND / TEAM_MEMBER_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public void create(long reviewerId, long teamId, ReviewCreateReqDto request) {
        User reviewer = findOneUser(reviewerId);

        for(ReviewDefaultReqDto req : request.getReviews())
            validateTeamMember(teamId, req.getUserId());

        for(ReviewDefaultReqDto req : request.getReviews()) {
            User reviewee = findOneUser(req.getUserId());
            Team team = findOneTeam(teamId);

            Review review = Review.builder()
                    .reviewer(reviewer)
                    .reviewee(reviewee)
                    .team(team)
                    .rate(req.getRate())
                    .post(req.getPost())
                    .build();

            saveReview(review);

            reviewee.rate(review.getRate());
        }
    }

    /**
     * 리뷰 저장 |
     * 500(SERVER_ERROR)
     */
    private void saveReview(Review review) {
        try {
            reviewRepository.save(review);
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 회원의 리뷰 가능한 팀 단건 조회 |
     * 404(USER_NOT_FOUND / TEAM_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public Team findOneReviewableTeam(long userId, long teamId) {
        User user = findOneUser(userId);
        List<TeamMember> teamMembers = findManyCompleteTeamMember(user);

        for (TeamMember teamMember : teamMembers)
            if (teamMember.getTeam().getId().equals(teamId))
                return teamMember.getTeam();

        throw new CustomException(TEAM_NOT_FOUND);
    }

    /**
     * 회원의 리뷰 가능한 팀 전체 조회 |
     * 500(SERVER_ERROR)
     */
    public List<Team> findAllReviewableTeams(long userId) {
        User user = findOneUser(userId);
        List<TeamMember> teamMembers = findManyCompleteTeamMember(user);
        List<Team> reviewableTeams = new ArrayList<>();

        for(TeamMember teamMember : teamMembers) {
            Team team = teamMember.getTeam();

            if (team.getCompletedAt() != null) {
                if (LocalDateTime.now().minusWeeks(4).isBefore(team.getCompletedAt())) {
                    boolean isReviewable = isReviewableTeam(user, team);

                    if (isReviewable)
                        reviewableTeams.add(team);
                } else {
                    break;
                }
            }
        }

        return reviewableTeams;
    }

    /**
     * 리뷰 대상자로 리뷰 페이징 다건 조회 |
     * 404(USER_NOT_FOUND)
     * 500(SERVER_ERROR)
     */
    public Page<Review> findManyReviews(long userId, long pageFrom, Integer pageSize) {
        User reviewee = findOneUser(userId);

        pageFrom = pageProvider.validatePageFrom(pageFrom);
        Pageable pageable = pageProvider.validatePageable(pageSize, 20);

        try {
            return reviewRepository.searchOrderByCreatedAt(pageFrom, reviewee, pageable); // TODO pageCount Check
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 식별자로 회원 단건 조회 |
     * 404(USER_NOT_FOUND)
     */
    private User findOneUser(Long userId) {
        return userRepository.findByIdAndIsDeletedIsFalse(userId)
                .orElseThrow(() -> {
                    throw new CustomException(USER_NOT_FOUND);
                });
    }

    /**
     * 식별자로 팀 단건 조회 |
     * 404(TEAM_NOT_FOUND)
     */
    public Team findOneTeam(Long teamId) {
        return teamRepository.findByIdAndIsDeletedIsFalse(teamId)
                .orElseThrow(() -> {
                    throw new CustomException(TEAM_NOT_FOUND);
                });
    }

    /**
     * 회원 식별자로 팀원 다건 조회
     */
    private List<TeamMember> findManyCompleteTeamMember(User user) {
        return teamMemberRepository.findByUserAndIsDeletedIsTrue(user);
    }

    /**
     * 리뷰 작성자와 팀으로 리뷰 가능한 팀 검증 |
     * 500(SERVER_ERROR)
     */
    private boolean isReviewableTeam(User reviewer, Team team) {
        try {
            Optional<Review> review = reviewRepository.findByReviewerAndTeamAndIsDeletedIsFalse(reviewer, team);

            return review.isEmpty();
        } catch (RuntimeException e) {
            throw new CustomException(SERVER_ERROR);
        }
    }

    /**
     * 팀원 여부 검증 |
     * 404(USER_NOT_FOUND / TEAM_NOT_FOUND / TEAM_MEMBER_NOT_FOUND)
     */
    public void validateTeamMember(long teamId, long userId) {
        User user = findOneUser(userId);
        Team team = findOneTeam(teamId);

        teamMemberRepository.findByUserAndTeamAndIsDeletedIsTrue(user, team)
                .orElseThrow(() -> {
                    throw new CustomException(TEAM_MEMBER_NOT_FOUND);
                });
    }
}
