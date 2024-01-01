package com.gabojait.gabojaitspring.api.service.favorite;

import com.gabojait.gabojaitspring.api.dto.common.response.PageData;
import com.gabojait.gabojaitspring.api.dto.favorite.request.FavoriteDefaultRequest;
import com.gabojait.gabojaitspring.api.dto.favorite.response.FavoriteTeamPageResponse;
import com.gabojait.gabojaitspring.api.dto.favorite.response.FavoriteUserPageResponse;
import com.gabojait.gabojaitspring.domain.favorite.Favorite;
import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.domain.user.User;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.repository.favorite.FavoriteRepository;
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

import static com.gabojait.gabojaitspring.common.code.ErrorCode.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class FavoriteServiceTest {

    @Autowired private FavoriteService favoriteService;
    @Autowired private FavoriteRepository favoriteRepository;
    @Autowired private ContactRepository contactRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TeamRepository teamRepository;

    @Test
    @DisplayName("회원을 찜한다.")
    void givenAdd_whenUpdateFavoriteUser_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일", Position.BACKEND);
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이", Position.BACKEND);

        FavoriteDefaultRequest request = createValidFavoriteDefaultRequest(true);

        // when
        favoriteService.updateFavoriteUser(user1.getUsername(), user2.getId(), request);

        // then
        Optional<Favorite> favorite = favoriteRepository.findUser(user1.getId(), user2.getId());

        assertThat(favorite).isPresent();
    }

    @Test
    @DisplayName("이미 존재하는 찜한 회원을 추가한다.")
    void givenAddExisting_whenUpdateFavoriteUser_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일", Position.BACKEND);
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이", Position.BACKEND);
        createSavedFavorite(user1, user2, null);

        FavoriteDefaultRequest request = createValidFavoriteDefaultRequest(true);

        // when
        favoriteService.updateFavoriteUser(user1.getUsername(), user2.getId(), request);

        // then
        Optional<Favorite> foundFavorite = favoriteRepository.findUser(user1.getId(), user2.getId());

        assertThat(foundFavorite).isPresent();
    }

    @Test
    @DisplayName("찜한 회원을 제거한다.")
    void givenDelete_whenUpdateFavoriteUser_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일", Position.BACKEND);
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이", Position.BACKEND);
        createSavedFavorite(user1, user2, null);

        FavoriteDefaultRequest request = createValidFavoriteDefaultRequest(false);

        // when
        favoriteService.updateFavoriteUser(user1.getUsername(), user2.getId(), request);

        // then
        Optional<Favorite> foundFavorite = favoriteRepository.findUser(user1.getId(), user2.getId());

        assertThat(foundFavorite).isEmpty();
    }

    @Test
    @DisplayName("이미 존재하지 않은 찜한 회원을 제거한다.")
    void givenDeleteNonExisting_whenUpdateFavoriteUser_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일", Position.BACKEND);
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이", Position.BACKEND);
        createSavedFavorite(user1, user2, null);

        FavoriteDefaultRequest request = createValidFavoriteDefaultRequest(false);

        // when
        favoriteService.updateFavoriteUser(user1.getUsername(), user2.getId(), request);

        // then
        Optional<Favorite> foundFavorite = favoriteRepository.findUser(user1.getId(), user2.getId());

        assertThat(foundFavorite).isEmpty();
    }

    @Test
    @DisplayName("팀을 찜한다.")
    void givenAdd_whenUpdateFavoriteTeam_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);
        Team team = createSavedTeam("가보자잇", (byte) 1);

        FavoriteDefaultRequest request = createValidFavoriteDefaultRequest(true);

        // when
        favoriteService.updateFavoriteTeam(user.getUsername(), team.getId(), request);

        // then
        Optional<Favorite> favorite = favoriteRepository.findTeam(user.getId(), team.getId());

        assertThat(favorite).isPresent();
    }

    @Test
    @DisplayName("이미 존재하는 찜한 팀을 추가한다.")
    void givenAddExisting_whenUpdateFavoriteTeam_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);
        Team team = createSavedTeam("가보자잇", (byte) 1);
        createSavedFavorite(user, null, team);

        FavoriteDefaultRequest request = createValidFavoriteDefaultRequest(true);

        // when
        favoriteService.updateFavoriteTeam(user.getUsername(), team.getId(), request);

        // then
        Optional<Favorite> foundFavorite = favoriteRepository.findTeam(user.getId(), team.getId());

        assertThat(foundFavorite).isPresent();
    }

    @Test
    @DisplayName("찜한 팀을 제거한다.")
    void givenDelete_whenUpdateFavoriteTeam_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);
        Team team = createSavedTeam("가보자잇", (byte) 1);
        createSavedFavorite(user, null, team);

        FavoriteDefaultRequest request = createValidFavoriteDefaultRequest(false);

        // when
        favoriteService.updateFavoriteTeam(user.getUsername(), team.getId(), request);

        // then
        Optional<Favorite> foundFavorite = favoriteRepository.findTeam(user.getId(), team.getId());

        assertThat(foundFavorite).isEmpty();
    }

    @Test
    @DisplayName("이미 존재하지 않은 찜한 팀을 제거한다.")
    void givenDeleteNonExisting_whenUpdateFavoriteTeam_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);
        Team team = createSavedTeam("가보자잇", (byte) 1);

        FavoriteDefaultRequest request = createValidFavoriteDefaultRequest(false);

        // when
        favoriteService.updateFavoriteTeam(user.getUsername(), team.getId(), request);

        // then
        Optional<Favorite> foundFavorite = favoriteRepository.findTeam(user.getId(), team.getId());

        assertThat(foundFavorite).isEmpty();
    }

    @Test
    @DisplayName("찜한 회원 페이징 조회를 한다.")
    void givenValid_whenFindPageFavoriteUser_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일", Position.BACKEND);
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이", Position.BACKEND);
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼", Position.BACKEND);
        User user4 = createSavedDefaultUser("tester4@gabojait.com", "tester4", "테스터사", Position.BACKEND);

        Favorite favorite1 = createSavedFavorite(user1, user2, null);
        Favorite favorite2 = createSavedFavorite(user1, user3, null);
        Favorite favorite3 = createSavedFavorite(user1, user4, null);

        long pageFrom = Long.MAX_VALUE;
        int pageSize = 2;

        // when
        PageData<List<FavoriteUserPageResponse>> responses = favoriteService.findPageFavoriteUser(user1.getUsername(),
                pageFrom, pageSize);

        // then
        assertThat(responses.getData())
                .extracting("favoriteId", "userId", "nickname", "position", "reviewCnt", "rating", "createdAt",
                        "updatedAt")
                .containsExactly(
                        tuple(favorite3.getId(), user4.getId(), user4.getNickname(), user4.getPosition(),
                                user4.getReviewCnt(), user4.getRating(), user4.getCreatedAt(), user4.getUpdatedAt()),
                        tuple(favorite2.getId(), user3.getId(), user3.getNickname(), user3.getPosition(),
                                user3.getReviewCnt(), user3.getRating(), user3.getCreatedAt(), user3.getUpdatedAt())
                );

        assertEquals(pageSize, responses.getData().size());
        assertEquals(3L, responses.getTotal());
    }

    @Test
    @DisplayName("존재하지 않은 회원으로 찜한 회원 페이징 조회를 하면 예외가 발생한다.")
    void givenNonExistingUser_whenFindPageFavoriteUser_thenThrow() {
        // given
        String username = "tester";

        long pageFrom = Long.MAX_VALUE;
        int pageSize = 2;

        // when & then
        assertThatThrownBy(() -> favoriteService.findPageFavoriteUser(username, pageFrom, pageSize))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("찜한 팀 페이징 조회를 한다.")
    void givenValid_whenFindPageFavoriteTeam_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND);
        Team team1 = createSavedTeam("가보자잇1", (byte) 1);
        Team team2 = createSavedTeam("가보자잇2", (byte) 2);
        Team team3 = createSavedTeam("가보자잇3", (byte) 3);

        Favorite favorite1 = createSavedFavorite(user, null, team1);
        Favorite favorite2 = createSavedFavorite(user, null, team2);
        Favorite favorite3 = createSavedFavorite(user, null, team3);

        long pageFrom = Long.MAX_VALUE;
        int pageSize = 2;

        // when
        PageData<List<FavoriteTeamPageResponse>> responses = favoriteService.findPageFavoriteTeam(user.getUsername(),
                pageFrom, pageSize);

        // then
        assertThat(responses.getData())
                .extracting("favoriteId", "teamId", "projectName", "designerCurrentCnt", "backendCurrentCnt", "frontendCurrentCnt",
                        "managerCurrentCnt", "designerMaxCnt", "backendMaxCnt", "frontendMaxCnt", "managerMaxCnt",
                        "createdAt", "updatedAt")
                .containsExactly(
                        tuple(favorite3.getId(), team3.getId(), team3.getProjectName(), team3.getDesignerCurrentCnt(),
                                team3.getBackendCurrentCnt(), team3.getFrontendCurrentCnt(),
                                team3.getManagerCurrentCnt(), team3.getDesignerMaxCnt(), team3.getBackendMaxCnt(),
                                team3.getFrontendMaxCnt(), team3.getManagerMaxCnt(), team3.getCreatedAt(),
                                team3.getUpdatedAt()),
                        tuple(favorite2.getId(), team2.getId(), team2.getProjectName(), team2.getDesignerCurrentCnt(),
                                team2.getBackendCurrentCnt(), team2.getFrontendCurrentCnt(),
                                team2.getManagerCurrentCnt(), team2.getDesignerMaxCnt(), team2.getBackendMaxCnt(),
                                team2.getFrontendMaxCnt(), team2.getManagerMaxCnt(), team2.getCreatedAt(),
                                team2.getUpdatedAt())
                );

        assertEquals(pageSize, responses.getData().size());
        assertEquals(3L, responses.getTotal());
    }

    private FavoriteDefaultRequest createValidFavoriteDefaultRequest(boolean isAddFavorite) {
        return FavoriteDefaultRequest.builder()
                .isAddFavorite(isAddFavorite)
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

    private Team createSavedTeam(String projectName, byte maxCnt) {
        Team team = Team.builder()
                .projectName(projectName)
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