package com.gabojait.gabojaitspring.api.service.review;

import com.gabojait.gabojaitspring.api.dto.common.response.PageData;
import com.gabojait.gabojaitspring.api.dto.review.request.ReviewCreateManyRequest;
import com.gabojait.gabojaitspring.api.dto.review.request.ReviewCreateOneRequest;
import com.gabojait.gabojaitspring.api.dto.review.response.ReviewFindAllTeamResponse;
import com.gabojait.gabojaitspring.api.dto.review.response.ReviewFindTeamResponse;
import com.gabojait.gabojaitspring.api.dto.review.response.ReviewPageResponse;
import com.gabojait.gabojaitspring.domain.review.Review;
import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.domain.user.User;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.repository.review.ReviewRepository;
import com.gabojait.gabojaitspring.repository.team.TeamMemberRepository;
import com.gabojait.gabojaitspring.repository.team.TeamRepository;
import com.gabojait.gabojaitspring.repository.user.ContactRepository;
import com.gabojait.gabojaitspring.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ReviewServiceTest {

    @Autowired private ReviewService reviewService;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private ContactRepository contactRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TeamMemberRepository teamMemberRepository;
    @Autowired private TeamRepository teamRepository;

    @Test
    @DisplayName("리뷰 가능한 팀 전체 조회를 한다.")
    void givenValid_whenFindAllReviewableTeams_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.MANAGER);
        LocalDateTime now = LocalDateTime.now();

        Team team1 = createSavedTeam("가보자잇1");
        Team team2 = createSavedTeam("가보자잇2");
        Team team3 = createSavedTeam("가보자잇3");
        teamRepository.saveAll(List.of(team1, team2));

        TeamMember teamMember1 = createSavedTeamMember(true, user, team1);
        teamMember1.complete("github.com/gabojait1", now.minusWeeks(4).minusSeconds(1));
        TeamMember teamMember2 = createSavedTeamMember(true, user, team2);
        teamMember2.complete("github.com/gabojait2", now.minusWeeks(4).plusSeconds(1));
        TeamMember teamMember3 = createSavedTeamMember(false, user, team3);
        teamMember3.incomplete();
        teamMemberRepository.saveAll(List.of(teamMember1, teamMember2, teamMember3));

        // when
        PageData<List<ReviewFindAllTeamResponse>> responses = reviewService.findAllReviewableTeams(user.getId(), now);

        // then
        assertThat(responses.getData())
                .extracting("teamId", "projectName", "designerCurrentCnt", "backendCurrentCnt", "frontendCurrentCnt",
                        "managerCurrentCnt", "designerMaxCnt", "backendMaxCnt", "frontendMaxCnt", "managerMaxCnt",
                        "createdAt", "updatedAt")
                .containsExactly(
                        tuple(team2.getId(), team2.getProjectName(), team2.getDesignerCurrentCnt(),
                                team2.getBackendCurrentCnt(), team2.getFrontendCurrentCnt(),
                                team2.getManagerCurrentCnt(), team2.getDesignerMaxCnt(), team2.getBackendMaxCnt(),
                                team2.getFrontendMaxCnt(), team2.getManagerMaxCnt(), team2.getCreatedAt(),
                                team2.getUpdatedAt())
                );

        assertEquals(1L, responses.getTotal());
    }

    @Test
    @DisplayName("리뷰 가능한 팀 조회를 한다.")
    void givenValid_whenFindReviewableTeam_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일", Position.MANAGER);
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이", Position.BACKEND);
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼", Position.FRONTEND);
        LocalDateTime now = LocalDateTime.now();

        Team team = createSavedTeam("가보자잇");
        teamRepository.save(team);
        TeamMember teamMember1 = createSavedTeamMember(true, user1, team);
        TeamMember teamMember2 = createSavedTeamMember(false, user2, team);
        TeamMember teamMember3 = createSavedTeamMember(false, user3, team);
        teamMember1.complete("github.com/gabojait", now);
        teamMember2.complete("github.com/gabojait", now);
        teamMember3.complete("github.com/gabojait", now);
        teamMemberRepository.saveAll(List.of(teamMember1, teamMember2, teamMember3));

        // when
        ReviewFindTeamResponse response = reviewService.findReviewableTeam(user1.getId(), team.getId(), now);

        // then
        assertAll(() -> assertThat(response)
                        .extracting("teamId", "projectName")
                        .containsExactly(team.getId(), team.getProjectName()),
                () -> assertThat(response.getTeamMembers())
                        .extracting("userId", "username", "nickname", "position", "isLeader")
                        .containsExactly(
                                tuple(user3.getId(), user3.getUsername(), user3.getNickname(),
                                        teamMember3.getPosition(), teamMember3.getIsLeader()),
                                tuple(user2.getId(), user2.getUsername(), user2.getNickname(),
                                        teamMember2.getPosition(), teamMember2.getIsLeader())
                        )
        );
    }

    @Test
    @DisplayName("리뷰 생성을 한다.")
    void givenValid_whenCreateReview_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일", Position.MANAGER);
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이", Position.BACKEND);
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼", Position.FRONTEND);
        LocalDateTime now = LocalDateTime.now();

        Team team = createSavedTeam("가보자잇");
        teamRepository.save(team);
        TeamMember teamMember1 = createSavedTeamMember(true, user1, team);
        teamMember1.complete("github.com/gabojait", now.minusWeeks(4).plusSeconds(1));
        TeamMember teamMember2 = createSavedTeamMember(false, user2, team);
        teamMember2.complete("github.com/gabojait", now.minusWeeks(4).plusSeconds(1));
        TeamMember teamMember3 = createSavedTeamMember(false, user3, team);
        teamMember3.complete("github.com/gabojait", now.minusWeeks(4).plusSeconds(1));
        teamMemberRepository.saveAll(List.of(teamMember1, teamMember2, teamMember3));

        ReviewCreateManyRequest request = createValidReviewCreateManyRequest(
                List.of(teamMember1.getId(), teamMember2.getId(), teamMember3.getId())
        );

        // when
        reviewService.createReview(user1.getId(), team.getId(), request);

        // then
        boolean exists = reviewRepository.exists(user1.getId(), team.getId());
        assertTrue(exists);
    }

    @Test
    @DisplayName("존재하지 않은 팀원으로 리뷰 생성을 하면 예외가 발생한다.")
    void givenNonExistingTeamMember_whenCreateReview_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.MANAGER);
        long teamId = 1L;
        ReviewCreateManyRequest request = createValidReviewCreateManyRequest(List.of(1L));

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(user.getId(), teamId, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TEAM_MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("회원 리뷰 페이징 조회를 한다.")
    void givenValid_whenFindPageReviews_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일", Position.MANAGER);
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이", Position.BACKEND);
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼", Position.FRONTEND);
        User user4 = createSavedDefaultUser("tester4@gabojait.com", "tester4", "테스터사", Position.DESIGNER);

        LocalDateTime now = LocalDateTime.now();

        Team team = createSavedTeam("가보자잇");
        teamRepository.save(team);
        TeamMember teamMember1 = createSavedTeamMember(true, user1, team);
        teamMember1.complete("github.com/gabojait", now);
        TeamMember teamMember2 = createSavedTeamMember(false, user2, team);
        teamMember2.complete("github.com/gabojait", now);
        TeamMember teamMember3 = createSavedTeamMember(false, user3, team);
        teamMember3.complete("github.com/gabojait", now);
        TeamMember teamMember4 = createSavedTeamMember(false, user4, team);
        teamMember4.complete("github.com/gabojait", now);
        teamMemberRepository.saveAll(List.of(teamMember1, teamMember2, teamMember3, teamMember4));

        Review review1 = createSavedReview(teamMember2, teamMember1);
        Review review2 = createSavedReview(teamMember3, teamMember1);
        Review review3 = createSavedReview(teamMember4, teamMember1);

        long pageFrom = review3.getId();
        int pageSize = 2;

        // when
        PageData<List<ReviewPageResponse>> responses = reviewService.findPageReviews(user1.getId(), pageFrom, pageSize);

        // then
        assertThat(responses.getData())
                .extracting("reviewId", "reviewer", "rating", "post", "createdAt", "updatedAt")
                .containsExactly(
                        tuple(review2.getId(), "익명2", review2.getRating(), review2.getPost(), review2.getCreatedAt(),
                                review2.getUpdatedAt()),
                        tuple(review1.getId(), "익명1", review1.getRating(), review1.getPost(), review1.getCreatedAt(),
                                review1.getUpdatedAt())
                );

        assertEquals(3L, responses.getTotal());
    }

    private ReviewCreateManyRequest createValidReviewCreateManyRequest(List<Long> revieweeMemberIds) {
        List<ReviewCreateOneRequest> reviews = revieweeMemberIds.stream()
                .map(revieweeMemberId -> ReviewCreateOneRequest.builder()
                        .teamMemberId(revieweeMemberId)
                        .rating((byte) 3)
                        .post("열정적인 팀원입니다.")
                        .build())
                .collect(Collectors.toList());

        return ReviewCreateManyRequest.builder()
                .reviews(reviews)
                .build();
    }

    private Review createSavedReview(TeamMember reviewer, TeamMember reviewee) {
        Review review = Review.builder()
                .reviewer(reviewer)
                .reviewee(reviewee)
                .rating((byte) 3)
                .post("열정적인 팀원입니다.")
                .build();

        return reviewRepository.save(review);
    }

    private Team createSavedTeam(String projectName) {
        Team team = Team.builder()
                .projectName(projectName)
                .projectDescription("프로젝트 설명입니다.")
                .expectation("열정적인 팀원을 찾습니다.")
                .openChatUrl("kakao.com/o/gabojait")
                .designerMaxCnt((byte) 5)
                .backendMaxCnt((byte) 5)
                .frontendMaxCnt((byte) 5)
                .managerMaxCnt((byte) 5)
                .build();

        return teamRepository.save(team);
    }

    private TeamMember createSavedTeamMember(boolean isLeader,
                                             User user,
                                             Team team) {
        TeamMember teamMember = TeamMember.builder()
                .position(user.getPosition())
                .isLeader(isLeader)
                .user(user)
                .team(team)
                .build();

        return teamMemberRepository.save(teamMember);
    }

    private User createSavedDefaultUser(String email, String username, String nickname, Position position) {
        Contact contact = Contact.builder()
                .email(email)
                .verificationCode("000000")
                .build();
        contact.verified();
        contactRepository.save(contact);

        User user = User.builder()
                .username(username)
                .password("password1!")
                .nickname(nickname)
                .gender(Gender.M)
                .birthdate(LocalDate.of(1997, 2, 11))
                .lastRequestAt(LocalDateTime.now())
                .contact(contact)
                .build();
        user.updatePosition(position);

        return userRepository.save(user);
    }
}
