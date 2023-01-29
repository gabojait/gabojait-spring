package com.inuappcenter.gabojaitspring.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    /**
     * Profile
     */
    // 200 OK
    EDUCATION_UPDATED(OK, "학력 업데이트를 완료 했습니다."),
    EDUCATION_DELETED(OK, "학력 제거를 완료 했습니다"),
    WORK_UPDATED(OK, "경력 업데이트를 완료 했습니다"),
    WORK_DELETED(OK, "경력 제거를 완료 했습니다."),
    SKILL_UPDATED(OK, "기술 업데이트 완료 했습니다"),
    SKILL_DELETED(OK, "기술 제거를 완료 했습니다."),
    PORTFOLIO_FILE_UPDATED(OK, "포트폴리오 파일 업데이트를 완료 했습니다"),
    PORTFOLIO_LINK_UPDATED(OK, "포트폴리오 링크 업데이트를 완료 했습니다"),
    PORTFOLIO_DELETED(OK, "포트폴리오 제거를 완료 했습니다."),


    // 201 CREATED
    EDUCATION_CREATED(CREATED, "학력 생성을 완료 했습니다."),
    WORK_CREATED(CREATED, "경력 생성을 완료 했습니다."),
    SKILL_CREATED(CREATED, "기술 생성을 완료 했습니다."),
    PORTFOLIO_FILE_CREATED(CREATED, "포트폴리오 파일 생성을 완료 했습니다."),
    PORTFOLIO_LINK_CREATED(CREATED, "포트폴리오 링크 생성을 완료 했습니다."),



    /**
     * User
     */
    // 200 OK
    USERNAME_NO_DUPLICATE(OK, "아이디 중복 확인을 완료 했습니다."),
    NICKNAME_NO_DUPLICATE(OK, "닉네임 중복 확인을 완료 했습니다."),
    USER_LOGGED_IN(OK, "로그인 완료 했습니다."),
    USER_TOKEN_RENEWED(OK, "회원 토큰 재발급을 완료 했습니다."),
    USERNAME_EMAIL_SENT(OK, "이메일로 아이디를 전송 했습니다."),
    PASSWORD_EMAIL_SENT(OK, "이메일로 임시 비밀번호를 전송 했습니다."),
    PASSWORD_UPDATED(OK, "비밀번호 업데이트를 완료 했습니다."),
    NICKNAME_UPDATED(OK, "닉네임 업데이트를 완료 했습니다."),
    PASSWORD_VERIFIED(OK, "비밀번호 검증을 완료 했습니다."),
    PASSWORD_FORCE_UPDATE(OK, "비밀번호 재설정이 필요합니다."),
    USER_DEACTIVATED(OK, "회원 탈퇴를 완료 했습니다."),

    // 201 CREATED
    USER_REGISTERED(CREATED, "회원 가입을 완료 했습니다."),


    /**
     * Contact
     */
    // 200 OK
    EMAIL_VERIFIED(OK, "이메일 인증번호 확인을 완료 했습니다."),

    // 201 CREATED
    EMAIL_NO_DUPLICATE(CREATED, "이메일 중복 확인을 완료 했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
