package com.inuappcenter.gabojaitspring.profile.domain.type;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import lombok.Getter;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

public enum PortfolioType {

    LINK('L'),
    FILE('F');

    @Getter
    Character type;

    PortfolioType(Character type) {
        this.type = type;
    }

    public static PortfolioType fromString(String type) {

        try {
            return PortfolioType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(PORTFOLIOTYPE_FORMAT_INVALID);
        }
    }

    public static PortfolioType toEnum(Character type) {

        switch (type) {
            case 'L':
                return PortfolioType.LINK;
            case 'F':
                return PortfolioType.FILE;
            default:
                throw new CustomException(SERVER_ERROR);
        }
    }
}