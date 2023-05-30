package com.gabojait.gabojaitspring.profile.domain.type;

import com.gabojait.gabojaitspring.exception.CustomException;
import lombok.Getter;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.MEDIA_TYPE_INVALID;
import static com.gabojait.gabojaitspring.common.code.ErrorCode.SERVER_ERROR;

@Getter
public enum Media {

    LINK('L'),
    FILE('F');

    @Getter
    Character type;

    Media(Character type) {
        this.type = type;
    }

    public static Media fromString(String type) {
        try {
            return Media.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(e, MEDIA_TYPE_INVALID);
        }
    }

    public static Media fromChar(Character type) {
        Media media;

        switch (type) {
            case 'L':
                media = Media.LINK;
                break;
            case 'F':
                media = Media.FILE;
                break;
            default:
                throw new CustomException(SERVER_ERROR);
        }

        return media;
    }
}
