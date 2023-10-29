package com.gabojait.gabojaitspring.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class GenderTest {

    @Test
    @DisplayName("남자 텍스트를 반환한다.")
    void givenM_whenGetText_thenReturn() {
        // given
        Gender gender = Gender.M;

        // when
        String text = gender.getText();

        // then
        assertThat(text).isEqualTo("남자");
    }

    @Test
    @DisplayName("여자 텍스트를 반환한다.")
    void givenF_whenGetText_thenReturn() {
        // given
        Gender gender = Gender.F;

        // when
        String text = gender.getText();

        // then
        assertThat(text).isEqualTo("여자");
    }

    @Test
    @DisplayName("선택 안함 텍스트를 반환한다.")
    void givenN_whenGetText_thenReturn() {
        // given
        Gender gender = Gender.N;

        // when
        String text = gender.getText();

        // then
        assertThat(text).isEqualTo("선택 안함");
    }



    @Test
    @DisplayName("전체 성별을 반환한다.")
    void values() {
        // given & when
        Gender[] genders = Gender.values();

        // then
        assertThat(genders).containsExactlyInAnyOrder(Gender.M, Gender.F, Gender.N);
    }

    @Test
    @DisplayName("값 남자를 성별로 변환한다.")
    void givenM_whenValueOf_thenReturn() {
        // given
        String s = "M";

        // when
        Gender gender = Gender.valueOf(s);

        // then
        assertThat(gender).isEqualTo(Gender.M);
    }

    @Test
    @DisplayName("값 여자를 성별로 변환한다.")
    void givenF_whenValueOf_thenReturn() {
        // given
        String s = "F";

        // when
        Gender gender = Gender.valueOf(s);

        // then
        assertThat(gender).isEqualTo(Gender.F);
    }

    @Test
    @DisplayName("값 선택 안함을 성별로 변환한다.")
    void givenN_whenValueOf_thenReturn() {
        // given
        String s = "N";

        // when
        Gender gender = Gender.valueOf(s);

        // then
        assertThat(gender).isEqualTo(Gender.N);
    }

    @Test
    @DisplayName("잘못된 값을 성별로 변환하면 예외가 발생한다.")
    void givenInvalid_whenValueOf_thenThrow() {
        // given
        String s = "INVALID";

        // when & then
        assertThatThrownBy(() -> Gender.valueOf(s))
                .isInstanceOf(IllegalArgumentException.class);
    }
}