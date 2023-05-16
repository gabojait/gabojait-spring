package com.gabojait.gabojaitspring.profile.domain.type;

import com.gabojait.gabojaitspring.exception.CustomException;
import lombok.Getter;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.POSITION_TYPE_INVALID;

@Getter
public enum Position {

    DESIGNER('D'),
    BACKEND('B'),
    FRONTEND('F'),
    MANAGER('M'),
    NONE('N');

    Character type;

    Position(Character type) {
        this.type = type;
    }

    public static Position fromString(String type) {
        try {
            return Position.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(null, POSITION_TYPE_INVALID);
        }
    }

    public static Position fromChar(Character type) {
        Position position;

        switch (type) {
            case 'D':
                position = Position.DESIGNER;
                break;
            case 'B':
                position = Position.BACKEND;
                break;
            case 'F':
                position = Position.FRONTEND;
                break;
            case 'M':
                position = Position.MANAGER;
                break;
            default:
                position = Position.NONE;
                break;
        }

        return position;
    }
}
