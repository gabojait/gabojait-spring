package com.gabojait.gabojaitspring.domain.notification;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class DeepLinkTypeTest {

    private static Stream<Arguments> providerGetUrlAndDescription() {
        return Stream.of(
                Arguments.of(DeepLinkType.HOME_PAGE, "gabojait://MainBottomTabNavigation/Home/GroupList", "홈페이지"),
                Arguments.of(DeepLinkType.TEAM_PAGE, "gabojait://MainBottomTabNavigation/Team", "팀 페이지"),
                Arguments.of(DeepLinkType.REVIEW_PAGE, "gabojait://MainNavigation/TeamHistory", "리뷰 작성 페이지"),
                Arguments.of(DeepLinkType.USER_OFFER_RECEIVE_PAGE, "gabojait://MainNavigation/OfferFromTeam", "회원 제안 페이지"),
                Arguments.of(DeepLinkType.TEAM_OFFER_RECEIVE_PAGE, "gabojait://MainNavigation/TeamHistory", "팀 제안 페이지")
        );
    }

    @ParameterizedTest(name = "[{index}] {0} 딥링크 URL은 {1}이고 설명은 {2}다")
    @MethodSource("providerGetUrlAndDescription")
    @DisplayName("딥링크 타입을 반환한다")
    void givenProvider_whenGetUrlAndDescription_thenReturn(DeepLinkType deepLinkType, String url, String description) {
        // when & then
        assertThat(deepLinkType)
                .extracting("url", "description")
                .isEqualTo(List.of(url, description));
    }

    @Test
    @DisplayName("전체 딥링크 타입 반환이 정상 작동한다")
    void givenValid_whenValues_thenReturn() {
        // given & when
        DeepLinkType[] deepLinkTypes = DeepLinkType.values();

        // then
        assertThat(deepLinkTypes)
                .containsExactly(DeepLinkType.HOME_PAGE, DeepLinkType.TEAM_PAGE, DeepLinkType.REVIEW_PAGE,
                        DeepLinkType.USER_OFFER_RECEIVE_PAGE, DeepLinkType.TEAM_OFFER_RECEIVE_PAGE);
    }

    private static Stream<Arguments> providerValueOf() {
        return Stream.of(
                Arguments.of("HOME_PAGE", DeepLinkType.HOME_PAGE),
                Arguments.of("TEAM_PAGE", DeepLinkType.TEAM_PAGE),
                Arguments.of("REVIEW_PAGE", DeepLinkType.REVIEW_PAGE),
                Arguments.of("USER_OFFER_RECEIVE_PAGE", DeepLinkType.USER_OFFER_RECEIVE_PAGE),
                Arguments.of("TEAM_OFFER_RECEIVE_PAGE", DeepLinkType.TEAM_OFFER_RECEIVE_PAGE)
        );
    }

    @ParameterizedTest(name = "[{index}] {0} 값을 {1} 딥링크 타입으로 변환한다")
    @MethodSource("providerValueOf")
    @DisplayName("딥링크 타입 값을 변환한다")
    void givenProvider_whenValueOf_thenReturn(String value, DeepLinkType deepLinkType) {
        // when & then
        assertThat(DeepLinkType.valueOf(value)).isEqualTo(deepLinkType);
    }
}