package com.inuappcenter.gabojaitspring.review.domain;

import lombok.Getter;

public enum Rating {

    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5);

    @Getter
    Byte type;

    Rating(Integer type) {
        this.type = type.byteValue();
    }
}
