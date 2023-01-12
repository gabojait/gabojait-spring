package com.inuappcenter.gabojaitspring.profile.domain;

import lombok.Getter;

public enum PortfolioType {

    LINK('L'),
    FILE('F');

    @Getter
    Character type;

    PortfolioType(Character type) {
        this.type = type;
    }
}
