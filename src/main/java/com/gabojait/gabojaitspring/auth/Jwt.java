package com.gabojait.gabojaitspring.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Jwt {

    ACCESS("액세스"),
    REFRESH("리프레시");

    private final String text;
}
