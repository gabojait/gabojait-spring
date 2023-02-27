package com.inuappcenter.gabojaitspring.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    /**
     * Team
     */
    // 200 OK
    TEAMMATES_FOUND(OK, "팀원 찾기를 완료 했습니다."),
    TEAMMATES_ZERO(OK, "더이상 찾을 수 잇는 팀원이 없습니다."),
    TEAMS_FOUND(OK, "팀 찾기를 완료 했습니다."),
    TEAM_ZERO(OK, "더이상 찾을 수 있는 팀이 없습니다."),
    TEAM_VISIBILITY_UPDATED(OK, "팀 공개 여부 업데이트를 완료 했습니다."),
    OFFERS_FOUND(OK, "제안 찾기를 완료 했습니다."),
    OFFER_ZERO(OK, "더이상 찾을 수 있는 제안이 없습니다."),


    // 201 CREATED
    TEAM_CREATED(CREATED, "팀 생성을 완료 했습니다."),
    OFFER_CREATED(CREATED, "제안 생성을 완료했습니다."),

    /**
     * Profile
     */
    // 200 OK
    PROFILE_VISIBILITY_UPDATED(OK, "프로필 공개 여부 업데이트를 완료 했습니다."),
    PROFILE_DESCRIPTION_UPDATED(OK, "자기소개 업데이트를 완료 했습니다."),
    EDUCATION_UPDATED(OK, "학력 업데이트를 완료 했습니다."),
    EDUCATION_DELETED(OK, "학력 제거를 완료 했습니다"),
    WORK_UPDATED(OK, "경력 업데이트를 완료 했습니다"),
    WORK_DELETED(OK, "경력 제거를 완료 했습니다."),
    SKILL_UPDATED(OK, "기술 업데이트 완료 했습니다"),
    SKILL_DELETED(OK, "기술 제거를 완료 했습니다."),
    PORTFOLIO_FILE_UPDATED(OK, "포트폴리오 파일 업데이트를 완료 했습니다"),
    PORTFOLIO_LINK_UPDATED(OK, "포트폴리오 링크 업데이트를 완료 했습니다"),
    PORTFOLIO_DELETED(OK, "포트폴리오 제거를 완료 했습니다."),
    POSITION_UPDATED(OK, "포지션 업데이트를 완료 했습니다."),
    PROFILE_FOUND(OK, "프로필 단건 조회를 완료 했습니다."),
    MY_PROFILE_FOUND(OK, "본인 프로필 정보 조회를 완료 했습니다."),


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
    USER_FOUND(OK, "회원 단건 조희를 완료 했습니다."),
    MY_USER_FOUND(OK, "본인 회원 정보 조회를 완료 했습니다."),

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
