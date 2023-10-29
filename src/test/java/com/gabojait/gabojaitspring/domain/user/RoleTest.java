package com.gabojait.gabojaitspring.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoleTest {

    @Test
    @DisplayName("회원 텍스트를 반환한다.")
    void givenUser_whenGetText_thenReturn() {
        // given
        Role role = Role.USER;

        // when
        String text = role.getText();

        // then
        assertThat(text).isEqualTo("회원");
    }

    @Test
    @DisplayName("관리자 텍스트를 반환한다.")
    void givenAdmin_whenGetText_thenReturn() {
        // given
        Role role = Role.ADMIN;

        // when
        String text = role.getText();

        // then
        assertThat(text).isEqualTo("관리자");
    }

    @Test
    @DisplayName("마스터 텍스트를 반환한다.")
    void givenMaster_whenGetText_thenReturn() {
        // given
        Role role = Role.MASTER;

        // when
        String text = role.getText();

        // then
        assertThat(text).isEqualTo("마스터");
    }

    @Test
    @DisplayName("전체 권한을 반환한다.")
    void values() {
        // given & when
        Role[] roles = Role.values();

        // then
        assertThat(roles).containsExactlyInAnyOrder(Role.USER, Role.ADMIN, Role.MASTER);
    }

    @Test
    @DisplayName("값 회원을 권한으로 변환한다.")
    void givenUser_whenValueOf_thenReturn() {
        // given
        String s = "USER";

        // when
        Role role = Role.valueOf(s);

        // then
        assertThat(role).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("값 관리자를 권한으로 변환한다.")
    void givenAdmin_whenValueOf_thenReturn() {
        // given
        String s = "ADMIN";

        // when
        Role role = Role.valueOf(s);

        // then
        assertThat(role).isEqualTo(Role.ADMIN);
    }

    @Test
    @DisplayName("값 마스터를 권한으로 변환한다.")
    void givenMaster_whenValueOf_thenReturn() {
        // given
        String s = "MASTER";

        // when
        Role role = Role.valueOf(s);

        // then
        assertThat(role).isEqualTo(Role.MASTER);
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