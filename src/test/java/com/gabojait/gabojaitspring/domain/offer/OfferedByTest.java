package com.gabojait.gabojaitspring.domain.offer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OfferedByTest {

    private static Stream<Arguments> providerGetText() {
        return Stream.of(
                Arguments.of(OfferedBy.USER, "회원"),
                Arguments.of(OfferedBy.LEADER, "리더")
        );
    }

    @ParameterizedTest(name = "[{index}] {0} 제안자 텍스트는 {1}다")
    @MethodSource("providerGetText")
    @DisplayName("제안자 텍스트를 반환한다.")
    void givenProvider_whenGetText_thenReturn(OfferedBy offeredBy, String text) {
        // when & then
        assertThat(offeredBy.getText()).isEqualTo(text);
    }

    @Test
    @DisplayName("전체 제안자를 반환이 정상 작동한다")
    void values() {
        // given & when
        OfferedBy[] offeredBys = OfferedBy.values();

        // then
        assertThat(offeredBys).containsExactlyInAnyOrder(OfferedBy.USER, OfferedBy.LEADER);
    }

    private static Stream<Arguments> providerValueOf() {
        return Stream.of(
                Arguments.of("USER", OfferedBy.USER),
                Arguments.of("LEADER", OfferedBy.LEADER)
        );
    }

    @ParameterizedTest(name = "[{index}] {0} 값을 {1} 제안자로 변환한다")
    @MethodSource("providerValueOf")
    @DisplayName("제안자 값을 반환한다")
    void givenProvider_whenValueOf_thenReturn(String value, OfferedBy offeredBy) {
        // when & then
        assertThat(OfferedBy.valueOf(value)).isEqualTo(offeredBy);
    }

    @Test
    @DisplayName("잘못된 값을 제안자로 변환하면 예외가 발생한다")
    void givenInvalid_whenValueOf_thenThrow() {
        // given
        String value = "INVALID";

        // when & then
        assertThatThrownBy(() -> OfferedBy.valueOf(value))
                .isInstanceOf(IllegalArgumentException.class);
    }
}