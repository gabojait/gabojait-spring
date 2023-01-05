package com.inuappcenter.gabojaitspring.profile.domain;

import lombok.Getter;

public enum Level {
    LOW(1),
    MID(2),
    HIGH(3);

    @Getter
    Byte type;

    Level(Integer type) {
        this.type = type.byteValue();
    }
}
