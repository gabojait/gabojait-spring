package com.gabojait.gabojaitspring.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {
    M("남자"),
    F("여자"),
    N("선택안함");

    private final String text;
}
