package com.gabojait.gabojaitspring.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

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
        LocalDateTime lastRequestAt = LocalDateTime.now();
        Role role = Role.USER;

        // when
        UserRole userRole = createDefaultUserRole(email, verificationCode, username, password, nickname, gender, birthdate,
                lastRequestAt, role);

        // then
        assertEquals(role, userRole.getRole());
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