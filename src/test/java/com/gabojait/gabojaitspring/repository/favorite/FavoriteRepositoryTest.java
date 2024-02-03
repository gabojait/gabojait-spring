package com.gabojait.gabojaitspring.repository.favorite;

import com.gabojait.gabojaitspring.common.response.PageData;
import com.gabojait.gabojaitspring.domain.favorite.Favorite;
import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.User;
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
import static org.junit.jupiter.api.Assertions.assertAll;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class FavoriteRepositoryTest {

    @Autowired private FavoriteRepository favoriteRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ContactRepository contactRepository;
    @Autowired private TeamRepository teamRepository;

    @Test
    @DisplayName("존재하는 회원의 찜 여부 조회시 참을 반환한다")
    void givenExistingFavorite_whenExistsUser_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "터스터일");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "터스터이");

        Favorite favorite = createFavorite(user1, user2, null);
        favoriteRepository.save(favorite);

        // when
        boolean result = favoriteRepository.existsUser(user1.getId(), user2.getId());

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("존재하지 않은 회원의 찜 여부 조회시 거짓을 반환한다")
    void givenNonExistingFavorite_whenExistsByUserIdAndTargetUserId_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "터스터일");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "터스터이");

        // when
        boolean result = favoriteRepository.existsUser(user1.getId(), user2.getId());

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("존재하는 팀의 찜 여부 조회시 참을 반환한다")
    void givenExistingFavorite_whenExistsTeam_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Team team = createSavedTeam("가보자잇");

        Favorite favorite = createFavorite(user, null, team);
        favoriteRepository.save(favorite);

        // when
        boolean result = favoriteRepository.existsTeam(user.getId(), team.getId());

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("존재하지 않은 팀의 찜 여부 조회시 거짓을 반환한다")
    void givenNonExistingFavorite_whenExistsTeam_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Team team = createSavedTeam("가보자잇");

        // when
        boolean result = favoriteRepository.existsTeam(user.getId(), team.getId());

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("존재하는 회원 찜으로 찜 단건 조회가 정상 작동한다")
    void givenExistingFavorite_whenFindUser_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");

        Favorite favorite = createFavorite(user1, user2, null);
        favoriteRepository.save(favorite);

        // when
        Favorite foundFavorite = favoriteRepository.findUser(user1.getId(), user2.getId()).get();

        // then
        assertThat(foundFavorite).isEqualTo(favorite);
    }

    @Test
    @DisplayName("존재하는 찜이 없을시 회원 찜으로 찜 단건 조회가 정상 작동한다")
    void givenNonExistingFavorite_whenFindUser_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");

        // when
        Optional<Favorite> foundFavorite = favoriteRepository.findUser(user1.getId(), user2.getId());

        // then
        assertThat(foundFavorite).isEmpty();
    }

    @Test
    @DisplayName("존재하는 회원 찜으로 찜 단건 조회가 정상 작동한다")
    void givenValid_whenFindTeam_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");
        Team team = createSavedTeam("가보자잇");

        Favorite favorite = createFavorite(user, null, team);
        favoriteRepository.save(favorite);

        // when
        Favorite foundFavorite = favoriteRepository.findTeam(user.getId(), team.getId()).get();

        // then
        assertThat(foundFavorite).isEqualTo(favorite);
    }

    @Test
    @DisplayName("존재하지 않은 회원 찜으로 찜 단건 조회가 정상 작동한다")
    void givenNonExistingFavorite_whenFindTeam_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");
        Team team = createSavedTeam("가보자잇");

        // when
        Optional<Favorite> foundFavorite = favoriteRepository.findTeam(user.getId(), team.getId());

        // then
        assertThat(foundFavorite).isEmpty();
    }

    @Test
    @DisplayName("찜한 회원이 있을시 찜한 회원 페이징 조회가 정상 작동한다")
    void givenValid_whenFindPageUser_thenReturn() {
        // given
        User user1 = createSavedDefaultUser("tester1@gabojait.com", "tester1", "테스터일");
        User user2 = createSavedDefaultUser("tester2@gabojait.com", "tester2", "테스터이");
        User user3 = createSavedDefaultUser("tester3@gabojait.com", "tester3", "테스터삼");
        User user4 = createSavedDefaultUser("tester4@gabojait.com", "tester4", "테스터사");

        Favorite favorite1 = createFavorite(user1, user2, null);
        Favorite favorite2 = createFavorite(user1, user3, null);
        Favorite favorite3 = createFavorite(user1, user4, null);
        favoriteRepository.saveAll(List.of(favorite1, favorite2, favorite3));

        long pageFrom = Long.MAX_VALUE;
        int pageSize = 2;

        // when
        PageData<List<Favorite>> favorites = favoriteRepository.findPageUser(user1.getId(), pageFrom, pageSize);

        // then
        assertAll(
                () -> assertThat(favorites.getData()).containsExactly(favorite3, favorite2),
                () -> assertThat(favorites.getData().size()).isEqualTo(pageSize),
                () -> assertThat(favorites.getTotal()).isEqualTo(3L)
        );
    }

    @Test
    @DisplayName("찜한 회원이 없을시 찜한 회원 페이징 조회가 정상 작동한다")
    void givenNoneExisting_whenFindPageUser_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        long pageFrom = Long.MAX_VALUE;
        int pageSize = 2;

        // when
        PageData<List<Favorite>> favorites = favoriteRepository.findPageUser(user.getId(), pageFrom, pageSize);

        // then
        assertAll(
                () -> assertThat(favorites.getData()).isEmpty(),
                () -> assertThat(favorites.getTotal()).isEqualTo(0L)
        );
    }

    @Test
    @DisplayName("찜한 팀이 있을시 찜한 팀 페이징 조회가 정상 작동한다")
    void givenValid_whenFindPageTeam_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        Team team1 = createSavedTeam("가보자잇1");
        Team team2 = createSavedTeam("가보자잇2");
        Team team3 = createSavedTeam("가보자잇3");

        Favorite favorite1 = createFavorite(user, null, team1);
        Favorite favorite2 = createFavorite(user, null, team2);
        Favorite favorite3 = createFavorite(user, null, team3);
        favoriteRepository.saveAll(List.of(favorite1, favorite2, favorite3));

        long pageFrom = Long.MAX_VALUE;
        int pageSize = 2;

        // when
        PageData<List<Favorite>> favorites = favoriteRepository.findPageTeam(user.getId(), pageFrom, pageSize);

        // then
        assertAll(
                () -> assertThat(favorites.getData()).containsExactly(favorite3, favorite2),
                () -> assertThat(favorites.getData().size()).isEqualTo(pageSize),
                () -> assertThat(favorites.getTotal()).isEqualTo(3L)
        );
    }

    @Test
    @DisplayName("찜한 팀이 없을시 찜한 팀 페이징 조회가 정상 작동한다")
    void givenNoneExisting_whenFindPageTeam_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");

        long pageFrom = Long.MAX_VALUE;
        int pageSize = 2;

        // when
        PageData<List<Favorite>> favorites = favoriteRepository.findPageTeam(user.getId(), pageFrom, pageSize);

        // then
        assertAll(
                () -> assertThat(favorites.getData()).isEmpty(),
                () -> assertThat(favorites.getTotal()).isEqualTo(0L)
        );
    }

    private Favorite createFavorite(User user, User favoriteUser, Team favoriteTeam) {
        return Favorite.builder()
                .user(user)
                .favoriteUser(favoriteUser)
                .favoriteTeam(favoriteTeam)
                .build();
    }

    private Team createSavedTeam(String projectName) {
        Team team = Team.builder()
                .projectName(projectName)
                .projectDescription("설명입니다.")
                .expectation("바라는 점입니다.")
                .openChatUrl("kakao.com/o/gabojait")
                .designerMaxCnt((byte) 0)
                .backendMaxCnt((byte) 0)
                .frontendMaxCnt((byte) 0)
                .managerMaxCnt((byte) 0)
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

        return userRepository.save(user);
    }

}