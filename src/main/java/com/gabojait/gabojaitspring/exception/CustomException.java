package com.gabojait.gabojaitspring.exception;

import com.amazonaws.AmazonServiceException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.gabojait.gabojaitspring.common.code.ErrorCode;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.Getter;

import java.io.IOException;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode exceptionCode;
    private final Throwable throwable;

    public CustomException(ErrorCode exceptionCode) {
        this.exceptionCode = exceptionCode;
        this.throwable = null;
    }

    public CustomException(Exception exception, ErrorCode exceptionCode) {
        this.exceptionCode = exceptionCode;
        this.throwable = exception.getCause();
    }

    public CustomException(RuntimeException exception, ErrorCode exceptionCode) {
        this.exceptionCode = exceptionCode;
        this.throwable = exception.getCause();
    }

    public CustomException(IOException exception, ErrorCode exceptionCode) {
        this.exceptionCode = exceptionCode;
        this.throwable = exception.getCause();
    }

    public CustomException(IllegalArgumentException exception, ErrorCode exceptionCode) {
        this.exceptionCode = exceptionCode;
        this.throwable = exception.getCause();
    }

    public CustomException(FirebaseMessagingException exception, ErrorCode exceptionCode) {
        this.exceptionCode = exceptionCode;
        this.throwable = exception.getCause();
    }

    public CustomException(JWTVerificationException exception, ErrorCode exceptionCode) {
        this.exceptionCode = exceptionCode;
        this.throwable = exception.getCause();
    }

    public CustomException(AmazonServiceException exception, ErrorCode exceptionCode) {
        this.exceptionCode = exceptionCode;
        this.throwable = exception.getCause();
    }
}
