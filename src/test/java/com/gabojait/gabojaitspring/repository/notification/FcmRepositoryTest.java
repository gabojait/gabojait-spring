package com.gabojait.gabojaitspring.repository.notification;

import com.gabojait.gabojaitspring.domain.notification.Fcm;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class FcmRepositoryTest {

    @Autowired private FcmRepository fcmRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ContactRepository contactRepository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private TeamMemberRepository teamMemberRepository;

    @Test
    @DisplayName("회원과 FCM 토큰으로 FCM 단건 조회를 한다.")
    void findByUserAndFcmToken() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");
        Fcm fcm = createSavedFcm(user, "fcm-token");

        // when
        Optional<Fcm> foundFcm = fcmRepository.findByUserAndFcmToken(user, fcm.getFcmToken());

        // then
        assertThat(foundFcm.get())
                .extracting("id", "fcmToken", "createdAt", "updatedAt")
                .containsExactly(fcm.getId(), fcm.getFcmToken(), fcm.getCreatedAt(), fcm.getUpdatedAt());
    }

    @Test
    @DisplayName("회원으로 FCM 전체 조회를 한다.")
    void findAllByUser() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Fcm fcm1 = createSavedFcm(user, "fcm-token1");
        Fcm fcm2 = createSavedFcm(user, "fcm-token2");
        Fcm fcm3 = createSavedFcm(user, "fcm-token3");

        // when
        List<Fcm> fcms = fcmRepository.findAllByUser(user);

        // then
        assertThat(fcms)
                .extracting("id", "fcmToken")
                .containsExactlyInAnyOrder(
                        tuple(fcm1.getId(), fcm1.getFcmToken()),
                        tuple(fcm2.getId(), fcm2.getFcmToken()),
                        tuple(fcm3.getId(), fcm3.getFcmToken())
                );
    }

    @Test
    @DisplayName("팀 식별자로 전체 팀원의 FCM을 전체 조회를 한다.")
    void findAllTeam() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼");

        Fcm fcm1 = createSavedFcm(user1, "fcm-token1");
        Fcm fcm2 = createSavedFcm(user2, "fcm-token2");
        Fcm fcm3 = createSavedFcm(user3, "fcm-token3");

        Team team = createSavedTeam();
        createSavedTeamMember(true, user1, team);
        createSavedTeamMember(false, user2, team);
        createSavedTeamMember(false, user3, team);

        // when
        List<String> fcmTokens = fcmRepository.findAllTeam(team.getId());

        // then
        assertThat(fcmTokens).containsExactlyInAnyOrder(fcm1.getFcmToken(), fcm2.getFcmToken(), fcm3.getFcmToken());
    }

    @Test
    @DisplayName("회원 식별자로 FCM을 전체 조회를 한다.")
    void findAllUser() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Fcm fcm1 = createSavedFcm(user, "fcm-token1");
        Fcm fcm2 = createSavedFcm(user, "fcm-token2");

        // when
        List<String> fcmTokens = fcmRepository.findAllUser(user.getId());

        // then
        assertThat(fcmTokens).containsExactlyInAnyOrder(fcm1.getFcmToken(), fcm2.getFcmToken());
    }

    @Test
    @DisplayName("한 회원을 제외한 전체 팀원의 FCM을 전체 조회를 한다.")
    void findAllTeamExceptUser() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼");

        Fcm fcm1 = createSavedFcm(user1, "fcm-token1");
        Fcm fcm2 = createSavedFcm(user2, "fcm-token2");
        Fcm fcm3 = createSavedFcm(user3, "fcm-token3");

        Team team = createSavedTeam();
        TeamMember teamMember1 = createSavedTeamMember(true, user1, team);
        TeamMember teamMember2 = createSavedTeamMember(false, user2, team);
        TeamMember teamMember3 = createSavedTeamMember(false, user3, team);
        teamMember3.updateTeamMemberStatus(TeamMemberStatus.QUIT);
        teamMemberRepository.save(teamMember3);

        // when
        List<String> fcms = fcmRepository.findAllTeamExceptUser(team.getId(), user3.getId());

        // then
        assertThat(fcms)
                .containsExactlyInAnyOrder(fcm1.getFcmToken(), fcm2.getFcmToken());
    }

    private Fcm createSavedFcm(User user, String fcmToken) {
        Fcm fcm = Fcm.builder()
                .user(user)
                .fcmToken(fcmToken)
                .build();
        fcmRepository.save(fcm);

        return fcm;
    }

    private TeamMember createSavedTeamMember(boolean isLeader, User user, Team team) {
        TeamMember teamMember = TeamMember.builder()
                .isLeader(isLeader)
                .position(user.getPosition())
                .user(user)
                .team(team)
                .build();
        return teamMemberRepository.save(teamMember);
    }

    private Team createSavedTeam() {
        Team team = Team.builder()
                .projectName("가보자잇")
                .projectDescription("설명입니다.")
                .expectation("바라는 점입니다.")
                .openChatUrl("kakao.com/o/gabojait")
                .designerMaxCnt((byte) 4)
                .backendMaxCnt((byte) 4)
                .frontendMaxCnt((byte) 4)
                .managerMaxCnt((byte) 4)
                .build();
        teamRepository.save(team);

        return team;
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