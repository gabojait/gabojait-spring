package com.gabojait.gabojaitspring.domain.team;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TeamMemberStatusTest {

    @Test
    @DisplayName("진행으로 텍스트를 변환한다.")
    void givenProgress_WhenGetText_thenReturn() {
        // given
        TeamMemberStatus teamMemberStatus = TeamMemberStatus.PROGRESS;

        // when
        String text = teamMemberStatus.getText();

        // then
        assertThat(text).isEqualTo("진행");
    }

    @Test
    @DisplayName("완료로 텍스트를 변환한다.")
    void givenComplete_WhenGetText_thenReturn() {
        // given
        TeamMemberStatus teamMemberStatus = TeamMemberStatus.COMPLETE;

        // when
        String text = teamMemberStatus.getText();

        // then
        assertThat(text).isEqualTo("완료");
    }

    @Test
    @DisplayName("미완료로 텍스트를 변환한다.")
    void givenIncomplete_WhenGetText_thenReturn() {
        // given
        TeamMemberStatus teamMemberStatus = TeamMemberStatus.INCOMPLETE;

        // when
        String text = teamMemberStatus.getText();

        // then
        assertThat(text).isEqualTo("미완료");
    }

    @Test
    @DisplayName("추방으로 텍스트를 변환한다.")
    void givenFired_WhenGetText_thenReturn() {
        // given
        TeamMemberStatus teamMemberStatus = TeamMemberStatus.FIRED;

        // when
        String text = teamMemberStatus.getText();

        // then
        assertThat(text).isEqualTo("추방");
    }

    @Test
    @DisplayName("포기로 텍스트를 변환한다.")
    void givenQuit_WhenGetText_thenReturn() {
        // given
        TeamMemberStatus teamMemberStatus = TeamMemberStatus.QUIT;

        // when
        String text = teamMemberStatus.getText();

        // then
        assertThat(text).isEqualTo("포기");
    }

    @Test
    @DisplayName("전체 팀원상태를 반환한다.")
    void values() {
        // given & when
        TeamMemberStatus[] teamMemberStatuses = TeamMemberStatus.values();

        // then
        assertThat(teamMemberStatuses).containsExactlyInAnyOrder(TeamMemberStatus.PROGRESS, TeamMemberStatus.COMPLETE,
                TeamMemberStatus.INCOMPLETE, TeamMemberStatus.FIRED, TeamMemberStatus.QUIT);
    }

    @Test
    @DisplayName("값 진행을 팀원상태로 반환한다.")
    void givenProgress_whenValueOf_thenReturn() {
        // given
        String s = "PROGRESS";

        // when
        TeamMemberStatus teamMemberStatus = TeamMemberStatus.valueOf(s);

        // then
        assertThat(teamMemberStatus).isEqualTo(TeamMemberStatus.PROGRESS);
    }

    @Test
    @DisplayName("값 완료를 팀원상태로 반환한다.")
    void givenComplete_whenValueOf_thenReturn() {
        // given
        String s = "COMPLETE";

        // when
        TeamMemberStatus teamMemberStatus = TeamMemberStatus.valueOf(s);

        // then
        assertThat(teamMemberStatus).isEqualTo(TeamMemberStatus.COMPLETE);
    }

    @Test
    @DisplayName("값 미완료를 팀원상태로 반환한다.")
    void givenIncomplete_whenValueOf_thenReturn() {
        // given
        String s = "INCOMPLETE";

        // when
        TeamMemberStatus teamMemberStatus = TeamMemberStatus.valueOf(s);

        // then
        assertThat(teamMemberStatus).isEqualTo(TeamMemberStatus.INCOMPLETE);
    }

    @Test
    @DisplayName("값 추방을 팀원상태로 반환한다.")
    void givenFired_whenValueOf_thenReturn() {
        // given
        String s = "FIRED";

        // when
        TeamMemberStatus teamMemberStatus = TeamMemberStatus.valueOf(s);

        // then
        assertThat(teamMemberStatus).isEqualTo(TeamMemberStatus.FIRED);
    }

    @Test
    @DisplayName("값 포기를 팀원상태로 반환한다.")
    void givenQuit_whenValueOf_thenReturn() {
        // given
        String s = "QUIT";

        // when
        TeamMemberStatus teamMemberStatus = TeamMemberStatus.valueOf(s);

        // then
        assertThat(teamMemberStatus).isEqualTo(TeamMemberStatus.QUIT);
    }

    @Test
    @DisplayName("잘못된 값을 팀원상태로 반환하면 예외가 발생한다.")
    void givenInvalid_whenValueOf_thenReturn() {
        // given
        String s = "INVALID";

        // when & then
        assertThatThrownBy(() -> TeamMemberStatus.valueOf(s))
                .isInstanceOf(IllegalArgumentException.class);
    }
}