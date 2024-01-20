package com.gabojait.gabojaitspring.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoleTest {

    private static Stream<Arguments> providerGetText() {
        return Stream.of(
                Arguments.of(Role.USER, "회원"),
                Arguments.of(Role.ADMIN, "관리자"),
                Arguments.of(Role.MASTER, "마스터")
        );
    }

    @ParameterizedTest(name = "[{index}] {0} 권한 텍스트는 {1}다")
    @MethodSource("providerGetText")
    @DisplayName("권한 텍스트 반환이 정상 작동한다")
    void givenProvider_whenGetText_thenReturn(Role role, String text) {
        // when & then
        assertThat(role.getText()).isEqualTo(text);
    }

    @Test
    @DisplayName("전체 권한 반환이 정상 작동한다")
    void givenValid_whenValues_thenReturn() {
        // given & when
        Role[] roles = Role.values();

        // then
        assertThat(roles).containsExactlyInAnyOrder(Role.USER, Role.ADMIN, Role.MASTER);
    }

    private static Stream<Arguments> providerValueOf() {
        return Stream.of(
                Arguments.of("USER", Role.USER),
                Arguments.of("ADMIN", Role.ADMIN),
                Arguments.of("MASTER", Role.MASTER)
        );
    }

    @ParameterizedTest(name = "[{index}] {0} 값을 {1} 권한으로 변환한다")
    @MethodSource("providerValueOf")
    @DisplayName("권한 값 변환이 정상 작동한다")
    void givenProvider_whenValueOf_thenReturn(String value, Role role) {
        // when & then
        assertThat(Role.valueOf(value)).isEqualTo(role);
    }

    @Test
    @DisplayName("잘못된 값을 권한으로 변환하면 예외가 발생한다.")
    void givenInvalid_whenValueOf_thenThrow() {
        // given
        String s = "INVALID";

        // when & then
        assertThatThrownBy(() -> Role.valueOf(s))
                .isInstanceOf(IllegalArgumentException.class);
    }
}