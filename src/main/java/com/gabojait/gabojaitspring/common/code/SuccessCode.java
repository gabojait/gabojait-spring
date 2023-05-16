package com.gabojait.gabojaitspring.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    /**
     * Team Controller
     */
    // 200 Ok
    TEAM_UPDATED(OK, "팀 정보를 업데이트 했습니다."),
    TEAM_FOUND(OK, "팀 단건 조회를 했습니다."),
    TEAMS_FINDING_USERS_FOUND(OK, "팀원을 구하는 팀 다건 조회를 했습니다."),
    TEAM_IS_RECRUITING_UPDATED(OK, "팀원 모집 여부를 업데이트 했습니다."),
    PROJECT_INCOMPLETE(OK, "미완료 프로젝트를 종료 했습니다."),
    PROJECT_COMPLETE(OK, "완료 프로젝트를 종료 했습니다."),
    TEAMMATE_FIRED(OK, "팀원을 추방 했습니다."),
    USER_FAVORITE_UPDATED(OK, "회원 찜 목록을 업데이트 했습니다."),
    FAVORITE_USERS_FOUND(OK, "찜한 회원 전체 조회를 했습니다."),

    // 201 Created
    TEAM_CREATED(CREATED, "팀을 생성 했습니다."),

    /**
     * Profile Controller
     */
    // 200 Ok
    SELF_PROFILE_FOUND(OK, "본인 프로필을 조회 했습니다."),
    PROFILE_FOUND(OK, "프로필을 조회 했습니다."),
    PROFILE_IMAGE_UPLOADED(OK, "프로필 이미지를 업로드 했습니다."),
    PROFILE_IMAGE_DELETED(OK, "프로필 이미지를 삭제 했습니다."),
    PROFILE_VISIBILITY_UPDATED(OK, "프로필 공개 여부를 업데이트 했습니다."),
    PROFILE_DESCRIPTION_UPDATED(OK, "자기소개를 업데이트 했습니다."),
    POSITION_AND_SKILL_UPDATED(OK, "포지션과 기술을 업데이트 했습니다."),
    EDUCATION_AND_WORK_UPDATED(OK, "학력과 경력을 업데이트 했습니다."),
    LINK_PORTFOLIO_UPDATED(OK, "링크 포트폴리오를 업데이트 했습니다."),
    FILE_PORTFOLIO_UPDATED(OK, "파일 포트폴리오를 업데이트 했습니다."),
    USERS_FINDING_TEAM_FOUND(OK, "팀을 구하는 회원 다건 조회를 했습니다."),
    TEAM_FAVORITE_UPDATED(OK, "팀 찜 목록을 업데이트 했습니다."),
    FAVORITE_TEAMS_FOUND(OK, "찜한 팀 전체 조회를 했습니다."),

    // 201 Created

    /**
     * User Controller
     */
    // 200 Ok
    USERNAME_AVAILABLE(OK, "아이디 중복 확인을 했습니다."),
    NICKNAME_AVAILABLE(OK, "닉네임 중복 확인을 했습니다."),
    USER_LOGIN(OK, "로그인을 했습니다."),
    SELF_USER_FOUND(OK, "본인을 조회 했습니다."),
    USER_FOUND(OK, "회원을 조회 했습니다."),
    TOKEN_RENEWED(OK, "토큰을 재발급 했습니다."),
    USERNAME_EMAIL_SENT(OK, "이메일로 아이디를 전송 했습니다."),
    PASSWORD_EMAIL_SENT(OK, "이메일로 임시 비밀번호를 전송 했습니다."),
    PASSWORD_VERIFIED(OK, "비밀번호 검증을 했습니다."),
    USER_DELETED(OK, "회원 탈퇴를 했습니다."),

    // 201 Created
    USER_REGISTERED(CREATED, "회원 가입을 했습니다."),

    /**
     * Contact Controller
     */
    // 200 Ok
    EMAIL_VERIFIED(OK, "이메일 인증번호 확인을 했습니다."),

    // 201 Created
    VERIFICATION_CODE_SENT(CREATED, "인증코드를 이메일로 전송 했습니다."),

    /**
     * Develop Controller
     */
    // 200 Ok
    SERVER_HEALTH_OK(OK, "헬스 체크를 했습니다."),
    TOKEN_ISSUED(OK, "테스트 계정 토큰을 발급 했습니다."),
    TEST_DATA_INJECTED(OK, "데이터베이스 초기화 후 테스트 데이터를 주입했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
