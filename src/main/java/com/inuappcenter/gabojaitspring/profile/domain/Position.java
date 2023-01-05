package com.inuappcenter.gabojaitspring.profile.domain;

import lombok.Getter;

public enum Position {

    DESIGNER('D'),
    BACKEND('B'),
    FRONTEND('F'),
    MANAGER('M');

    @Getter
    Character type;

    Position(Character type) {
        this.type = type;
    }
}
