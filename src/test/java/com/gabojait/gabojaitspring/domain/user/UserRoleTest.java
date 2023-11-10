package com.gabojait.gabojaitspring.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;

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

    private static Stream<Arguments> providerEquals() {
        LocalDateTime now = LocalDateTime.now();

        UserRole userRole = createDefaultUserRole("tester@gabojait.com", "000000", "tester", "password1!", "테스터",
                Gender.N, LocalDate.of(1997, 2, 11), now, Role.USER);

        return Stream.of(
                Arguments.of(userRole, userRole, true),
                Arguments.of(userRole, new Object(), false),
                Arguments.of(
                        createDefaultUserRole("tester@gabojait.com", "000000", "tester", "password1!", "테스터",
                                Gender.N, LocalDate.of(1997, 2, 11), now, Role.USER),
                        createDefaultUserRole("tester@gabojait.com", "000000", "tester", "password1!", "테스터",
                                Gender.N, LocalDate.of(1997, 2, 11), now, Role.USER),
                        true
                ),
                Arguments.of(
                        createDefaultUserRole("tester@gabojait.com", "000000", "tester1", "password1!", "테스터",
                                Gender.N, LocalDate.of(1997, 2, 11), now, Role.USER),
                        createDefaultUserRole("tester@gabojait.com", "000000", "tester2", "password1!", "테스터",
                                Gender.N, LocalDate.of(1997, 2, 11), now, Role.USER),
                        false
                ),
                Arguments.of(
                        createDefaultUserRole("tester@gabojait.com", "000000", "tester", "password1!", "테스터",
                                Gender.N, LocalDate.of(1997, 2, 11), now, Role.USER),
                        createDefaultUserRole("tester@gabojait.com", "000000", "tester", "password1!", "테스터",
                                Gender.N, LocalDate.of(1997, 2, 11), now, Role.ADMIN),
                        false
                )
        );
    }

    @ParameterizedTest(name = "[{index}] 권한 객체를 비교한다.")
    @MethodSource("providerEquals")
    @DisplayName("권한 객체를 비교한다.")
    void givenProvider_whenEquals_thenReturn(UserRole userRole, Object object, boolean result) {
        // when & then
        assertThat(userRole.equals(object)).isEqualTo(result);
    }

    private static Stream<Arguments> providerHashCode() {
        LocalDateTime now = LocalDateTime.now();

        return Stream.of(
                Arguments.of(
                        createDefaultUserRole("tester@gabojait.com", "000000", "tester", "password1!", "테스터",
                                Gender.N, LocalDate.of(1997, 2, 11), now, Role.USER),
                        createDefaultUserRole("tester@gabojait.com", "000000", "tester", "password1!", "테스터",
                                Gender.N, LocalDate.of(1997, 2, 11), now, Role.USER),
                        true
                ),
                Arguments.of(
                        createDefaultUserRole("tester@gabojait.com", "000000", "tester", "password1!", "테스터",
                                Gender.N, LocalDate.of(1997, 2, 11), now, Role.USER),
                        createDefaultUserRole("tester@gabojait.com", "000000", "tester", "password1!", "테스터",
                                Gender.N, LocalDate.of(1997, 2, 11), now, Role.ADMIN),
                        false
                )
        );
    }

    @ParameterizedTest(name = "[{index}] 권한 해시코드를 비교한다.")
    @MethodSource("providerHashCode")
    @DisplayName("권한 해시코드를 비교한다.")
    void givenProvider_whenHashCode_thenReturn(UserRole userRole1, UserRole userRole2, boolean result) {
        // when
        int hashCode1 = userRole1.hashCode();
        int hashCode2 = userRole2.hashCode();

        // then
        assertThat(hashCode1 == hashCode2).isEqualTo(result);
    }

    private static UserRole createDefaultUserRole(String email,
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