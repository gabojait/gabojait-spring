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
    // @NotBlank, @NotNull
    FIELD_REQUIRED(BAD_REQUEST, "모든 필수 정보를 입력해주세요."),

    // @Size
    USERNAME_LENGTH_INVALID(BAD_REQUEST, "아이디는 5~15자만 가능합니다."),
    PASSWORD_LENGTH_INVALID(BAD_REQUEST, "비밀번호는 8~30자만 가능합니다."),
    LEGALNAME_LENGTH_INVALID(BAD_REQUEST, "실명은 2~5자만 가능합니다."),
    NICKNAME_LENGTH_INVALID(BAD_REQUEST, "닉네임은 2~8자만 가능합니다."),
    INISTITUTIONNAME_LENGTH_INVALID(BAD_REQUEST, "학교명은 3~20자만 가능합니다."),
    CORPORTATIONNAME_LENGTH_INVALID(BAD_REQUEST, "기관명은 1~20자만 가능합니다."),
    PORTFOLIONAME_LENGTH_INVALID(BAD_REQUEST, "포트폴리오명은 1~10자만 가능합니다."),
    URL_LENGTH_INVALID(BAD_REQUEST, "URL은 1~1000자만 가능합니다"),
    SKILLNAME_LENGTH_INVALID(BAD_REQUEST, "기술명은 1~20자만 가능합니다."),


    // @Email @Pattern
    EMAIL_FORMAT_INVALID(BAD_REQUEST, "올바른 이메일 형식이 아닙니다."),
    USERNAME_FORMAT_INVALID(BAD_REQUEST, "아이디는 영문과 숫자의 형식만 가능합니다."),
    NICKNAME_FORMAT_INVALID(BAD_REQUEST, "닉네임은 한글 형식만 가능합니다."),
    PASSWORD_FORMAT_INVALID(BAD_REQUEST, "비밀번호는 영문, 숫자, 특수문자(#$@!%&*?)의 조합의 형식만 가능합니다."),
    LEGALNAME_FORMAT_INVALID(BAD_REQUEST, "실명은 한글 형식만 가능합니다."),

    // Custom
    EMAIL_VERIFICATION_INVALID(BAD_REQUEST, "이메일 인증을 먼저 해주세요."),
    VERIFICATIONCODE_INVALID(BAD_REQUEST, "인증번호가 틀렸습니다."),
    GENDER_FORMAT_INVALID(BAD_REQUEST, "성별은 male, female 중 하나입니다."),
    PASSWORD_MATCH_INVALID(BAD_REQUEST, "비밀번호와 비밀번호 재입력이 동일하지 않습니다."),
    LEVEL_FORMAT_INVALID(BAD_REQUEST, "레벨은 low, mid, high 중 하나입니다."),
    PORTFOLIOTYPE_FORMAT_INVALID(BAD_REQUEST, "포트폴리오 타입은 file, link 중 하나입니다."),
    POSITION_FORMAT_INVALID(BAD_REQUEST, "포지션은 designer, backend, frontend, manager 중 하나입니다."),

    /**
     * 401 UNAUTHORIZED
     */
    TOKEN_AUTHENTICATION_FAIL(UNAUTHORIZED, "토큰 인증에 실패했습니다. 다시 로그인한 후 이용해주세요."),
    TOKEN_REQUIRED_FAIL(UNAUTHORIZED, "헤더에 토큰이 없습니다."),
    LOGIN_FAIL(UNAUTHORIZED, "로그인에 실패했습니다."),
    USERNAME_EMAIL_NO_MATCH(UNAUTHORIZED, "아이디와 이메일 정보가 일치하지 않습니다."),
    PASSWORD_AUTHENTICATION_FAIL(UNAUTHORIZED, "비밀번호가 틀렸습니다."),

    /**
     * 403 FORBIDDEN
     */
    ROLE_NOT_ALLOWED(FORBIDDEN, "권한이 없습니다."),
    TOKEN_NOT_ALLOWED(FORBIDDEN, "권한이 없는 토큰입니다. 다시 로그인한 후 이용해주세요."),

    /**
     * 404 NOT_FOUND
     */
    USER_NOT_FOUND(NOT_FOUND, "존재하지 않는 사용자입니다."),
    EMAIL_NOT_FOUND(NOT_FOUND, "존재하지 않는 이메일입니다."),
    EDUCATION_NOT_FOUND(NOT_FOUND, "존재하지 않는 학력입니다."),
    WORK_NOT_FOUND(NOT_FOUND, "존재하지 않는 경력입니다."),
    SKILL_NOT_FOUND(NOT_FOUND, "존재하지 않은 기술입니다."),
    PORTFOLIO_NOT_FOUND(NOT_FOUND, "존재하지 않은 포트폴리오입니다."),

    /**
     * 405 METHOD_NOT_ALLOWED
     */
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "사용할 수 없는 메소드 입니다."),

    /**
     * 409 CONFLICT
     */
    EXISTING_EMAIL(CONFLICT, "이미 사용중인 이메일입니다."),
    EXISTING_USERNAME(CONFLICT, "이미 사용중인 아이디입니다."),
    EXISTING_NICKNAME(CONFLICT, "이미 사용중인 닉네임입니다."),


    /**
     * 413 PAYLOAD_TOO_LARGE
     */
    FILE_SIZE_EXCEED(PAYLOAD_TOO_LARGE, "파일 용량이 초과되었습니다"),
    FILE_COUNT_EXCEED(PAYLOAD_TOO_LARGE, "파일 개수가 초과되었습니다"),

    /**
     * 500 INTERNAL_SERVER_ERROR
     */
    SERVER_ERROR(INTERNAL_SERVER_ERROR,"서버 에러가 발생했습니다. 최대한 빠른 시일내 수정하겠습니다."),
    MAIL_SENDING_ERROR(INTERNAL_SERVER_ERROR, "이메일 발송 중 에러가 발생했습니다. 최대한 빠른 시일내 수정하겠습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
