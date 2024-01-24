package com.gabojait.gabojaitspring.domain.team;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TeamMemberStatusTest {

    private static Stream<Arguments> providerGetText() {
        return Stream.of(
                Arguments.of(TeamMemberStatus.PROGRESS, "진행"),
                Arguments.of(TeamMemberStatus.COMPLETE, "완료"),
                Arguments.of(TeamMemberStatus.INCOMPLETE, "미완료"),
                Arguments.of(TeamMemberStatus.FIRED, "추방"),
                Arguments.of(TeamMemberStatus.QUIT, "포기")
        );
    }

    @ParameterizedTest(name = "[{index}] {0} 팀원 상태 텍스트는 {1}다")
    @MethodSource("providerGetText")
    @DisplayName("팀원 상태 텍스트 반환이 정상 작동한다")
    void givenProvider_whenGetText_thenReturn(TeamMemberStatus teamMemberStatus, String text) {
        // when & then
        assertThat(teamMemberStatus.getText()).isEqualTo(text);
    }

    @Test
    @DisplayName("전체 팀원 상태 반환이 정상 작동한다")
    void givenValid_whenValues_thenReturn() {
        // given & when
        TeamMemberStatus[] teamMemberStatuses = TeamMemberStatus.values();

        // then
        assertThat(teamMemberStatuses).containsExactlyInAnyOrder(TeamMemberStatus.PROGRESS, TeamMemberStatus.COMPLETE,
                TeamMemberStatus.INCOMPLETE, TeamMemberStatus.FIRED, TeamMemberStatus.QUIT);
    }

    private static Stream<Arguments> providerValueOf() {
        return Stream.of(
                Arguments.of("PROGRESS", TeamMemberStatus.PROGRESS),
                Arguments.of("COMPLETE", TeamMemberStatus.COMPLETE),
                Arguments.of("INCOMPLETE", TeamMemberStatus.INCOMPLETE),
                Arguments.of("FIRED", TeamMemberStatus.FIRED),
                Arguments.of("QUIT", TeamMemberStatus.QUIT)
        );
    }

    @ParameterizedTest(name = "[{index}] {0} 값을 {1} 팀원 상태로 변환한다")
    @MethodSource("providerValueOf")
    @DisplayName("팀원 상태 값을 변환이 정상 작동한다")
    void givenProvider_whenValueOf_thenReturn(String value, TeamMemberStatus teamMemberStatus) {
        // when & then
        assertThat(TeamMemberStatus.valueOf(value)).isEqualTo(teamMemberStatus);
    }

    @Test
    @DisplayName("잘못된 값을 팀원 상태로 반환하면 예외가 발생한다")
    void givenInvalid_whenValueOf_thenReturn() {
        // given
        String value = "INVALID";

        // when & then
        assertThatThrownBy(() -> TeamMemberStatus.valueOf(value))
                .isInstanceOf(IllegalArgumentException.class);
    }
}