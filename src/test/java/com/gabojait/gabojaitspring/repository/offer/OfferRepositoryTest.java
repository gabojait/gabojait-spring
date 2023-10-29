package com.gabojait.gabojaitspring.repository.offer;

import com.gabojait.gabojaitspring.domain.offer.Offer;
import com.gabojait.gabojaitspring.domain.offer.OfferedBy;
import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class OfferRepositoryTest {

    @Autowired private OfferRepository offerRepository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private TeamMemberRepository teamMemberRepository;
    @Autowired private ContactRepository contactRepository;
    @Autowired private UserRepository userRepository;

    @Test
    @DisplayName("회원의 제안 전체 조회를 한다.")
    void findAllByUserId() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        Team team = createSavedTeam("가보자잇");
        TeamMember teamMember = createSavedTeamMember(Position.BACKEND, true, user1, team);

        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");

        Offer offer1 = createSavedOffer(OfferedBy.LEADER, Position.DESIGNER, user2, team);
        Offer offer2 = createSavedOffer(OfferedBy.LEADER, Position.BACKEND, user2, team);
        Offer offer3 = createSavedOffer(OfferedBy.LEADER, Position.FRONTEND, user2, team);
        Offer offer4 = createSavedOffer(OfferedBy.LEADER, Position.MANAGER, user2, team);
        offerRepository.saveAll(List.of(offer1, offer2, offer3, offer4));

        // when
        List<Offer> offers = offerRepository.findAllByUserId(user2.getId(), user1.getId());

        // then
        assertThat(offers)
                .extracting("id", "offeredBy", "position", "isAccepted", "isDeleted", "createdAt", "updatedAt")
                .containsExactly(
                        tuple(offer4.getId(), offer4.getOfferedBy(), offer4.getPosition(), offer4.getIsAccepted(),
                                offer4.getIsDeleted(), offer4.getCreatedAt(), offer4.getUpdatedAt()),
                        tuple(offer3.getId(), offer3.getOfferedBy(), offer3.getPosition(), offer3.getIsAccepted(),
                                offer3.getIsDeleted(), offer3.getCreatedAt(), offer3.getUpdatedAt()),
                        tuple(offer2.getId(), offer2.getOfferedBy(), offer2.getPosition(), offer2.getIsAccepted(),
                                offer2.getIsDeleted(), offer2.getCreatedAt(), offer2.getUpdatedAt()),
                        tuple(offer1.getId(), offer1.getOfferedBy(), offer1.getPosition(), offer1.getIsAccepted(),
                                offer1.getIsDeleted(), offer1.getCreatedAt(), offer1.getUpdatedAt())
                );
    }

    @Test
    @DisplayName("팀장인 회원으로 여러 회원의 제안 전체 조회를 한다.")
    void givenTeamLeader_whenFindAllInUserIds_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        Team team = createSavedTeam("가보자잇");
        TeamMember teamMember = createSavedTeamMember(Position.BACKEND, true, user1, team);

        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼");

        Offer offer1 = createSavedOffer(OfferedBy.LEADER, Position.BACKEND, user2, team);
        Offer offer2 = createSavedOffer(OfferedBy.LEADER, Position.BACKEND, user3, team);
        offerRepository.saveAll(List.of(offer1, offer2));

        // when
        List<Offer> offers = offerRepository.findAllInUserIds(List.of(user2.getId(), user3.getId()), user1.getId());

        // then
        assertThat(offers)
                .extracting("id", "offeredBy", "position", "isAccepted", "isDeleted", "createdAt", "updatedAt")
                .containsExactly(
                        tuple(offer2.getId(), offer2.getOfferedBy(), offer2.getPosition(), offer2.getIsAccepted(),
                                offer2.getIsDeleted(), offer2.getCreatedAt(), offer2.getUpdatedAt()),
                        tuple(offer1.getId(), offer1.getOfferedBy(), offer1.getPosition(), offer1.getIsAccepted(),
                                offer1.getIsDeleted(), offer1.getCreatedAt(), offer1.getUpdatedAt())
                );
    }

    @Test
    @DisplayName("팀원인 회원으로 여러 회원의 제안 전체 조회를 한다.")
    void givenTeamMember_whenFindAllInUserIds_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        Team team = createSavedTeam("가보자잇");
        TeamMember teamMember = createSavedTeamMember(Position.BACKEND, true, user1, team);

        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼");
        User user4 = createSavedDefaultUser("tester4@gabojait.com", "tester4", "테스터사");

        Offer offer1 = createSavedOffer(OfferedBy.LEADER, Position.BACKEND, user2, team);
        Offer offer2 = createSavedOffer(OfferedBy.LEADER, Position.BACKEND, user3, team);
        Offer offer3 = createSavedOffer(OfferedBy.LEADER, Position.BACKEND, user4, team);
        offerRepository.saveAll(List.of(offer1, offer2, offer3));

        // when
        List<Offer> offers = offerRepository.findAllInUserIds(List.of(user3.getId(), user4.getId()), user2.getId());

        // then
        assertThat(offers).isEmpty();
    }

    @Test
    @DisplayName("회원 식별자와 팀 식별자로 제안 전체 조회를 한다.")
    void findAllByTeamId() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        Team team = createSavedTeam("가보자잇");
        TeamMember teamMember = createSavedTeamMember(Position.BACKEND, true, user1, team);

        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");

        Offer offer1 = createSavedOffer(OfferedBy.LEADER, Position.BACKEND, user2, team);
        Offer offer2 = createSavedOffer(OfferedBy.LEADER, Position.FRONTEND, user2, team);

        // when
        List<Offer> offers = offerRepository.findAllByTeamId(user2.getId(), team.getId());

        // then
        assertThat(offers)
                .extracting("id", "offeredBy", "position", "isAccepted", "isDeleted", "createdAt", "updatedAt")
                .containsExactlyInAnyOrder(
                        tuple(offer2.getId(), offer2.getOfferedBy(), offer2.getPosition(), offer2.getIsAccepted(),
                                offer2.getIsDeleted(), offer2.getCreatedAt(), offer2.getUpdatedAt()),
                        tuple(offer1.getId(), offer1.getOfferedBy(), offer1.getPosition(), offer1.getIsAccepted(),
                                offer1.getIsDeleted(), offer1.getCreatedAt(), offer1.getUpdatedAt())
                );
    }

    @Test
    @DisplayName("회원 식별자와 제안 식별자로 제안 단건 조회를 한다.")
    void findFetchTeam()  {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        Team team = createSavedTeam("가보자잇1");
        TeamMember teamMember = createSavedTeamMember(Position.BACKEND, true, user1, team);
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");

        Offer offer = createSavedOffer(OfferedBy.LEADER, Position.DESIGNER, user2, team);

        // when
        Optional<Offer> foundOffer = offerRepository.findFetchTeam(user2.getId(), offer.getId(), OfferedBy.LEADER);

        // then
        assertThat(foundOffer.get())
                .extracting("id", "offeredBy", "position", "isAccepted", "isDeleted", "createdAt", "updatedAt")
                .containsExactly(offer.getId(), offer.getOfferedBy(), offer.getPosition(), offer.getIsAccepted(),
                        offer.getIsDeleted(), offer.getCreatedAt(), offer.getUpdatedAt());
    }

    @Test
    @DisplayName("팀 식별자와 제안 식별자로 제안 단건 조회를 한다.")
    void findFetchUser()  {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        Team team = createSavedTeam("가보자잇1");
        TeamMember teamMember = createSavedTeamMember(Position.BACKEND, true, user1, team);
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");

        Offer offer = createSavedOffer(OfferedBy.USER, Position.DESIGNER, user2, team);

        // when
        Optional<Offer> foundOffer = offerRepository.findFetchUser(team.getId(), offer.getId(), OfferedBy.USER);

        // then
        assertThat(foundOffer.get())
                .extracting("id", "offeredBy", "position", "isAccepted", "isDeleted", "createdAt", "updatedAt")
                .containsExactly(offer.getId(), offer.getOfferedBy(), offer.getPosition(), offer.getIsAccepted(),
                        offer.getIsDeleted(), offer.getCreatedAt(), offer.getUpdatedAt());
    }

    @Test
    @DisplayName("회원이 받은 제안 페이징 조회를 한다.")
    void givenReceivedByUser_whenFindPageFetchUser_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");

        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");
        Team team1 = createSavedTeam("가보자잇1");
        TeamMember teamMember1 = createSavedTeamMember(Position.BACKEND, true, user2, team1);
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼");
        Team team2 = createSavedTeam("가보자잇2");
        TeamMember teamMember2 = createSavedTeamMember(Position.BACKEND, true, user3, team2);

        Offer offer1 = createSavedOffer(OfferedBy.LEADER, Position.DESIGNER, user1, team1);
        Offer offer2 = createSavedOffer(OfferedBy.LEADER, Position.DESIGNER, user1, team2);

        long pageFrom = Long.MAX_VALUE;
        int pageSize = 1;
        OfferedBy offeredBy = OfferedBy.LEADER;

        // when
        Page<Offer> offers = offerRepository.findPageFetchUser(user1.getId(), offeredBy, pageFrom, pageSize);

        // then
        assertThat(offers)
                .extracting("id", "offeredBy", "position", "isAccepted", "isDeleted", "createdAt", "updatedAt")
                .containsExactly(
                        tuple(offer2.getId(), offer2.getOfferedBy(), offer2.getPosition(), offer2.getIsAccepted(),
                                offer2.getIsDeleted(), offer2.getCreatedAt(), offer2.getUpdatedAt())
                );

        assertEquals(offers.getSize(), pageSize);
        assertEquals(offers.getTotalElements(), 2);
    }

    @Test
    @DisplayName("회원이 보낸 제안 페이징 조회를 한다.")
    void givneSentByUser_whenFindPageFetchUser_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");

        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");
        Team team1 = createSavedTeam("가보자잇1");
        TeamMember teamMember1 = createSavedTeamMember(Position.BACKEND, true, user2, team1);
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼");
        Team team2 = createSavedTeam("가보자잇2");
        TeamMember teamMember2 = createSavedTeamMember(Position.BACKEND, true, user3, team2);

        Offer offer1 = createSavedOffer(OfferedBy.USER, Position.DESIGNER, user1, team1);
        Offer offer2 = createSavedOffer(OfferedBy.USER, Position.DESIGNER, user1, team2);

        long pageFrom = Long.MAX_VALUE;
        int pageSize = 1;
        OfferedBy offeredBy = OfferedBy.USER;

        // when
        Page<Offer> offers = offerRepository.findPageFetchUser(user1.getId(), offeredBy, pageFrom, pageSize);

        // then
        assertThat(offers)
                .extracting("id", "offeredBy", "position", "isAccepted", "isDeleted", "createdAt", "updatedAt")
                .containsExactly(
                        tuple(offer2.getId(), offer2.getOfferedBy(), offer2.getPosition(), offer2.getIsAccepted(),
                                offer2.getIsDeleted(), offer2.getCreatedAt(), offer2.getUpdatedAt())
                );

        assertEquals(offers.getSize(), pageSize);
        assertEquals(offers.getTotalElements(), 2);
    }

    @Test
    @DisplayName("팀이 받은 제안 페이징 조회를 한다.")
    void givenReceivedByTeam_whenFindPageFetchTeam_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        Team team = createSavedTeam("가보자잇");
        TeamMember teamMember = createSavedTeamMember(Position.BACKEND, true, user1, team);

        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼");

        Offer offer1 = createSavedOffer(OfferedBy.USER, Position.DESIGNER, user2, team);
        Offer offer2 = createSavedOffer(OfferedBy.USER, Position.DESIGNER, user3, team);

        OfferedBy offeredBy = OfferedBy.USER;
        Position position = Position.DESIGNER;
        long pageFrom = Long.MAX_VALUE;
        int pageSize = 1;

        // when
        Page<Offer> offers = offerRepository.findPageFetchTeam(team.getId(), position, offeredBy, pageFrom, pageSize);

        // then
        assertThat(offers)
                .extracting("id", "offeredBy", "position", "isAccepted", "isDeleted", "createdAt", "updatedAt")
                .containsExactly(
                        tuple(offer2.getId(), offer2.getOfferedBy(), offer2.getPosition(), offer2.getIsAccepted(),
                                offer2.getIsDeleted(), offer2.getCreatedAt(), offer2.getUpdatedAt())
                );

        assertEquals(offers.getSize(), pageSize);
        assertEquals(offers.getTotalElements(), 2);
    }

    @Test
    @DisplayName("팀이 보낸 제안 페이징 조회를 한다.")
    void givenSentByTeam_whenFindPageFetchTeam_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        Team team = createSavedTeam("가보자잇");
        TeamMember teamMember = createSavedTeamMember(Position.BACKEND, true, user1, team);

        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼");

        Offer offer1 = createSavedOffer(OfferedBy.LEADER, Position.DESIGNER, user2, team);
        Offer offer2 = createSavedOffer(OfferedBy.LEADER, Position.DESIGNER, user3, team);

        OfferedBy offeredBy = OfferedBy.LEADER;
        Position position = Position.DESIGNER;
        long pageFrom = Long.MAX_VALUE;
        int pageSize = 1;

        // when
        Page<Offer> offers = offerRepository.findPageFetchTeam(team.getId(), position, offeredBy, pageFrom, pageSize);

        // then
        assertThat(offers)
                .extracting("id", "offeredBy", "position", "isAccepted", "isDeleted", "createdAt", "updatedAt")
                .containsExactly(
                        tuple(offer2.getId(), offer2.getOfferedBy(), offer2.getPosition(), offer2.getIsAccepted(),
                                offer2.getIsDeleted(), offer2.getCreatedAt(), offer2.getUpdatedAt())
                );

        assertEquals(offers.getSize(), pageSize);
        assertEquals(offers.getTotalElements(), 2);
    }

    private Offer createSavedOffer(OfferedBy offeredBy, Position position, User user, Team team) {
        Offer offer = Offer.builder()
                .offeredBy(offeredBy)
                .position(position)
                .user(user)
                .team(team)
                .build();

        return offerRepository.save(offer);
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