package com.inuappcenter.gabojaitspring.user.domain.type;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import lombok.Getter;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

public enum Gender {

    MALE('M'),
    FEMALE('F');

    @Getter
    Character type;

    Gender(Character type) {
        this.type = type;
    }

    public static Gender fromString(String type) {

        try {
            return Gender.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(GENDER_FORMAT_INVALID);
        }
    }

    public static Gender toEnum(Character type) {

        switch (type) {
            case 'M':
                return Gender.MALE;
            case 'F':
                return Gender.FEMALE;
            default:
                throw new CustomException(SERVER_ERROR);
        }
    }
}
