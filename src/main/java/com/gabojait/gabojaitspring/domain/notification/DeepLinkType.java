package com.gabojait.gabojaitspring.domain.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeepLinkType {

    HOME_PAGE("gabojait://MainBottomTabNavigation/Home/GroupList", "홈페이지"),
    TEAM_PAGE("gabojait://MainBottomTabNavigation/Team", "팀 페이지"),
    REVIEW_PAGE("gabojait://MainNavigation/ApplyStatus", "리뷰 작성 페이지"),
    USER_OFFER_RECEIVE_PAGE("gabojait://MainNavigation/OfferFromTeam", "회원 제안 페이지"),
    TEAM_OFFER_RECEIVE_PAGE("gabojait://MainNavigation/TeamHistory", "팀 제안 페이지")
    ;

    private final String url;
    private final String description;
}
