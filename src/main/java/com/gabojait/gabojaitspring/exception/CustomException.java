package com.gabojait.gabojaitspring.exception;

import com.gabojait.gabojaitspring.common.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException {

    private final Exception exception;
    private final ErrorCode exceptionCode;
}
