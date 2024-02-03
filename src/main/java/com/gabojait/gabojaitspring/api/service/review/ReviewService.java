package com.gabojait.gabojaitspring.api.service.review;

import com.gabojait.gabojaitspring.common.response.PageData;
import com.gabojait.gabojaitspring.api.dto.review.request.ReviewCreateManyRequest;
import com.gabojait.gabojaitspring.api.dto.review.response.ReviewFindAllTeamResponse;
import com.gabojait.gabojaitspring.api.dto.review.response.ReviewFindTeamResponse;
import com.gabojait.gabojaitspring.api.dto.review.response.ReviewPageResponse;
import com.gabojait.gabojaitspring.domain.review.Review;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
import com.gabojait.gabojaitspring.domain.team.TeamMemberStatus;
import com.gabojait.gabojaitspring.common.exception.CustomException;
import com.gabojait.gabojaitspring.repository.review.ReviewRepository;
import com.gabojait.gabojaitspring.repository.team.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.TEAM_MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final TeamMemberRepository teamMemberRepository;

    /**
     * 리뷰 가능한 팀 전체 조회 |
     * @param userId 회원 식별자
     * @param now 현재 시간
     * @return 리뷰 가능한 전체 팀 응답들
     */
    public PageData<List<ReviewFindAllTeamResponse>> findAllReviewableTeams(long userId, LocalDateTime now) {
        List<TeamMember> teamMembers = teamMemberRepository.findAllReviewableFetchTeam(userId, now);

        return new PageData<>(teamMembers.stream()
                .map(TeamMember::getTeam)
                .map(ReviewFindAllTeamResponse::new)
                .collect(Collectors.toList()),
                teamMembers.size());
    }

    /**
     * 리뷰 가능한 팀 조회 |
     * 404(TEAM_MEMBER_NOT_FOUND)
     * @param userId 회원 식별자
     * @param teamId 팀 식별자
     * @param now 현재 시간
     * @return 리뷰 가능한 팀 응답
     */
    public ReviewFindTeamResponse findReviewableTeam(long userId, long teamId, LocalDateTime now) {
        TeamMember teamMember = findCompleteTeamMember(userId, teamId, now);
        List<TeamMember> teamMembers = teamMemberRepository.findAllCompleteFetchTeam(teamId);
        teamMembers.remove(teamMember);

        return new ReviewFindTeamResponse(teamMember.getTeam(), teamMembers);
    }

    /**
     * 리뷰 생성 |
     * 404(TEAM_MEMBER_NOT_FOUND)
     * @param userId 회원 식별자
     * @param teamId 팀 식별자
     * @param request 리뷰 다건 생성 요청
     */
    @Transactional
    public void createReview(long userId, long teamId, ReviewCreateManyRequest request) {
        boolean isExist = reviewRepository.exists(userId, teamId);
        if (isExist) return;

        TeamMember reviewer = findCompleteTeamMember(userId, teamId);
        List<TeamMember> teamMembers = teamMemberRepository.findAllCompleteFetchTeam(teamId);

        List<Review> reviews = request.getReviews().stream()
                .flatMap(r -> teamMembers.stream()
                        .filter(reviewee -> reviewee.getId().equals(r.getTeamMemberId()))
                        .map(reviewee -> r.toEntity(reviewer, reviewee)))
                .collect(Collectors.toList());

        reviewRepository.saveAll(reviews);
    }

    /**
     * 회원 리뷰 페이징 조회
     * @param userId 회원 식별자
     * @param pageFrom 페이지 시작점
     * @param pageSize 페이지 크기
     * @return 리뷰 페이징 응답들
     */
    public PageData<List<ReviewPageResponse>> findPageReviews(long userId, long pageFrom, int pageSize) {
        PageData<List<Review>> reviews = reviewRepository.findPage(userId, pageFrom, pageSize);
        long reviewCnt = reviewRepository.countPrevious(userId, pageFrom);

        List<ReviewPageResponse> responses = IntStream.range(0, Math.min(pageSize, reviews.getData().size()))
                .mapToObj(i -> new ReviewPageResponse(reviews.getData().get(i), (int) (reviewCnt - i)))
                .collect(Collectors.toList());

        return new PageData<>(responses, reviews.getTotal());
    }

    /**
     * 완료한 팀원 조회 |
     * 404(TEAM_MEMBER_NOT_FOUND)
     * @param userId 회원 식별자
     * @param teamId 팀 식별자
     * @param now 현재 시간
     */
    private TeamMember findCompleteTeamMember(long userId, long teamId, LocalDateTime now) {
        return teamMemberRepository.findReviewableFetchTeam(userId, teamId, now)
                .orElseThrow(() -> {
                    throw new CustomException(TEAM_MEMBER_NOT_FOUND);
                });
    }

    /**
     * 완료한 팀원 조회 |
     * 404(TEAM_MEMBER_NOT_FOUND)
     * @param userId 회원 식별자
     * @param teamId 팀 식별자
     * @return 팀원
     */
    private TeamMember findCompleteTeamMember(long userId, long teamId) {
        return teamMemberRepository.find(userId, teamId, TeamMemberStatus.COMPLETE)
                .orElseThrow(() -> {
                    throw new CustomException(TEAM_MEMBER_NOT_FOUND);
                });
    }
}
