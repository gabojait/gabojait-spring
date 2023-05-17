package com.gabojait.gabojaitspring.user.domain.type;

import com.gabojait.gabojaitspring.exception.CustomException;
import lombok.Getter;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.GENDER_TYPE_INVALID;
import static com.gabojait.gabojaitspring.common.code.ErrorCode.SERVER_ERROR;

@Getter
public enum Gender {

    MALE('M'),
    FEMALE('F'),
    NONE('N');

    Character type;

    Gender(Character type) {
        this.type = type;
    }

    public static Gender fromString(String type) {
        try {
            return Gender.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(e, GENDER_TYPE_INVALID);
        }
    }

    public static Gender fromChar(Character type) {
        Gender gender;

        switch (type) {
            case 'M':
                gender = Gender.MALE;
                break;
            case 'F':
                gender = Gender.FEMALE;
                break;
            default:
                gender = Gender.NONE;
                break;
        }

        return gender;
    }
}
