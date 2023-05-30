package com.gabojait.gabojaitspring.offer.domain.type;

import com.gabojait.gabojaitspring.exception.CustomException;
import lombok.Getter;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.SERVER_ERROR;

@Getter
public enum OfferedBy {

    TEAM('T'),
    USER('U');

    Character type;

    OfferedBy(Character type) {
        this.type = type;
    }

    public static OfferedBy fromChar(Character type) {
        OfferedBy offeredBy;

        switch (type) {
            case 'T':
                offeredBy = OfferedBy.TEAM;
                break;
            case 'U':
                offeredBy = OfferedBy.USER;
                break;
            default:
                throw new CustomException(SERVER_ERROR);
        }

        return offeredBy;
    }


}
