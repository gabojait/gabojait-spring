package com.gabojait.gabojaitspring.common.constant.deepLink;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeepLink {

    HOME_PAGE("gabojait://home", "홈페이지"),
    TEAM_PAGE("gabojait://team", "팀 페이지"),
    REVIEW_PAGE("gabojait://my/team-history/review", "리뷰 작성 페이지"),
    USER_OFFER_RECEIVE_PAGE("gabojait://my/offer/user/received", "회원 제안 페이지"),
    TEAM_OFFER_RECEIVE_PAGE("gabojait://my/offer/team/received", "팀 제안 페이지")
    ;

    private final String url;
    private final String description;
}
