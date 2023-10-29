package com.gabojait.gabojaitspring.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Position {

    DESIGNER("디자이너"),
    BACKEND("백엔드 개발자"),
    FRONTEND("프런트엔드 개발자"),
    MANAGER("프로젝트 매니저"),
    NONE("선택 안함");

    private final String text;
}
