package com.gabojait.gabojaitspring.common.code;

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
    // Annotation
    // @NotBlank, @NotNull
    EMAIL_FIELD_REQUIRED(BAD_REQUEST, "이메일을 입력해 주세요."),
    VERIFICATION_CODE_FIELD_REQUIRED(BAD_REQUEST, "인증코드를 입력해 주세요."),
    USER_ID_FIELD_REQUIRED(BAD_REQUEST, "회원 식별자를 입력해 주세요."),
    USERNAME_FIELD_REQUIRED(BAD_REQUEST, "아이디를 입력해 주세요."),
    PASSWORD_FIELD_REQUIRED(BAD_REQUEST, "비밀번호를 입력해 주세요."),
    PASSWORD_RE_ENTERED_REQUIRED(BAD_REQUEST, "비밀번호 재입력을 입력해 주세요."),
    LEGAL_NAME_FIELD_REQUIRED(BAD_REQUEST, "실명을 입력해 주세요."),
    NICKNAME_FIELD_REQUIRED(BAD_REQUEST, "닉네임을 입력해 주세요."),
    GENDER_FIELD_REQUIRED(BAD_REQUEST, "성별을 입력해 주세요."),
    BIRTHDATE_FIELD_REQUIRED(BAD_REQUEST, "생년월일을 입력해 주세요."),
    IS_PUBLIC_FIELD_REQUIRED(BAD_REQUEST, "공개 여부를 입력해 주세요."),
    SKILL_ID_FIELD_REQUIRED(BAD_REQUEST, "기술 식별자를 입력해 주세요."),
    SKILL_NAME_FIELD_REQUIRED(BAD_REQUEST, "기술명을 입력해 주세요."),
    IS_EXPERIENCED_FIELD_REQUIRED(BAD_REQUEST, "경험 여부를 입력해 주세요."),
    LEVEL_FIELD_REQUIRED(BAD_REQUEST, "레벨을 입력해 주세요."),
    POSITION_FIELD_REQUIRED(BAD_REQUEST, "포지션을 입력해 주세요."),
    INSTITUTION_NAME_FIELD_REQUIRED(BAD_REQUEST, "학교명을 입력해 주세요."),
    STARTED_DATE_FIELD_REQUIRED(BAD_REQUEST, "시작일을 입력해 주세요."),
    ENDED_DATE_FIELD_REQUIRED(BAD_REQUEST, "종료일을 입력해 주세요."),
    IS_CURRENT_FIELD_REQUIRED(BAD_REQUEST, "현재 여부를 입력해 주세요."),
    EDUCATION_ID_FIELD_REQUIRED(BAD_REQUEST, "학력 식별자를 입력해 주세요."),
    CORPORATION_NAME_FIELD_REQUIRED(BAD_REQUEST, "기관명을 입력해 주세요."),
    WORK_ID_FIELD_REQUIRED(BAD_REQUEST, "기관명 식별자를 입력해 주세요."),
    PORTFOLIO_NAME_FIELD_REQUIRED(BAD_REQUEST, "포트폴리오명을 입력해 주세요."),
    URL_FIELD_REQUIRED(BAD_REQUEST, "URL을 입력해 주세요."),
    PORTFOLIO_ID_FIELD_REQUIRED(BAD_REQUEST, "포트폴리오 식별자를 입력해 주세요."),
    PAGE_FROM_FIELD_REQUIRED(BAD_REQUEST, "페이지 시작점을 입력해 주세요."),
    PROFILE_ORDER_FIELD_REQUIRED(BAD_REQUEST, "프로필 정렬 기준을 입력해 주세요."),
    TEAM_ID_FIELD_REQUIRED(BAD_REQUEST, "팀 식별자를 입력해 주세요."),
    PROJECT_NAME_FIELD_REQUIRED(BAD_REQUEST, "프로젝트명을 입력해 주세요."),
    PROJECT_DESCRIPTION_FIELD_REQUIRED(BAD_REQUEST, "프로젝트 설명을 입력해 주세요."),
    DESIGNER_TOTAL_RECRUIT_CNT_FIELD_REQUIRED(BAD_REQUEST, "디자이너 총 팀원 수를 입력해 주세요."),
    BACKEND_TOTAL_RECRUIT_CNT_FIELD_REQUIRED(BAD_REQUEST, "백엔드 개발자 총 팀원 수를 입력해 주세요."),
    FRONTEND_TOTAL_RECRUIT_CNT_FIELD_REQUIRED(BAD_REQUEST, "프론트엔드 개발자 총 팀원 수를 입력해 주세요."),
    MANAGER_TOTAL_RECRUIT_CNT_FIELD_REQUIRED(BAD_REQUEST, "매니저 총 팀원 수를 입력해 주세요."),
    EXPECTATION_FIELD_REQUIRED(BAD_REQUEST, "바라는 점을 입력해 주세요."),
    OPEN_CHAT_URL_FIELD_REQUIRED(BAD_REQUEST, "오픈 채팅 링크를 입력해 주세요."),
    TEAM_ORDER_FIELD_REQUIRED(BAD_REQUEST, "팀 정렬 기준을 입력해 주세요."),
    IS_RECRUITING_FIELD_REQUIRED(BAD_REQUEST, "팀원 모집 여부를 입력해 주세요."),
    PROJECT_URL_FIELD_REQUIRED(BAD_REQUEST, "완료된 프로젝트 URL을 입력해 주세요."),
    IS_ADD_FAVORITE_FIELD_REQUIRED(BAD_REQUEST, "찜 추가 여부를 입력해 주세요."),

    // @Size
    USERNAME_LENGTH_INVALID(BAD_REQUEST, "아이디는 5~15자만 가능합니다."),
    PASSWORD_LENGTH_INVALID(BAD_REQUEST, "비밀번호는 8~30자만 가능합니다."),
    LEGAL_NAME_LENGTH_INVALID(BAD_REQUEST, "실명은 2~5자만 가능합니다."),
    NICKNAME_LENGTH_INVALID(BAD_REQUEST, "닉네임은 2~8자만 가능합니다."),
    SKILL_NAME_LENGTH_INVALID(BAD_REQUEST, "기술명은 1~20자만 가능합니다."),
    PROFILE_DESCRIPTION_LENGTH_INVALID(BAD_REQUEST, "자기소개는 0~120자만 가능합니다."),
    INSTITUTION_NAME_LENGTH_INVALID(BAD_REQUEST, "학교명은 3~20자만 가능합니다."),
    CORPORATION_NAME_LENGTH_INVALID(BAD_REQUEST, "기관명을 1~20자만 가능합니다."),
    WORK_DESCRIPTION_LENGTH_INVALID(BAD_REQUEST, "경력 설명은 0~100자만 가능합니다."),
    URL_LENGTH_INVALID(BAD_REQUEST, "URL은 1~1000자만 가능합니다."),
    PROJECT_NAME_LENGTH_INVALID(BAD_REQUEST, "프로젝트명은 1~20자만 가능합니다."),
    PROJECT_DESCRIPTION_LENGTH_INVALID(BAD_REQUEST, "프로젝트 설명은 1~500자만 가능합니다."),
    EXPECTATION_LENGTH_INVALID(BAD_REQUEST, "바라는 점은 1~200자만 가능합니다."),
    OPEN_CHAT_URL_LENGTH_INVALID(BAD_REQUEST, "오픈 채팅 링크는 25~100자만 가능합니다."),

    // @Pattern, @Email - format
    EMAIL_FORMAT_INVALID(BAD_REQUEST, "올바른 이메일 형식을 입력해 주세요."),
    USERNAME_FORMAT_INVALID(BAD_REQUEST, "아이디는 소문자 영어와 숫자의 조합으로 입력해 주세요."),
    PASSWORD_FORMAT_INVALID(BAD_REQUEST, "비밀번호는 영어, 숫자, 특수문자(#$@!%&*?)의 조합으로 입력해 주세요."),
    LEGAL_NAME_FORMAT_INVALID(BAD_REQUEST, "실명은 한글 조합으로 입력해 주세요."),
    NICKNAME_FORMAT_INVALID(BAD_REQUEST, "닉네임은 한글 조합으로 입력해 주세요."),
    OPEN_CHAT_URL_FORMAT_INVALID(BAD_REQUEST, "오픈 채팅 링크는 카카오 오픈 채팅 URL 형식만 가능합니다."),

    // @Pattern - type
    GENDER_TYPE_INVALID(BAD_REQUEST, "성별은 'MALE', 'FEMALE', 또는 'NONE' 중 하나여야 됩니다."),
    POSITION_TYPE_INVALID(BAD_REQUEST, "포지션은 'DESIGNER', 'BACKEND', 'FRONTEND', 'MANAGER', 또는 'NONE' 중 하나여야 됩니다."),
    LEVEL_TYPE_INVALID(BAD_REQUEST, "레벨은 'LOW', 'MID', 또는 'HIGH' 중 하나여야 됩니다."),
    MEDIA_TYPE_INVALID(BAD_REQUEST, "미디어는 'LINK' 또는 'FILE' 중 하나여야 됩니다."),
    REVIEW_CATEGORY_TYPE_INVALID(BAD_REQUEST, "리뷰는 'NUMERIC' 또는 'VERBAL' 중 하나여야 됩니다."),
    PROFILE_ORDER_TYPE_INVALID(BAD_REQUEST, "순서는 'ACTIVE', 'POPULARITY', 또는 'RATING' 중 하나여야 됩니다."),
    TEAM_ORDER_TYPE_INVALID(BAD_REQUEST, "순서는 'CREATED', 'ACTIVE', 또는 'POPULARITY' 중 하나여야 됩니다."),

    // @Positive, @PositiveOrZero
    PAGE_FROM_POS_OR_ZERO_ONLY(BAD_REQUEST, "페이지 시작점은 0 또는 양수만 가능합니다."),
    PAGE_SIZE_POS_ONLY(BAD_REQUEST, "페이지 사이즈는 양수만 가능합니다."),
    DESIGNER_TOTAL_CNT_POS_OR_ZERO_ONLY(BAD_REQUEST, "디자이너 수는 0 또는 양수만 가능합니다."),
    BACKEND_TOTAL_CNT_POS_OR_ZERO_ONLY(BAD_REQUEST, "백엔드 개발자 수는 0 또는 양수만 가능합니다."),
    FRONTEND_TOTAL_CNT_POS_OR_ZERO_ONLY(BAD_REQUEST, "프론트엔드 개발자 수는 0 또는 양수만 가능합니다."),
    MANAGER_TOTAL_CNT_POS_OR_ZERO_ONLY(BAD_REQUEST, "매니저 수는 0 또는 양수만 가능합니다."),


    // Custom
    ID_CONVERT_INVALID(BAD_REQUEST, "잘못된 식별자입니다."),
    VERIFICATION_CODE_INVALID(BAD_REQUEST, "인증코드가 틀렸습니다."),
    PASSWORD_MATCH_INVALID(BAD_REQUEST, "비밀번호와 비밀번호 재입력이 동일하지 않습니다."),
    PASSWORD_INVALID(BAD_REQUEST, "비밀번호가 틀렸습니다."),
    MYSELF_REQUEST_INVALID(BAD_REQUEST, "본인 관련 잘못된 요청입니다."),
    USERNAME_EMAIL_MATCH_INVALID(BAD_REQUEST, "가입한 이메일 아이디와 일치하지 않습니다."),
    PORTFOLIO_NAME_LENGTH_INVALID(BAD_REQUEST, "포트폴리오명은 1~10자만 가능합니다."),
    CREATE_PORTFOLIO_CNT_MATCH_INVALID(BAD_REQUEST, "생성할 포트폴리명과 포트폴리오 파일 수가 동일하지 않습니다."),
    UPDATE_PORTFOLIO_CNT_MATCH_INVALID(BAD_REQUEST, "업데이트할 포트폴리오 식별자, 포트폴리오명, 포트폴리오 파일 수가 동일하지 않습니다."),
    FILE_FIELD_REQUIRED(BAD_REQUEST, "파일을 첨부해 주세요."),

    /**
     * 401 Unauthorized
     */
    TOKEN_UNAUTHORIZED(UNAUTHORIZED, "승인되지 않은 요청입니다. 로그인 후에 다시 시도 해주세요."),
    LOGIN_FAIL(UNAUTHORIZED, "아이디 또는 비밀번호가 틀렸습니다."),

    /**
     * 403 Forbidden
     */
    TOKEN_FORBIDDEN(FORBIDDEN, "권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요."),
    REQUEST_FORBIDDEN(FORBIDDEN, "권한이 없는 요청입니다."),

    /**
     * 404 Not found
     */
    EMAIL_NOT_FOUND(NOT_FOUND, "이메일 인증하기를 먼저 해주세요."),
    CONTACT_NOT_FOUND(NOT_FOUND, "존재하지 않는 연락처입니다."),
    USER_NOT_FOUND(NOT_FOUND, "존재하지 않는 회원입니다."),
    TEAM_NOT_FOUND(NOT_FOUND, "존재하지 않는 팀입니다."),
    EDUCATION_NOT_FOUND(NOT_FOUND, "존재하지 않는 학력입니다."),
    PORTFOLIO_NOT_FOUND(NOT_FOUND, "존재하지 않는 포트폴리오입니다."),
    SKILL_NOT_FOUND(NOT_FOUND, "존재하지 않는 기술입니다."),
    WORK_NOT_FOUND(NOT_FOUND, "존재하지 않는 경력입니다."),

    /**
     * 405 Method not allowed
     */
    METHOD_DISABLED(METHOD_NOT_ALLOWED, "불가능한 요청 방법 입니다."),

    /**
     * 409 Conflict
     */
    EXISTING_CONTACT(CONFLICT, "이미 사용중인 이메일입니다."),
    EXISTING_USERNAME(CONFLICT, "이미 사용중인 아이디입니다."),
    UNAVAILABLE_USERNAME(CONFLICT, "사용 불가능한 아이디입니다."),
    UNAVAILABLE_NICKNAME(CONFLICT, "사용 불가능한 닉네임입니다."),
    EXISTING_NICKNAME(CONFLICT, "이미 사용중인 닉네임입니다."),
    EXISTING_CURRENT_TEAM(CONFLICT, "현재 소속되어 있는 팀이 존재합니다."),
    NON_EXISTING_POSITION(CONFLICT, "포지션이 존재하지 않습니다."),
    TEAM_POSITION_UNAVAILABLE(CONFLICT, "선택하신 포지션 모집은 마감 되었습니다."),
    NON_EXISTING_CURRENT_TEAM(CONFLICT, "현재 소속되어 있는 팀이 존재하지 않습니다."),
    DESIGNER_CNT_UPDATE_UNAVAILABLE(CONFLICT, "현재 소속되어 있는 디자이너 팀원 수가 수정한 디자이너 팀원 수 보다 많습니다."),
    BACKEND_CNT_UPDATE_UNAVAILABLE(CONFLICT, "현재 소속되어 있는 백엔드 개발자 팀원 수가 수정한 백엔드 개발자 팀원 수 보다 많습니다."),
    FRONTEND_CNT_UPDATE_UNAVAILABLE(CONFLICT, "현재 소속되어 있는 프론트엔드 개발자 팀원 수가 수정한 프론트엔드 개발자 팀원 수 보다 많습니다."),
    MANAGER_CNT_UPDATE_UNAVAILABLE(CONFLICT, "현재 소속되어 있는 매니저 팀원 수가 수정한 매니저 팀원 수 보다 많습니다."),

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
     * 422 Unprocessable entity
     */

    /**
     * 500 Internal server error
     */
    // Custom
    EMAIL_SEND_ERROR(INTERNAL_SERVER_ERROR, "이메일 전송 중 서버 에러가 발생했습니다."),
    SERVER_ERROR(INTERNAL_SERVER_ERROR, "서버 에러가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}