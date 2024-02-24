package com.gabojait.gabojaitspring.api.service.team;

import com.gabojait.gabojaitspring.common.response.PageData;
import com.gabojait.gabojaitspring.api.dto.team.request.TeamCreateRequest;
import com.gabojait.gabojaitspring.api.dto.team.request.TeamUpdateRequest;
import com.gabojait.gabojaitspring.api.dto.team.response.*;
import com.gabojait.gabojaitspring.domain.favorite.Favorite;
import com.gabojait.gabojaitspring.domain.offer.Offer;
import com.gabojait.gabojaitspring.domain.offer.OfferedBy;
import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
import com.gabojait.gabojaitspring.domain.team.TeamMemberStatus;
import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.domain.user.User;
import com.gabojait.gabojaitspring.common.exception.CustomException;
import com.gabojait.gabojaitspring.repository.favorite.FavoriteRepository;
import com.gabojait.gabojaitspring.repository.offer.OfferRepository;
import com.gabojait.gabojaitspring.repository.team.TeamMemberRepository;
import com.gabojait.gabojaitspring.repository.team.TeamRepository;
import com.gabojait.gabojaitspring.repository.user.ContactRepository;
import com.gabojait.gabojaitspring.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.gabojait.gabojaitspring.common.constant.code.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class TeamServiceTest {

    @Autowired private TeamService teamService;
    @Autowired private TeamMemberRepository teamMemberRepository;
    @Autowired private ContactRepository contactRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private OfferRepository offerRepository;
    @Autowired private FavoriteRepository favoriteRepository;

    private static Stream<Arguments> providerCreateTeam() {
        return Stream.of(
                Arguments.of(Position.DESIGNER, (byte) 1, (byte) 0, (byte) 0, (byte) 0),
                Arguments.of(Position.BACKEND, (byte) 0, (byte) 1, (byte) 0, (byte) 0),
                Arguments.of(Position.FRONTEND, (byte) 0, (byte) 0, (byte) 1, (byte) 0),
                Arguments.of(Position.MANAGER, (byte) 0, (byte) 0, (byte) 0, (byte) 1)
        );
    }

    @ParameterizedTest(name = "[{index}] {0} 포지션으로 팀 생성을 한다")
    @MethodSource("providerCreateTeam")
    @DisplayName("팀 생성이 정상 작동한다")
    void givenValid_whenCreateTeam_theReturn(Position position,
                                             byte designerCurrentCnt,
                                             byte backendCurrentCnt,
                                             byte frontendCurrentCnt,
                                             byte managerCurrentCnt) {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        TeamCreateRequest request = createValidTeamCreateRequest(position);

        // when
        TeamCreateResponse response = teamService.createTeam(user.getId(), request);

        // then
        assertAll(
                () -> assertThat(response)
                        .extracting("projectName", "designerCurrentCnt", "backendCurrentCnt",
                                "frontendCurrentCnt", "managerCurrentCnt", "designerMaxCnt",
                                "backendMaxCnt", "frontendMaxCnt", "managerMaxCnt",
                                "projectDescription", "openChatUrl", "expectation")
                        .containsExactly(request.getProjectName(), designerCurrentCnt, backendCurrentCnt,
                                frontendCurrentCnt, managerCurrentCnt, request.getDesignerMaxCnt(),
                                request.getBackendMaxCnt(), request.getFrontendMaxCnt(), request.getManagerMaxCnt(),
                                request.getProjectDescription(), request.getOpenChatUrl(), request.getExpectation()),
                () -> assertThat(response.getTeamMembers())
                        .extracting("userId", "username", "nickname", "position", "isLeader")
                        .containsExactly(
                                tuple(user.getId(), user.getUsername(), user.getNickname(), position, true)
                        )
        );
    }

    @Test
    @DisplayName("존재하지 않은 회원 식별자로 팀을 생성시 예외가 발생한다")
    void givenNonExistingUser_whenCreateTeam_thenThrow() {
        // given
        long userId = 1L;

        TeamCreateRequest request = createValidTeamCreateRequest(Position.MANAGER);

        // when & then
        assertThatThrownBy(() -> teamService.createTeam(userId, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("소속된 팀이 있는 회원으로 팀을 생성시 예외가 발생한다")
    void givenExistingCurrentTeam_whenCreateTeam_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");
        Team team = createSavedTeam();
        TeamMember teamMember = createdSavedTeamMember(true, user, team, Position.MANAGER);

        TeamCreateRequest request = createValidTeamCreateRequest(Position.MANAGER);

        // when & then
        assertThatThrownBy(() -> teamService.createTeam(user.getId(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(EXISTING_CURRENT_TEAM);
    }

    @Test
    @DisplayName("디자이너 최대 인원이 0인데 디자이너 포지션으로 팀 생성시 예외가 발생한다")
    void givenUnavailableDesigner_whenCreateTeam_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        TeamCreateRequest request = createValidTeamCreateRequest(Position.DESIGNER);
        request.setDesignerMaxCnt((byte) 0);

        // when & then
        assertThatThrownBy(() -> teamService.createTeam(user.getId(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TEAM_LEADER_POSITION_UNAVAILABLE);
    }

    @Test
    @DisplayName("백엔드 최대 인원이 0인데 백엔드 포지션으로 팀 생성시 예외가 발생한다")
    void givenUnavailableBackend_whenCreateTeam_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        TeamCreateRequest request = createValidTeamCreateRequest(Position.BACKEND);
        request.setBackendMaxCnt((byte) 0);

        // when & then
        assertThatThrownBy(() -> teamService.createTeam(user.getId(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TEAM_LEADER_POSITION_UNAVAILABLE);
    }

    @Test
    @DisplayName("프런트엔드 최대 인원이 0인데 백엔드 포지션으로 팀 생성시 예외가 발생한다")
    void givenUnavailableFrontend_whenCreateTeam_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        TeamCreateRequest request = createValidTeamCreateRequest(Position.FRONTEND);
        request.setFrontendMaxCnt((byte) 0);

        // when & then
        assertThatThrownBy(() -> teamService.createTeam(user.getId(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TEAM_LEADER_POSITION_UNAVAILABLE);
    }

    @Test
    @DisplayName("매니저 최대 인원이 0인데 백엔드 포지션으로 팀 생성시 예외가 발생한다")
    void givenUnavailableManager_whenCreateTeam_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        TeamCreateRequest request = createValidTeamCreateRequest(Position.MANAGER);
        request.setManagerMaxCnt((byte) 0);

        // when & then
        assertThatThrownBy(() -> teamService.createTeam(user.getId(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TEAM_LEADER_POSITION_UNAVAILABLE);
    }

    @Test
    @DisplayName("팀 수정이 정상 작동한다")
    void givenValid_whenUpdateTeam_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");
        Team team = createSavedTeam();
        TeamMember teamMember = createdSavedTeamMember(true, user, team, Position.MANAGER);

        TeamUpdateRequest request = createValidTeamUpdateRequest();

        // when
        TeamUpdateResponse response = teamService.updateTeam(user.getId(), request);

        // then
        assertAll(
                () -> assertThat(response)
                        .extracting("projectName", "designerCurrentCnt", "backendCurrentCnt", "frontendCurrentCnt",
                                "managerCurrentCnt", "designerMaxCnt", "backendMaxCnt", "frontendMaxCnt", "managerMaxCnt",
                                "projectDescription", "openChatUrl", "expectation")
                        .containsExactly(request.getProjectName(), (byte) 0, (byte) 0, (byte) 0, (byte) 1,
                                request.getDesignerMaxCnt(), request.getBackendMaxCnt(), request.getFrontendMaxCnt(),
                                request.getManagerMaxCnt(), request.getProjectDescription(), request.getOpenChatUrl(),
                                request.getExpectation()),
                () -> assertThat(response.getTeamMembers())
                        .extracting("userId", "username", "nickname", "position", "isLeader")
                        .containsExactly(
                                tuple(user.getId(), user.getUsername(), user.getNickname(), teamMember.getPosition(),
                                        teamMember.getIsLeader())
                        )
        );
    }

    @Test
    @DisplayName("팀장이 아닌 회원이 팀 수정시 예외가 발생한다")
    void givenNonLeader_whenUpdateTeam_thenThrow() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");

        Team team = createSavedTeam();
        createdSavedTeamMember(true, user1, team, Position.MANAGER);
        createdSavedTeamMember(false, user2, team, Position.BACKEND);

        TeamUpdateRequest request = createValidTeamUpdateRequest();

        // when & then
        assertThatThrownBy(() -> teamService.updateTeam(user2.getId(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(REQUEST_FORBIDDEN);
    }

    @Test
    @DisplayName("현재 팀이 없는 회원이 팀 수정시 예외가 발생한다")
    void givenNoCurrentTeam_whenUpdateTeam_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        TeamUpdateRequest request = createValidTeamUpdateRequest();

        // when & then
        assertThatThrownBy(() -> teamService.updateTeam(user.getId(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CURRENT_TEAM_NOT_FOUND);
    }

    @Test
    @DisplayName("현재 디자이너 수보다 적은 디자이너 최대 수로 팀 수정시 예외가 발생한다")
    void givenUnavailableDesignerCnt_whenUpdateTeam_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Team team = createSavedTeam();
        createdSavedTeamMember(true, user, team, Position.DESIGNER);

        TeamUpdateRequest request = createValidTeamUpdateRequest();
        request.setLeaderPosition(Position.DESIGNER.name());
        request.setDesignerMaxCnt((byte) 0);

        // when & then
        assertThatThrownBy(() -> teamService.updateTeam(user.getId(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(DESIGNER_CNT_UPDATE_UNAVAILABLE);
    }

    @Test
    @DisplayName("현재 백엔드 수보다 적은 백엔드 최대 수로 팀 수정시 예외가 발생한다")
    void givenUnavailableBackendCnt_whenUpdateTeam_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Team team = createSavedTeam();
        createdSavedTeamMember(true, user, team, Position.BACKEND);

        TeamUpdateRequest request = createValidTeamUpdateRequest();
        request.setLeaderPosition(Position.BACKEND.name());
        request.setBackendMaxCnt((byte) 0);

        // when & then
        assertThatThrownBy(() -> teamService.updateTeam(user.getId(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(BACKEND_CNT_UPDATE_UNAVAILABLE);
    }

    @Test
    @DisplayName("현재 프런트엔드 수보다 적은 프런트엔드 최대 수로 팀 수정시 예외가 발생한다")
    void givenUnavailableFrontendCnt_whenUpdateTeam_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Team team = createSavedTeam();
        createdSavedTeamMember(true, user, team, Position.FRONTEND);

        TeamUpdateRequest request = createValidTeamUpdateRequest();
        request.setLeaderPosition(Position.FRONTEND.name());
        request.setFrontendMaxCnt((byte) 0);

        // when & then
        assertThatThrownBy(() -> teamService.updateTeam(user.getId(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(FRONTEND_CNT_UPDATE_UNAVAILABLE);
    }

    @Test
    @DisplayName("현재 매니저 수보다 적은 매니저 최대 수로 팀 수정시 예외가 발생한다")
    void givenUnavailableManagerCnt_whenUpdateTeam_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Team team = createSavedTeam();
        createdSavedTeamMember(true, user, team, Position.MANAGER);

        TeamUpdateRequest request = createValidTeamUpdateRequest();
        request.setLeaderPosition(Position.MANAGER.name());
        request.setManagerMaxCnt((byte) 0);

        // when & then
        assertThatThrownBy(() -> teamService.updateTeam(user.getId(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(MANAGER_CNT_UPDATE_UNAVAILABLE);
    }

    @Test
    @DisplayName("현재 본인 팀 조회가 정상 작동한다")
    void givenValid_whenFindCurrentTeam_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Team team = createSavedTeam();
        TeamMember teamMember = createdSavedTeamMember(true, user, team, Position.MANAGER);

        // when
        TeamMyCurrentResponse response = teamService.findCurrentTeam(user.getId());

        // then
        assertAll(
                () -> assertThat(response)
                        .extracting("projectName", "designerCurrentCnt", "backendCurrentCnt", "frontendCurrentCnt",
                                "managerCurrentCnt", "designerMaxCnt", "backendMaxCnt", "frontendMaxCnt", "managerMaxCnt",
                                "projectDescription", "openChatUrl", "expectation")
                        .containsExactly(team.getProjectName(), team.getDesignerCurrentCnt(), team.getBackendCurrentCnt(),
                                team.getFrontendCurrentCnt(), team.getManagerCurrentCnt(), team.getDesignerMaxCnt(),
                                team.getBackendMaxCnt(), team.getFrontendMaxCnt(), team.getManagerMaxCnt(),
                                team.getProjectDescription(), team.getOpenChatUrl(), team.getExpectation()),
                () -> assertThat(response.getTeamMembers())
                        .extracting("userId", "username", "nickname", "position", "isLeader")
                        .containsExactly(
                                tuple(user.getId(), user.getUsername(), user.getNickname(), teamMember.getPosition(),
                                        teamMember.getIsLeader())
                        )
        );
    }

    @Test
    @DisplayName("현재 소속된 팀이 없는 회원 아이디로 현재 팀 조회시 예외가 발생한다")
    void givenNonExistingCurrentTeam_whenFindCurrentTeam_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        // when & then
        assertThatThrownBy(() -> teamService.findCurrentTeam(user.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CURRENT_TEAM_NOT_FOUND);
    }

    @Test
    @DisplayName("내 팀 단건 조회가 정상 작동한다")
    void givenMyTeam_whenFindTeam_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");
        Team team = createSavedTeam();
        TeamMember teamMember = createdSavedTeamMember(true, user, team, Position.MANAGER);

        // when
        TeamFindResponse response = teamService.findOtherTeam(user.getId(), team.getId());

        // then
        assertAll(
                () -> assertThat(team.getVisitedCnt()).isEqualTo(0),
                () -> assertThat(response)
                        .extracting("teamId", "projectName", "designerCurrentCnt", "backendCurrentCnt",
                                "frontendCurrentCnt", "managerCurrentCnt", "designerMaxCnt", "backendMaxCnt", "frontendMaxCnt",
                                "managerMaxCnt", "createdAt", "updatedAt", "projectDescription", "openChatUrl", "expectation",
                                "isFavorite")
                        .containsExactly(team.getId(), team.getProjectName(), team.getDesignerCurrentCnt(),
                                team.getBackendCurrentCnt(), team.getFrontendCurrentCnt(), team.getManagerCurrentCnt(),
                                team.getDesignerMaxCnt(), team.getBackendMaxCnt(), team.getFrontendMaxCnt(),
                                team.getManagerMaxCnt(), team.getCreatedAt(), team.getUpdatedAt(), team.getProjectDescription(),
                                team.getOpenChatUrl(), team.getExpectation(), false),
                () -> assertThat(response.getTeamMembers())
                        .extracting("userId", "username", "nickname", "position", "isLeader")
                        .containsExactlyInAnyOrder(
                                tuple(user.getId(), user.getUsername(), user.getNickname(), teamMember.getPosition(),
                                        teamMember.getIsLeader())
                        ),
                () -> assertThat(response.getOffers()).isEmpty()
        );
    }

    @Test
    @DisplayName("내 팀이 아닌 팀 단건 조회가 정상 작동한다")
    void givenNotMyTeam_whenFindTeam_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");

        Team team = createSavedTeam();
        TeamMember teamMember = createdSavedTeamMember(true, user1, team, Position.MANAGER);

        Favorite favorite = createSavedFavorite(user2, null, team);
        Offer offer = createSavedOffer(team, user2, Position.MANAGER);

        // when
        TeamFindResponse response = teamService.findOtherTeam(user2.getId(), team.getId());

        // then
        assertAll(
                () -> assertThat(team.getVisitedCnt()).isEqualTo(1),
                () -> assertThat(response)
                        .extracting("teamId", "projectName", "designerCurrentCnt", "backendCurrentCnt",
                                "frontendCurrentCnt", "managerCurrentCnt", "designerMaxCnt", "backendMaxCnt", "frontendMaxCnt",
                                "managerMaxCnt", "createdAt", "updatedAt", "projectDescription", "openChatUrl", "expectation",
                                "isFavorite")
                        .containsExactly(team.getId(), team.getProjectName(), team.getDesignerCurrentCnt(),
                                team.getBackendCurrentCnt(), team.getFrontendCurrentCnt(), team.getManagerCurrentCnt(),
                                team.getDesignerMaxCnt(), team.getBackendMaxCnt(), team.getFrontendMaxCnt(),
                                team.getManagerMaxCnt(), team.getCreatedAt(), team.getUpdatedAt(), team.getProjectDescription(),
                                team.getOpenChatUrl(), team.getExpectation(), true),
                () -> assertThat(response.getOffers())
                        .extracting("offerId", "position", "isAccepted", "offeredBy", "createdAt", "updatedAt")
                        .containsExactlyInAnyOrder(
                                tuple(offer.getId(), offer.getPosition(), offer.getIsAccepted(), offer.getOfferedBy(),
                                        offer.getCreatedAt(), offer.getUpdatedAt())
                        )
        );
    }

    @Test
    @DisplayName("존재하지 않은 팀 식별자로 다른 팀 단건 조회시 예외가 발생한다")
    void givenNonExistingTeam_whenFindTeam_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");
        long teamId = 1L;

        // when & then
        assertThatThrownBy(() -> teamService.findOtherTeam(user.getId(), teamId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TEAM_NOT_FOUND);
    }

    @Test
    @DisplayName("팀 페이징 조회가 정상 작동한다")
    void givenValid_whenFindPageTeam_thenReturn() {
        // given
        Team team1 = createSavedTeam();
        Team team2 = createSavedTeam();
        Team team3 = createSavedTeam();

        Position position = Position.NONE;
        long pageFrom = Long.MAX_VALUE;
        int pageSize = 2;

        // when
        PageData<List<TeamPageResponse>> teams = teamService.findPageTeam(position, pageFrom, pageSize);

        // then
        assertAll(
                () -> assertThat(teams.getData())
                        .extracting("teamId", "projectName", "designerCurrentCnt", "backendCurrentCnt", "frontendCurrentCnt",
                                "managerCurrentCnt", "designerMaxCnt", "backendMaxCnt", "frontendMaxCnt", "managerMaxCnt",
                                "createdAt", "updatedAt")
                        .containsExactly(
                                tuple(team3.getId(), team3.getProjectName(), team3.getDesignerCurrentCnt(),
                                        team3.getBackendCurrentCnt(), team3.getFrontendCurrentCnt(),
                                        team3.getManagerCurrentCnt(), team3.getDesignerMaxCnt(), team3.getBackendMaxCnt(),
                                        team3.getFrontendMaxCnt(), team3.getManagerMaxCnt(), team3.getCreatedAt(),
                                        team3.getUpdatedAt()),
                                tuple(team2.getId(), team2.getProjectName(), team2.getDesignerCurrentCnt(),
                                        team2.getBackendCurrentCnt(), team2.getFrontendCurrentCnt(),
                                        team2.getManagerCurrentCnt(), team2.getDesignerMaxCnt(), team2.getBackendMaxCnt(),
                                        team2.getFrontendMaxCnt(), team2.getManagerMaxCnt(), team2.getCreatedAt(),
                                        team2.getUpdatedAt())
                        ),
                () -> assertThat(teams.getData().size()).isEqualTo(pageSize),
                () -> assertThat(teams.getTotal()).isEqualTo(3)
        );
    }
    
    @Test
    @DisplayName("팀원 모집 여부를 업데이트가 정상 작동한다")
    void givenValid_whenUpdateIsRecruiting_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");
        Team team = createSavedTeam();
        createdSavedTeamMember(true, user, team, Position.MANAGER);

        boolean isRecruiting = false;

        // when
        teamService.updateIsRecruiting(user.getId(), isRecruiting);

        // then
        assertThat(team.getIsRecruiting()).isFalse();
    }

    @Test
    @DisplayName("팀 리더가 아닌 회원으로 팀원 모집 여부 업데이트를 하면 예외가 발생한다")
    void givenNonLeader_whenUpdateIsRecruiting_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");
        Team team = createSavedTeam();
        createdSavedTeamMember(false, user, team, Position.MANAGER);

        boolean isRecruiting = false;

        // when & then
        assertThatThrownBy(() -> teamService.updateIsRecruiting(user.getId(), isRecruiting))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(REQUEST_FORBIDDEN);
    }

    @Test
    @DisplayName("현재 소속 팀 없이 팀원 모집 여부 업데이트를 하면 예외가 발생한다")
    void givenNonExistingCurrentTeam_whenUpdateIsRecruiting_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        boolean isRecruiting = false;

        // when & then
        assertThatThrownBy(() -> teamService.updateIsRecruiting(user.getId(), isRecruiting))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CURRENT_TEAM_NOT_FOUND);
    }

    @Test
    @DisplayName("프로젝트 미완료로 프로젝트 종료가 정상 작동한다")
    void givenIncomplete_whenEndProject_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");
        Team team = createSavedTeam();

        TeamMember teamMember = createdSavedTeamMember(true, user, team, Position.MANAGER);

        LocalDateTime completeAt = LocalDateTime.now();

        // when
        teamService.endProject(user.getId(), "", completeAt);

        // then
        assertThat(teamMember)
                .extracting("id", "position", "isLeader", "teamMemberStatus", "isDeleted")
                .containsExactly(teamMember.getId(), teamMember.getPosition(), true, TeamMemberStatus.INCOMPLETE, true);
    }

    @Test
    @DisplayName("프로젝트 완료로 프로젝트 종료가 정상 작동한다")
    void givenComplete_whenEndProject_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");
        Team team = createSavedTeam();

        TeamMember teamMember = createdSavedTeamMember(true, user, team, Position.MANAGER);

        String projectUrl = "https://github.com/gabojait";
        LocalDateTime completeAt = LocalDateTime.now();

        // when
        teamService.endProject(user.getId(), projectUrl, completeAt);

        // then
        assertThat(teamMember)
                .extracting("id", "position", "isLeader", "teamMemberStatus", "isDeleted")
                .containsExactly(teamMember.getId(), teamMember.getPosition(), true, TeamMemberStatus.COMPLETE, false);
    }

    @Test
    @DisplayName("팀원 추방이 정상 작동한다")
    void givenValid_whenFire_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터1");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터2");
        Team team = createSavedTeam();

        createdSavedTeamMember(true, user1, team, Position.MANAGER);
        createdSavedTeamMember(false, user2, team, Position.BACKEND);

        // when
        teamService.fire(user1.getId(), user2.getId());

        // then
        Optional<TeamMember> foundTeamMember = teamMemberRepository.find(user2.getId(), team.getId(),
                TeamMemberStatus.PROGRESS);

        assertThat(foundTeamMember).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않은 팀장 식별자로 팀원 추방시 예외가 발생한다")
    void givenNonExistingTeamLeaderUserId_whenFire_thenThrow() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터1");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터2");

        // when & then
        assertThatThrownBy(() -> teamService.fire(user1.getId(), user2.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CURRENT_TEAM_NOT_FOUND);
    }

    @Test
    @DisplayName("회원이 팀장이 아닐시 팀원 추방을 하면 예외가 발생한다")
    void givenNonLeaderUsername_whenFire_thenThrow() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터1");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터2");
        Team team = createSavedTeam();

        createdSavedTeamMember(false, user1, team, Position.MANAGER);
        createdSavedTeamMember(false, user2, team, Position.BACKEND);

        // when & then
        assertThatThrownBy(() -> teamService.fire(user1.getId(), user2.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(REQUEST_FORBIDDEN);
    }

    @Test
    @DisplayName("존재하지 않은 팀원 식별자로 팀원 추방시 예외가 발생한다")
    void givenNonExistingTeamMemberUserId_whenFire_thenThrow() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터1");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터2");
        Team team = createSavedTeam();

        createdSavedTeamMember(true, user1, team, Position.MANAGER);

        // when & then
        assertThatThrownBy(() -> teamService.fire(user1.getId(), user2.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CURRENT_TEAM_NOT_FOUND);
    }

    @Test
    @DisplayName("존재하지 않은 현재 팀을 가진 회원으로 팀원 추방을 하면 예외가 발생한다")
    void givenNonExistingCurrentTeam_whenFire_thenThrow() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터1");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터2");

        // when & then
        assertThatThrownBy(() -> teamService.fire(user1.getId(), user2.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CURRENT_TEAM_NOT_FOUND);
    }

    @Test
    @DisplayName("팀을 나가면 정상 작동한다")
    void givenValid_whenLeave_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");
        Team team = createSavedTeam();

        TeamMember teamMember = createdSavedTeamMember(false, user, team, Position.MANAGER);

        // when
        teamService.leave(user.getId());

        // then
        assertThat(teamMember)
                .extracting("teamMemberStatus", "isDeleted")
                .containsExactly(TeamMemberStatus.QUIT, true);
    }

    @Test
    @DisplayName("존재하지 않은 회원으로 팀을 나가면 예외가 발생한다")
    void givenNonExistingUser_whenLeave_thenThrow() {
        // given
        long userId = 1L;

        // when & then
        assertThatThrownBy(() -> teamService.leave(userId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("현재 소속된 팀이 없는 회원으로 팀을 나가면 예외가 발생한다")
    void givenNonExistingCurrentTeam_whenLeave_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        // when & then
        assertThatThrownBy(() -> teamService.leave(user.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CURRENT_TEAM_NOT_FOUND);
    }

    @Test
    @DisplayName("팀장인 회원으로 팀을 나가면 예외가 발생한다")
    void givenTeamLeader_whenLeave_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");
        Team team = createSavedTeam();

        createdSavedTeamMember(true, user, team, Position.MANAGER);

        // when & then
        assertThatThrownBy(() -> teamService.leave(user.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TEAM_LEADER_UNAVAILABLE);
    }

    private TeamCreateRequest createValidTeamCreateRequest(Position position) {
        return TeamCreateRequest.builder()
                .projectName("가볼까잇")
                .projectDescription("가볼까잇 설명입니다.")
                .expectation("열정적인 팀원을 구해요.")
                .openChatUrl("kakao.com/o/gabojait")
                .leaderPosition(position.name())
                .designerMaxCnt((byte) 5)
                .backendMaxCnt((byte) 5)
                .frontendMaxCnt((byte) 5)
                .managerMaxCnt((byte) 5)
                .build();
    }

    private TeamUpdateRequest createValidTeamUpdateRequest() {
        return TeamUpdateRequest.builder()
                .projectName("가볼까잇")
                .projectDescription("가볼까잇 설명입니다.")
                .expectation("열정적인 팀원을 구해요.")
                .openChatUrl("kakao.com/o/gabojait")
                .leaderPosition(Position.MANAGER.name())
                .designerMaxCnt((byte) 5)
                .backendMaxCnt((byte) 5)
                .frontendMaxCnt((byte) 5)
                .managerMaxCnt((byte) 5)
                .build();
    }

    private Favorite createSavedFavorite(User user, User favoriteUser, Team favoriteTeam) {
        Favorite favorite = Favorite.builder()
                .user(user)
                .favoriteUser(favoriteUser)
                .favoriteTeam(favoriteTeam)
                .build();
        favoriteRepository.save(favorite);

        return favorite;
    }

    private Offer createSavedOffer(Team team, User user, Position position) {
        Offer offer = Offer.builder()
                .position(position)
                .offeredBy(OfferedBy.LEADER)
                .team(team)
                .user(user)
                .build();
        offerRepository.save(offer);

        return offer;
    }

    private Team createSavedTeam() {
        Team team = Team.builder()
                .projectName("가보자잇")
                .projectDescription("프로젝트 설명입니다.")
                .expectation("열정적인 팀원을 구해요")
                .openChatUrl("kakao.com/o/gabojait")
                .designerMaxCnt((byte) 2)
                .backendMaxCnt((byte) 2)
                .frontendMaxCnt((byte) 2)
                .managerMaxCnt((byte) 2)
                .build();
        teamRepository.save(team);

        return team;
    }

    private TeamMember createdSavedTeamMember(boolean isLeader, User user, Team team, Position position) {
        TeamMember teamMember = TeamMember.builder()
                .isLeader(isLeader)
                .position(position)
                .user(user)
                .team(team)
                .build();
        teamMemberRepository.save(teamMember);

        return teamMember;
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