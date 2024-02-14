package com.gabojait.gabojaitspring.api.service.notification;

import com.gabojait.gabojaitspring.api.dto.notification.response.NotificationPageResponse;
import com.gabojait.gabojaitspring.common.response.PageData;
import com.gabojait.gabojaitspring.domain.notification.DeepLinkType;
import com.gabojait.gabojaitspring.domain.notification.Notification;
import com.gabojait.gabojaitspring.domain.offer.Offer;
import com.gabojait.gabojaitspring.domain.offer.OfferedBy;
import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.domain.user.User;
import com.gabojait.gabojaitspring.repository.notification.NotificationRepository;
import com.gabojait.gabojaitspring.repository.offer.OfferRepository;
import com.gabojait.gabojaitspring.repository.team.TeamMemberRepository;
import com.gabojait.gabojaitspring.repository.team.TeamRepository;
import com.gabojait.gabojaitspring.repository.user.ContactRepository;
import com.gabojait.gabojaitspring.repository.user.UserRepository;
import com.google.firebase.FirebaseApp;
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
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class NotificationServiceTest {

    @Autowired private NotificationService notificationService;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private TeamMemberRepository teamMemberRepository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ContactRepository contactRepository;
    @Autowired private OfferRepository offerRepository;
    @Autowired private FirebaseApp firebaseApp;

    @Test
    @DisplayName("알림 페이징 조회가 정상 작동한다")
    void givenValid_whenFindPageNotifications_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Notification notification1 = createSavedNotification(user, "알림 제목1", "알림1이 왔습니다.", DeepLinkType.HOME_PAGE);
        Notification notification2 = createSavedNotification(user, "알림 제목2", "알림2이 왔습니다.", DeepLinkType.HOME_PAGE);
        Notification notification3 = createSavedNotification(user, "알림 제목3", "알림3이 왔습니다.", DeepLinkType.HOME_PAGE);

        long pageFrom = Long.MAX_VALUE;
        int pageSize = 2;

        // when
        PageData<List<NotificationPageResponse>> responses =
                notificationService.findPageNotifications(user.getId(), pageFrom, pageSize);

        // then
        assertAll(
                () -> assertThat(responses.getData())
                        .extracting("notificationId", "title", "body", "isRead", "createdAt", "updatedAt")
                        .containsExactly(
                                tuple(notification3.getId(), notification3.getTitle(), notification3.getBody(),
                                        notification3.getIsRead(), notification3.getCreatedAt(),
                                        notification3.getUpdatedAt()),
                                tuple(notification2.getId(), notification2.getTitle(), notification2.getBody(),
                                        notification2.getIsRead(), notification2.getCreatedAt(),
                                        notification2.getUpdatedAt())
                        ),
                () -> assertThat(responses.getData())
                        .extracting("deepLink").extracting("url", "description")
                        .containsExactly(
                                tuple(notification3.getDeepLinkType().getUrl(),
                                        notification3.getDeepLinkType().getDescription()),
                                tuple(notification2.getDeepLinkType().getUrl(),
                                        notification2.getDeepLinkType().getDescription())
                        ),
                () -> assertThat(responses.getTotal()).isEqualTo(3)
        );
    }

    @Test
    @DisplayName("알림 읽기가 정상 작동한다")
    void giveValid_whenReadNotification_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Notification notification = createSavedNotification(user, "알림 제목", "알림이 왔습니다.", DeepLinkType.HOME_PAGE);

        // when
        notificationService.readNotification(user.getId(), notification.getId());

        // then
        Optional<Notification> foundNotification = notificationRepository.findUnread(user.getId(), notification.getId());

        assertThat(foundNotification).isEmpty();
    }

    @Test
    @DisplayName("이미 읽은 알림으로 알림 읽기가 정상 작동한다")
    void giveRead_whenReadNotification_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Notification notification = createSavedNotification(user, "알림 제목", "알림이 왔습니다.", DeepLinkType.HOME_PAGE);
        notification.read();
        notificationRepository.save(notification);

        // when
        notificationService.readNotification(user.getId(), notification.getId());

        // then
        Optional<Notification> foundNotification = notificationRepository.findUnread(user.getId(), notification.getId());

        assertThat(foundNotification).isEmpty();
    }

    @Test
    @DisplayName("새 팀원 합류 알림 전송이 정상 작동한다")
    void givenValid_whenSendTeamMemberJoin_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼");
        Team team = createSavedTeam();

        createSavedTeamMember(Position.MANAGER, true, user1, team);
        createSavedTeamMember(Position.DESIGNER, false, user2, team);

        Offer offer = createSavedOffer(OfferedBy.LEADER, Position.BACKEND, team, user3);

        // when
        notificationService.sendTeamMemberJoin(offer);

        // then
        List<Notification> notifications1 = notificationRepository.findAllByUser(user1);
        List<Notification> notifications2 = notificationRepository.findAllByUser(user2);
        List<Notification> notifications3 = notificationRepository.findAllByUser(user3);

        assertAll(
                () -> assertThat(notifications1)
                        .extracting("title", "body", "isRead", "deepLinkType", "isDeleted", "user")
                        .containsExactlyInAnyOrder(
                                tuple("새로운 " + offer.getPosition().getText() + " 합류",
                                        user3.getNickname() + "님이 " + offer.getPosition().getText() + "로 팀에 합류하였어요.",
                                        false, DeepLinkType.TEAM_PAGE, false, user1)
                        ),
                () -> assertThat(notifications2)
                        .extracting("title", "body", "isRead", "deepLinkType", "isDeleted", "user")
                        .containsExactlyInAnyOrder(
                                tuple("새로운 " + offer.getPosition().getText() + " 합류",
                                        user3.getNickname() + "님이 " + offer.getPosition().getText() + "로 팀에 합류하였어요.",
                                        false, DeepLinkType.TEAM_PAGE, false, user2)
                        ),
                () -> assertThat(notifications3)
                        .extracting("title", "body", "isRead", "deepLinkType", "isDeleted", "user")
                        .containsExactlyInAnyOrder(
                                tuple(team.getProjectName() + "팀 합류",
                                        offer.getPosition().getText() + "로서 역량을 펼쳐보세요!",
                                        false, DeepLinkType.TEAM_PAGE, false, user3)
                        )
        );
    }

    @Test
    @DisplayName("팀원 추방 알림 전송이 정상 작동한다")
    void givenValid_whenSendTeamMemberFired_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼");
        Team team = createSavedTeam();

        createSavedTeamMember(Position.MANAGER, true, user1, team);
        createSavedTeamMember(Position.DESIGNER, false, user2, team);
        createSavedTeamMember(Position.BACKEND, false, user3, team);

        // when
        notificationService.sendTeamMemberFired(user3, team);

        // then
        List<Notification> notifications1 = notificationRepository.findAllByUser(user1);
        List<Notification> notifications2 = notificationRepository.findAllByUser(user2);
        List<Notification> notifications3 = notificationRepository.findAllByUser(user3);

        assertAll(
                () -> assertThat(notifications1)
                        .extracting("title", "body", "isRead", "deepLinkType", "isDeleted", "user")
                        .containsExactlyInAnyOrder(
                                tuple(user3.getNickname() + "님 팀에서 추방",
                                        user3.getNickname() + "님이 팀장에 의해 추방되었어요.", false, DeepLinkType.TEAM_PAGE,
                                        false, user1)
                        ),
                () -> assertThat(notifications2)
                        .extracting("title", "body", "isRead", "deepLinkType", "isDeleted", "user")
                        .containsExactlyInAnyOrder(
                                tuple(user3.getNickname() + "님 팀에서 추방",
                                        user3.getNickname() + "님이 팀장에 의해 추방되었어요.", false, DeepLinkType.TEAM_PAGE,
                                        false, user2)
                        ),
                () -> assertThat(notifications3)
                        .extracting("title", "body", "isRead", "deepLinkType", "isDeleted", "user")
                        .containsExactlyInAnyOrder(
                                tuple(team.getProjectName() + "팀에서 추방",
                                        team.getProjectName() + "팀에서 추방 되었어요. 아쉽지만 새로운 팀을 찾아보세요.", false,
                                        DeepLinkType.HOME_PAGE, false, user3)
                        )
        );
    }

    @Test
    @DisplayName("팀원 탈퇴 알림 전송이 정상 작동한다")
    void givenValid_whenSendTeamMemberQuit_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼");
        Team team = createSavedTeam();

        createSavedTeamMember(Position.MANAGER, true, user1, team);
        createSavedTeamMember(Position.DESIGNER, false, user2, team);
        createSavedTeamMember(Position.BACKEND, false, user3, team);

        // when
        notificationService.sendTeamMemberQuit(user3, team);

        // then
        List<Notification> notifications1 = notificationRepository.findAllByUser(user1);
        List<Notification> notifications2 = notificationRepository.findAllByUser(user2);
        List<Notification> notifications3 = notificationRepository.findAllByUser(user3);

        assertAll(
                () -> assertThat(notifications1)
                        .extracting("title", "body", "isRead", "deepLinkType", "isDeleted", "user")
                        .containsExactlyInAnyOrder(
                                tuple(user3.getNickname() + "님 팀 탈퇴", user3.getNickname() + "님이 팀에서 탈퇴하였습니다.",
                                        false, DeepLinkType.TEAM_PAGE, false, user1)
                        ),
                () -> assertThat(notifications2)
                        .extracting("title", "body", "isRead", "deepLinkType", "isDeleted", "user")
                        .containsExactlyInAnyOrder(
                                tuple(user3.getNickname() + "님 팀 탈퇴", user3.getNickname() + "님이 팀에서 탈퇴하였습니다.",
                                        false, DeepLinkType.TEAM_PAGE, false, user2)
                        ),
                () -> assertThat(notifications3).isEmpty()
        );
    }

    @Test
    @DisplayName("팀 해산 알림 전송이 정상 작동한다")
    void givenValid_whenSendTeamIncomplete_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼");
        Team team = createSavedTeam();

        createSavedTeamMember(Position.MANAGER, true, user1, team);
        createSavedTeamMember(Position.DESIGNER, false, user2, team);
        createSavedTeamMember(Position.BACKEND, false, user3, team);

        // when
        notificationService.sendTeamIncomplete(team);

        // then
        List<Notification> notifications1 = notificationRepository.findAllByUser(user1);
        List<Notification> notifications2 = notificationRepository.findAllByUser(user2);
        List<Notification> notifications3 = notificationRepository.findAllByUser(user3);

        assertAll(
                () -> assertThat(notifications1)
                        .extracting("title", "body", "isRead", "deepLinkType", "isDeleted", "user")
                        .containsExactlyInAnyOrder(
                                tuple(team.getProjectName() + "팀 해산",
                                        "아쉽지만 팀장에 의해 " + team.getProjectName() + "팀이 해산 되었어요.", false,
                                        DeepLinkType.TEAM_PAGE, false, user1)
                        ),
                () -> assertThat(notifications2)
                        .extracting("title", "body", "isRead", "deepLinkType", "isDeleted", "user")
                        .containsExactlyInAnyOrder(
                                tuple(team.getProjectName() + "팀 해산",
                                        "아쉽지만 팀장에 의해 " + team.getProjectName() + "팀이 해산 되었어요.", false,
                                        DeepLinkType.TEAM_PAGE, false, user2)
                        ),
                () -> assertThat(notifications3)
                        .extracting("title", "body", "isRead", "deepLinkType", "isDeleted", "user")
                        .containsExactlyInAnyOrder(
                                tuple(team.getProjectName() + "팀 해산",
                                        "아쉽지만 팀장에 의해 " + team.getProjectName() + "팀이 해산 되었어요.", false,
                                        DeepLinkType.TEAM_PAGE, false, user3)
                        )
        );
    }

    @Test
    @DisplayName("팀 완료 알림 전송이 정상 작동한다")
    void givenValid_whenSendTeamComplete_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼");
        Team team = createSavedTeam();

        createSavedTeamMember(Position.MANAGER, true, user1, team);
        createSavedTeamMember(Position.DESIGNER, false, user2, team);
        createSavedTeamMember(Position.BACKEND, false, user3, team);

        // when
        notificationService.sendTeamComplete(team);

        // then
        List<Notification> notifications1 = notificationRepository.findAllByUser(user1);
        List<Notification> notifications2 = notificationRepository.findAllByUser(user2);
        List<Notification> notifications3 = notificationRepository.findAllByUser(user3);

        assertAll(
                () -> assertThat(notifications1)
                        .extracting("title", "body", "isRead", "deepLinkType", "isDeleted", "user")
                        .containsExactlyInAnyOrder(
                                tuple(team.getProjectName() + " 프로젝트 완료",
                                        "수고하셨어요! 프로젝트를 완료했어요. 팀원 리뷰를 작성해보세요!", false,
                                        DeepLinkType.REVIEW_PAGE, false, user1)
                        ),
                () -> assertThat(notifications2)
                        .extracting("title", "body", "isRead", "deepLinkType", "isDeleted", "user")
                        .containsExactlyInAnyOrder(
                                tuple(team.getProjectName() + " 프로젝트 완료",
                                        "수고하셨어요! 프로젝트를 완료했어요. 팀원 리뷰를 작성해보세요!", false,
                                        DeepLinkType.REVIEW_PAGE, false, user2)
                        ),
                () -> assertThat(notifications3)
                        .extracting("title", "body", "isRead", "deepLinkType", "isDeleted", "user")
                        .containsExactlyInAnyOrder(
                                tuple(team.getProjectName() + " 프로젝트 완료",
                                        "수고하셨어요! 프로젝트를 완료했어요. 팀원 리뷰를 작성해보세요!", false,
                                        DeepLinkType.REVIEW_PAGE, false, user3)
                        )
        );
    }

    @Test
    @DisplayName("팀 프로필 수정 알림 전송이 정상 작동한다")
    void givenValid_whenSendTeamProfileUpdated_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼");
        Team team = createSavedTeam();

        createSavedTeamMember(Position.MANAGER, true, user1, team);
        createSavedTeamMember(Position.DESIGNER, false, user2, team);
        createSavedTeamMember(Position.BACKEND, false, user3, team);

        // when
        notificationService.sendTeamProfileUpdated(team);

        // then
        List<Notification> notifications1 = notificationRepository.findAllByUser(user1);
        List<Notification> notifications2 = notificationRepository.findAllByUser(user2);
        List<Notification> notifications3 = notificationRepository.findAllByUser(user3);

        assertAll(
                () -> assertThat(notifications1)
                        .extracting("title", "body", "isRead", "deepLinkType", "isDeleted", "user")
                        .containsExactlyInAnyOrder(
                                tuple(team.getProjectName() + "팀 프로필 수정",
                                        team.getProjectName() + "팀 프로필이 팀장에 의해 수정되었어요.", false,
                                        DeepLinkType.TEAM_PAGE, false, user1)
                        ),
                () -> assertThat(notifications2)
                        .extracting("title", "body", "isRead", "deepLinkType", "isDeleted", "user")
                        .containsExactlyInAnyOrder(
                                tuple(team.getProjectName() + "팀 프로필 수정",
                                        team.getProjectName() + "팀 프로필이 팀장에 의해 수정되었어요.", false,
                                        DeepLinkType.TEAM_PAGE, false, user2)
                        ),
                () -> assertThat(notifications3)
                        .extracting("title", "body", "isRead", "deepLinkType", "isDeleted", "user")
                        .containsExactlyInAnyOrder(
                                tuple(team.getProjectName() + "팀 프로필 수정",
                                        team.getProjectName() + "팀 프로필이 팀장에 의해 수정되었어요.", false,
                                        DeepLinkType.TEAM_PAGE, false, user3)
                        )
        );
    }

    @Test
    @DisplayName("팀이 회원에게 제안 알림 전송이 정상 작동한다")
    void givenValid_whenSendOfferByTeam_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼");
        Team team = createSavedTeam();

        createSavedTeamMember(Position.MANAGER, true, user1, team);
        createSavedTeamMember(Position.DESIGNER, false, user2, team);

        Offer offer = createSavedOffer(OfferedBy.LEADER, Position.BACKEND, team, user3);

        // when
        notificationService.sendOfferByTeam(offer);

        // then
        List<Notification> notifications1 = notificationRepository.findAllByUser(user1);
        List<Notification> notifications2 = notificationRepository.findAllByUser(user2);
        List<Notification> notifications3 = notificationRepository.findAllByUser(user3);

        assertAll(
                () -> assertThat(notifications3)
                        .extracting("title", "body", "isRead", "deepLinkType", "isDeleted", "user")
                        .containsExactlyInAnyOrder(
                                tuple(offer.getPosition().toString() + " 스카웃 제의",
                                        offer.getTeam().getProjectName() + "팀에서 " + offer.getPosition().getText() + " 스카웃 제의가 왔어요!",
                                        false, DeepLinkType.USER_OFFER_RECEIVE_PAGE, false, user3)
                        ),
                () -> assertThat(notifications1).isEmpty(),
                () -> assertThat(notifications2).isEmpty()
        );
    }

    @Test
    @DisplayName("회원이 팀에게 제안 알림 전송이 정상 작동한다")
    void givenValid_whenSendOfferByUser_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼");
        Team team = createSavedTeam();

        createSavedTeamMember(Position.MANAGER, true, user1, team);
        createSavedTeamMember(Position.DESIGNER, false, user2, team);

        Offer offer = createSavedOffer(OfferedBy.USER, Position.FRONTEND, team, user3);

        // when
        notificationService.sendOfferByUser(offer);

        // then
        List<Notification> notifications1 = notificationRepository.findAllByUser(user1);
        List<Notification> notifications2 = notificationRepository.findAllByUser(user2);
        List<Notification> notifications3 = notificationRepository.findAllByUser(user3);

        assertAll(
                () -> assertThat(notifications1)
                        .extracting("title", "body", "isRead", "deepLinkType", "isDeleted", "user")
                        .containsExactlyInAnyOrder(
                                tuple(offer.getPosition().getText() + " 지원",
                                        offer.getPosition().getText() + " " + offer.getUser().getNickname() + "님이 지원을 했습니다.",
                                        false, DeepLinkType.TEAM_OFFER_RECEIVE_PAGE, false, user1)
                        ),
                () -> assertThat(notifications2).isEmpty(),
                () -> assertThat(notifications3).isEmpty()
        );
    }

    private Notification createSavedNotification(User user, String title, String body, DeepLinkType deepLinkType) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .body(body)
                .deepLinkType(deepLinkType)
                .build();

        return notificationRepository.save(notification);
    }

    private Offer createSavedOffer(OfferedBy offeredBy, Position position, Team team, User user) {
        Offer offer = Offer.builder()
                .offeredBy(offeredBy)
                .position(position)
                .team(team)
                .user(user)
                .build();

        return offerRepository.save(offer);
    }

    private void createSavedTeamMember(Position position, boolean isLeader, User user, Team team) {
        TeamMember teamMember = TeamMember.builder()
                .position(position)
                .isLeader(isLeader)
                .user(user)
                .team(team)
                .build();

        teamMemberRepository.save(teamMember);
    }

    private Team createSavedTeam() {
        Team team = Team.builder()
                .projectName("가보자잇")
                .projectDescription("프로젝트 설명입니다.")
                .expectation("열정적인 팀원을 구합니다.")
                .openChatUrl("kakao.com/o/gabojait")
                .designerMaxCnt((byte) 3)
                .backendMaxCnt((byte) 3)
                .frontendMaxCnt((byte) 3)
                .designerMaxCnt((byte) 3)
                .managerMaxCnt((byte) 3)
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