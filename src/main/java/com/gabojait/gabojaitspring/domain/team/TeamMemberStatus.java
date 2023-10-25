package com.gabojait.gabojaitspring.domain.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TeamMemberStatus {
    PROGRESS("진행"),
    COMPLETE("완료"),
    INCOMPLETE("미완료"),
    FIRED("추방"),
    QUIT("포기");

    private final String text;
}
