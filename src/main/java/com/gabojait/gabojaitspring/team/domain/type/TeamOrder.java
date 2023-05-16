package com.gabojait.gabojaitspring.team.domain.type;

import com.gabojait.gabojaitspring.exception.CustomException;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.TEAM_ORDER_TYPE_INVALID;

public enum TeamOrder {

    CREATED,
    ACTIVE,
    POPULARITY;

    public static TeamOrder fromString(String type) {
        try {
            return TeamOrder.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(e, TEAM_ORDER_TYPE_INVALID);
        }
    }
}
