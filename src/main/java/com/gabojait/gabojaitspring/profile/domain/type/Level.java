package com.gabojait.gabojaitspring.profile.domain.type;

import com.gabojait.gabojaitspring.exception.CustomException;
import lombok.Getter;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.LEVEL_TYPE_INVALID;
import static com.gabojait.gabojaitspring.common.code.ErrorCode.SERVER_ERROR;

@Getter
public enum Level {

    LOW('L'),
    MID('M'),
    HIGH('H');

    Character type;

    Level(Character type) {
        this.type = type;
    }

    public static Level fromString(String type) {
        try {
            return Level.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(e, LEVEL_TYPE_INVALID);
        }
    }

    public static Level fromChar(Character type) {
        Level level;

        switch (type) {
            case 'L':
                level = Level.LOW;
                break;
            case 'M':
                level = Level.MID;
                break;
            case 'H':
                level = Level.HIGH;
                break;
            default:
                throw new CustomException(SERVER_ERROR);
        }

        return level;
    }
}
