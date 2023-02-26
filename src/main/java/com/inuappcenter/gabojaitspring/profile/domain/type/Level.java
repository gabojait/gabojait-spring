package com.inuappcenter.gabojaitspring.profile.domain.type;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import lombok.Getter;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

public enum Level {
    LOW('L'),
    MID('M'),
    HIGH('H');

    @Getter
    Character type;

    Level(Character type) {
        this.type = type;
    }

    public static Level fromString(String type) {

        try {
            return Level.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(LEVEL_FORMAT_INVALID);
        }
    }

    public static Level toEnum(Character type) {

        switch (type) {
            case 'L':
                return Level.LOW;
            case 'M':
                return Level.MID;
            case 'H':
                return Level.HIGH;
            default:
                throw new CustomException(SERVER_ERROR);
        }
    }
}
