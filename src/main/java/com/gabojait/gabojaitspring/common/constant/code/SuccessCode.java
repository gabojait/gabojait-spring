package com.gabojait.gabojaitspring.common.constant.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    /**
     * Notification Controller
     */
    // 200 Ok
    NOTIFICATIONS_FOUND(OK, "알림 다건 조회를 했습니다."),
    NOTIFICATION_READ(OK, "알림을 읽음 처리 했습니다."),

    /**
     * Review controller
     */
    // 200 Ok
    REVIEWABLE_TEAMS_FOUND(OK, "리뷰 작성 가능한 팀 전체 조회를 했습니다."),
    REVIEWABLE_TEAM_FOUND(OK, "리뷰 작성 가능한 팀 단건 조회를 했습니다."),
    USER_REVIEWS_FOUND(OK, "회원의 리뷰 다건 조회를 했습니다."),

    // 201 Created
    USER_REVIEWED(CREATED, "리뷰를 작성했습니다."),

    /**
     * Offer controller
     */
    // 200 Ok
    USER_RECEIVED_OFFER_FOUND(OK, "회원이 받은 제안 다건 조회를 했습니다."),
    TEAM_RECEIVED_OFFER_FOUND(OK, "팀이 받은 제안 다건 조회를 했습니다."),
    USER_SENT_OFFER_FOUND(OK, "회원이 보낸 제안 다건 조회를 했습니다."),
    TEAM_SENT_OFFER_FOUND(OK, "팀이 보낸 제안 다건 조회를 했습니다."),
    USER_DECIDED_OFFER(OK, "회원이 받은 제안을 결정했습니다."),
    TEAM_DECIDED_OFFER(OK, "팀이 받은 제안을 결정했습니다."),
    OFFER_CANCEL_BY_USER(OK, "회원이 보낸 제안을 취소했습니다."),
    OFFER_CANCEL_BY_TEAM(OK, "팀이 보낸 제안을 취소했습니다."),

    // 201 Created
    OFFERED_BY_USER(CREATED, "팀에 지원을 했습니다."),
    OFFERED_BY_TEAM(CREATED, "회원에게 채용 제안을 했습니다."),

    /**
     * Favorite controller
     */
    // 200 Ok
    FAVORITE_TEAM_DELETED(OK, "찜 목록에 팀을 제거하였습니다."),
    FAVORITE_TEAMS_FOUND(OK, "찜한 팀 다건 조회를 했습니다."),
    FAVORITE_USER_DELETED(OK, "찜 목록에 회원을 제거하였습니다."),
    FAVORITE_USERS_FOUND(OK, "찜한 회원 다건 조회를 했습니다."),

    // 201 Created
    FAVORITE_TEAM_ADDED(CREATED, "찜 목록에 팀을 추가하였습니다."),
    FAVORITE_USER_ADDED(CREATED, "찜 목록에 회원을 추가하였습니다."),

    /**
     * Team controller
     */
    // 200 Ok
    TEAM_UPDATED(OK, "팀 정보를 업데이트 했습니다."),
    SELF_TEAM_FOUND(OK, "본인 팀을 조회 했습니다."),
    TEAM_FOUND(OK, "팀 단건 조회를 했습니다."),
    TEAMS_RECRUITING_USERS_FOUND(OK, "팀원을 구하는 팀 다건 조회를 했습니다."),
    TEAM_IS_RECRUITING_UPDATED(OK, "팀원 모집 여부를 업데이트 했습니다."),
    PROJECT_INCOMPLETE(OK, "미완료 프로젝트를 종료 했습니다."),
    PROJECT_COMPLETE(OK, "완료 프로젝트를 종료 했습니다."),
    TEAMMATE_FIRED(OK, "팀원을 추방 했습니다."),

    // 201 Created
    TEAM_CREATED(CREATED, "팀을 생성 했습니다."),

    /**
     * Profile controller
     */
    // 200 Ok
    SELF_PROFILE_FOUND(OK, "본인 프로필을 조회 했습니다."),
    PROFILE_FOUND(OK, "프로필을 조회 했습니다."),
    PROFILE_IMAGE_UPLOADED(OK, "프로필 이미지를 업로드 했습니다."),
    PROFILE_IMAGE_DELETED(OK, "프로필 이미지를 삭제 했습니다."),
    PROFILE_SEEKING_TEAM_UPDATED(OK, "프로필 팀 찾기 여부 수정을 업데이트 했습니다."),
    PROFILE_DESCRIPTION_UPDATED(OK, "자기소개를 업데이트 했습니다."),
    PROFILE_UPDATED(OK, "프로필을 업데이트 했습니다."),
    USERS_SEEKING_TEAM_FOUND(OK, "팀을 구하는 회원 다건 조회를 했습니다."),
    USER_LEFT_TEAM(OK, "회원이 팀을 탈퇴 했습니다."),

    // 201 Created
    PORTFOLIO_FILE_UPLOADED(CREATED, "포트폴리오 파일을 업로드 했습니다."),

    /**
     * User controller
     */
    // 200 Ok
    USERNAME_AVAILABLE(OK, "아이디 중복 확인을 했습니다."),
    NICKNAME_AVAILABLE(OK, "닉네임 중복 확인을 했습니다."),
    USER_LOGIN(OK, "회원이 로그인을 했습니다."),
    USER_LOGOUT(OK, "회원 로그아웃을 했습니다."),
    SELF_USER_FOUND(OK, "본인을 조회 했습니다."),
    TOKEN_RENEWED(OK, "토큰을 재발급 했습니다."),
    USERNAME_EMAIL_SENT(OK, "이메일로 아이디를 전송 했습니다."),
    PASSWORD_EMAIL_SENT(OK, "이메일로 임시 비밀번호를 전송 했습니다."),
    PASSWORD_VERIFIED(OK, "비밀번호 검증을 했습니다."),
    NICKNAME_UPDATED(OK, "닉네임을 업데이트 했습니다."),
    PASSWORD_UPDATED(OK, "비밀번호를 업데이트 했습니다."),
    IS_NOTIFIED_UPDATED(OK, "알림 여부를 업데이트 했습니다."),
    USER_DELETED(OK, "회원 탈퇴를 했습니다."),

    // 201 Created
    USER_REGISTERED(CREATED, "회원 가입을 했습니다."),

    /**
     * Contact controller
     */
    // 200 Ok
    EMAIL_VERIFIED(OK, "이메일 인증번호 확인을 했습니다."),

    // 201 Created
    VERIFICATION_CODE_SENT(CREATED, "인증코드를 이메일로 전송 했습니다."),

    /**
     * Admin controller
     */
    // 200 Ok
    ADMIN_LOGIN(OK, "관리자가 로그인을 했습니다."),
    UNREGISTERED_ADMIN_FOUND(OK, "관리자 가입 대기자 다건 조회를 했습니다."),
    ADMIN_REGISTER_DECIDED(OK, "관리자 가입 결정을 했습니다."),

    // 201 Created
    ADMIN_REGISTERED(CREATED, "관리자 가입을 했습니다."),

    /**
     * Develop controller
     */
    // 200 Ok
    SERVER_OK(OK, "헬스 체크를 했습니다."),
    TESTER_TOKEN_ISSUED(OK, "테스트 계정 토큰을 발급 했습니다."),
    TEST_FCM_SENT(OK, "테스트 FCM을 보냈습니다."),
    DATABASE_RESET(OK, "데이터베이스 초기화 후 테스트 데이터를 주입했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
