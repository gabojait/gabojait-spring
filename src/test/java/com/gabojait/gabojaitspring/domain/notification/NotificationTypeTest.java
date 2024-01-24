package com.gabojait.gabojaitspring.domain.notification;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationTypeTest {

    private static Stream<Arguments> providerGetText() {
        return Stream.of(
                Arguments.of(NotificationType.NONE, "없음"),
                Arguments.of(NotificationType.TEAM_PROFILE_UPDATED, "팀 프로필 수정"),
                Arguments.of(NotificationType.USER_OFFER, "회원 제안"),
                Arguments.of(NotificationType.TEAM_OFFER, "팀 제안"),
                Arguments.of(NotificationType.NEW_TEAM_MEMBER, "새 팀원"),
                Arguments.of(NotificationType.FIRED_TEAM_MEMBER, "팀원 추방"),
                Arguments.of(NotificationType.QUIT_TEAM_MEMBER, "팀원 포기"),
                Arguments.of(NotificationType.TEAM_INCOMPLETE, "프로젝트 미완료"),
                Arguments.of(NotificationType.TEAM_COMPLETE, "프로젝트 완료")
        );
    }

    @ParameterizedTest(name = "[{index}] {0} 알림 타입은 {1}다")
    @MethodSource("providerGetText")
    @DisplayName("알림 타입을 반환한다")
    void givenProvider_whenGetText_thenReturn(NotificationType notificationType, String text) {
        // when & then
        assertThat(notificationType.getText()).isEqualTo(text);
    }

    @Test
    @DisplayName("전체 알림 타입 반환이 정상 작동한다")
    void values() {
        // given & when
        NotificationType[] notificationTypes = NotificationType.values();

        // then
        assertThat(notificationTypes).containsExactlyInAnyOrder(NotificationType.NONE,
                NotificationType.TEAM_PROFILE_UPDATED, NotificationType.USER_OFFER, NotificationType.TEAM_OFFER,
                NotificationType.NEW_TEAM_MEMBER, NotificationType.FIRED_TEAM_MEMBER, NotificationType.QUIT_TEAM_MEMBER,
                NotificationType.TEAM_INCOMPLETE, NotificationType.TEAM_COMPLETE);
    }

    private static Stream<Arguments> providerValueOf() {
        return Stream.of(
                Arguments.of("NONE", NotificationType.NONE),
                Arguments.of("TEAM_PROFILE_UPDATED", NotificationType.TEAM_PROFILE_UPDATED),
                Arguments.of("USER_OFFER", NotificationType.USER_OFFER),
                Arguments.of("TEAM_OFFER", NotificationType.TEAM_OFFER),
                Arguments.of("NEW_TEAM_MEMBER", NotificationType.NEW_TEAM_MEMBER),
                Arguments.of("FIRED_TEAM_MEMBER", NotificationType.FIRED_TEAM_MEMBER),
                Arguments.of("QUIT_TEAM_MEMBER", NotificationType.QUIT_TEAM_MEMBER),
                Arguments.of("TEAM_INCOMPLETE", NotificationType.TEAM_INCOMPLETE),
                Arguments.of("TEAM_COMPLETE", NotificationType.TEAM_COMPLETE)
        );
    }

    @ParameterizedTest(name = "[{index}] {0} 값을 {1} 알림 타입으로 변환한다")
    @MethodSource("providerValueOf")
    @DisplayName("알림 타입 값을 변환한다")
    void givenProvider_whenValueOf_thenReturn(String value, NotificationType notificationType) {
        // when & then
        assertThat(NotificationType.valueOf(value)).isEqualTo(notificationType);
    }
}