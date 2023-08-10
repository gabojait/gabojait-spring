package com.gabojait.gabojaitspring.profile.controller;

import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.dto.DefaultMultiResDto;
import com.gabojait.gabojaitspring.common.dto.DefaultNoResDto;
import com.gabojait.gabojaitspring.common.dto.DefaultSingleResDto;
import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.profile.domain.type.ProfileOrder;
import com.gabojait.gabojaitspring.profile.dto.ProfileSeekPageDto;
import com.gabojait.gabojaitspring.profile.dto.req.*;
import com.gabojait.gabojaitspring.profile.dto.res.*;
import com.gabojait.gabojaitspring.profile.service.ProfileService;
import com.gabojait.gabojaitspring.team.service.TeamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.GroupSequence;
import javax.validation.Valid;
import javax.validation.constraints.*;

import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "프로필")
@Validated
@GroupSequence({ProfileController.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class ProfileController {

    private final ProfileService profileService;
    private final TeamService teamService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "본인 프로필 조회",
            notes = "<응답 코드>\n" +
                    "- 200 = SELF_PROFILE_FOUND\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND || TEAM_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ProfileDefaultResDto.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/profile")
    public ResponseEntity<DefaultSingleResDto<Object>> findMyself(HttpServletRequest servletRequest) {
        long userId = jwtProvider.getId(servletRequest.getHeader(AUTHORIZATION));

        ProfileDefaultResDto response = profileService.findOneProfile(userId);

        return ResponseEntity.status(SELF_PROFILE_FOUND.getHttpStatus())
                .body(DefaultSingleResDto.singleDataBuilder()
                        .responseCode(SELF_PROFILE_FOUND.name())
                        .responseMessage(SELF_PROFILE_FOUND.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "프로필 단건 조회",
            notes = "<검증>\n" +
                    "- user-id = NotNull && Positive\n\n" +
                    "<응답 코드>\n" +
                    "- 200 = PROFILE_FOUND\n" +
                    "- 400 = USER_ID_FIELD_REQUIRED || USER_ID_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ProfileOfferAndFavoriteResDto.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/{user-id}/profile")
    public ResponseEntity<DefaultSingleResDto<Object>> findOther(
            HttpServletRequest servletRequest,
            @PathVariable(value = "user-id", required = false)
            @NotNull(message = "회원 식별자는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
            @Positive(message = "회원 식별자는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
            Long userId
    ) {
        long uId = jwtProvider.getId(servletRequest.getHeader(AUTHORIZATION));

        ProfileOfferAndFavoriteResDto response = profileService.findOneOtherProfile(uId, userId);

        return ResponseEntity.status(PROFILE_FOUND.getHttpStatus())
                .body(DefaultSingleResDto.singleDataBuilder()
                        .responseCode(PROFILE_FOUND.name())
                        .responseMessage(PROFILE_FOUND.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "프로필 사진 업로드 또는 수정",
            notes = "<검증>\n" +
                    "- image = NotNull\n\n" +
                    "<응답 코드>\n" +
                    "- 200 = PROFILE_IMAGE_UPLOADED\n" +
                    "- 400 = FILE_FIELD_REQUIRED\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND\n" +
                    "- 413 = FILE_SIZE_EXCEED\n" +
                    "- 415 = IMAGE_TYPE_UNSUPPORTED\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ProfileDefaultResDto.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "413", description = "PAYLOAD TOO LARGE"),
            @ApiResponse(responseCode = "415", description = "UNSUPPORTED MEDIA TYPE"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PostMapping(value = "/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultSingleResDto<Object>> uploadProfileImage(HttpServletRequest servletRequest,
                                                                    @RequestPart(value = "image", required = false)
                                                                    MultipartFile image) {
        long userId = jwtProvider.getId(servletRequest.getHeader(AUTHORIZATION));

        ProfileDefaultResDto response = profileService.uploadProfileImage(userId, image);

        return ResponseEntity.status(PROFILE_IMAGE_UPLOADED.getHttpStatus())
                .body(DefaultSingleResDto.singleDataBuilder()
                        .responseCode(PROFILE_IMAGE_UPLOADED.name())
                        .responseMessage(PROFILE_IMAGE_UPLOADED.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "프로필 사진 삭제",
            notes = "<응답 코드>\n" +
                    "- 200 = PROFILE_IMAGE_DELETED\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ProfileDefaultResDto.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @DeleteMapping("/image")
    public ResponseEntity<DefaultSingleResDto<Object>> deleteProfileImage(HttpServletRequest servletRequest) {
        long userId = jwtProvider.getId(servletRequest.getHeader(AUTHORIZATION));

        ProfileDefaultResDto response = profileService.deleteProfileImage(userId);

        return ResponseEntity.status(PROFILE_IMAGE_DELETED.getHttpStatus())
                .body(DefaultSingleResDto.singleDataBuilder()
                        .responseCode(PROFILE_IMAGE_DELETED.name())
                        .responseMessage(PROFILE_IMAGE_DELETED.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "팀 찾기 여부 수정",
            notes = "<응답 코드>\n" +
                    "- 200 = PROFILE_SEEKING_TEAM_UPDATED\n" +
                    "- 400 = IS_SEEKING_TEAM_FIELD_REQUIRED\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PatchMapping("/seeking-team")
    public ResponseEntity<DefaultNoResDto> updateIsSeekingTeam(HttpServletRequest servletRequest,
                                                               @RequestBody @Valid
                                                               ProfileIsSeekingTeamUpdateReqDto request) {
        long userId = jwtProvider.getId(servletRequest.getHeader(AUTHORIZATION));

        profileService.updateIsSeekingTeam(userId, request.getIsSeekingTeam());

        return ResponseEntity.status(PROFILE_SEEKING_TEAM_UPDATED.getHttpStatus())
                .body(DefaultNoResDto.noDataBuilder()
                        .responseCode(PROFILE_SEEKING_TEAM_UPDATED.name())
                        .responseMessage(PROFILE_SEEKING_TEAM_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "자기소개 업데이트",
            notes = "<응답 코드>\n" +
                    "- 200 = PROFILE_DESCRIPTION_UPDATED\n" +
                    "- 400 = PROFILE_DESCRIPTION_LENGTH_INVALID\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PatchMapping("/description")
    public ResponseEntity<DefaultNoResDto> updateDescription(HttpServletRequest servletRequest,
                                                                   @RequestBody @Valid
                                                                   ProfileDescriptionUpdateReqDto request) {
        long userId = jwtProvider.getId(servletRequest.getHeader(AUTHORIZATION));

        profileService.updateProfileDescription(userId, request.getProfileDescription());

        return ResponseEntity.status(PROFILE_DESCRIPTION_UPDATED.getHttpStatus())
                .body(DefaultNoResDto.noDataBuilder()
                        .responseCode(PROFILE_DESCRIPTION_UPDATED.name())
                        .responseMessage(PROFILE_DESCRIPTION_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "프로필 업데이트",
            notes = "<응답 코드>\n" +
                    "- 200 = PROFILE_UPDATED\n" +
                    "- 400 = POSITION_FIELD_REQUIRED || POSITION_TYPE_INVALID || SKILL_NAME_FIELD_REQUIRED || " +
                    "SKILL_NAME_LENGTH_INVALID || IS_EXPERIENCED_FIELD_REQUIRED || LEVEL_FIELD_REQUIRED || " +
                    "LEVEL_TYPE_INVALID || INSTITUTION_NAME_FIELD_REQUIRED || INSTITUTION_NAME_LENGTH_INVALID || " +
                    "STARTED_AT_FIELD_REQUIRED || IS_CURRENT_FIELD_REQUIRED || CORPORATION_NAME_FIELD_REQUIRED || " +
                    "CORPORATION_NAME_LENGTH_INVALID || WORK_DESCRIPTION_LENGTH_INVALID || " +
                    "PORTFOLIO_NAME_FIELD_REQUIRED || PORTFOLIO_NAME_LENGTH_INVALID || " +
                    "PORTFOLIO_URL_FIELD_REQUIRED || PORTFOLIO_URL_LENGTH_INVALID || MEDIA_FIELD_REQUIRED || " +
                    "MEDIA_TYPE_INVALID || EDUCATION_DATE_INVALID || EDUCATION_ENDED_AT_FIELD_REQUIRED || " +
                    "WORK_DATE_INVALID || WORK_ENDED_AT_FIELD_REQUIRED\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ProfileDefaultResDto.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PostMapping("/profile")
    public ResponseEntity<DefaultSingleResDto<Object>> updateProfile(HttpServletRequest servletRequest,
                                                                     @RequestBody @Valid
                                                                     ProfileDefaultReqDto request) {
        long userId = jwtProvider.getId(servletRequest.getHeader(AUTHORIZATION));

        ProfileDefaultResDto response = profileService.updateProfile(userId, request);

        return ResponseEntity.status(PROFILE_UPDATED.getHttpStatus())
                .body(DefaultSingleResDto.singleDataBuilder()
                        .responseCode(PROFILE_UPDATED.name())
                        .responseMessage(PROFILE_UPDATED.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "파일 포트폴리오 업로드",
            notes = "<응답 코드>\n" +
                    "- 201 = PORTFOLIO_FILE_UPLOADED\n" +
                    "- 400 = FILE_FIELD_REQUIRED\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND\n" +
                    "- 413 = FILE_SIZE_EXCEED\n" +
                    "- 415 = FILE_TYPE_UNSUPPORTED\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "CREATED",
                    content = @Content(schema = @Schema(implementation = PortfolioUrlResDto.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "413", description = "PAYLOAD TOO LARGE"),
            @ApiResponse(responseCode = "415", description = "UNSUPPORTED MEDIA TYPE"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(value = "/portfolio/file",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultSingleResDto<Object>> uploadPortfolioFile(HttpServletRequest servletRequest,
                                                                    @RequestPart(value = "file", required = false)
                                                                    MultipartFile file) {
        long userId = jwtProvider.getId(servletRequest.getHeader(AUTHORIZATION));

        String portfolioUrl = profileService.uploadPortfolioFile(userId, file);

        PortfolioUrlResDto response = new PortfolioUrlResDto(portfolioUrl);

        return ResponseEntity.status(PORTFOLIO_FILE_UPLOADED.getHttpStatus())
                .body(DefaultSingleResDto.singleDataBuilder()
                        .responseCode(PORTFOLIO_FILE_UPLOADED.name())
                        .responseMessage(PORTFOLIO_FILE_UPLOADED.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "팀을 찾는 회원 다건 조회",
            notes = "<옵션>\n" +
                    "- position[default: NONE] = DESIGNER(디자이너) || BACKEND(백엔드) || FRONTEND(프론트엔드) || " +
                    "MANAGER(매니저) || NONE(전체)\n" +
                    "- profile-order[default: ACTIVE] = ACTIVE(활동순) || POPULARITY(인기순) || RATING(평점순)\n\n" +
                    "<검증>\n" +
                    "- position = NotBlank && Pattern(regex = ^(DESIGNER|BACKEND|FRONTEND|MANAGER|NONE))\n" +
                    "- profile-order = NotBlank && Pattern(regex = ^(ACTIVE|POPULARITY|RATING))\n" +
                    "- page-from = NotNull && PositiveOrZero\n" +
                    "- page-size = Positive\n\n" +
                    "<응답 코드>\n" +
                    "- 200 = USERS_SEEKING_TEAM_FOUND\n" +
                    "- 400 = POSITION_FIELD_REQUIRED || PROFILE_ORDER_FIELD_REQUIRED || PAGE_FROM_FIELD_REQUIRED || " +
                    "POSITION_TYPE_INVALID || PROFILE_ORDER_TYPE_INVALID || PAGE_FROM_POSITIVE_OR_ZERO_ONLY || " +
                    "|| PAGE_SIZE_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ProfileSeekResDto.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/seeking-team")
    public ResponseEntity<DefaultMultiResDto<Object>> findUsersLookingForTeam(
            HttpServletRequest servletRequest,
            @RequestParam(value = "position", required = false)
            @NotBlank(message = "포지션은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
            @Pattern(regexp = "^(DESIGNER|BACKEND|FRONTEND|MANAGER|NONE)",
                    message = "포지션은 'DESIGNER', 'BACKEND', 'FRONTEND', 'MANAGER', 또는 'NONE' 중 하나여야 됩니다.",
                    groups = ValidationSequence.Format.class)
            String position,
            @RequestParam(value = "profile-order", required = false)
            @NotBlank(message = "프로필 정렬 기준은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
            @Pattern(regexp = "^(ACTIVE|POPULARITY|RATING)",
                    message = "정렬 기준은 'ACTIVE', 'POPULARITY', 'RATING' 중 하나여야 됩니다.",
                    groups = ValidationSequence.Format.class)
            String profileOrder,
            @RequestParam(value = "page-from", required = false)
            @NotNull(message = "페이지 시작점은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
            @PositiveOrZero(message = "페이지 시작점은 0 또는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
            Integer pageFrom,
            @RequestParam(value = "page-size", required = false)
            @Positive(message = "페이지 사이즈는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
            Integer pageSize
    ) {
        long userId = jwtProvider.getId(servletRequest.getHeader(AUTHORIZATION));

        ProfileSeekPageDto response = profileService.findManyUsersByPositionWithProfileOrder(userId,
                Position.valueOf(position),
                ProfileOrder.valueOf(profileOrder),
                pageFrom,
                pageSize);

        return ResponseEntity.status(USERS_SEEKING_TEAM_FOUND.getHttpStatus())
                .body(DefaultMultiResDto.multiDataBuilder()
                        .responseCode(USERS_SEEKING_TEAM_FOUND.name())
                        .responseMessage(USERS_SEEKING_TEAM_FOUND.getMessage())
                        .data(response.getProfileSeekResDtos())
                        .size(response.getPageSize())
                        .build());
    }

    @ApiOperation(value = "팀 탈퇴",
            notes = "<응답 코드>\n" +
                    "- 200 = USER_LEFT_TEAM\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND || CURRENT_TEAM_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PatchMapping("/team/leave")
    public ResponseEntity<DefaultNoResDto> leaveTeam(HttpServletRequest servletRequest) {
        long userId = jwtProvider.getId(servletRequest.getHeader(AUTHORIZATION));

        teamService.leaveTeam(userId);

        return ResponseEntity.status(USER_LEFT_TEAM.getHttpStatus())
                .body(DefaultNoResDto.noDataBuilder()
                        .responseCode(USER_LEFT_TEAM.name())
                        .responseMessage(USER_LEFT_TEAM.getMessage())
                        .build());
    }
}
