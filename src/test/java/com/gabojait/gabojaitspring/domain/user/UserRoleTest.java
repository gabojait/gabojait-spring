package com.gabojait.gabojaitspring.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserRoleTest {

    @Test
    @DisplayName("회원 권한을 생성한다.")
    void builder() {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime now = LocalDateTime.now();
        Role role = Role.USER;

        // when
        UserRole userRole = createDefaultUserRole(email, verificationCode, username, password, nickname, gender,
                birthdate, now, role);

        // then
        assertThat(userRole.getRole()).isEqualTo(role);
    }

    @Test
    @DisplayName("같은 객체인 권한을 비교하면 동일하다.")
    void givenEqualInstance_whenEquals_thenReturn() {
        // given
        UserRole userRole = createDefaultUserRole("tester@gabojait.com", "000000", "tester", "password1!", "테스터",
                Gender.N, LocalDate.of(1997, 2, 11), LocalDateTime.now(), Role.USER);

        // when
        boolean result = userRole.equals(userRole);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("같은 정보인 권한을 비교하면 동일하다.")
    void givenEqualData_whenEquals_thenReturn() {
        // given
        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime now = LocalDateTime.now();
        Role role = Role.USER;

        UserRole userRole1 = createDefaultUserRole(email, verificationCode, username, password, nickname, gender,
                birthdate, now, role);
        UserRole userRole2 = createDefaultUserRole(email, verificationCode, username, password, nickname, gender,
                birthdate, now, role);

        // when
        boolean result = userRole1.equals(userRole2);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("다른 객체로 권한을 비교하면 동일하지 않다.")
    void givenUnequalInstance_whenEquals_thenReturn() {
        // given
        UserRole userRole = createDefaultUserRole("tester@gabojait.com", "000000", "tester", "password1!", "테스터",
                Gender.N, LocalDate.of(1997, 2, 11), LocalDateTime.now(), Role.USER);
        Object object = new Object();

        // when
        boolean result = userRole.equals(object);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 회원인 권한을 비교하면 동일하지 않다.")
    void givenUnequalUser_whenEquals_thenReturn() {
        // given
        String username1 = "tester1";
        String username2 = "tester2";

        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime now = LocalDateTime.now();
        Role role = Role.USER;

        UserRole userRole1 = createDefaultUserRole(email, verificationCode, username1, password, nickname, gender,
                birthdate, now, role);
        UserRole userRole2 = createDefaultUserRole(email, verificationCode, username2, password, nickname, gender,
                birthdate, now, role);

        // when
        boolean result = userRole1.equals(userRole2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 권한인 권한을 비교하면 동일하지 않다.")
    void givenUnequalRole_whenEquals_thenReturn() {
        // given
        Role role1 = Role.USER;
        Role role2 = Role.ADMIN;

        String email = "tester@gabojait.com";
        String verificationCode = "000000";
        String username = "tester";
        String password = "password1!";
        String nickname = "테스터";
        Gender gender = Gender.M;
        LocalDate birthdate = LocalDate.of(1997, 2, 11);
        LocalDateTime now = LocalDateTime.now();

        UserRole userRole1 = createDefaultUserRole(email, verificationCode, username, password, nickname, gender,
                birthdate, now, role1);
        UserRole userRole2 = createDefaultUserRole(email, verificationCode, username, password, nickname, gender,
                birthdate, now, role2);

        // when
        boolean result = userRole1.equals(userRole2);

        // then
        assertThat(result).isFalse();
    }

    private UserRole createDefaultUserRole(String email,
                                           String verificationCode,
                                           String username,
                                           String password,
                                           String nickname,
                                           Gender gender,
                                           LocalDate birthdate,
                                           LocalDateTime lastRequestAt,
                                           Role role) {
        Contact contact = Contact.builder()
                .email(email)
                .verificationCode(verificationCode)
                .build();
        contact.verified();

        User user = User.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .gender(gender)
                .birthdate(birthdate)
                .lastRequestAt(lastRequestAt)
                .contact(contact)
                .build();

        return UserRole.builder()
                .user(user)
                .role(role)
                .build();
    }
}