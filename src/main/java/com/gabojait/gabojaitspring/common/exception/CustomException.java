package com.gabojait.gabojaitspring.common.exception;

import com.gabojait.gabojaitspring.common.code.ErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Throwable throwable;

    public CustomException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.throwable = null;
    }

    public CustomException(ErrorCode errorCode, Throwable throwable) {
        this.errorCode = errorCode;
        this.throwable = throwable;
    }
}
