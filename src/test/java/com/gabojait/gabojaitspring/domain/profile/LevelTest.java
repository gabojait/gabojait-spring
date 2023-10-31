package com.gabojait.gabojaitspring.domain.profile;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LevelTest {

    @Test
    @DisplayName("하 텍스트를 반환한다.")
    void givenLow_whenGetText_thenReturn() {
        // given
        Level level = Level.LOW;

        // when
        String text = level.getText();

        // then
        assertThat(text).isEqualTo("하");
    }

    @Test
    @DisplayName("중 텍스트를 반환한다.")
    void givenMid_whenGetText_thenReturn() {
        // given
        Level level = Level.MID;

        // when
        String text = level.getText();

        // then
        assertThat(text).isEqualTo("중");
    }

    @Test
    @DisplayName("상 텍스트를 반환한다.")
    void givenHigh_whenGetText_thenReturn() {
        // given
        Level level = Level.HIGH;

        // when
        String text = level.getText();

        // then
        assertThat(text).isEqualTo("상");
    }

    @Test
    @DisplayName("전체 레벨을 반환한다.")
    void values() {
        // given & when
        Level[] levels = Level.values();

        // then
        assertThat(levels).containsExactlyInAnyOrder(Level.LOW, Level.MID, Level.HIGH);
    }

    @Test
    @DisplayName("값 하를 레벨로 변환한다.")
    void givenLow_whenValueOf_thenReturn() {
        // given
        String s = "LOW";

        // when
        Level level = Level.valueOf(s);

        // then
        assertThat(level).isEqualTo(Level.LOW);
    }

    @Test
    @DisplayName("값 중을 레벨로 변환한다.")
    void givenMid_whenValueOf_thenReturn() {
        // given
        String s = "MID";

        // when
        Level level = Level.valueOf(s);

        // then
        assertThat(level).isEqualTo(Level.MID);
    }

    @Test
    @DisplayName("값 상을 레벨로 변환한다.")
    void givenHigh_whenValueOf_thenReturn() {
        // given
        String s = "HIGH";

        // when
        Level level = Level.valueOf(s);

        // then
        assertThat(level).isEqualTo(Level.HIGH);
    }
}