package com.gabojait.gabojaitspring.domain.profile;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Level {
    LOW("하"),
    MID("중"),
    HIGH("상");

    private final String text;
}
