package com.gabojait.gabojaitspring.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("회원"),
    ADMIN("관리자"),
    MASTER("마스터");

    private final String text;
}
