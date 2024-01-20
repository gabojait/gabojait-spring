package com.gabojait.gabojaitspring.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PositionTest {

    private static Stream<Arguments> providerGetText() {
        return Stream.of(
                Arguments.of(Position.DESIGNER, "디자이너"),
                Arguments.of(Position.BACKEND, "백엔드 개발자"),
                Arguments.of(Position.FRONTEND, "프런트엔드 개발자"),
                Arguments.of(Position.MANAGER, "프로젝트 매니저"),
                Arguments.of(Position.NONE, "선택 안함")
        );
    }

    @ParameterizedTest(name = "[{index}] {0} 포지션 텍스트는 {1}다")
    @MethodSource("providerGetText")
    @DisplayName("포지션 텍스트 반환이 정상 작동한다")
    void givenProvider_whenGetText_thenReturn(Position position, String text) {
        // when & then
        assertThat(position.getText()).isEqualTo(text);
    }

    @Test
    @DisplayName("전체 포지션 반환이 정상 작동한다")
    void givenValid_whenValues_thenReturn() {
        // given & when
        Position[] positions = Position.values();

        // then
        assertThat(positions).containsExactlyInAnyOrder(Position.DESIGNER, Position.BACKEND, Position.FRONTEND,
                Position.MANAGER, Position.NONE);
    }

    private static Stream<Arguments> providerValueOf() {
        return Stream.of(
                Arguments.of("DESIGNER", Position.DESIGNER),
                Arguments.of("BACKEND", Position.BACKEND),
                Arguments.of("FRONTEND", Position.FRONTEND),
                Arguments.of("MANAGER", Position.MANAGER),
                Arguments.of("NONE", Position.NONE)
        );
    }

    @ParameterizedTest(name = "[{index}] {0} 값을 {1} 포지션으로 변환한다")
    @MethodSource("providerValueOf")
    @DisplayName("포지션 값을 변환이 정상 작동한다")
    void givenProvider_whenValueOf_thenReturn(String value, Position position) {
        // when & then
        assertThat(Position.valueOf(value)).isEqualTo(position);
    }

    @Test
    @DisplayName("잘못된 값을 포지션으로 변환하면 예외가 발생한다")
    void givenInvalid_whenValueOf_thenThrow() {
        // given
        String s = "INVALID";

        // when & then
        assertThatThrownBy(() -> Position.valueOf(s))
                .isInstanceOf(IllegalArgumentException.class);
    }
}