package com.gabojait.gabojaitspring.common.constant.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /**
     * 400 Bad request
     */
    // @NotBlank, @NotNull
    EMAIL_FIELD_REQUIRED(BAD_REQUEST, "이메일은 필수 입력입니다."),
    VERIFICATION_CODE_FIELD_REQUIRED(BAD_REQUEST, "인증코드는 필수 입력입니다."),
    USER_ID_FIELD_REQUIRED(BAD_REQUEST, "회원 식별자는 필수 입력입니다."),
    USERNAME_FIELD_REQUIRED(BAD_REQUEST, "아이디는 필수 입력입니다."),
    PASSWORD_FIELD_REQUIRED(BAD_REQUEST, "비밀번호는 필수 입력입니다."),
    PASSWORD_RE_ENTERED_FIELD_REQUIRED(BAD_REQUEST, "비밀번호 재입력은 필수 입력입니다."),
    IS_SEEKING_TEAM_FIELD_REQUIRED(BAD_REQUEST, "팀 찾기 여부는 필수 입력입니다."),
    IS_EXPERIENCED_FIELD_REQUIRED(BAD_REQUEST, "경험 여부는 필수 입력입니다."),
    STARTED_AT_FIELD_REQUIRED(BAD_REQUEST, "시작일은 필수 입력입니다."),
    IS_CURRENT_FIELD_REQUIRED(BAD_REQUEST, "현재 여부는 필수 입력입니다."),
    LEADER_POSITION_FIELD_REQUIRED(BAD_REQUEST, "팀장 포지션은 필수 입력입니다."),
    DESIGNER_MAX_CNT_FIELD_REQUIRED(BAD_REQUEST, "디자이너 최대 수는 필수 입력입니다."),
    BACKEND_MAX_CNT_FIELD_REQUIRED(BAD_REQUEST, "백엔드 최대 수는 필수 입력입니다."),
    FRONTEND_MAX_CNT_FIELD_REQUIRED(BAD_REQUEST, "프런트 최대 수는 필수 입력입니다."),
    MANAGER_MAX_CNT_FIELD_REQUIRED(BAD_REQUEST, "매니저 최대 수는 필수 입력입니다."),
    IS_RECRUITING_FIELD_REQUIRED(BAD_REQUEST, "팀원 모집 여부는 필수 입력입니다."),
    PROJECT_URL_FIELD_REQUIRED(BAD_REQUEST, "완료된 프로젝트 URL은 필수 입력입니다."),
    IS_ADD_FAVORITE_FIELD_REQUIRED(BAD_REQUEST, "찜 추가 여부는 필수 입력입니다."),
    IS_ACCEPTED_FIELD_REQUIRED(BAD_REQUEST, "수락 여부는 필수 입력입니다."),
    TEAM_MEMBER_ID_FIELD_REQUIRED(BAD_REQUEST, "팀원 식별자는 필수 입력입니다."),
    RATING_FIELD_REQUIRED(BAD_REQUEST, "평점은 필수 입력입니다."),
    OFFER_ID_FIELD_REQUIRED(BAD_REQUEST, "제안 식별자는 필수 입력입니다."),
    BIRTHDATE_FIELD_REQUIRED(BAD_REQUEST, "생년월일은 필수 입력입니다."),
    IS_APPROVED_FIELD_REQUIRED(BAD_REQUEST, "승인 여부는 필수 입력입니다."),
    IS_NOTIFIED_FIELD_REQUIRED(BAD_REQUEST, "알림 여부는 필수 입력입니다."),
    FCM_TITLE_FIELD_REQUIRED(BAD_REQUEST, "FCM 제목은 필수 입력입니다."),
    FCM_MESSAGE_FIELD_REQUIRED(BAD_REQUEST, "FCM 메세지는 필수 입력입니다."),
    LEGAL_NAME_FIELD_REQUIRED(BAD_REQUEST, "실명은 필수 입력입니다."),
    ADMIN_ID_FIELD_REQUIRED(BAD_REQUEST, "관리자 식별자는 필수 입력입니다."),
    TESTER_ID_FIELD_REQUIRED(BAD_REQUEST, "테스터 식별자는 필수 입력입니다."),

    // @Size
    USERNAME_LENGTH_INVALID(BAD_REQUEST, "아이디는 5~15자만 가능합니다."),
    PASSWORD_LENGTH_INVALID(BAD_REQUEST, "비밀번호는 8~30자만 가능합니다."),
    NICKNAME_LENGTH_INVALID(BAD_REQUEST, "닉네임은 2~8자만 가능합니다."),
    SKILL_NAME_LENGTH_INVALID(BAD_REQUEST, "기술명은 1~20자만 가능합니다."),
    PORTFOLIO_NAME_LENGTH_INVALID(BAD_REQUEST, "포트폴리오명은 1~10자만 가능합니다."),
    PROFILE_DESCRIPTION_LENGTH_INVALID(BAD_REQUEST, "자기소개는 0~120자만 가능합니다."),
    INSTITUTION_NAME_LENGTH_INVALID(BAD_REQUEST, "학교명은 3~20자만 가능합니다."),
    CORPORATION_NAME_LENGTH_INVALID(BAD_REQUEST, "기관명은 1~20자만 가능합니다."),
    WORK_DESCRIPTION_LENGTH_INVALID(BAD_REQUEST, "경력 설명은 0~100자만 가능합니다."),
    PORTFOLIO_URL_LENGTH_INVALID(BAD_REQUEST, "포트폴리오 URL은 10~1000자만 가능합니다."),
    PROJECT_NAME_LENGTH_INVALID(BAD_REQUEST, "프로젝트명은 1~20자만 가능합니다."),
    PROJECT_DESCRIPTION_LENGTH_INVALID(BAD_REQUEST, "프로젝트 설명은 1~500자만 가능합니다."),
    EXPECTATION_LENGTH_INVALID(BAD_REQUEST, "바라는 점은 1~200자만 가능합니다."),
    OPEN_CHAT_URL_LENGTH_INVALID(BAD_REQUEST, "오픈 채팅 URL은 26~100자만 가능합니다."),
    POST_LENGTH_INVALID(BAD_REQUEST, "후기는 1~200자만 가능합니다."),
    LEGAL_NAME_LENGTH_INVALID(BAD_REQUEST, "실명은 1~5자만 가능합니다."),

    // @Pattern, @Email - format
    EMAIL_FORMAT_INVALID(BAD_REQUEST, "올바른 이메일 형식을 입력해 주세요."),
    USERNAME_FORMAT_INVALID(BAD_REQUEST, "아이디는 소문자 영어와 숫자의 조합으로 입력해 주세요."),
    PASSWORD_FORMAT_INVALID(BAD_REQUEST, "비밀번호는 영어, 숫자, 특수문자(#$@!%&*?)의 조합으로 입력해 주세요."),
    LEGAL_NAME_FORMAT_INVALID(BAD_REQUEST, "실명은 한글 조합으로 입력해 주세요."),
    NICKNAME_FORMAT_INVALID(BAD_REQUEST, "닉네임은 한글 조합으로 입력해 주세요."),
    OPEN_CHAT_URL_FORMAT_INVALID(BAD_REQUEST, "오픈 채팅 URL은 카카오 오픈 채팅 형식만 가능합니다."),

    // @Pattern - type
    GENDER_TYPE_INVALID(BAD_REQUEST, "성별은 'M', 'F', 또는 'N' 중 하나여야 됩니다."),
    POSITION_TYPE_INVALID(BAD_REQUEST, "포지션은 'DESIGNER', 'BACKEND', 'FRONTEND', 'MANAGER', 또는 'NONE' 중 하나여야 됩니다."),
    OFFER_POSITION_TYPE_INVALID(BAD_REQUEST, "제안할 포지션은 'DESIGNER', 'BACKEND', 'FRONTEND', 또는 'MANAGER' 중 하나여야 됩니다."),
    LEADER_POSITION_TYPE_INVALID(BAD_REQUEST, "팀장 포지션은 'DESIGNER', 'BACKEND', 'FRONTEND', 또는 'MANAGER' 중 하나여야 됩니다."),
    LEVEL_TYPE_INVALID(BAD_REQUEST, "레벨은 'LOW', 'MID', 또는 'HIGH' 중 하나여야 됩니다."),
    MEDIA_TYPE_INVALID(BAD_REQUEST, "미디어는 'LINK' 또는 'FILE' 중 하나여야 됩니다."),
    PORTFOLIO_URL_FORMAT_INVALID(BAD_REQUEST, "포트폴리오 URL은 'https://'로 시작하는 형식만 가능합니다."),

    // @Positive, @PositiveOrZero
    USER_ID_POSITIVE_ONLY(BAD_REQUEST, "회원 식별자는 양수만 가능합니다."),
    TEAM_ID_POSITIVE_ONLY(BAD_REQUEST, "팀 식별자는 양수만 가능합니다."),
    PAGE_FROM_POSITIVE_ONLY(BAD_REQUEST, "페이지 시작점은 양수만 가능합니다."),
    PAGE_SIZE_POSITIVE_ONLY(BAD_REQUEST, "페이지 사이즈는 양수만 가능합니다."),
    DESIGNER_MAX_CNT_POSITIVE_OR_ZERO_ONLY(BAD_REQUEST, "디자이너 최대 수는 0 또는 양수만 가능합니다."),
    BACKEND_MAX_CNT_POSITIVE_OR_ZERO_ONLY(BAD_REQUEST, "백엔드 최대 수는 0 또는 양수만 가능합니다."),
    FRONTEND_MAX_CNT_POSITIVE_OR_ZERO_ONLY(BAD_REQUEST, "프런트 최대 수는 0 또는 양수만 가능합니다."),
    MANAGER_MAX_CNT_POSITIVE_OR_ZERO_ONLY(BAD_REQUEST, "매니저 최대 수는 0 또는 양수만 가능합니다."),
    TEAM_MEMBER_ID_POSITIVE_ONLY(BAD_REQUEST, "팀원 식별자는 양수만 가능합니다."),
    OFFER_ID_POSITIVE_ONLY(BAD_REQUEST, "제안 식별자는 양수만 가능합니다."),
    NOTIFICATION_ID_POSITIVE_ONLY(BAD_REQUEST, "알림 식별자는 양수만 가능합니다."),
    ADMIN_ID_POSITIVE_ONLY(BAD_REQUEST, "관리자 식별자는 양수만 가능합니다."),
    TESTER_ID_POSITIVE_ONLY(BAD_REQUEST, "테스터 식별자는 양수만 가능합니다."),

    // @Min, @Max
    RATING_RANGE_INVALID(BAD_REQUEST, "평점은 1부터 5까지의 수만 가능합니다."),
    PAGE_SIZE_RANGE_INVALID(BAD_REQUEST, "페이지 사이즈는 100까지의 수만 가능합니다."),

    // Custom
    VERIFICATION_CODE_INVALID(BAD_REQUEST, "인증코드가 틀렸습니다."),
    PASSWORD_MATCH_INVALID(BAD_REQUEST, "비밀번호와 비밀번호 재입력이 동일하지 않습니다."),
    USERNAME_EMAIL_MATCH_INVALID(BAD_REQUEST, "가입한 이메일 아이디와 일치하지 않습니다."),
    FILE_FIELD_REQUIRED(BAD_REQUEST, "파일을 첨부해 주세요."),
    EDUCATION_ENDED_AT_FIELD_REQUIRED(BAD_REQUEST, "현재 진행 중이 아닌 경우 학력 종료일은 필수 입력입니다."),
    WORK_ENDED_AT_FIELD_REQUIRED(BAD_REQUEST, "현재 진행 중이 아닌 경우 학력 종료일은 필수 입력입니다."),
    EDUCATION_DATE_INVALID(BAD_REQUEST, "학력 시작일은 종료일보다 이전 이어야 됩니다."),
    WORK_DATE_INVALID(BAD_REQUEST, "경력 시작일은 종료일보다 이전 이어야 됩니다."),

    /**
     * 401 Unauthorized
     */
    TOKEN_UNAUTHENTICATED(UNAUTHORIZED, "승인되지 않은 요청입니다. 로그인 후에 다시 시도 해주세요."),
    LOGIN_UNAUTHENTICATED(UNAUTHORIZED, "아이디 또는 비밀번호가 틀렸습니다."),
    PASSWORD_UNAUTHENTICATED(UNAUTHORIZED, "비밀번호가 틀렸습니다."),

    /**
     * 403 Forbidden
     */
    TOKEN_UNAUTHORIZED(FORBIDDEN, "권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요."),
    REQUEST_FORBIDDEN(FORBIDDEN, "권한이 없는 요청입니다."),
    SUSPEND_FORBIDDEN(FORBIDDEN, "일시 정지된 계정입니다."),

    /**
     * 404 Not found
     */
    EMAIL_NOT_FOUND(NOT_FOUND, "이메일 인증하기를 해주세요."),
    CONTACT_NOT_FOUND(NOT_FOUND, "존재하지 않는 연락처입니다."),
    USER_NOT_FOUND(NOT_FOUND, "존재하지 않는 회원입니다."),
    TEAM_NOT_FOUND(NOT_FOUND, "존재하지 않는 팀입니다."),
    CURRENT_TEAM_NOT_FOUND(NOT_FOUND, "현재 팀이 존재하지 않습니다."),
    TEAM_MEMBER_NOT_FOUND(NOT_FOUND, "존재하지 않는 팀원입니다."),
    OFFER_NOT_FOUND(NOT_FOUND, "존재하지 않는 제안입니다."),
    ADMIN_NOT_FOUND(NOT_FOUND, "존재하지 않는 관리자입니다."),
    TESTER_NOT_FOUND(NOT_FOUND, "존재하지 않는 테스터입니다."),

    /**
     * 405 Method not allowed
     */
    METHOD_DISABLED(METHOD_NOT_ALLOWED, "불가능한 요청 방법 입니다."),

    /**
     * 409 Conflict
     */
    EXISTING_CONTACT(CONFLICT, "이미 사용중인 이메일입니다."),
    UNAVAILABLE_USERNAME(CONFLICT, "사용 불가능한 아이디입니다."),
    EXISTING_USERNAME(CONFLICT, "이미 사용중인 아이디입니다."),
    UNAVAILABLE_NICKNAME(CONFLICT, "사용 불가능한 닉네임입니다."),
    EXISTING_NICKNAME(CONFLICT, "이미 사용중인 닉네임입니다."),
    EXISTING_CURRENT_TEAM(CONFLICT, "현재 소속되어 있는 팀이 존재합니다."),
    NON_EXISTING_POSITION(CONFLICT, "현재 포지션이 존재하지 않습니다."),
    TEAM_POSITION_UNAVAILABLE(CONFLICT, "선택하신 포지션 모집은 마감 되었습니다."),
    DESIGNER_CNT_UPDATE_UNAVAILABLE(CONFLICT, "현재 소속되어 있는 디자이너 팀원 수가 수정한 디자이너 팀원 수 보다 많습니다."),
    BACKEND_CNT_UPDATE_UNAVAILABLE(CONFLICT, "현재 소속되어 있는 백엔드 개발자 팀원 수가 수정한 백엔드 개발자 팀원 수 보다 많습니다."),
    FRONTEND_CNT_UPDATE_UNAVAILABLE(CONFLICT, "현재 소속되어 있는 프론트엔드 개발자 팀원 수가 수정한 프론트엔드 개발자 팀원 수 보다 많습니다."),
    MANAGER_CNT_UPDATE_UNAVAILABLE(CONFLICT, "현재 소속되어 있는 매니저 팀원 수가 수정한 매니저 팀원 수 보다 많습니다."),
    TEAM_LEADER_UNAVAILABLE(CONFLICT, "팀장은 할 수 없는 행동입니다."),
    UNREGISTER_UNAVAILABLE(CONFLICT, "현재 팀장인 상태에서 회원탈퇴를 할 수 없습니다."),
    CURRENTLY_UNAVAILABLE(CONFLICT, "현재 동시 요청이 많아 잠시 뒤에 다시 시도해 주세요."),
    TEAM_LEADER_POSITION_UNAVAILABLE(CONFLICT, "팀장의 포지션은 1 이상이여야 됩니다."),

    /**
     * 413 Payload too large
     */
    FILE_SIZE_EXCEED(PAYLOAD_TOO_LARGE, "파일 사이즈는 8MB 이하만 가능합니다."),
    FILE_COUNT_EXCEED(PAYLOAD_TOO_LARGE, "파일 수 제한 5개를 초과 하였습니다."),

    /**
     * 415 Unsupported media type
     */
    IMAGE_TYPE_UNSUPPORTED(UNSUPPORTED_MEDIA_TYPE, "이미지는 '.pdf', '.jpeg', '.jpg', 또는 '.png'만 가능합니다."),
    FILE_TYPE_UNSUPPORTED(UNSUPPORTED_MEDIA_TYPE, "파일은 '.jpeg', '.jpg', 또는 '.png'만 가능합니다."),

    /**
     * 500 Internal server error
     */
    EMAIL_SEND_ERROR(INTERNAL_SERVER_ERROR, "이메일 전송 중 서버 에러가 발생했습니다."),
    SERVER_ERROR(INTERNAL_SERVER_ERROR, "서버 에러가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
