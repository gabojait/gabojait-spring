package com.gabojait.gabojaitspring.domain.review;

import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ReviewTest {

    @Test
    @DisplayName("리뷰 생성이 정상 작동한다")
    void givenValid_whenBuilder_thenReturn() {
        // given
        User user1 = createDefaultUser("tester1@gabojait.com", "tester1", "테스터일",
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        User user2 = createDefaultUser("tester2@gabojait.com", "tester2", "테스터이",
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        Team team = createTeam();
        TeamMember reviewer = createTeamMember(Position.BACKEND, true, user1, team);
        TeamMember reviewee = createTeamMember(Position.MANAGER, false, user2, team);
        byte rating = 3;
        String post = "좋은 팀원이였습니다.";

        // when
        Review review = createReview(rating, post, reviewer, reviewee);

        // then
        assertAll(
                () -> assertThat(review)
                        .extracting("rating", "post", "isDeleted", "reviewer", "reviewee")
                        .containsExactly(rating, post, false, reviewer, reviewee),
                () -> assertThat(user2.getRating()).isEqualTo(rating)
        );
    }

    private static Stream<Arguments> providerEquals() {
        User user1 = createDefaultUser("tester1@gabojait.com", "tester1", "테스터일",
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        User user2 = createDefaultUser("tester2@gabojait.com", "tester2", "테스터이",
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam();
        TeamMember teamMember1 = createTeamMember(Position.BACKEND, false, user1, team);
        TeamMember teamMember2 = createTeamMember(Position.FRONTEND, false, user2, team);

        Review review = createReview((byte) 3, "좋은 팀원이였습니다.", teamMember1, teamMember2);

        TeamMember reviewer1 = createTeamMember(Position.BACKEND, false, user1, team);
        Review reviewerReview1 = createReview((byte) 3, "좋은 팀원이였습니다.", reviewer1, teamMember1);
        TeamMember reviewer2 = createTeamMember(Position.FRONTEND, false, user1, team);
        Review reviewerReview2 = createReview((byte) 3, "좋은 팀원이였습니다.", reviewer2, teamMember1);

        TeamMember reviewee1 = createTeamMember(Position.BACKEND, false, user1, team);
        Review revieweeReview1 = createReview((byte) 3, "좋은 팀원이였습니다.", teamMember1, reviewee1);
        TeamMember reviewee2 = createTeamMember(Position.FRONTEND, false, user1, team);
        Review revieweeReview2 = createReview((byte) 3, "좋은 팀원이였습니다.", teamMember1, reviewee2);

        return Stream.of(
                Arguments.of(review, review, true),
                Arguments.of(review, new Object(), false),
                Arguments.of(
                        createReview((byte) 3, "좋은 팀원이였습니다.", teamMember1, teamMember2),
                        createReview((byte) 3, "좋은 팀원이였습니다.", teamMember1, teamMember2),
                        true
                ),
                Arguments.of(
                        createReview((byte) 3, "좋은 팀원이였습니다.", teamMember1, teamMember2),
                        createReview((byte) 2, "좋은 팀원이였습니다.", teamMember1, teamMember2),
                        false
                ),
                Arguments.of(
                        createReview((byte) 3, "좋은 팀원이였습니다.1", teamMember1, teamMember2),
                        createReview((byte) 3, "좋은 팀원이였습니다.2", teamMember1, teamMember2),
                        false
                ),
                Arguments.of(reviewerReview1, reviewerReview2, false),
                Arguments.of(revieweeReview1, revieweeReview2, false)
        );
    }

    @ParameterizedTest(name = "[{index}] 리뷰 객체를 비교한다")
    @MethodSource("providerEquals")
    @DisplayName("리뷰 객체 비교가 정상 작동한다")
    void givenProvider_whenEquals_thenReturn(Review review, Object object, boolean result) {
        // when & then
        assertThat(review.equals(object)).isEqualTo(result);
    }

    private static Stream<Arguments> providerHashCode() {
        User user1 = createDefaultUser("tester1@gabojait.com", "tester1", "테스터일",
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        User user2 = createDefaultUser("tester2@gabojait.com", "tester2", "테스터이",
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam();
        TeamMember teamMember1 = createTeamMember(Position.BACKEND, false, user1, team);
        TeamMember teamMember2 = createTeamMember(Position.FRONTEND, false, user2, team);

        return Stream.of(
                Arguments.of(
                        createReview((byte) 3, "좋은 팀원이였습니다.", teamMember1, teamMember2),
                        createReview((byte) 3, "좋은 팀원이였습니다.", teamMember1, teamMember2),
                        true
                ),
                Arguments.of(
                        createReview((byte) 3, "좋은 팀원이였습니다.", teamMember1, teamMember2),
                        createReview((byte) 2, "좋은 팀원이였습니다.", teamMember1, teamMember2),
                        false
                )
        );
    }

    @ParameterizedTest(name = "[{index}] 리뷰 해시코드를 비교한다")
    @MethodSource("providerHashCode")
    @DisplayName("리뷰 해시코드 비교가 정상 작동한다")
    void givenProvider_whenHashCode_thenReturn(Review review1, Review review2, boolean result) {
        // when
        int hashCode1 = review1.hashCode();
        int hashCode2 = review2.hashCode();

        // then
        assertThat(hashCode1 == hashCode2).isEqualTo(result);
    }

    private static Review createReview(byte rating, String post, TeamMember reviewer, TeamMember reviewee) {
        return Review.builder()
                .rating(rating)
                .post(post)
                .reviewer(reviewer)
                .reviewee(reviewee)
                .build();
    }

    private static TeamMember createTeamMember(Position position, boolean isLeader, User user, Team team) {
        return TeamMember.builder()
                .position(position)
                .isLeader(isLeader)
                .team(team)
                .user(user)
                .build();
    }

    private static Team createTeam() {
        return Team.builder()
                .projectName("가보자잇")
                .projectDescription("프로젝트 설명입니다.")
                .expectation("열정적인 팀원을 구합니다.")
                .openChatUrl("kakao.com/o/gabojait")
                .designerMaxCnt((byte) 3)
                .backendMaxCnt((byte) 3)
                .frontendMaxCnt((byte) 3)
                .managerMaxCnt((byte) 3)
                .build();
    }

    private static User createDefaultUser(String email,
                                          String username,
                                          String nickname,
                                          LocalDate birthdate,
                                          LocalDateTime lastRequestAt) {
        Contact contact = Contact.builder()
                .email(email)
                .verificationCode("000000")
                .build();
        contact.verified();

        return User.builder()
                .username(username)
                .password("password1!")
                .nickname(nickname)
                .gender(Gender.M)
                .birthdate(birthdate)
                .lastRequestAt(lastRequestAt)
                .contact(contact)
                .build();
    }
}