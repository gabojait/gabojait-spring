package com.inuappcenter.gabojaitspring.profile.domain.type;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import lombok.Getter;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

public enum Position {

    DESIGNER('D'),
    BACKEND('B'),
    FRONTEND('F'),
    PM('P');

    @Getter
    Character type;

    Position(Character type) {
        this.type = type;
    }

    public static Position fromString(String type) {

        try {
            return Position.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(POSITION_FORMAT_INVALID);
        }
    }

    public static Position toEnum(Character type) {

        switch (type) {
            case 'D':
                return Position.DESIGNER;
            case 'B':
                return Position.BACKEND;
            case 'F':
                return Position.FRONTEND;
            case 'P':
                return Position.PM;
            default:
                throw new CustomException(SERVER_ERROR);
        }
    }
}
