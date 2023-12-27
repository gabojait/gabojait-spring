package com.gabojait.gabojaitspring.repository.team;

import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
import com.gabojait.gabojaitspring.domain.team.TeamMemberStatus;
import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.domain.user.User;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class TeamMemberRepositoryTest {

    @Autowired private TeamMemberRepository teamMemberRepository;
    @Autowired private ContactRepository contactRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TeamRepository teamRepository;

    @Test
    @DisplayName("현재까지 완료된 또는 진행중인 모든 팀원 정보를 조회한다.")
    void findAllFetchTeam() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Team team1 = createTeam("프로젝트1");
        teamRepository.save(team1);
        TeamMember teamMember1 = createTeamMember(true, user, team1);
        teamMemberRepository.save(teamMember1);
        team1.complete("github.com/gabojait1", LocalDateTime.now());
        teamRepository.save(team1);

        Team team2 = createTeam("프로젝트2");
        teamRepository.save(team2);
        TeamMember teamMember2 = createTeamMember(true, user, team2);
        teamMemberRepository.save(teamMember2);
        team2.complete("github.com/gabojait2", LocalDateTime.now());
        teamRepository.save(team2);

        Team team3 = createTeam("프로젝트3");
        teamRepository.save(team3);
        TeamMember teamMember3 = createTeamMember(true, user, team3);
        teamMemberRepository.save(teamMember3);

        // when
        List<TeamMember> teamMembers = teamMemberRepository.findAllFetchTeam(user.getId());

        // then
        assertThat(teamMembers)
                .extracting("id", "position", "isLeader", "teamMemberStatus", "isDeleted", "createdAt", "updatedAt")
                .containsExactly(
                        tuple(teamMember3.getId(), teamMember3.getPosition(), teamMember3.getIsLeader(),
                                teamMember3.getTeamMemberStatus(), teamMember3.getIsDeleted(), teamMember3.getCreatedAt(),
                                teamMember3.getUpdatedAt()),
                        tuple(teamMember2.getId(), teamMember2.getPosition(), teamMember2.getIsLeader(),
                                teamMember2.getTeamMemberStatus(), teamMember2.getIsDeleted(), teamMember2.getCreatedAt(),
                                teamMember2.getUpdatedAt()),
                        tuple(teamMember1.getId(), teamMember1.getPosition(), teamMember1.getIsLeader(),
                                teamMember1.getTeamMemberStatus(), teamMember1.getIsDeleted(), teamMember1.getCreatedAt(),
                                teamMember1.getUpdatedAt())
                );
    }

    @Test
    @DisplayName("현재까지 관련된 모든 팀원 정보를 조회한다.")
    void findAll() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Team team1 = createTeam("프로젝트1");
        teamRepository.save(team1);
        TeamMember teamMember1 = createTeamMember(false, user, team1);
        teamMember1.quit();
        teamMemberRepository.save(teamMember1);

        Team team2 = createTeam("프로젝트2");
        teamRepository.save(team2);
        TeamMember teamMember2 = createTeamMember(true, user, team2);
        teamMember2.complete("github.com/gabojait2", LocalDateTime.now());
        teamMemberRepository.save(teamMember2);

        Team team3 = createTeam("프로젝트3");
        teamRepository.save(team3);
        TeamMember teamMember3 = createTeamMember(true, user, team3);
        teamMemberRepository.save(teamMember3);

        // when
        List<TeamMember> teamMembers = teamMemberRepository.findAll(user.getId());

        // then
        assertThat(teamMembers)
                .extracting("id", "position", "isLeader", "teamMemberStatus", "isDeleted", "createdAt", "updatedAt")
                .containsExactly(
                        tuple(teamMember3.getId(), teamMember3.getPosition(), teamMember3.getIsLeader(),
                                teamMember3.getTeamMemberStatus(), teamMember3.getIsDeleted(),
                                teamMember3.getCreatedAt(), teamMember3.getUpdatedAt()),
                        tuple(teamMember2.getId(), teamMember2.getPosition(), teamMember2.getIsLeader(),
                                teamMember2.getTeamMemberStatus(), teamMember2.getIsDeleted(),
                                teamMember2.getCreatedAt(), teamMember2.getUpdatedAt()),
                        tuple(teamMember1.getId(), teamMember1.getPosition(), teamMember1.getIsLeader(),
                                teamMember1.getTeamMemberStatus(), teamMember1.getIsDeleted(),
                                teamMember1.getCreatedAt(), teamMember1.getUpdatedAt())
                );
    }

    @Test
    @DisplayName("현재 소속된 팀이 있을시 현재 소속된 팀원 정보를 조회한다.")
    void givenExistingCurrentTeam_whenFindCurrentFetchTeam_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Team team = createTeam("프로젝트1");
        teamRepository.save(team);
        TeamMember teamMember = createTeamMember(true, user, team);
        teamMemberRepository.save(teamMember);

        // when
        TeamMember foundTeamMember = teamMemberRepository.findCurrentFetchTeam(user.getId()).get();

        // then
        assertThat(foundTeamMember).isEqualTo(teamMember);
        assertAll(
                () -> assertThat(foundTeamMember)
                        .extracting("id", "position", "isLeader", "teamMemberStatus", "isDeleted", "createdAt", "updatedAt")
                        .containsExactly(teamMember.getId(), teamMember.getPosition(), teamMember.getIsLeader(),
                                teamMember.getTeamMemberStatus(), teamMember.getIsDeleted(), teamMember.getCreatedAt(),
                                teamMember.getUpdatedAt()),
                () -> assertThat(foundTeamMember.getTeam().getId()).isEqualTo(team.getId()),
                () -> assertThat(foundTeamMember.getUser().getId()).isEqualTo(user.getId())
        );
    }

    @Test
    @DisplayName("현재 소속된 팀이 없을시 현재 소속된 팀원 정보를 조회한다.")
    void givenNonExistingCurrentTeam_whenFindCurrentFetchTeam_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Team team = createTeam("프로젝트");
        teamRepository.save(team);
        TeamMember teamMember = createTeamMember(true, user, team);
        teamMember.complete("github.com/gabojait", LocalDateTime.now());
        teamMemberRepository.save(teamMember);

        // when
        Optional<TeamMember> foundTeamMember = teamMemberRepository.findCurrentFetchTeam(user.getId());

        // then
        assertThat(foundTeamMember).isEmpty();
    }

    @Test
    @DisplayName("현재 소속된 팀의 팀원 정보를 조회한다.")
    void givenExistingCurrentTeam_whenFindCurrent_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");
        Team team = createTeam("가보자잇");
        teamRepository.save(team);

        TeamMember teamMember = createTeamMember(false, user, team);
        teamMemberRepository.save(teamMember);

        TeamMemberStatus teamMemberStatus = TeamMemberStatus.PROGRESS;

        // when
        TeamMember foundTeamMember = teamMemberRepository.find(user.getId(), team.getId(), teamMemberStatus).get();

        // then
        assertThat(foundTeamMember)
                .extracting("id", "position", "teamMemberStatus", "isLeader", "isDeleted", "createdAt", "updatedAt")
                .containsExactly(teamMember.getId(), teamMember.getPosition(), teamMember.getTeamMemberStatus(),
                        teamMember.getIsLeader(), teamMember.getIsDeleted(), teamMember.getCreatedAt(),
                        teamMember.getUpdatedAt());
    }

    @Test
    @DisplayName("현재 소속된 팀이 없을시 현재 소속된 팀의 팀원 정보를 조회한다.")
    void givenNonExistingCurrentTeam_whenFind_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");
        Team team = createTeam("가보자잇");
        teamRepository.save(team);

        TeamMemberStatus teamMemberStatus = TeamMemberStatus.PROGRESS;

        // when
        Optional<TeamMember> foundTeamMember = teamMemberRepository.find(user.getId(), team.getId(), teamMemberStatus);

        // then
        assertThat(foundTeamMember).isEmpty();
    }

    @Test
    @DisplayName("팀장을 조회한다.")
    void findLeaderFetchUser() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼");

        Team team = createTeam("가보자잇");
        teamRepository.save(team);

        TeamMember teamMember1 = createTeamMember(true, user1, team);
        TeamMember teamMember2 = createTeamMember(false, user2, team);
        TeamMember teamMember3 = createTeamMember(false, user3, team);
        teamMember3.quit();
        teamMemberRepository.saveAll(List.of(teamMember1, teamMember2, teamMember3));

        // when
        TeamMember teamMember = teamMemberRepository.findLeaderFetchUser(team.getId()).get();

        // then
        assertThat(teamMember)
                .extracting("id", "position", "teamMemberStatus", "isLeader", "isDeleted", "createdAt", "updatedAt")
                .containsExactly(teamMember1.getId(), teamMember1.getPosition(),
                        teamMember1.getTeamMemberStatus(), teamMember1.getIsLeader(), teamMember1.getIsDeleted(),
                        teamMember1.getCreatedAt(), teamMember1.getUpdatedAt());
    }

    @Test
    @DisplayName("완료한 팀에 팀원 전체를 조회한다.")
    void findAllCompleteFetchTeam() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼");
        LocalDateTime now = LocalDateTime.now();

        Team team = createTeam("가보자잇");
        teamRepository.save(team);

        TeamMember teamMember1 = createTeamMember(true, user1, team);
        TeamMember teamMember2 = createTeamMember(false, user2, team);
        TeamMember teamMember3 = createTeamMember(false, user3, team);
        teamMember1.complete("github.com/gabojait", now);
        teamMember2.complete("github.com/gabojait", now);
        teamMember3.complete("github.com/gabojait", now);
        teamMemberRepository.saveAll(List.of(teamMember1, teamMember2, teamMember3));

        // when
        List<TeamMember> teamMembers = teamMemberRepository.findAllCompleteFetchTeam(team.getId());

        // then
        assertThat(teamMembers)
                .extracting("id", "position", "teamMemberStatus", "isLeader", "isDeleted", "createdAt", "updatedAt")
                .containsExactly(
                        tuple(teamMember3.getId(), teamMember3.getPosition(), teamMember3.getTeamMemberStatus(),
                                teamMember3.getIsLeader(), teamMember3.getIsDeleted(), teamMember3.getCreatedAt(),
                                teamMember3.getUpdatedAt()),
                        tuple(teamMember2.getId(), teamMember2.getPosition(), teamMember2.getTeamMemberStatus(),
                                teamMember2.getIsLeader(), teamMember2.getIsDeleted(), teamMember2.getCreatedAt(),
                                teamMember2.getUpdatedAt()),
                        tuple(teamMember1.getId(), teamMember1.getPosition(), teamMember1.getTeamMemberStatus(),
                                teamMember1.getIsLeader(), teamMember1.getIsDeleted(), teamMember1.getCreatedAt(),
                                teamMember1.getUpdatedAt())
                );
    }

    @Test
    @DisplayName("현재 소속된 팀원 전체를 조회한다.")
    void findAllCurrentFetchUser() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼");

        Team team = createTeam("가보자잇");
        teamRepository.save(team);

        TeamMember teamMember1 = createTeamMember(true, user1, team);
        TeamMember teamMember2 = createTeamMember(false, user2, team);
        TeamMember teamMember3 = createTeamMember(false, user3, team);
        teamMemberRepository.saveAll(List.of(teamMember1, teamMember2, teamMember3));

        // when
        List<TeamMember> teamMembers = teamMemberRepository.findAllCurrentFetchUser(team.getId());

        // then
        assertThat(teamMembers)
                .extracting("id", "position", "isLeader", "teamMemberStatus", "isDeleted", "createdAt", "updatedAt")
                .containsExactlyInAnyOrder(
                        tuple(teamMember3.getId(), teamMember3.getPosition(), teamMember3.getIsLeader(),
                                teamMember3.getTeamMemberStatus(), teamMember3.getIsDeleted(), teamMember3.getCreatedAt(),
                                teamMember3.getUpdatedAt()),
                        tuple(teamMember2.getId(), teamMember2.getPosition(), teamMember2.getIsLeader(),
                                teamMember2.getTeamMemberStatus(), teamMember2.getIsDeleted(), teamMember2.getCreatedAt(),
                                teamMember2.getUpdatedAt()),
                        tuple(teamMember1.getId(), teamMember1.getPosition(), teamMember1.getIsLeader(),
                                teamMember1.getTeamMemberStatus(), teamMember1.getIsDeleted(), teamMember1.getCreatedAt(),
                                teamMember1.getUpdatedAt())
                );
    }

    @Test
    @DisplayName("현재 소속된 또는 완료된 팀원 전체를 조회한다.")
    void findAllFetchUser() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼");

        Team team = createTeam("가보자잇");
        teamRepository.save(team);

        TeamMember teamMember1 = createTeamMember(true, user1, team);
        TeamMember teamMember2 = createTeamMember(false, user2, team);
        TeamMember teamMember3 = createTeamMember(false, user3, team);
        teamMemberRepository.saveAll(List.of(teamMember1, teamMember2, teamMember3));

        // when
        List<TeamMember> teamMembers = teamMemberRepository.findAllFetchUser(team.getId());

        // then
        assertThat(teamMembers)
                .extracting("id", "position", "teamMemberStatus", "isLeader", "isDeleted", "createdAt", "updatedAt")
                .containsExactlyInAnyOrder(
                        tuple(teamMember3.getId(), teamMember3.getPosition(), teamMember3.getTeamMemberStatus(),
                                teamMember3.getIsLeader(), teamMember3.getIsDeleted(), teamMember3.getCreatedAt(),
                                teamMember3.getUpdatedAt()),
                        tuple(teamMember2.getId(), teamMember2.getPosition(), teamMember2.getTeamMemberStatus(),
                                teamMember2.getIsLeader(), teamMember2.getIsDeleted(), teamMember2.getCreatedAt(),
                                teamMember2.getUpdatedAt()),
                        tuple(teamMember1.getId(), teamMember1.getPosition(), teamMember1.getTeamMemberStatus(),
                                teamMember1.getIsLeader(), teamMember1.getIsDeleted(), teamMember1.getCreatedAt(),
                                teamMember1.getUpdatedAt())
                );
    }

    @Test
    @DisplayName("한 회원을 제외한 팀원 전체를 조회한다.")
    void findAllExceptUserFetchUser() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼");
        User user4 = createSavedDefaultUser("tester4@gabojait.com", "tester4", "테스터사");

        Team team = createTeam("가보자잇");
        teamRepository.save(team);

        TeamMember teamMember1 = createTeamMember(true, user1, team);
        TeamMember teamMember2 = createTeamMember(false, user2, team);
        TeamMember teamMember3 = createTeamMember(false, user3, team);
        TeamMember teamMember4 = createTeamMember(false, user4, team);
        teamMember4.quit();
        teamMemberRepository.saveAll(List.of(teamMember1, teamMember2, teamMember3, teamMember4));

        // when
        List<TeamMember> teamMembers = teamMemberRepository.findAllExceptUserFetchUser(team.getId(), user1.getId());

        // then
        assertThat(teamMembers)
                .extracting("id", "position", "teamMemberStatus", "isLeader", "isDeleted", "createdAt", "updatedAt")
                .containsExactlyInAnyOrder(
                        tuple(teamMember2.getId(), teamMember2.getPosition(), teamMember2.getTeamMemberStatus(),
                                teamMember2.getIsLeader(), teamMember2.getIsDeleted(), teamMember2.getCreatedAt(),
                                teamMember2.getUpdatedAt()),
                        tuple(teamMember3.getId(), teamMember3.getPosition(), teamMember3.getTeamMemberStatus(),
                                teamMember3.getIsLeader(), teamMember3.getIsDeleted(), teamMember3.getCreatedAt(),
                                teamMember3.getUpdatedAt())
                );
    }

    @Test
    @DisplayName("리뷰 가능한 팀원을 조회한다.")
    void findReviewableFetchTeam() {
        // given
        User user = createSavedDefaultUser("tester@gaobjait.com", "tester", "테스터");
        LocalDateTime now = LocalDateTime.now();

        Team team = createTeam("가보자잇");
        teamRepository.save(team);
        TeamMember teamMember = createTeamMember(true, user, team);
        teamMember.complete("github.com/gabojait", now);
        teamMemberRepository.save(teamMember);

        // when
        TeamMember foundTeamMember = teamMemberRepository.findReviewableFetchTeam(user.getId(), team.getId(), now).get();

        // then
        assertThat(foundTeamMember)
                .extracting("id", "position", "teamMemberStatus", "isLeader", "isDeleted", "createdAt", "updatedAt")
                .containsExactly(teamMember.getId(), teamMember.getPosition(), teamMember.getTeamMemberStatus(),
                        teamMember.getIsLeader(), teamMember.getIsDeleted(), teamMember.getCreatedAt(),
                        teamMember.getUpdatedAt());
    }

    @Test
    @DisplayName("리뷰 가능한 팀원을 전체 조회한다.")
    void findAllReviewableFetchTeam() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");
        LocalDateTime now = LocalDateTime.now();

        Team team1 = createTeam("가보자잇1");
        Team team2 = createTeam("가보자잇2");
        team1.complete("github.com/gabojait", now.minusWeeks(4).minusSeconds(1));
        teamRepository.saveAll(List.of(team1, team2));
        TeamMember teamMember1 = createTeamMember(true, user, team1);
        teamMember1.complete("github.com/gabojait", now.minusWeeks(4).minusSeconds(1));
        TeamMember teamMember2 = createTeamMember(true, user, team2);
        teamMember2.complete("github.com/gabojait", now.minusWeeks(4).plusSeconds(1));
        teamMemberRepository.saveAll(List.of(teamMember1, teamMember2));

        // when
        List<TeamMember> teamMembers = teamMemberRepository.findAllReviewableFetchTeam(user.getId(), now);

        // then
        assertThat(teamMembers)
                .extracting("id", "position", "teamMemberStatus", "isLeader", "isDeleted", "createdAt", "updatedAt")
                .containsExactly(
                        tuple(teamMember2.getId(), teamMember2.getPosition(), teamMember2.getTeamMemberStatus(),
                                teamMember2.getIsLeader(), teamMember2.getIsDeleted(), teamMember2.getCreatedAt(),
                                teamMember2.getUpdatedAt())
                );

        assertEquals(1, teamMembers.size());
    }

    @Test
    @DisplayName("현재 소속된 팀이 있을시 현재 소속된 팀 여부를 확인한다.")
    void givenExistingCurrentTeam_whenExistsCurrent_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Team team1 = createTeam("프로젝트1");
        teamRepository.save(team1);
        TeamMember teamMember1 = createTeamMember(true, user, team1);
        teamMemberRepository.save(teamMember1);

        // when
        boolean result = teamMemberRepository.existsCurrent(user.getId());

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("현재 소속된 팀이 없을시 현재 소속된 팀 여부를 확인한다.")
    void givenNonExistingCurrentTeam_whenExistsCurrent_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        // when
        boolean result = teamMemberRepository.existsCurrent(user.getId());

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("팀에 소속되어 있을시 팀에 소속 여부를 확인한다.")
    void givenExistingTeamMember_whenExists_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Team team = createTeam("가보자잇");
        teamRepository.save(team);

        TeamMember teamMember = createTeamMember(true, user, team);
        teamMemberRepository.save(teamMember);

        // when
        boolean result = teamMemberRepository.exists(user.getId(), team.getId());

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("팀에 소속되어 있지 않을시 팀에 소속 여부를 확인한다.")
    void givenNonExistingTeamMember_whenExists_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Team team = createTeam("가보자잇");
        teamRepository.save(team);

        // when
        boolean result = teamMemberRepository.exists(user.getId(), team.getId());

        // then
        assertFalse(result);
    }

    private TeamMember createTeamMember(boolean isLeader, User user, Team team) {
        return TeamMember.builder()
                .position(user.getPosition())
                .isLeader(isLeader)
                .user(user)
                .team(team)
                .build();
    }

    private Team createTeam(String projectName) {
        return Team.builder()
                .projectName(projectName)
                .projectDescription("프로젝트 설명입니다.")
                .expectation("열정적인 팀원을 찾습니다.")
                .openChatUrl("kakao.com/o/gabojait")
                .designerMaxCnt((byte) 5)
                .backendMaxCnt((byte) 5)
                .frontendMaxCnt((byte) 5)
                .managerMaxCnt((byte) 5)
                .build();
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
        user.updatePosition(Position.BACKEND);
        userRepository.save(user);

        return user;
    }
}