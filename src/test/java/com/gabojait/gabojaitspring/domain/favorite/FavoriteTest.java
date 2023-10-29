package com.gabojait.gabojaitspring.domain.favorite;

import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.user.Contact;
import com.gabojait.gabojaitspring.domain.user.Gender;
import com.gabojait.gabojaitspring.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FavoriteTest {

    @Test
    @DisplayName("회원 찜을 생성한다.")
    void givenFavoriteUser_whenBuilder_thenReturn() {
        // given
        User user1 = createDefaultUser("tester1@gabojait.com", "000000", "tester1", "password1!", "테스터일", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        User user2 = createDefaultUser("tester2@gabojait.com", "000000", "tester2", "password1!", "테스터이", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());

        // when
        Favorite favorite = createFavorite(user1, null, user2);

        // then
        assertThat(favorite)
                .extracting("user", "favoriteTeam", "favoriteUser")
                .containsExactly(user1, null, user2);
    }

    @Test
    @DisplayName("팀 찜을 생성한다.")
    void givenFavoriteTeam_whenBuilder_thenReturn() {
        // given
        User user = createDefaultUser("tester@gabojait.com", "000000", "tester", "password1!", "테스터", Gender.M,
                LocalDate.of(1997, 2, 11), LocalDateTime.now());
        Team team = createTeam("가보자잇", (byte) 2);

        // when
        Favorite favorite = createFavorite(user, team, null);

        // then
        assertThat(favorite)
                .extracting("user", "favoriteTeam", "favoriteUser")
                .containsExactly(user, team, null);
    }

    private Favorite createFavorite(User user, Team favoriteTeam, User favoriteUser) {
        return Favorite.builder()
                .user(user)
                .favoriteTeam(favoriteTeam)
                .favoriteUser(favoriteUser)
                .build();
    }

    private Team createTeam(String projectName,
                            byte maxCnt) {
        return Team.builder()
                .projectName(projectName)
                .projectDescription("프로젝트 설명입니다.")
                .expectation("열정적인 팀원을 구합니다.")
                .openChatUrl("kakao.com/o/gabojait")
                .designerMaxCnt(maxCnt)
                .backendMaxCnt(maxCnt)
                .frontendMaxCnt(maxCnt)
                .managerMaxCnt(maxCnt)
                .build();
    }

    private User createDefaultUser(String email,
                                   String verificationCode,
                                   String username,
                                   String password,
                                   String nickname,
                                   Gender gender,
                                   LocalDate birthdate,
                                   LocalDateTime lastRequestAt) {
        Contact contact = Contact.builder()
                .email(email)
                .verificationCode(verificationCode)
                .build();
        contact.verified();

        return User.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .gender(gender)
                .birthdate(birthdate)
                .lastRequestAt(lastRequestAt)
                .contact(contact)
                .build();
    }
}