package com.gabojait.gabojaitspring.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PositionTest {

    @Test
    @DisplayName("디자이너 텍스트를 반환한다.")
    void givenDesigner_whenGetText_thenReturn() {
        // given
        Position position = Position.DESIGNER;

        // when
        String text = position.getText();

        // then
        assertThat(text).isEqualTo("디자이너");
    }

    @Test
    @DisplayName("백엔드 개발자 텍스트를 반환한다.")
    void givenBackend_whenGetText_thenReturn() {
        // given
        Position position = Position.BACKEND;

        // when
        String text = position.getText();

        // then
        assertThat(text).isEqualTo("백엔드 개발자");
    }

    @Test
    @DisplayName("프런트엔드 개발자 텍스트를 반환한다.")
    void givenFrontend_whenGetText_thenReturn() {
        // given
        Position position = Position.FRONTEND;

        // when
        String text = position.getText();

        // then
        assertThat(text).isEqualTo("프런트엔드 개발자");
    }

    @Test
    @DisplayName("프로젝트 매니저 텍스트를 반환한다.")
    void givenManager_whenGetText_thenReturn() {
        // given
        Position position = Position.MANAGER;

        // when
        String text = position.getText();

        // then
        assertThat(text).isEqualTo("프로젝트 매니저");
    }

    @Test
    @DisplayName("선택 안함 텍스트를 반환한다.")
    void givenNone_whenGetText_thenReturn() {
        // given
        Position position = Position.NONE;

        // when
        String text = position.getText();

        // then
        assertThat(text).isEqualTo("선택 안함");
    }

    @Test
    @DisplayName("전체 포지션을 반환한다.")
    void values() {
        // given & when
        Position[] positions = Position.values();

        // then
        assertThat(positions).containsExactlyInAnyOrder(Position.DESIGNER, Position.BACKEND, Position.FRONTEND,
                Position.MANAGER, Position.NONE);
    }

    @Test
    @DisplayName("값 디자이너를 포지션으로 변환한다.")
    void givenDesigner_whenValueOf_thenReturn() {
        // given
        String s = "DESIGNER";

        // when
        Position position = Position.valueOf(s);

        // then
        assertThat(position).isEqualTo(Position.DESIGNER);
    }

    @Test
    @DisplayName("값 백엔드 개발자를 포지션으로 변환한다.")
    void givenBackend_whenValueOf_thenReturn() {
        // given
        String s = "BACKEND";

        // when
        Position position = Position.valueOf(s);

        // then
        assertThat(position).isEqualTo(Position.BACKEND);
    }

    @Test
    @DisplayName("값 프런트엔드 개발자를 포지션으로 변환한다.")
    void givenFrontend_whenValueOf_thenReturn() {
        // given
        String s = "FRONTEND";

        // when
        Position position = Position.valueOf(s);

        // then
        assertThat(position).isEqualTo(Position.FRONTEND);
    }

    @Test
    @DisplayName("값 프로젝트 매니저를 포지션으로 변환한다.")
    void givenManager_whenValueOf_thenReturn() {
        // given
        String s = "MANAGER";

        // when
        Position position = Position.valueOf(s);

        // then
        assertThat(position).isEqualTo(Position.MANAGER);
    }

    @Test
    @DisplayName("값 선택 안함을 포지션으로 변환한다.")
    void givenNone_whenValueOf_thenReturn() {
        // given
        String s = "NONE";

        // when
        Position position = Position.valueOf(s);

        // then
        assertThat(position).isEqualTo(Position.NONE);
    }

    @Test
    @DisplayName("잘못된 값을 포지션으로 반환하면 예외가 발생한다.")
    void givenInvalid_whenValueOf_thenThrow() {
        // given
        String s = "INVALID";

        // when & then
        assertThatThrownBy(() -> Position.valueOf(s))
                .isInstanceOf(IllegalArgumentException.class);
    }
}