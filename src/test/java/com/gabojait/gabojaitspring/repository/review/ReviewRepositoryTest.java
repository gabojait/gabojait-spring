package com.gabojait.gabojaitspring.repository.review;

import com.gabojait.gabojaitspring.domain.review.Review;
import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
import com.gabojait.gabojaitspring.domain.team.TeamMemberStatus;
import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.domain.user.User;
import com.gabojait.gabojaitspring.repository.team.TeamMemberRepository;
import com.gabojait.gabojaitspring.repository.team.TeamRepository;
import com.gabojait.gabojaitspring.repository.user.ContactRepository;
import com.gabojait.gabojaitspring.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ReviewRepositoryTest {

    @Autowired private ReviewRepository reviewRepository;
    @Autowired private ContactRepository contactRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private TeamMemberRepository teamMemberRepository;


    @Test
    @DisplayName("리뷰 페이징 조회를 한다.")
    void findPage() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        Team team = createSavedTeam("가보자잇");
        TeamMember teamMember1 = createSavedTeamMember(Position.BACKEND, true, user1, team);
        teamMember1.updateTeamMemberStatus(TeamMemberStatus.COMPLETE);

        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");
        TeamMember teamMember2 = createSavedTeamMember(Position.FRONTEND, true, user2, team);
        teamMember2.updateTeamMemberStatus(TeamMemberStatus.COMPLETE);

        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼");
        TeamMember teamMember3 = createSavedTeamMember(Position.MANAGER, true, user3, team);
        teamMember3.updateTeamMemberStatus(TeamMemberStatus.COMPLETE);

        User user4 = createSavedDefaultUser("tester4@gabojait.com", "tester4", "테스터사");
        TeamMember teamMember4 = createSavedTeamMember(Position.DESIGNER, true, user4, team);
        teamMember4.updateTeamMemberStatus(TeamMemberStatus.COMPLETE);

        teamMemberRepository.saveAll(List.of(teamMember1, teamMember2, teamMember3, teamMember4));

        team.complete("github.com/gabojait", LocalDateTime.now());
        teamRepository.save(team);

        Review review1 = createSavedReview(teamMember2, teamMember1);
        Review review2 = createSavedReview(teamMember3, teamMember1);
        Review review3 = createSavedReview(teamMember4, teamMember1);

        long pageFrom = Long.MAX_VALUE;
        int pageSize = 2;

        // when
        Page<Review> reviews = reviewRepository.findPage(user1.getId(), pageFrom, pageSize);

        // then
        assertThat(reviews.getContent())
                .extracting("id", "rating", "post", "createdAt", "updatedAt")
                .containsExactly(
                        tuple(review3.getId(), review3.getRating(), review3.getPost(),
                                review3.getCreatedAt(), review3.getUpdatedAt()),
                        tuple(review2.getId(), review2.getRating(), review2.getPost(),
                                review2.getCreatedAt(), review2.getUpdatedAt())
                );

        assertEquals(pageSize, reviews.getSize());
        assertEquals(3, reviews.getTotalElements());
    }

    @Test
    @DisplayName("존재하는 리뷰 존재 여부 조회를 한다.")
    void givenExistingReview_whenExists_thenReturn() {
        // given
        LocalDateTime now = LocalDateTime.now();

        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");

        Team team = createSavedTeam("가보자잇");
        team.complete("github.com/gabojait", now);
        teamRepository.save(team);

        TeamMember teamMember1 = createSavedTeamMember(Position.MANAGER, true, user1, team);
        TeamMember teamMember2 = createSavedTeamMember(Position.FRONTEND, false, user2, team);
        teamMember1.updateTeamMemberStatus(TeamMemberStatus.COMPLETE);
        teamMember2.updateTeamMemberStatus(TeamMemberStatus.COMPLETE);
        teamMemberRepository.saveAll(List.of(teamMember1, teamMember2));

        createSavedReview(teamMember1, teamMember2);

        // when
        boolean result = reviewRepository.exists(user1.getId(), team.getId());

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("존재하지 않은 리뷰 존재 여부 조회를 한다.")
    void givenNonExistingReview_whenExists_thenReturn() {
        // given
        LocalDateTime now = LocalDateTime.now();

        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");

        Team team = createSavedTeam("가보자잇");
        team.complete("github.com/gabojait", now);
        teamRepository.save(team);

        TeamMember teamMember1 = createSavedTeamMember(Position.MANAGER, true, user1, team);
        TeamMember teamMember2 = createSavedTeamMember(Position.FRONTEND, false, user2, team);
        teamMember1.updateTeamMemberStatus(TeamMemberStatus.COMPLETE);
        teamMember2.updateTeamMemberStatus(TeamMemberStatus.COMPLETE);
        teamMemberRepository.saveAll(List.of(teamMember1, teamMember2));

        // when
        boolean result = reviewRepository.exists(user1.getId(), team.getId());

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("존재하는 리뷰가 있을시 열의 수를 조회한다.")
    void givenExistingReview_whenCountPrevious_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼");
        User user4 = createSavedDefaultUser("tester4@gabojait.com", "tester4", "테스터사");
        LocalDateTime now = LocalDateTime.now();

        Team team = createSavedTeam("가보자잇");
        team.complete("github.com/gabojait", now);
        teamRepository.save(team);

        TeamMember teamMember1 = createSavedTeamMember(Position.MANAGER, true, user1, team);
        TeamMember teamMember2 = createSavedTeamMember(Position.FRONTEND, false, user2, team);
        TeamMember teamMember3 = createSavedTeamMember(Position.BACKEND, false, user3, team);
        TeamMember teamMember4 = createSavedTeamMember(Position.DESIGNER, false, user4, team);
        teamMember1.updateTeamMemberStatus(TeamMemberStatus.COMPLETE);
        teamMember2.updateTeamMemberStatus(TeamMemberStatus.COMPLETE);
        teamMember3.updateTeamMemberStatus(TeamMemberStatus.COMPLETE);
        teamMember4.updateTeamMemberStatus(TeamMemberStatus.COMPLETE);
        teamMemberRepository.saveAll(List.of(teamMember1, teamMember2, teamMember3, teamMember4));

        Review review1 = createSavedReview(teamMember2, teamMember1);
        Review review2 = createSavedReview(teamMember3, teamMember1);
        Review review3 = createSavedReview(teamMember4, teamMember1);

        long pageFrom = review2.getId();

        // when
        long result = reviewRepository.countPrevious(user1.getId(), pageFrom);

        // then
        assertEquals(1L, result);
    }

    @Test
    @DisplayName("존재하는 리뷰가 없을시 열의 수를 조회한다.")
    void givenNonExistingReview_whenCountPrevious_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");
        long pageFrom = Long.MAX_VALUE;

        // when
        long result = reviewRepository.countPrevious(user.getId(), pageFrom);

        // then
        assertEquals(0L, result);
    }

    private Review createSavedReview(TeamMember reviewer, TeamMember reviewee) {
        Review review = Review.builder()
                .rating((byte) 3)
                .post("좋습니다.")
                .reviewer(reviewer)
                .reviewee(reviewee)
                .build();

        return reviewRepository.save(review);
    }

    private TeamMember createSavedTeamMember(Position position, boolean isLeader, User user, Team team) {
        TeamMember teamMember = TeamMember.builder()
                .position(position)
                .isLeader(isLeader)
                .user(user)
                .team(team)
                .build();

        return teamMemberRepository.save(teamMember);
    }

    private Team createSavedTeam(String projectName) {
        Team team = Team.builder()
                .projectName(projectName)
                .projectDescription("프로젝트 설명")
                .expectation("내용입니다.")
                .openChatUrl("kakao.com/o/project")
                .designerMaxCnt((byte) 2)
                .backendMaxCnt((byte) 2)
                .frontendMaxCnt((byte) 2)
                .managerMaxCnt((byte) 2)
                .build();

        return teamRepository.save(team);
    }

    private User createSavedDefaultUser(String email, String username, String nickname) {
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

        return userRepository.save(user);
    }

}