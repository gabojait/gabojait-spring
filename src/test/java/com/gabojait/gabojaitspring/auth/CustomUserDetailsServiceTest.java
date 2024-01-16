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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class CustomUserDetailsServiceTest {

    @Autowired private CustomUserDetailsService customUserDetailsService;
    @Autowired private ContactRepository contactRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private UserRoleRepository userRoleRepository;

    @Test
    @DisplayName("회원 아이디로 회원 상세 정보를 조회한다.")
    void givenValid_whenLoadUserByUsername_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");
        Role role1 = Role.USER;
        UserRole userRole1 = createUserRole(user, role1);
        Role role2 = Role.ADMIN;
        UserRole userRole2 = createUserRole(user, role2);
        userRoleRepository.saveAll(List.of(userRole1, userRole2));

        // when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());

        // then
        assertAll(
                () -> assertThat(userDetails)
                        .extracting("username", "password")
                        .containsExactly(user.getUsername(), user.getPassword()),
                () -> assertThat(userDetails.getAuthorities())
                        .extracting("authority")
                        .containsExactlyInAnyOrder(role1.name(), role2.name())
        );
    }

    @Test
    @DisplayName("존재하지 않은 회원 식별자로 회원 상세 정보를 조회하면 예외가 발생한다.")
    void givenNonExistingUser_whenLoadUserByUsername_thenThrow() {
        // given
        String username = "tester";

        // when & then
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(username))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("회원 식별자로 회원 상세 정보를 조회한다")
    void givenValid_whenLoadUserByUserId_thenReturn() {
        // given
        User user = createSavedDefaultUser("tester@gabojait.com", "tester", "테스터");
        Role role1 = Role.USER;
        UserRole userRole1 = createUserRole(user, role1);
        Role role2 = Role.ADMIN;
        UserRole userRole2 = createUserRole(user, role2);
        userRoleRepository.saveAll(List.of(userRole1, userRole2));

        // when
        UserDetails userDetails = customUserDetailsService.loadUserByUserId(user.getId());

        // then
        assertAll(
                () -> assertThat(userDetails)
                        .extracting("username", "password")
                        .containsExactly(user.getUsername(), user.getPassword()),
                () -> assertThat(userDetails.getAuthorities())
                        .extracting("authority")
                        .containsExactlyInAnyOrder(role1.name(), role2.name())
        );
    }

    @Test
    @DisplayName("존재하지 않은 회원 식별자로 회원 상세 정보를 조회하면 예외가 발생한다.")
    void givenNonExistingUser_whenLoadUserByUserId_thenThrow() {
        // given
        long userId = 1L;

        // when & then
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUserId(userId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_FOUND);
    }

    private UserRole createUserRole(User user, Role role) {
        return UserRole.builder()
                .role(role)
                .user(user)
                .build();
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