package com.gabojait.gabojaitspring.config.auth;

import com.gabojait.gabojaitspring.config.auth.Jwt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTest {

    @Test
    @DisplayName("액세스 텍스트를 반환한다.")
    void givenAccess_whenGetText_thenReturn() {
        // given
        Jwt jwt = Jwt.ACCESS;

        // when
        String text = jwt.getText();

        // then
        assertThat(text).isEqualTo("액세스");
    }

    @Test
    @DisplayName("리프레시 텍스트를 반환한다.")
    void givenRefresh_whenGetText_thenReturn() {
        // given
        Jwt jwt = Jwt.REFRESH;

        // when
        String text = jwt.getText();

        // then
        assertThat(text).isEqualTo("리프레시");
    }

    @Test
    @DisplayName("전체 Jwt를 반환한다.")
    void values() {
        // given & when
        Jwt[] jwts = Jwt.values();

        // then
        assertThat(jwts).containsExactlyInAnyOrder(Jwt.ACCESS, Jwt.REFRESH);
    }

    @Test
    @DisplayName("값 액세스를 Jwt로 변환한다.")
    void givenAccess_whenValueOf_thenReturn() {
        // given
        String s = "ACCESS";

        // when
        Jwt jwt = Jwt.valueOf(s);

        // when
        assertThat(jwt).isEqualTo(Jwt.ACCESS);
    }

    @Test
    @DisplayName("값 리프레시를 Jwt로 변환한다.")
    void givenRefresh_whenValueOf_thenReturn() {
        // given
        String s = "REFRESH";

        // when
        Jwt jwt = Jwt.valueOf(s);

        // when
        assertThat(jwt).isEqualTo(Jwt.REFRESH);
    }
}