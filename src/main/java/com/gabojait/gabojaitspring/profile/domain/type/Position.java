package com.gabojait.gabojaitspring.profile.domain.type;

import com.gabojait.gabojaitspring.exception.CustomException;
import lombok.Getter;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.POSITION_TYPE_INVALID;
import static com.gabojait.gabojaitspring.common.code.ErrorCode.SERVER_ERROR;

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
            throw new CustomException(e, POSITION_TYPE_INVALID);
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

    public static String toKorean(Character type) {
        String position;

        switch (type) {
            case 'D':
                position = "디자이너";
                break;
            case 'B':
                position = "백엔드 개발자";
                break;
            case 'F':
                position = "프론트엔드 개발자";
                break;
            case 'M':
                position = "매니저";
                break;
            default:
                throw new CustomException(SERVER_ERROR);
        }

        return position;
    }
}
