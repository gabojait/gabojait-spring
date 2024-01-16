package com.gabojait.gabojaitspring.auth;

import com.gabojait.gabojaitspring.domain.user.*;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.repository.user.ContactRepository;
import com.gabojait.gabojaitspring.repository.user.UserRepository;
import com.gabojait.gabojaitspring.repository.user.UserRoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.TOKEN_UNAUTHENTICATED;
import static com.gabojait.gabojaitspring.common.code.ErrorCode.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class JwtProviderTest {

    @Autowired private JwtProvider jwtProvider;
    @Autowired private CustomUserDetailsService customUserDetailsService;
    @Autowired private ContactRepository contactRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private UserRoleRepository userRoleRepository;
    private final String tokenPrefix = "Bearer ";

    @Test
    @DisplayName("회원의 Jwt를 생성한다.")
    void givenUser_whenCreateJwt_thenReturn() {
        // given
        List<Role> roles = new ArrayList<>(List.of(Role.USER));
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND, roles);

        // when
        HttpHeaders headers = jwtProvider.createJwt(user.getId());

        // then
        String accessToken = headers.get(AUTHORIZATION).get(0);
        String refreshToken = headers.get("Refresh-Token").get(0);
        jwtProvider.authenticate(tokenPrefix + accessToken, Jwt.ACCESS);
        jwtProvider.authenticate(tokenPrefix + refreshToken, Jwt.REFRESH);
    }

    @Test
    @DisplayName("관리자의 Jwt를 생성한다.")
    void givenAdmin_whenCreateJwt_thenReturn() {
        // given
        List<Role> roles = new ArrayList<>(List.of(Role.USER, Role.ADMIN));
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND, roles);

        // when
        HttpHeaders headers = jwtProvider.createJwt(user.getId());

        // then
        String accessToken = headers.get(AUTHORIZATION).get(0);
        String refreshToken = headers.get("Refresh-Token").get(0);
        jwtProvider.authenticate(tokenPrefix + accessToken, Jwt.ACCESS);
        jwtProvider.authenticate(tokenPrefix + refreshToken, Jwt.REFRESH);
    }

    @Test
    @DisplayName("마스터의 Jwt를 생성한다.")
    void givenMaster_whenCreateJwt_thenReturn() {
        // given
        List<Role> roles = new ArrayList<>(List.of(Role.USER, Role.ADMIN, Role.MASTER));
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND,
                roles);

        // when
        HttpHeaders headers = jwtProvider.createJwt(user.getId());

        // then
        String accessToken = headers.get(AUTHORIZATION).get(0);
        jwtProvider.authenticate(tokenPrefix + accessToken, Jwt.ACCESS);
        assertThat(headers.getOrEmpty("Refresh-Token")).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않은 회원으로 Jwt를 생성하면 예외가 발생한다.")
    void givenNonExistingUser_whenCreateJwt_thenThrow() {
        // given
        long userId = 1L;

        // when
        assertThatThrownBy(() -> jwtProvider.createJwt(userId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test // TODO
    @DisplayName("")
    void authenticate() {
    }

    @Test
    @DisplayName("액세스 토큰으로 회원 식별자를 반환한다.")
    void givenUserAccessToken_whenGetUserId_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND, List.of(Role.USER));
        String token = jwtProvider.createJwt(user.getId()).get(AUTHORIZATION).get(0);

        // when
        long gotUserId = jwtProvider.getUserId(tokenPrefix + token);

        // then
        assertThat(gotUserId).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("리프레시 토큰으로 회원 식별자를 반환한다.")
    void givenUserRefreshToken_whenGetUserId_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND, List.of(Role.USER));
        String token = jwtProvider.createJwt(user.getId()).get("Refresh-Token").get(0);

        // when
        long gotUserId = jwtProvider.getUserId(tokenPrefix + token);

        // then
        assertThat(gotUserId).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("액세스 토큰으로 관리자 회원 식별자를 반환한다.")
    void givenAdminAccessToken_whenGetUserId_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND,
                List.of(Role.USER, Role.ADMIN));
        String token = jwtProvider.createJwt(user.getId()).get("Refresh-Token").get(0);

        // when
        long gotUserId = jwtProvider.getUserId(tokenPrefix + token);

        // then
        assertThat(gotUserId).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("리프레시 토큰으로 관리자 회원 식별자를 반환한다.")
    void givenAdminRefreshToken_whenGetUserId_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND,
                List.of(Role.USER, Role.ADMIN));
        String token = jwtProvider.createJwt(user.getId()).get("Refresh-Token").get(0);

        // when
        long gotUserId = jwtProvider.getUserId(tokenPrefix + token);

        // then
        assertThat(gotUserId).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("액세스 토큰으로 마스터 회원 식별자를 반환한다.")
    void givenMasterAccessToken_whenGetUserId_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터", Position.BACKEND,
                List.of(Role.USER, Role.ADMIN));
        String token = jwtProvider.createJwt(user.getId()).get("Refresh-Token").get(0);

        // when
        long gotUserId = jwtProvider.getUserId(tokenPrefix + token);

        // then
        assertThat(gotUserId).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("NULL인 토큰으로 회원 식별자를 반환하면 예외가 발생한다.")
    void givenNull_whenGetUserId_thenThrow() {
        // given
        String token = null;

        // when & then
        assertThatThrownBy(() -> jwtProvider.getUserId(token))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TOKEN_UNAUTHENTICATED);
    }

    @Test
    @DisplayName("미입력된 토큰으로 회원 식별자를 반환하면 예외가 발생한다.")
    void givenBlank_whenGetUserId_thenThrow() {
        // given
        String token = "";

        // when & then
        assertThatThrownBy(() -> jwtProvider.getUserId(token))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TOKEN_UNAUTHENTICATED);
    }

    private User createSavedDefaultUser(String email,
                                        String username,
                                        String nickname,
                                        Position position,
                                        List<Role> roles) {
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

        roles.forEach(r -> {
                    userRoleRepository.save(UserRole.builder()
                            .user(user)
                            .role(r)
                            .build());
                });

        return user;
    }
}