package com.gabojait.gabojaitspring.api.service.team;

import com.gabojait.gabojaitspring.api.dto.common.response.PageData;
import com.gabojait.gabojaitspring.api.dto.team.request.TeamDefaultRequest;
import com.gabojait.gabojaitspring.api.dto.team.response.TeamAbstractResponse;
import com.gabojait.gabojaitspring.api.dto.team.response.TeamDefaultResponse;
import com.gabojait.gabojaitspring.api.dto.team.response.TeamOfferFavoriteResponse;
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
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.repository.favorite.FavoriteRepository;
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
import java.util.Optional;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    @DisplayName("팀을 생성한다.")
    void givenValid_whenCreateTeam_theReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);

        TeamDefaultRequest request = createValidTeamDefaultRequest();

        // when
        TeamDefaultResponse response = teamService.createTeam(user.getUsername(), request);

        // then
        assertThat(response)
                .extracting("projectName", "designerCurrentCnt", "backendCurrentCnt", "frontendCurrentCnt",
                        "managerCurrentCnt", "designerMaxCnt", "backendMaxCnt", "frontendMaxCnt", "managerMaxCnt",
                        "projectDescription", "openChatUrl", "expectation")
                .containsExactly(request.getProjectName(), (byte) 0, (byte) 1, (byte) 0, (byte) 0,
                        request.getDesignerMaxCnt(), request.getBackendMaxCnt(), request.getFrontendMaxCnt(),
                        request.getManagerMaxCnt(), request.getProjectDescription(), request.getOpenChatUrl(),
                        request.getExpectation());

        assertThat(response.getTeamMembers())
                .extracting("userId", "username", "nickname", "position", "isLeader")
                .containsExactly(
                        tuple(user.getId(), user.getUsername(), user.getNickname(), user.getPosition(), true)
                );
    }

    @Test
    @DisplayName("존재하지 않은 회원 아이디로 팀을 생성시 예외가 발생한다.")
    void givenNonExistingUser_whenCreateTeam_thenThrow() {
        // given
        String username = "tester";

        TeamDefaultRequest request = createValidTeamDefaultRequest();

        // when & then
        assertThatThrownBy(() -> teamService.createTeam(username, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("소속된 팀이 있는 회원으로 팀을 생성시 예외가 발생한다.")
    void givenExistingCurrentTeam_whenCreateTeam_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);
        Team team = createSavedTeam();
        TeamMember teamMember = createdSavedTeamMember(true, user, team);

        TeamDefaultRequest request = createValidTeamDefaultRequest();

        // when & then
        assertThatThrownBy(() -> teamService.createTeam(user.getUsername(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(EXISTING_CURRENT_TEAM);
    }

    @Test
    @DisplayName("포지션이 없는 회원으로 팀 생성시 예외가 발생한다.")
    void givenNoPosition_whenCreateTeam_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);
        user.updatePosition(Position.NONE);
        userRepository.save(user);

        TeamDefaultRequest request = createValidTeamDefaultRequest();

        // when & then
        assertThatThrownBy(() -> teamService.createTeam(user.getUsername(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(NON_EXISTING_POSITION);
    }

    @Test
    @DisplayName("불가능한 포지션으로 팀 생성시 예외가 발생한다.")
    void givenUnavailablePosition_whenCreateTeam_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);

        TeamDefaultRequest request = createValidTeamDefaultRequest();
        request.setBackendMaxCnt((byte) 0);

        // when & then
        assertThatThrownBy(() -> teamService.createTeam(user.getUsername(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TEAM_POSITION_UNAVAILABLE);
    }

    @Test
    @DisplayName("팀을 수정한다.")
    void givenValid_whenUpdateTeam_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);
        Team team = createSavedTeam();
        TeamMember teamMember = createdSavedTeamMember(true, user, team);

        TeamDefaultRequest request = createValidTeamDefaultRequest();

        // when
        TeamDefaultResponse response = teamService.updateTeam(user.getUsername(), request);

        // then
        assertThat(response)
                .extracting("projectName", "designerCurrentCnt", "backendCurrentCnt", "frontendCurrentCnt",
                        "managerCurrentCnt", "designerMaxCnt", "backendMaxCnt", "frontendMaxCnt", "managerMaxCnt",
                        "projectDescription", "openChatUrl", "expectation")
                .containsExactly(request.getProjectName(), (byte) 0, (byte) 1, (byte) 0, (byte) 0,
                        request.getDesignerMaxCnt(), request.getBackendMaxCnt(), request.getFrontendMaxCnt(),
                        request.getManagerMaxCnt(), request.getProjectDescription(), request.getOpenChatUrl(),
                        request.getExpectation());

        assertThat(response.getTeamMembers())
                .extracting("userId", "username", "nickname", "position", "isLeader")
                .containsExactly(
                        tuple(user.getId(), user.getUsername(), user.getNickname(), teamMember.getPosition(),
                                teamMember.getIsLeader())
                );
    }

    @Test
    @DisplayName("팀장이 아닌 회원이 팀 수정시 예외가 발생한다.")
    void givenNonLeader_whenUpdateTeam_thenThrow() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일", Position.BACKEND);
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이", Position.BACKEND);

        Team team = createSavedTeam();
        createdSavedTeamMember(true, user1, team);
        createdSavedTeamMember(false, user2, team);

        TeamDefaultRequest request = createValidTeamDefaultRequest();

        // when & then
        assertThatThrownBy(() -> teamService.updateTeam(user2.getUsername(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(REQUEST_FORBIDDEN);
    }

    @Test
    @DisplayName("존재하지 않은 회원 아이디로 팀 수정시 예외가 발생한다.")
    void givenNonExistingUser_whenUpdateTeam_thenThrow() {
        // given
        String username = "tester";

        TeamDefaultRequest request = createValidTeamDefaultRequest();

        // when & then
        assertThatThrownBy(() -> teamService.updateTeam(username, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("현재 팀이 없는 회원이 팀 수정시 예외가 발생한다.")
    void givenNoCurrentTeam_whenUpdateTeam_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);

        TeamDefaultRequest request = createValidTeamDefaultRequest();

        // when & then
        assertThatThrownBy(() -> teamService.updateTeam(user.getUsername(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CURRENT_TEAM_NOT_FOUND);
    }

    @Test
    @DisplayName("현재 디자이너 수보다 적은 디자이너 최대 수로 팀 수정시 예외가 발생한다.")
    void givenUnavailableDesignerCnt_whenUpdateTeam_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.DESIGNER);

        Team team = createSavedTeam();
        createdSavedTeamMember(true, user, team);

        TeamDefaultRequest request = createValidTeamDefaultRequest();
        request.setDesignerMaxCnt((byte) 0);

        // when & then
        assertThatThrownBy(() -> teamService.updateTeam(user.getUsername(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(DESIGNER_CNT_UPDATE_UNAVAILABLE);
    }

    @Test
    @DisplayName("현재 백엔드 수보다 적은 디자이너 최대 수로 팀 수정시 예외가 발생한다.")
    void givenUnavailableBackendCnt_whenUpdateTeam_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);

        Team team = createSavedTeam();
        createdSavedTeamMember(true, user, team);

        TeamDefaultRequest request = createValidTeamDefaultRequest();
        request.setBackendMaxCnt((byte) 0);

        // when & then
        assertThatThrownBy(() -> teamService.updateTeam(user.getUsername(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(BACKEND_CNT_UPDATE_UNAVAILABLE);
    }

    @Test
    @DisplayName("현재 프런트 수보다 적은 디자이너 최대 수로 팀 수정시 예외가 발생한다.")
    void givenUnavailableFrontendCnt_whenUpdateTeam_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.FRONTEND);

        Team team = createSavedTeam();
        createdSavedTeamMember(true, user, team);

        TeamDefaultRequest request = createValidTeamDefaultRequest();
        request.setFrontendMaxCnt((byte) 0);

        // when & then
        assertThatThrownBy(() -> teamService.updateTeam(user.getUsername(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(FRONTEND_CNT_UPDATE_UNAVAILABLE);
    }

    @Test
    @DisplayName("현재 매니저 수보다 적은 디자이너 최대 수로 팀 수정시 예외가 발생한다.")
    void givenUnavailableManagerCnt_whenUpdateTeam_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.MANAGER);

        Team team = createSavedTeam();
        createdSavedTeamMember(true, user, team);

        TeamDefaultRequest request = createValidTeamDefaultRequest();
        request.setManagerMaxCnt((byte) 0);

        // when & then
        assertThatThrownBy(() -> teamService.updateTeam(user.getUsername(), request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(MANAGER_CNT_UPDATE_UNAVAILABLE);
    }

    @Test
    @DisplayName("현재 팀 조회를 한다.")
    void givenValid_whenFindCurrentTeam_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.MANAGER);

        Team team = createSavedTeam();
        TeamMember teamMember = createdSavedTeamMember(true, user, team);

        // when
        TeamDefaultResponse response = teamService.findCurrentTeam(user.getUsername());

        // then
        assertThat(response)
                .extracting("projectName", "designerCurrentCnt", "backendCurrentCnt", "frontendCurrentCnt",
                        "managerCurrentCnt", "designerMaxCnt", "backendMaxCnt", "frontendMaxCnt", "managerMaxCnt",
                        "projectDescription", "openChatUrl", "expectation")
                .containsExactly(team.getProjectName(), team.getDesignerCurrentCnt(), team.getBackendCurrentCnt(),
                        team.getFrontendCurrentCnt(), team.getManagerCurrentCnt(), team.getDesignerMaxCnt(),
                        team.getBackendMaxCnt(), team.getFrontendMaxCnt(), team.getManagerMaxCnt(),
                        team.getProjectDescription(), team.getOpenChatUrl(), team.getExpectation());

        assertThat(response.getTeamMembers())
                .extracting("userId", "username", "nickname", "position", "isLeader")
                .containsExactly(
                        tuple(user.getId(), user.getUsername(), user.getNickname(), teamMember.getPosition(),
                                teamMember.getIsLeader())
                );
    }

    @Test
    @DisplayName("현재 소속된 팀이 없는 회원 아이디로 현재 팀 조회시 예외가 발생한다.")
    void givenNonExistingCurrentTeam_whenFindCurrentTeam_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.MANAGER);

        // when & then
        assertThatThrownBy(() -> teamService.findCurrentTeam(user.getUsername()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CURRENT_TEAM_NOT_FOUND);
    }

    @Test
    @DisplayName("존재하지 않은 회원 아이디로 현재 팀 조회시 예외가 발생한다.")
    void givenNonExistingUser_whenFindCurrentTeam_thenThrow() {
        // given
        String username = "tester";


        // when & then
        assertThatThrownBy(() -> teamService.findCurrentTeam(username))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("내 팀으로 다른 팀 단건 조회를 한다.")
    void givenMyTeam_whenFindOtherTeam_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);
        Team team = createSavedTeam();
        TeamMember teamMember = createdSavedTeamMember(true, user, team);

        // when
        TeamOfferFavoriteResponse response = teamService.findOtherTeam(user.getUsername(), team.getId());

        // then
        assertThat(team.getVisitedCnt()).isEqualTo(0L);

        assertThat(response)
                .extracting("teamId", "projectName", "designerCurrentCnt", "backendCurrentCnt",
                        "frontendCurrentCnt", "managerCurrentCnt", "designerMaxCnt", "backendMaxCnt", "frontendMaxCnt",
                        "managerMaxCnt", "createdAt", "updatedAt", "projectDescription", "openChatUrl", "expectation",
                        "isFavorite")
                .containsExactly(team.getId(), team.getProjectName(), team.getDesignerCurrentCnt(),
                        team.getBackendCurrentCnt(), team.getFrontendCurrentCnt(), team.getManagerCurrentCnt(),
                        team.getDesignerMaxCnt(), team.getBackendMaxCnt(), team.getFrontendMaxCnt(),
                        team.getManagerMaxCnt(), team.getCreatedAt(), team.getUpdatedAt(), team.getProjectDescription(),
                        team.getOpenChatUrl(), team.getExpectation(), null);

        assertThat(response.getTeamMembers())
                .extracting("userId", "username", "nickname", "position", "isLeader")
                .containsExactlyInAnyOrder(
                        tuple(user.getId(), user.getUsername(), user.getNickname(), teamMember.getPosition(),
                                teamMember.getIsLeader())
                );

        assertThat(response.getOffers()).isEmpty();
    }

    @Test
    @DisplayName("내 팀이 아닌 다른 팀 단건 조회를 한다.")
    void givenNotMyTeam_whenFindOtherTeam_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일", Position.BACKEND);
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이", Position.BACKEND);

        Team team = createSavedTeam();
        TeamMember teamMember = createdSavedTeamMember(true, user1, team);

        Favorite favorite = createSavedFavorite(user2, null, team);
        Offer offer = createSavedOffer(team, user2, Position.MANAGER);

        // when
        TeamOfferFavoriteResponse response = teamService.findOtherTeam(user2.getUsername(), team.getId());

        // then
        assertThat(team.getVisitedCnt()).isEqualTo(1L);

        assertThat(response)
                .extracting("teamId", "projectName", "designerCurrentCnt", "backendCurrentCnt",
                        "frontendCurrentCnt", "managerCurrentCnt", "designerMaxCnt", "backendMaxCnt", "frontendMaxCnt",
                        "managerMaxCnt", "createdAt", "updatedAt", "projectDescription", "openChatUrl", "expectation",
                        "isFavorite")
                .containsExactly(team.getId(), team.getProjectName(), team.getDesignerCurrentCnt(),
                        team.getBackendCurrentCnt(), team.getFrontendCurrentCnt(), team.getManagerCurrentCnt(),
                        team.getDesignerMaxCnt(), team.getBackendMaxCnt(), team.getFrontendMaxCnt(),
                        team.getManagerMaxCnt(), team.getCreatedAt(), team.getUpdatedAt(), team.getProjectDescription(),
                        team.getOpenChatUrl(), team.getExpectation(), true);

        assertThat(response.getTeamMembers())
                .extracting("userId", "username", "nickname", "position", "isLeader")
                .containsExactlyInAnyOrder(
                        tuple(user1.getId(), user1.getUsername(), user1.getNickname(), teamMember.getPosition(),
                                teamMember.getIsLeader())
                );

        assertThat(response.getOffers())
                .extracting("offerId", "position", "isAccepted", "offeredBy", "createdAt", "updatedAt")
                .containsExactlyInAnyOrder(
                        tuple(offer.getId(), offer.getPosition(), offer.getIsAccepted(), offer.getOfferedBy(),
                                offer.getCreatedAt(), offer.getUpdatedAt())
                );
    }

    @Test
    @DisplayName("존재하지 않은 회원 아이디로 다른 팀 단건 조회시 예외가 발생한다.")
    void givenNonExistingUser_whenFindOtherTeam_thenThrow() {
        // given
        String username = "tester";
        Team team = createSavedTeam();

        // when & then
        assertThatThrownBy(() -> teamService.findOtherTeam(username, team.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("존재하지 않은 팀 식별자로 다른 팀 단건 조회시 예외가 발생한다.")
    void givenNonExistingTeam_whenFindOtherTeam_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);
        long teamId = 1L;

        // when & then
        assertThatThrownBy(() -> teamService.findOtherTeam(user.getUsername(), teamId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TEAM_NOT_FOUND);
    }

    @Test
    @DisplayName("팀 페이징 조회를 한다.")
    void givenValid_whenFindPageTeam_thenReturn() {
        // given
        Team team1 = createSavedTeam();
        Team team2 = createSavedTeam();
        Team team3 = createSavedTeam();

        Position position = Position.NONE;
        long pageFrom = Long.MAX_VALUE;
        int pageSize = 2;

        // when
        PageData<List<TeamAbstractResponse>> teams = teamService.findPageTeam(position, pageFrom, pageSize);

        // then
        assertThat(teams.getData())
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
                );

        assertEquals(pageSize, teams.getData().size());
        assertEquals(3, teams.getTotal());
    }
    
    @Test
    @DisplayName("팀원 모집 여부를 업데이트한다.")
    void givenValid_whenUpdateIsRecruiting_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);
        Team team = createSavedTeam();
        createdSavedTeamMember(true, user, team);

        boolean isRecruiting = false;

        // when
        teamService.updateIsRecruiting(user.getUsername(), isRecruiting);

        // then
        assertFalse(team.getIsRecruiting());
    }

    @Test
    @DisplayName("팀 리더가 아닌 회원으로 팀원 모집 여부 업데이트를 하면 예외가 발생한다.")
    void givenNonLeader_whenUpdateIsRecruiting_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);
        Team team = createSavedTeam();
        createdSavedTeamMember(false, user, team);

        boolean isRecruiting = false;

        // when & then
        assertThatThrownBy(() -> teamService.updateIsRecruiting(user.getUsername(), isRecruiting))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(REQUEST_FORBIDDEN);
    }

    @Test
    @DisplayName("존재하지 않은 회원으로 팀원 모집 여부 업데이트를 하면 예외가 발생한다.")
    void givenNonExistingUsername_whenUpdateIsRecruiting_thenThrow() {
        // given
        String username = "tester";

        boolean isRecruiting = false;

        // when & then
        assertThatThrownBy(() -> teamService.updateIsRecruiting(username, isRecruiting))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("현재 소속 팀 없이 팀원 모집 여부 업데이트를 하면 예외가 발생한다.")
    void givenNonExistingCurrentTeam_whenUpdateIsRecruiting_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);

        boolean isRecruiting = false;

        // when & then
        assertThatThrownBy(() -> teamService.updateIsRecruiting(user.getUsername(), isRecruiting))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CURRENT_TEAM_NOT_FOUND);
    }

    @Test
    @DisplayName("프로젝트 미완료로 프로젝트 종료한다.")
    void givenIncomplete_whenEndProject_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);
        Team team = createSavedTeam();

        TeamMember teamMember = createdSavedTeamMember(true, user, team);

        LocalDateTime completeAt = LocalDateTime.now();

        // when
        teamService.endProject(user.getUsername(), "", completeAt);

        // then
        assertThat(teamMember)
                .extracting("id", "position", "isLeader", "teamMemberStatus", "isDeleted")
                .containsExactly(teamMember.getId(), teamMember.getPosition(), true, TeamMemberStatus.INCOMPLETE, true);
    }

    @Test
    @DisplayName("프로젝트 완료로 프로젝트 종료한다.")
    void givenComplete_whenEndProject_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);
        Team team = createSavedTeam();

        TeamMember teamMember = createdSavedTeamMember(true, user, team);

        String projectUrl = "github.com/gabojait";
        LocalDateTime completeAt = LocalDateTime.now();

        // when
        teamService.endProject(user.getUsername(), projectUrl, completeAt);

        // then
        assertThat(teamMember)
                .extracting("id", "position", "isLeader", "teamMemberStatus", "isDeleted")
                .containsExactly(teamMember.getId(), teamMember.getPosition(), true, TeamMemberStatus.COMPLETE, false);
    }

    @Test
    @DisplayName("팀원 추방을 한다.")
    void givenValid_whenFire_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터1", Position.BACKEND);
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터2", Position.DESIGNER);
        Team team = createSavedTeam();

        createdSavedTeamMember(true, user1, team);
        createdSavedTeamMember(false, user2, team);

        // when
        teamService.fire(user1.getUsername(), user2.getId());

        // then
        Optional<TeamMember> foundTeamMember = teamMemberRepository.find(user2.getId(), team.getId(),
                TeamMemberStatus.PROGRESS);

        assertThat(foundTeamMember).isEmpty();
    }

    @Test
    @DisplayName("회원이 팀장이 아닐시 팀원 추방을 하면 예외가 발생한다.")
    void givenNonLeaderUsername_whenFire_thenThrow() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터1", Position.BACKEND);
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터2", Position.DESIGNER);
        Team team = createSavedTeam();

        createdSavedTeamMember(false, user1, team);
        createdSavedTeamMember(false, user2, team);

        // when & then
        assertThatThrownBy(() -> teamService.fire(user1.getUsername(), user2.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(REQUEST_FORBIDDEN);
    }

    @Test
    @DisplayName("존재하지 않은 회원 아이디로 팀원 추방을 하면 예외가 발생한다.")
    void givenNonExistingUsername_whenFire_thenThrow() {
        // given
        String username = "tester2";
        User user = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터1", Position.BACKEND);
        Team team = createSavedTeam();
        createdSavedTeamMember(false, user, team);

        // when & then
        assertThatThrownBy(() -> teamService.fire(username, user.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("존재하지 않은 현재 팀을 가진 회원으로 팀원 추방을 하면 예외가 발생한다.")
    void givenNonExistingCurrentTeam_whenFire_thenThrow() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터1", Position.BACKEND);
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터2", Position.DESIGNER);

        // when & then
        assertThatThrownBy(() -> teamService.fire(user1.getUsername(), user2.getId()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CURRENT_TEAM_NOT_FOUND);
    }

    @Test
    @DisplayName("팀을 나간다.")
    void givenValid_whenLeave_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);
        Team team = createSavedTeam();

        TeamMember teamMember = createdSavedTeamMember(false, user, team);

        // when
        teamService.leave(user.getUsername());

        // then
        assertThat(teamMember)
                .extracting("teamMemberStatus", "isDeleted")
                .containsExactly(TeamMemberStatus.QUIT, true);
    }

    @Test
    @DisplayName("존재하지 않은 회원으로 팀을 나가면 예외가 발생한다.")
    void givenNonExistingUser_whenLeave_thenThrow() {
        // given
        String username = "tester";

        // when & then
        assertThatThrownBy(() -> teamService.leave(username))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("현재 소속된 팀이 없는 회원으로 팀을 나가면 예외가 발생한다.")
    void givenNonExistingCurrentTeam_whenLeave_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);

        // when & then
        assertThatThrownBy(() -> teamService.leave(user.getUsername()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CURRENT_TEAM_NOT_FOUND);
    }

    @Test
    @DisplayName("팀장인 회원으로 팀을 나가면 예외가 발생한다.")
    void givenTeamLeader_whenLeave_thenThrow() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);
        Team team = createSavedTeam();

        createdSavedTeamMember(true, user, team);

        // when & then
        assertThatThrownBy(() -> teamService.leave(user.getUsername()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TEAM_LEADER_UNAVAILABLE);
    }

    private TeamDefaultRequest createValidTeamDefaultRequest() {
        return TeamDefaultRequest.builder()
                .projectName("가볼까잇")
                .projectDescription("가볼까잇 설명입니다.")
                .expectation("열정적인 팀원을 구해요.")
                .openChatUrl("kakao.com/o/gabojait")
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