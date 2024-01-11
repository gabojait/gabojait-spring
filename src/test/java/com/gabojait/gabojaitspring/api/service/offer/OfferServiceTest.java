package com.gabojait.gabojaitspring.api.service.offer;

import com.gabojait.gabojaitspring.api.dto.common.response.PageData;
import com.gabojait.gabojaitspring.api.dto.offer.request.OfferCreateRequest;
import com.gabojait.gabojaitspring.api.dto.offer.request.OfferDecideRequest;
import com.gabojait.gabojaitspring.api.dto.offer.response.OfferPageResponse;
import com.gabojait.gabojaitspring.domain.offer.Offer;
import com.gabojait.gabojaitspring.domain.offer.OfferedBy;
import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
import com.gabojait.gabojaitspring.domain.team.TeamMemberStatus;
import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.domain.user.User;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.repository.offer.OfferRepository;
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

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class OfferServiceTest {

    @Autowired private OfferService offerService;
    @Autowired private OfferRepository offerRepository;
    @Autowired private ContactRepository contactRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private TeamMemberRepository teamMemberRepository;

    @Test
    @DisplayName("회원이 팀에 제안을 한다.")
    void givenValid_whenOfferByUser_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일", Position.BACKEND);
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이", Position.FRONTEND);
        Team team = createSavedTeam((byte) 2);
        TeamMember teamMember = createdSavedTeamMember(true, user1, team);

        OfferCreateRequest request = createValidOfferCreateRequest();

        // when
        offerService.offerByUser(user2.getUsername(), team.getId(), request);

        // then
        List<Offer> offers = offerRepository.findAllByUserId(user2.getId(), user1.getId());

        assertThat(offers)
                .extracting("offeredBy", "position", "isAccepted", "isDeleted")
                .containsExactly(
                        tuple(OfferedBy.USER, Position.valueOf(request.getOfferPosition()), null, false)
                );
    }

    @Test
    @DisplayName("존재하지 않은 회원으로 팀에 제안시 예외가 발생한다.")
    void givenNonExistingUser_whenOfferByUser_thenThrow() {
        // given
        String username = "tester";
        long teamId = 1L;

        OfferCreateRequest request = createValidOfferCreateRequest();

        // when & then
        assertThatThrownBy(() -> offerService.offerByUser(username, teamId, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("존재하지 않은 팀에 회원이 팀에 제안시 예외가 발생한다.")
    void givenNonExistingTeam_whenOfferByUser_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);
        long teamId = 1L;

        OfferCreateRequest request = createValidOfferCreateRequest();

        // when & then
        assertThatThrownBy(() -> offerService.offerByUser(user.getUsername(), teamId, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TEAM_NOT_FOUND);
    }

    @Test
    @DisplayName("팀에 포지션이 찼는데 회원이 팀에 제안시 예외가 발생한다.")
    void givenUnavailablePosition_whenOfferByUser_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);
        Team team = createSavedTeam((byte) 0);

        OfferCreateRequest request = createValidOfferCreateRequest();

        // when & then
        assertThatThrownBy(() -> offerService.offerByUser(user.getUsername(), team.getId(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TEAM_POSITION_UNAVAILABLE);
    }

    @Test
    @DisplayName("팀이 회원에 제안한다.")
    void givenValid_whenOfferByTeam_thenReturn() {
        //  given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일", Position.BACKEND);
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이", Position.FRONTEND);
        Team team = createSavedTeam((byte) 2);
        TeamMember teamMember = createdSavedTeamMember(true, user1, team);

        OfferCreateRequest request = createValidOfferCreateRequest();

        // when
        offerService.offerByTeam(user1.getUsername(), user2.getId(), request);

        // then
        List<Offer> offers = offerRepository.findAllByUserId(user2.getId(), user1.getId());

        assertThat(offers)
                .extracting("offeredBy", "position", "isAccepted", "isDeleted")
                .containsExactly(
                        tuple(OfferedBy.LEADER, Position.valueOf(request.getOfferPosition()), null, false)
                );
    }

    @Test
    @DisplayName("팀장이 아닌 회원으로 팀이 회원에 제안을 하면 예외가 발생한다.")
    void givenNonLeader_whenOfferByTeam_thenThrow() {
        //  given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일", Position.BACKEND);
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이", Position.FRONTEND);
        Team team = createSavedTeam((byte) 2);
        TeamMember teamMember = createdSavedTeamMember(false, user1, team);

        OfferCreateRequest request = createValidOfferCreateRequest();

        // when & then
        assertThatThrownBy(() -> offerService.offerByTeam(user1.getUsername(), user2.getId(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(REQUEST_FORBIDDEN);
    }

    @Test
    @DisplayName("존재하지 않은 회원으로 팀이 회원에 제안을 하면 예외가 발생한다.")
    void givenFromNonExistingUser_whenOfferByTeam_thenThrow() {
        //  given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일", Position.BACKEND);
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이", Position.FRONTEND);

        OfferCreateRequest request = createValidOfferCreateRequest();

        // when & then
        assertThatThrownBy(() -> offerService.offerByTeam(user1.getUsername(), user2.getId(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CURRENT_TEAM_NOT_FOUND);
    }

    @Test
    @DisplayName("존재하지 않은 회원에게 팀이 회원에 제안을 하면 예외가 발생한다.")
    void givenToNonExistingUser_whenOfferByTeam_thenThrow() {
        //  given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일", Position.BACKEND);
        Team team = createSavedTeam((byte) 2);
        TeamMember teamMember = createdSavedTeamMember(true, user1, team);

        long userId = 2L;

        OfferCreateRequest request = createValidOfferCreateRequest();

        // when & then
        assertThatThrownBy(() -> offerService.offerByTeam(user1.getUsername(), userId, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("존재하지 않은 팀으로 팀이 회원에 제안을 하면 예외가 발생한다.")
    void givenNonExisingTeam_whenOfferByTeam_thenThrow() {
        //  given
        String username = "tester1";
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이", Position.FRONTEND);

        OfferCreateRequest request = createValidOfferCreateRequest();

        // when & then
        assertThatThrownBy(() -> offerService.offerByTeam(username, user2.getId(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("꽉찬 포지션으로 팀이 회원에 제안을 하면 예외가 발생한다.")
    void givenUnavailablePosition_whenOfferByTeam_thenThrow() {
        //  given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일", Position.BACKEND);
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이", Position.BACKEND);
        Team team = createSavedTeam((byte) 1);
        TeamMember teamMember = createdSavedTeamMember(true, user1, team);

        OfferCreateRequest request = createValidOfferCreateRequest();

        // when & then
        assertThatThrownBy(() -> offerService.offerByTeam(user1.getUsername(), user2.getId(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TEAM_POSITION_UNAVAILABLE);
    }

    @Test
    @DisplayName("회원이 받은 회원 관련 제안 페이징 조회를 한다.")
    void givenOfferedByLeader_whenFindPageUserOffer_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일", Position.BACKEND);
        Team team1 = createSavedTeam((byte) 1);
        TeamMember teamMember1 = createdSavedTeamMember(true, user1, team1);
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이", Position.BACKEND);
        Team team2 = createSavedTeam((byte) 1);
        TeamMember teamMember2 = createdSavedTeamMember(true, user2, team2);
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼", Position.BACKEND);

        Offer offer1 = createSavedOffer(team1, user3, OfferedBy.LEADER, Position.DESIGNER);
        Offer offer2 = createSavedOffer(team2, user3, OfferedBy.LEADER, Position.DESIGNER);

        OfferedBy offeredBy = OfferedBy.LEADER;
        long pageFrom = Long.MAX_VALUE;
        int pageSize = 1;

        // when
        PageData<List<OfferPageResponse>> responses = offerService.findPageUserOffer(user3.getUsername(), offeredBy,
                pageFrom, pageSize);

        // then
        assertThat(responses.getData())
                .extracting("offerId", "position", "isAccepted", "offeredBy", "createdAt", "updatedAt")
                .containsExactly(
                        tuple(offer2.getId(), offer2.getPosition(), offer2.getIsAccepted(), offer2.getOfferedBy(),
                                offer2.getCreatedAt(), offer2.getUpdatedAt())
                );
        assertThat(responses.getData().get(0).getUser())
                .extracting("userId", "nickname", "position", "reviewCnt", "rating", "createdAt", "updatedAt")
                .containsExactly(user3.getId(), user3.getNickname(), user3.getPosition(), user3.getReviewCnt(),
                        user3.getRating(), user3.getCreatedAt(), user3.getUpdatedAt());

        assertThat(responses.getData().get(0).getTeam())
                .extracting("teamId", "projectName", "designerCurrentCnt", "backendCurrentCnt", "frontendCurrentCnt",
                        "managerCurrentCnt", "designerMaxCnt", "backendMaxCnt", "frontendMaxCnt", "managerMaxCnt",
                        "createdAt", "updatedAt")
                .containsExactly(team2.getId(), team2.getProjectName(), team2.getDesignerCurrentCnt(),
                        team2.getBackendCurrentCnt(), team2.getFrontendCurrentCnt(), team2.getManagerCurrentCnt(),
                        team2.getDesignerMaxCnt(), team2.getBackendMaxCnt(), team2.getFrontendMaxCnt(),
                        team2.getManagerMaxCnt(), team2.getCreatedAt(), team2.getUpdatedAt());


        assertEquals(pageSize, responses.getData().size());
        assertEquals(2L, responses.getTotal());
    }

    @Test
    @DisplayName("회원이 보낸 회원 관련 제안 페이징 조회를 한다.")
    void givenOfferedByUser_whenFindPageUserOffer_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일", Position.BACKEND);
        Team team1 = createSavedTeam((byte) 1);
        TeamMember teamMember1 = createdSavedTeamMember(true, user1, team1);
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이", Position.BACKEND);
        Team team2 = createSavedTeam((byte) 1);
        TeamMember teamMember2 = createdSavedTeamMember(true, user2, team2);
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼", Position.BACKEND);

        Offer offer1 = createSavedOffer(team1, user3, OfferedBy.USER, Position.DESIGNER);
        Offer offer2 = createSavedOffer(team2, user3, OfferedBy.USER, Position.DESIGNER);

        OfferedBy offeredBy = OfferedBy.USER;
        long pageFrom = Long.MAX_VALUE;
        int pageSize = 1;

        // when
        PageData<List<OfferPageResponse>> responses = offerService.findPageUserOffer(user3.getUsername(), offeredBy,
                pageFrom, pageSize);

        // then
        assertThat(responses.getData())
                .extracting("offerId", "position", "isAccepted", "offeredBy", "createdAt", "updatedAt")
                .containsExactly(
                        tuple(offer2.getId(), offer2.getPosition(), offer2.getIsAccepted(), offer2.getOfferedBy(),
                                offer2.getCreatedAt(), offer2.getUpdatedAt())
                );
        assertThat(responses.getData().get(0).getUser())
                .extracting("userId", "nickname", "position", "reviewCnt", "rating", "createdAt", "updatedAt")
                .containsExactly(user3.getId(), user3.getNickname(), user3.getPosition(), user3.getReviewCnt(),
                        user3.getRating(), user3.getCreatedAt(), user3.getUpdatedAt());

        assertThat(responses.getData().get(0).getTeam())
                .extracting("teamId", "projectName", "designerCurrentCnt", "backendCurrentCnt", "frontendCurrentCnt",
                        "managerCurrentCnt", "designerMaxCnt", "backendMaxCnt", "frontendMaxCnt", "managerMaxCnt",
                        "createdAt", "updatedAt")
                .containsExactly(team2.getId(), team2.getProjectName(), team2.getDesignerCurrentCnt(),
                        team2.getBackendCurrentCnt(), team2.getFrontendCurrentCnt(), team2.getManagerCurrentCnt(),
                        team2.getDesignerMaxCnt(), team2.getBackendMaxCnt(), team2.getFrontendMaxCnt(),
                        team2.getManagerMaxCnt(), team2.getCreatedAt(), team2.getUpdatedAt());


        assertEquals(pageSize, responses.getData().size());
        assertEquals(2L, responses.getTotal());
    }

    @Test
    @DisplayName("존재하지 않은 회원으로 팀에게 받은 제안 페이징 조회를 하면 예외가 발생한다.")
    void givenNonExistingUser_whenFindPageUserOffer_thenThrow() {
        // given
        String username = "tester";

        OfferedBy offeredBy = OfferedBy.LEADER;
        long pageFrom = Long.MAX_VALUE;
        int pageSize = 1;

        // when & then
        assertThatThrownBy(() -> offerService.findPageUserOffer(username, offeredBy, pageFrom, pageSize))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("팀이 받은 팀 관련 제안 페이징 조회를 한다.")
    void givenOfferedByUser_whenFindPageTeamOffer_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일", Position.BACKEND);
        Team team = createSavedTeam((byte) 1);
        TeamMember teamMember = createdSavedTeamMember(true, user1, team);

        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이", Position.BACKEND);
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼", Position.BACKEND);
        Offer offer1 = createSavedOffer(team, user2, OfferedBy.USER, Position.DESIGNER);
        Offer offer2 = createSavedOffer(team, user3, OfferedBy.USER, Position.DESIGNER);

        Position position = Position.DESIGNER;
        OfferedBy offeredBy = OfferedBy.USER;
        long pageFrom = Long.MAX_VALUE;
        int pageSize = 1;

        // when
        PageData<List<OfferPageResponse>> responses = offerService.findPageTeamOffer(user1.getUsername(), position,
                offeredBy, pageFrom, pageSize);

        // then
        assertThat(responses.getData())
                .extracting("offerId", "position", "isAccepted", "offeredBy", "createdAt", "updatedAt")
                .containsExactly(
                        tuple(offer2.getId(), offer2.getPosition(), offer2.getIsAccepted(), offer2.getOfferedBy(),
                                offer2.getCreatedAt(), offer2.getUpdatedAt())
                );

        assertThat(responses.getData().get(0).getUser())
                .extracting("userId", "nickname", "position", "reviewCnt", "rating", "createdAt", "updatedAt")
                .containsExactly(user3.getId(), user3.getNickname(), user3.getPosition(), user3.getReviewCnt(),
                        user3.getRating(), user3.getCreatedAt(), user3.getUpdatedAt());

        assertThat(responses.getData().get(0).getTeam())
                .extracting("teamId", "projectName", "designerCurrentCnt", "backendCurrentCnt", "frontendCurrentCnt",
                        "managerCurrentCnt", "designerMaxCnt", "backendMaxCnt", "frontendMaxCnt", "managerMaxCnt",
                        "createdAt", "updatedAt")
                .containsExactly(team.getId(), team.getProjectName(), team.getDesignerCurrentCnt(),
                        team.getBackendCurrentCnt(), team.getFrontendCurrentCnt(), team.getManagerCurrentCnt(),
                        team.getDesignerMaxCnt(), team.getBackendMaxCnt(), team.getFrontendMaxCnt(),
                        team.getManagerMaxCnt(), team.getCreatedAt(), team.getUpdatedAt());

        assertEquals(pageSize, responses.getData().size());
        assertEquals(2L, responses.getTotal());
    }

    @Test
    @DisplayName("팀이 보낸 팀 관련 제안 페이징 조회를 한다.")
    void givenOfferedByLeader_whenFindPageTeamOffer_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일", Position.BACKEND);
        Team team = createSavedTeam((byte) 1);
        TeamMember teamMember = createdSavedTeamMember(true, user1, team);

        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이", Position.BACKEND);
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼", Position.BACKEND);
        Offer offer1 = createSavedOffer(team, user2, OfferedBy.LEADER, Position.DESIGNER);
        Offer offer2 = createSavedOffer(team, user3, OfferedBy.LEADER, Position.DESIGNER);

        Position position = Position.DESIGNER;
        OfferedBy offeredBy = OfferedBy.LEADER;
        long pageFrom = Long.MAX_VALUE;
        int pageSize = 1;

        // when
        PageData<List<OfferPageResponse>> responses = offerService.findPageTeamOffer(user1.getUsername(), position,
                offeredBy, pageFrom, pageSize);

        // then
        assertThat(responses.getData())
                .extracting("offerId", "position", "isAccepted", "offeredBy", "createdAt", "updatedAt")
                .containsExactly(
                        tuple(offer2.getId(), offer2.getPosition(), offer2.getIsAccepted(), offer2.getOfferedBy(),
                                offer2.getCreatedAt(), offer2.getUpdatedAt())
                );

        assertThat(responses.getData().get(0).getUser())
                .extracting("userId", "nickname", "position", "reviewCnt", "rating", "createdAt", "updatedAt")
                .containsExactly(user3.getId(), user3.getNickname(), user3.getPosition(), user3.getReviewCnt(),
                        user3.getRating(), user3.getCreatedAt(), user3.getUpdatedAt());

        assertThat(responses.getData().get(0).getTeam())
                .extracting("teamId", "projectName", "designerCurrentCnt", "backendCurrentCnt", "frontendCurrentCnt",
                        "managerCurrentCnt", "designerMaxCnt", "backendMaxCnt", "frontendMaxCnt", "managerMaxCnt",
                        "createdAt", "updatedAt")
                .containsExactly(team.getId(), team.getProjectName(), team.getDesignerCurrentCnt(),
                        team.getBackendCurrentCnt(), team.getFrontendCurrentCnt(), team.getManagerCurrentCnt(),
                        team.getDesignerMaxCnt(), team.getBackendMaxCnt(), team.getFrontendMaxCnt(),
                        team.getManagerMaxCnt(), team.getCreatedAt(), team.getUpdatedAt());

        assertEquals(pageSize, responses.getData().size());
        assertEquals(2L, responses.getTotal());
    }

    @Test
    @DisplayName("팀장이 아닐시 회원에게 받은 제안 페이징 조회를 하면 예외가 발생한다.")
    void givenNonLeader_whenFindPageTeamOffer_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);
        Team team = createSavedTeam((byte) 1);
        TeamMember teamMember = createdSavedTeamMember(false, user, team);

        OfferedBy offeredBy = OfferedBy.LEADER;
        Position position = Position.DESIGNER;
        long pageFrom = Long.MAX_VALUE;
        int pageSize = 1;

        // when
        assertThatThrownBy(() -> offerService.findPageTeamOffer(user.getUsername(), position, offeredBy, pageFrom, pageSize))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(REQUEST_FORBIDDEN);
    }

    @Test
    @DisplayName("존재하지 않은 회원으로 회원에게 받은 제안 페이징 조회를 하면 예외가 발생한다.")
    void givenNonExistingUser_whenFindPageTeamOffer_thenThrow() {
        // given
        String username = "tester";

        OfferedBy offeredBy = OfferedBy.LEADER;
        Position position = Position.DESIGNER;
        long pageFrom = Long.MAX_VALUE;
        int pageSize = 1;

        // when
        assertThatThrownBy(() -> offerService.findPageTeamOffer(username, position, offeredBy, pageFrom, pageSize))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("현재 팀이 없는 회원으로 회원에게 받은 제안 페이징 조회를 하면 예외가 발생한다.")
    void givenNonExistingCurrentTeam_whenFindPageTeamOffer_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);

        OfferedBy offeredBy = OfferedBy.LEADER;
        Position position = Position.DESIGNER;
        long pageFrom = Long.MAX_VALUE;
        int pageSize = 1;

        // when
        assertThatThrownBy(() -> offerService.findPageTeamOffer(user.getUsername(), position, offeredBy, pageFrom, pageSize))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CURRENT_TEAM_NOT_FOUND);
    }

    @Test
    @DisplayName("제안을 수락으로 회원이 받은 제안을 결정한다.")
    void givenAccept_whenUserDecideOffer_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일", Position.BACKEND);
        Team team = createSavedTeam((byte) 1);
        createdSavedTeamMember(true, user1, team);

        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이", Position.BACKEND);
        Offer offer1 = createSavedOffer(team, user2, OfferedBy.LEADER, Position.DESIGNER);
        Offer offer2 = createSavedOffer(team, user2, OfferedBy.LEADER, Position.BACKEND);
        Offer offer3 = createSavedOffer(team, user2, OfferedBy.LEADER, Position.FRONTEND);

        OfferDecideRequest request = createValidOfferDecideRequest(true);

        // when
        offerService.userDecideOffer(user2.getUsername(), offer1.getId(), request);

        // then
        TeamMember teamMember = teamMemberRepository.findCurrentFetchTeam(user2.getId()).get();

        assertThat(teamMember)
                .extracting("position", "teamMemberStatus", "isLeader")
                .containsExactly(offer1.getPosition(), TeamMemberStatus.PROGRESS, false);

        assertThat(offer1)
                .extracting("isAccepted", "isDeleted")
                .containsExactly(true, true);
        assertThat(offer2)
                .extracting("isAccepted", "isDeleted")
                .containsExactly(null, true);
        assertThat(offer3)
                .extracting("isAccepted", "isDeleted")
                .containsExactly(null, true);
    }

    @Test
    @DisplayName("제안을 거절로 회원이 받은 제안을 결정한다.")
    void givenDecline_whenUserDecideOffer_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일", Position.BACKEND);
        Team team = createSavedTeam((byte) 1);
        createdSavedTeamMember(true, user1, team);

        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이", Position.BACKEND);
        Offer offer = createSavedOffer(team, user2, OfferedBy.LEADER, Position.DESIGNER);

        OfferDecideRequest request = createValidOfferDecideRequest(false);

        // when
        offerService.userDecideOffer(user2.getUsername(), offer.getId(), request);

        // then
        assertThat(offer)
                .extracting("isAccepted", "isDeleted")
                .containsExactly(false, true);
    }

    @Test
    @DisplayName("존재하지 않은 회원이 받은 제안 결정을 하면 예외가 발생한다.")
    void givenNonExistingUser_whenUserDecideOffer_thenThrow() {
        // given
        String username = "tester";
        long offerId = 1L;
        OfferDecideRequest request = createValidOfferDecideRequest(true);

        // when & then
        assertThatThrownBy(() -> offerService.userDecideOffer(username, offerId, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("존재하지 않은 제안으로 회원이 받은 제안 결정을 하면 예외가 발생한다.")
    void givenNonExistingOffer_whenUserDecideOffer_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);
        long offerId = 1L;
        OfferDecideRequest request = createValidOfferDecideRequest(true);

        // when & then
        assertThatThrownBy(() -> offerService.userDecideOffer(user.getUsername(), offerId, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(OFFER_NOT_FOUND);
    }

    @Test
    @DisplayName("제안 수락으로 팀이 받은 제안을 결정한다.")
    void givenAccept_whenTeamDecideOffer_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일", Position.BACKEND);
        Team team = createSavedTeam((byte) 1);
        createdSavedTeamMember(true, user1, team);

        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이", Position.BACKEND);
        Offer offer1 = createSavedOffer(team, user2, OfferedBy.USER, Position.DESIGNER);
        Offer offer2 = createSavedOffer(team, user2, OfferedBy.USER, Position.BACKEND);
        Offer offer3 = createSavedOffer(team, user2, OfferedBy.USER, Position.FRONTEND);

        OfferDecideRequest request = createValidOfferDecideRequest(true);

        // when
        offerService.teamDecideOffer(user1.getUsername(), offer1.getId(), request);

        // then
        TeamMember teamMember = teamMemberRepository.findCurrentFetchTeam(user2.getId()).get();

        assertThat(teamMember)
                .extracting("position", "teamMemberStatus", "isLeader")
                .containsExactly(offer1.getPosition(), TeamMemberStatus.PROGRESS, false);

        assertThat(offer1)
                .extracting("isAccepted", "isDeleted")
                .containsExactly(true, true);
        assertThat(offer2)
                .extracting("isAccepted", "isDeleted")
                .containsExactly(null, true);
        assertThat(offer3)
                .extracting("isAccepted", "isDeleted")
                .containsExactly(null, true);
    }

    @Test
    @DisplayName("제안을 거절로 팀이 받은 제안을 결정한다.")
    void givenDecline_whenTeamDecideOffer_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일", Position.BACKEND);
        Team team = createSavedTeam((byte) 1);
        createdSavedTeamMember(true, user1, team);

        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이", Position.BACKEND);
        Offer offer = createSavedOffer(team, user2, OfferedBy.USER, Position.DESIGNER);

        OfferDecideRequest request = createValidOfferDecideRequest(false);

        // when
        offerService.teamDecideOffer(user1.getUsername(), offer.getId(), request);

        // then
        assertThat(offer)
                .extracting("isAccepted", "isDeleted")
                .containsExactly(false, true);
    }

    @Test
    @DisplayName("팀장이 아닌 회원으로 팀이 받은 제안을 결정하면 예외가 발생한다.")
    void givenNonLeader_whenTeamDecideOffer_thenThrow() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일", Position.BACKEND);
        Team team = createSavedTeam((byte) 1);
        createdSavedTeamMember(false, user1, team);

        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이", Position.BACKEND);
        Offer offer = createSavedOffer(team, user2, OfferedBy.USER, Position.DESIGNER);

        OfferDecideRequest request = createValidOfferDecideRequest(true);

        // when & then
        assertThatThrownBy(() -> offerService.teamDecideOffer(user1.getUsername(), offer.getId(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(REQUEST_FORBIDDEN);
    }

    @Test
    @DisplayName("존재하지 회원으로 팀이 받은 제안을 결정하면 예외가 발생한다.")
    void givenNonExistingUser_whenTeamDecideOffer_thenThrow() {
        // given
        String username = "tester";
        long offerId = 1L;
        OfferDecideRequest request = createValidOfferDecideRequest(true);

        // when & then
        assertThatThrownBy(() -> offerService.teamDecideOffer(username, offerId, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("팀에 소속되지 않은 회원으로 팀이 받은 제안을 결정하면 예외가 발생한다.")
    void givenNonExistingCurrentTeam_whenTeamDecideOffer_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);

        long offerId = 1L;
        OfferDecideRequest request = createValidOfferDecideRequest(true);

        // when & then
        assertThatThrownBy(() -> offerService.teamDecideOffer(user.getUsername(), offerId, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CURRENT_TEAM_NOT_FOUND);
    }

    @Test
    @DisplayName("존재하지 않은 제안으로 팀이 받은 제안을 결정하면 예외가 발생한다.")
    void givenNonExistingOffer_whenTeamDecideOffer_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);
        Team team = createSavedTeam((byte) 1);
        createdSavedTeamMember(true, user, team);

        long offerId = 1L;
        OfferDecideRequest request = createValidOfferDecideRequest(true);

        // when & then
        assertThatThrownBy(() -> offerService.teamDecideOffer(user.getUsername(), offerId, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(OFFER_NOT_FOUND);
    }

    @Test
    @DisplayName("회원이 보낸 제안을 취소한다.")
    void givenValid_whenCancelByUser_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일", Position.BACKEND);
        Team team = createSavedTeam((byte) 1);
        createdSavedTeamMember(true, user1, team);
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이", Position.BACKEND);

        Offer offer = createSavedOffer(team, user2, OfferedBy.USER, Position.DESIGNER);

        // when
        offerService.cancelByUser(user2.getUsername(), offer.getId());

        // then
        assertThat(offer)
                .extracting("isAccepted", "isDeleted")
                .containsExactly(null, true);
    }

    @Test
    @DisplayName("존재하지 않은 회원이 보낸 제안을 취소하면 예외가 발생한다.")
    void givenNonExistingUser_whenCancelByUser_thenThrow() {
        // given
        String username = "tester";
        long offerId = 1L;

        // when & then
        assertThatThrownBy(() -> offerService.cancelByUser(username, offerId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("존재하지 않은 제안으로 회원이 보낸 제안을 취소하면 예외가 발생한다.")
    void givenNonExistingOffer_whenCancelByUser_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);
        long offerId = 1L;

        // when & then
        assertThatThrownBy(() -> offerService.cancelByUser(user.getUsername(), offerId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(OFFER_NOT_FOUND);
    }

    @Test
    @DisplayName("팀이 보낸 제안을 취소한다.")
    void givenValid_whenCancelByTeam_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일", Position.BACKEND);
        Team team = createSavedTeam((byte) 1);
        createdSavedTeamMember(true, user1, team);
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이", Position.BACKEND);

        Offer offer = createSavedOffer(team, user2, OfferedBy.LEADER, Position.DESIGNER);

        // when
        offerService.cancelByTeam(user1.getUsername(), offer.getId());

        // then
        assertThat(offer)
                .extracting("isAccepted", "isDeleted")
                .containsExactly(null, true);
    }

    @Test
    @DisplayName("팀장이 아닌 회원이 팀이 보낸 제안을 취소하면 예외가 발생한다.")
    void givenNonLeader_whenCancelByTeam_thenThrow() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일", Position.BACKEND);
        Team team = createSavedTeam((byte) 1);
        createdSavedTeamMember(false, user1, team);
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이", Position.BACKEND);

        Offer offer = createSavedOffer(team, user2, OfferedBy.LEADER, Position.DESIGNER);

        // when & then
        assertThatThrownBy(() -> offerService.cancelByTeam(user1.getUsername(), offer.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(REQUEST_FORBIDDEN);
    }

    @Test
    @DisplayName("존재하지 않은 회원이 팀이 보낸 제안을 취소하면 예외가 발생한다.")
    void givenNonExistingUser_whenCancelByTeam_thenThrow() {
        // given
        String username = "tester";
        long offerId = 1L;

        // when & then
        assertThatThrownBy(() -> offerService.cancelByTeam(username, offerId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("팀에 속해있지 않은 회원이 팀이 보낸 제안을 취소하면 예외가 발생한다.")
    void givenNoCurrentTeam_whenCancelByTeam_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);
        long offerId = 1L;

        // when & then
        assertThatThrownBy(() -> offerService.cancelByTeam(user.getUsername(), offerId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CURRENT_TEAM_NOT_FOUND);
    }

    @Test
    @DisplayName("존재하지 않은 제안으로 팀이 보낸 제안을 취소하면 예외가 발생한다.")
    void givenNonExistingOffer_whenCancelByTeam_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);
        Team team = createSavedTeam((byte) 1);
        createdSavedTeamMember(true, user, team);
        long offerId = 1L;

        // when & then
        assertThatThrownBy(() -> offerService.cancelByTeam(user.getUsername(), offerId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(OFFER_NOT_FOUND);
    }

    private OfferDecideRequest createValidOfferDecideRequest(boolean isAccepted) {
        return OfferDecideRequest.builder()
                .isAccepted(isAccepted)
                .build();
    }

    private OfferCreateRequest createValidOfferCreateRequest() {
        return OfferCreateRequest.builder()
                .position(Position.BACKEND.name())
                .build();
    }

    private Offer createSavedOffer(Team team, User user, OfferedBy offeredBy, Position position) {
        Offer offer = Offer.builder()
                .position(position)
                .offeredBy(offeredBy)
                .team(team)
                .user(user)
                .build();
        offerRepository.save(offer);

        return offer;
    }

    private TeamMember createdSavedTeamMember(boolean isLeader, User user, Team team) {
        TeamMember teamMember = TeamMember.builder()
                .isLeader(isLeader)
                .position(user.getPosition())
                .user(user)
                .team(team)
                .build();
        teamMemberRepository.save(teamMember);

        return teamMember;
    }

    private Team createSavedTeam(byte maxCnt) {
        Team team = Team.builder()
                .projectName("가보자잇")
                .projectDescription("프로젝트 설명입니다.")
                .expectation("열정적인 팀원을 구해요")
                .openChatUrl("kakao.com/o/gabojait")
                .designerMaxCnt(maxCnt)
                .backendMaxCnt(maxCnt)
                .frontendMaxCnt(maxCnt)
                .managerMaxCnt(maxCnt)
                .build();
        teamRepository.save(team);

        return team;
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
        userRepository.save(user);

        return user;
    }
}