package com.gabojait.gabojaitspring.domain.offer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OfferedBy {
    USER("회원"),
    LEADER("리더");

    private final String text;
}
