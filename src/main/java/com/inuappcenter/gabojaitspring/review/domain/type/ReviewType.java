package com.inuappcenter.gabojaitspring.review.domain.type;

import com.inuappcenter.gabojaitspring.exception.CustomException;
import lombok.Getter;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.REVIEWTYPE_FORMAT_INVALID;
import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.SERVER_ERROR;

public enum ReviewType {
    ANSWER('A'),
    RATING('R');

    @Getter
    Character type;

    ReviewType(Character type) {
        this.type = type;
    }

    public static ReviewType fromString(String type) {

        try {
            return ReviewType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(REVIEWTYPE_FORMAT_INVALID);
        }
    }

    public static ReviewType toEnum(Character type) {

        switch (type) {
            case 'A':
                return ReviewType.ANSWER;
            case 'R':
                return ReviewType.RATING;
            default:
                throw new CustomException(SERVER_ERROR);
        }
    }
}
