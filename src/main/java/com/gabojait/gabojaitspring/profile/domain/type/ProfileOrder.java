package com.gabojait.gabojaitspring.profile.domain.type;

import com.gabojait.gabojaitspring.exception.CustomException;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.PROFILE_ORDER_TYPE_INVALID;

public enum ProfileOrder {

    ACTIVE,
    POPULARITY,
    RATING;

    public static ProfileOrder fromString(String type) {
        try {
            return ProfileOrder.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(e, PROFILE_ORDER_TYPE_INVALID);
        }
    }
}
