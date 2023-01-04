package com.inuappcenter.gabojaitspring.user.domain;

import lombok.Getter;

public enum Gender {
    MALE('M'),
    FEMALE('F');

    @Getter
    Character type;

    Gender(Character type) {
        this.type = type;
    }
}
