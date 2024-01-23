package com.gabojait.gabojaitspring.domain.profile;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class LevelTest {

    private static Stream<Arguments> providerGetText() {
        return Stream.of(
                Arguments.of(Level.HIGH, "상"),
                Arguments.of(Level.MID, "중"),
                Arguments.of(Level.LOW, "하")
        );
    }

    @ParameterizedTest(name = "[{index}] {0} 레벨 텍스트는 {1}이다")
    @MethodSource("providerGetText")
    @DisplayName("레벨 텍스트 반환이 정상 작동한다")
    void givenProvider_whenGetText_thenReturn(Level level, String text) {
        // when & then
        assertThat(level.getText()).isEqualTo(text);
    }

    @Test
    @DisplayName("전체 레벨 반환이 정상 작동한다")
    void givenProvider_whenValues_thenReturn() {
        // given & when
        Level[] levels = Level.values();

        // then
        assertThat(levels).containsExactlyInAnyOrder(Level.LOW, Level.MID, Level.HIGH);
    }

    private static Stream<Arguments> providerValueOf() {
        return Stream.of(
                Arguments.of("HIGH", Level.HIGH),
                Arguments.of("MID", Level.MID),
                Arguments.of("LOW", Level.LOW)
        );
    }

    @ParameterizedTest(name = "[{index}] {0} 값을 {1} 레벨로 변환한다")
    @MethodSource("providerValueOf")
    @DisplayName("레벨 값 변환이 정상 작동한다")
    void givenProvider_whenValueOf_thenReturn(String value, Level level) {
        // when & then
        assertThat(Level.valueOf(value)).isEqualTo(level);
    }

    @Test
    @DisplayName("잘못된 값을 레벨로 변환하면 예외가 발생한다")
    void givenInvalid_whenValueOf_thenThrow() {
        // given
        String value = "INVALID";

        // when & then
        assertThatThrownBy(() -> Level.valueOf(value))
                .isInstanceOf(IllegalArgumentException.class);
    }
}