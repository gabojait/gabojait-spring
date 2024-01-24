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

class MediaTest {

    private static Stream<Arguments> providerGetText() {
        return Stream.of(
                Arguments.of(Media.LINK, "링크"),
                Arguments.of(Media.FILE, "파일")
        );
    }

    @ParameterizedTest(name = "[{index}] {0} 미디어 텍스트는 {1}이다")
    @MethodSource("providerGetText")
    @DisplayName("미디어 텍스트 반환이 정상 작동한다")
    void givenProvider_whenGetText_thenReturn(Media media, String text) {
        // when & then
        assertThat(media.getText()).isEqualTo(text);
    }

    @Test
    @DisplayName("전체 미디어 반환이 정상 작동한다")
    void givenValid_whenValues_thenReturn() {
        // given & when
        Media[] medias = Media.values();

        // then
        assertThat(medias).containsExactlyInAnyOrder(Media.LINK, Media.FILE);
    }

    private static Stream<Arguments> providerValueOf() {
        return Stream.of(
                Arguments.of("LINK", Media.LINK),
                Arguments.of("FILE", Media.FILE)
        );
    }

    @ParameterizedTest(name = "[{index}] {0} 값을 {1} 미디어로 변환한다")
    @MethodSource("providerValueOf")
    @DisplayName("미디어 값 변환이 정상 작동한다")
    void givenProvider_whenValueOf_thenReturn(String value, Media media) {
        // when & then
        assertThat(Media.valueOf(value)).isEqualTo(media);
    }

    @Test
    @DisplayName("잘못된 값을 미디어로 변환하면 예외가 발생한다")
    void givenInvalid_whenValueOf_thenThrow() {
        // given
        String value = "INVALID";

        // when & then
        assertThatThrownBy(() -> Media.valueOf(value))
                .isInstanceOf(IllegalArgumentException.class);
    }
}