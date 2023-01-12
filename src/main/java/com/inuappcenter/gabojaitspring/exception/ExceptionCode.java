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
    GENDER_INCORRECT_TYPE(BAD_REQUEST, "성별은 M, F 중 하나입니다."),
    PASSWORD_VALIDATION_FAIL(BAD_REQUEST, "비밀번호와 비밀번호 재입력이 동일하지 않습니다."),
    POSITION_INCORRECT_TYPE(BAD_REQUEST, "포지션은 D, B, F, M 중 하나입니다."),
    INCORRECT_DATE(BAD_REQUEST, "시작일을 종료일 이후로 설정해주세요."),
    LEVEL_INCORRECT_TYPE(BAD_REQUEST, "기술 레벨은 1, 2, 3 중 하나입니다."),
    PORTFOLIO_TYPE_INCORRECT_TYPE(BAD_REQUEST, "포트폴리오 타입은 L, F 중 하나입니다."),

    /**
     * 401 UNAUTHORIZED
     */
    TOKEN_AUTHENTICATION_FAIL(UNAUTHORIZED, "토큰 인증에 실패했습니다. 다시 로그인한 후 이용해주세요."),
    TOKEN_AUTHORIZATION_FAIL(UNAUTHORIZED, "토큰 인가를 실패했습니다. 다시 로그인한 후 이용해주세요."),
    LOGIN_FAIL(UNAUTHORIZED, "아이디 또는 비밀번호가 틀렸습니다."),
    INCORRECT_PASSWORD(UNAUTHORIZED, "비밀번호가 틀렸습니다."),

    /**
     * 403 FORBIDDEN
     */

    /**
     * 404 NOT FOUND
     */
    NOT_VERIFIED_EMAIL(NOT_FOUND, "이메일 인증을 먼저 해주세요."),
    NON_EXISTING_USER(NOT_FOUND, "존재하지 않은 회원입니다."),
    NON_EXISTING_EMAIL(NOT_FOUND, "존재하지 않은 이메일입니다."),
    NON_EXISTING_PROFILE(NOT_FOUND, "존재하지 않은 프로필입니다."),
    NON_EXISTING_EDUCATION(NOT_FOUND, "존재하지 않은 학력 정보입니다."),
    NON_EXISTING_SKILL(NOT_FOUND, "존재하지 않은 기술 정보입니다."),
    NON_EXISTING_WORK(NOT_FOUND, "존재하지 않은 경력 정보입니다."),
    NON_EXISTING_PORTFOLIO(NOT_FOUND, "존재하지 않은 포트폴리오 정보입니다."),

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
    MAIL_SENDING_ERROR(INTERNAL_SERVER_ERROR, "이메일 발송 중 에러가 발생했습니다. gabojait.help@gmail.com으로 연락주세요.");

    private final HttpStatus httpStatus;
    private final String message;
}
