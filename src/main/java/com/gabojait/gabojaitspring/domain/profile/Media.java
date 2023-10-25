package com.gabojait.gabojaitspring.domain.profile;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Media {
    LINK("링크"),
    FILE("파일");

    private final String text;
}
