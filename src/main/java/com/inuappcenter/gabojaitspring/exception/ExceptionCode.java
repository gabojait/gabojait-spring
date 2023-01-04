package com.inuappcenter.gabojaitspring.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ExceptionCode {

    /**
     * 400 BAD_REQUEST
     */
    INCORRECT_VERIFICATION_CODE(BAD_REQUEST, "인증번호가 틀렸습니다."),

    /**
     * 401 UNAUTHORIZED
     */
    TOKEN_AUTHENTICATION_FAIL(UNAUTHORIZED, "로그인 후 이용해주세요."),

    /**
     * 403 FORBIDDEN
     */

    /**
     * 404 NOT FOUND
     */
    NOT_VERIFIED_EMAIL(NOT_FOUND, "이메일 인증을 먼저 해주세요."),

    /**
     * 409 CONFLICT
     */
    EXISTING_EMAIL(CONFLICT, "이미 사용중인 이메일입니다."),
    EXISTING_USERNAME(CONFLICT, "이미 사용중인 아이디입니다."),
    EXISTING_NICKNAME(CONFLICT, "이미 사용중인 닉네임입니다."),

    /**
     * 500 INTERNAL_SERVER_ERROR
     */
    SERVER_ERROR(INTERNAL_SERVER_ERROR,"서버 에러가 발생했습니다. gabojait.help@gmail.com으로 연락주세요."),
    MAIL_SENDING_ERROR(INTERNAL_SERVER_ERROR, "이메일 발송중 에러가 발생했습니다. gabojait.help@gmail.com으로 연락주세요.");

    private final HttpStatus httpStatus;
    private final String message;
}
