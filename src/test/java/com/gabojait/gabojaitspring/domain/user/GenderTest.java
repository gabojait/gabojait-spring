package com.gabojait.gabojaitspring.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GenderTest {

    private static Stream<Arguments> providerGetText() {
        return Stream.of(
                Arguments.of(Gender.M, "남자"),
                Arguments.of(Gender.F, "여자"),
                Arguments.of(Gender.N, "선택 안함")
        );
    }

    @ParameterizedTest(name = "[{index}] {0} 성별 텍스트는 {1}다.")
    @MethodSource("providerGetText")
    @DisplayName("성별 텍스트를 반환한다.")
    void givenProvider_whenGetText_thenReturn(Gender gender, String text) {
        // when & then
        assertThat(gender.getText()).isEqualTo(text);
    }



    @Test
    @DisplayName("전체 성별을 반환한다.")
    void values() {
        // given & when
        Gender[] genders = Gender.values();

        // then
        assertThat(genders).containsExactlyInAnyOrder(Gender.M, Gender.F, Gender.N);
    }

    private static Stream<Arguments> providerValueOf() {
        return Stream.of(
                Arguments.of("M", Gender.M),
                Arguments.of("F", Gender.F),
                Arguments.of("N", Gender.N)
        );
    }

    @ParameterizedTest(name = "[{index}] {0} 값을 {1} 성별로 변환한다.")
    @MethodSource("providerValueOf")
    @DisplayName("성별 값을 변환한다.")
    void givenProvider_whenValueOf_thenReturn(String value, Gender gender) {
        // when & then
        assertThat(Gender.valueOf(value)).isEqualTo(gender);
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