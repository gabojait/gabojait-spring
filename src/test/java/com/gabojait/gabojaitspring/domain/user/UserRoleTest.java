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
import static org.junit.jupiter.api.Assertions.assertAll;

class UserRoleTest {

    @Test
    @DisplayName("회원 권한 생성이 정상 작동한다")
    void givenValid_whenBuilder_thenReturn() {
        // given
        User user = createDefaultUser("tester", LocalDateTime.now());
        Role role = Role.USER;

        // when
        UserRole userRole = createDefaultUserRole(role, user);

        // then
        assertAll(
                () -> assertThat(userRole.getRole()).isEqualTo(role),
                () -> assertThat(userRole.getUser()).isEqualTo(user)
        );
    }

    private static Stream<Arguments> providerEquals() {
        LocalDateTime now = LocalDateTime.now();

        User user = createDefaultUser("tester", now);
        UserRole userRole = createDefaultUserRole(Role.USER, user);

        User user1 = createDefaultUser("tester1", now);
        User user2 = createDefaultUser("tester2", now);

        return Stream.of(
                Arguments.of(userRole, userRole, true),
                Arguments.of(userRole, new Object(), false),
                Arguments.of(
                        createDefaultUserRole(Role.USER, user),
                        createDefaultUserRole(Role.USER, user),
                        true
                ),
                Arguments.of(
                        createDefaultUserRole(Role.USER, user1),
                        createDefaultUserRole(Role.USER, user2),
                        false
                ),
                Arguments.of(
                        createDefaultUserRole(Role.USER, user),
                        createDefaultUserRole(Role.ADMIN, user),
                        false
                )
        );
    }

    @ParameterizedTest(name = "[{index}] 권한 객체를 비교한다")
    @MethodSource("providerEquals")
    @DisplayName("권한 객체 비교가 정상 작동한다")
    void givenProvider_whenEquals_thenReturn(UserRole userRole, Object object, boolean result) {
        // when & then
        assertThat(userRole.equals(object)).isEqualTo(result);
    }

    private static Stream<Arguments> providerHashCode() {
        User user = createDefaultUser("tester", LocalDateTime.now());

        return Stream.of(
                Arguments.of(
                        createDefaultUserRole(Role.USER, user),
                        createDefaultUserRole(Role.USER, user),
                        true
                ),
                Arguments.of(
                        createDefaultUserRole(Role.USER, user),
                        createDefaultUserRole(Role.ADMIN, user),
                        false
                )
        );
    }

    @ParameterizedTest(name = "[{index}] 권한 해시코드를 비교한다")
    @MethodSource("providerHashCode")
    @DisplayName("권한 해시코드 비교가 정상 작동한다")
    void givenProvider_whenHashCode_thenReturn(UserRole userRole1, UserRole userRole2, boolean result) {
        // when
        int hashCode1 = userRole1.hashCode();
        int hashCode2 = userRole2.hashCode();

        // then
        assertThat(hashCode1 == hashCode2).isEqualTo(result);
    }

    private static UserRole createDefaultUserRole(Role role, User user) {
        return UserRole.builder()
                .user(user)
                .role(role)
                .build();
    }

    private static User createDefaultUser(String username, LocalDateTime lastRequestAt) {
        Contact contact = Contact.builder()
                .email("tester@gabojait.com")
                .verificationCode("000000")
                .build();
        contact.verified();

        return User.builder()
                .username(username)
                .password("password1!")
                .nickname("테스터")
                .gender(Gender.M)
                .birthdate(LocalDate.of(1997, 2, 11))
                .lastRequestAt(lastRequestAt)
                .contact(contact)
                .build();
    }
}