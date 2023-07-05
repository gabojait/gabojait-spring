package com.gabojait.gabojaitspring.profile.controller;

import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.dto.DefaultResDto;
import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import com.gabojait.gabojaitspring.favorite.service.FavoriteUserService;
import com.gabojait.gabojaitspring.profile.dto.ProfileSeekPageDto;
import com.gabojait.gabojaitspring.profile.dto.req.*;
import com.gabojait.gabojaitspring.profile.dto.res.*;
import com.gabojait.gabojaitspring.profile.service.EducationAndWorkService;
import com.gabojait.gabojaitspring.profile.service.PortfolioService;
import com.gabojait.gabojaitspring.profile.service.PositionAndSkillService;
import com.gabojait.gabojaitspring.team.service.TeamService;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.GroupSequence;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

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

    private final UserService userService;
    private final PositionAndSkillService positionAndSkillService;
    private final EducationAndWorkService educationAndWorkService;
    private final PortfolioService portfolioService;
    private final TeamService teamService;
    private final FavoriteUserService favoriteUserService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "본인 프로필 조회",
            notes = "<응답 코드>\n" +
                    "- 200 = SELF_PROFILE_FOUND\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = TEAM_NOT_FOUND\n" +
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
    @GetMapping("/profile")
    public ResponseEntity<DefaultResDto<Object>> findMyself(HttpServletRequest servletRequest) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        ProfileDefaultResDto response = new ProfileDefaultResDto(user);

        return ResponseEntity.status(SELF_PROFILE_FOUND.getHttpStatus())
                .body(DefaultResDto.singleDataBuilder()
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
    public ResponseEntity<DefaultResDto<Object>> findOther(
            HttpServletRequest servletRequest,
            @PathVariable(value = "user-id", required = false)
            @NotNull(message = "회원 식별자는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
            @Positive(message = "회원 식별자는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
            Long userId
    ) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        ProfileOfferAndFavoriteResDto response = userService.findOneOtherProfile(userId, user);

        return ResponseEntity.status(PROFILE_FOUND.getHttpStatus())
                .body(DefaultResDto.singleDataBuilder()
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
            @ApiResponse(responseCode = "413", description = "PAYLOAD TOO LARGE"),
            @ApiResponse(responseCode = "415", description = "UNSUPPORTED MEDIA TYPE"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DefaultResDto<Object>> uploadProfileImage(HttpServletRequest servletRequest,
                                                                    @RequestPart(value = "image", required = false)
                                                                    MultipartFile image) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        userService.uploadProfileImage(user, image);

        ProfileDefaultResDto response = new ProfileDefaultResDto(user);

        return ResponseEntity.status(PROFILE_IMAGE_UPLOADED.getHttpStatus())
                .body(DefaultResDto.singleDataBuilder()
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
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ProfileDefaultResDto.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @DeleteMapping("/image")
    public ResponseEntity<DefaultResDto<Object>> deleteProfileImage(HttpServletRequest servletRequest) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        userService.deleteProfileImage(user);

        ProfileDefaultResDto response = new ProfileDefaultResDto(user);

        return ResponseEntity.status(PROFILE_IMAGE_DELETED.getHttpStatus())
                .body(DefaultResDto.singleDataBuilder()
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
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PatchMapping("/seeking-team")
    public ResponseEntity<DefaultResDto<Object>> updateIsSeekingTeam(HttpServletRequest servletRequest,
                                                                     @RequestBody @Valid
                                                                     ProfileIsSeekingTeamUpdateReqDto request) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        userService.updateIsSeekingTeam(user, request.getIsSeekingTeam());

        return ResponseEntity.status(PROFILE_SEEKING_TEAM_UPDATED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
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
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = SERVICE UNAVAILABLE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PatchMapping("/description")
    public ResponseEntity<DefaultResDto<Object>> updateDescription(HttpServletRequest servletRequest,
                                                                   @RequestBody @Valid
                                                                   ProfileDescriptionUpdateReqDto request) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        userService.updateProfileDescription(user, request.getProfileDescription());

        return ResponseEntity.status(PROFILE_DESCRIPTION_UPDATED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(PROFILE_DESCRIPTION_UPDATED.name())
                        .responseMessage(PROFILE_DESCRIPTION_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "포지션과 기술 생성, 수정, 삭제",
            notes = "<응답 코드>\n" +
                    "- 200 = POSITION_AND_SKILL_UPDATED\n" +
                    "- 400 = POSITION_FIELD_REQUIRED || SKILL_ID_FIELD_REQUIRED || SKILL_NAME_FIELD_REQUIRED || " +
                    "IS_EXPERIENCED_FIELD_REQUIRED || LEVEL_FIELD_REQUIRED || SKILL_NAME_LENGTH_INVALID || " +
                    "SKILL_ID_POSITIVE_ONLY || POSITION_TYPE_INVALID || LEVEL_TYPE_INVALID\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = SKILL_NOT_FOUND\n" +
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
    @PostMapping("/position-and-skill")
    public ResponseEntity<DefaultResDto<Object>> updatePositionAndSkill(HttpServletRequest servletRequest,
                                                                        @RequestBody @Valid
                                                                        PositionAndSkillDefaultReqDto request) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        positionAndSkillService.createUpdateDelete(user, request);

        return ResponseEntity.status(POSITION_AND_SKILL_UPDATED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(POSITION_AND_SKILL_UPDATED.name())
                        .responseMessage(POSITION_AND_SKILL_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "학력과 경력 생성, 수정, 삭제",
            notes = "<응답 코드>\n" +
                    "- 200 = EDUCATION_AND_WORK_UPDATED\n" +
                    "- 400 = EDUCATION_ID_FIELD_REQUIRED || INSTITUTION_NAME_FIELD_REQUIRED || " +
                    "STARTED_AT_FIELD_REQUIRED || ENDED_AT_FIELD_REQUIRED || IS_CURRENT_FIELD_REQUIRED || " +
                    "WORK_ID_FIELD_REQUIRED || CORPORATION_NAME_FIELD_REQUIRED || INSTITUTION_NAME_LENGTH_INVALID " +
                    "CORPORATION_NAME_LENGTH_INVALID || WORK_DESCRIPTION_LENGTH_INVALID || " +
                    "EDUCATION_ID_POSITIVE_ONLY || WORK_ID_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = EDUCATION_NOT_FOUND || WORK_NOT_FOUND\n" +
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
    @PostMapping("/education-and-work")
    public ResponseEntity<DefaultResDto<Object>> updateEducationAndWork(HttpServletRequest servletRequest,
                                                                        @RequestBody @Valid
                                                                        EducationAndWorkDefaultReqDto request) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        educationAndWorkService.createUpdateDelete(user, request);

        return ResponseEntity.status(EDUCATION_AND_WORK_UPDATED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(EDUCATION_AND_WORK_UPDATED.name())
                        .responseMessage(EDUCATION_AND_WORK_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "링크 포트폴리오 생성, 수정, 삭제",
            notes = "<응답 코드>\n" +
                    "- 200 = LINK_PORTFOLIO_UPDATED\n" +
                    "- 400 = PORTFOLIO_ID_FIELD_REQUIRED || PORTFOLIO_NAME_FIELD_REQUIRED || " +
                    "PORTFOLIO_URL_FIELD_REQUIRED || PORTFOLIO_NAME_LENGTH_INVALID || " +
                    "PORTFOLIO_URL_LENGTH_INVALID || PORTFOLIO_ID_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = PORTFOLIO_NOT_FOUND\n" +
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
    @PostMapping("/portfolio/link")
    public ResponseEntity<DefaultResDto<Object>> updateLinkPortfolio(HttpServletRequest servletRequest,
                                                                     @RequestBody @Valid
                                                                     PortfolioLinkDefaultReqDto request) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        portfolioService.createUpdateDeleteLink(user, request);

        return ResponseEntity.status(LINK_PORTFOLIO_UPDATED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(LINK_PORTFOLIO_UPDATED.name())
                        .responseMessage(LINK_PORTFOLIO_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "파일 포트폴리오 생성, 수정, 삭제",
            notes = "<응답 코드>\n" +
                    "- 200 = FILE_PORTFOLIO_UPDATED\n" +
                    "- 400 = PORTFOLIO_NAME_LENGTH_INVALID || CREATE_PORTFOLIO_CNT_MATCH_INVALID || " +
                    "UPDATE_PORTFOLIO_CNT_MATCH_INVALID || FILE_FIELD_REQUIRED || PORTFOLIO_ID_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED || REQUEST_FORBIDDEN\n" +
                    "- 404 = PORTFOLIO_NOT_FOUND\n" +
                    "- 413 = FILE_SIZE_EXCEED || FILE_COUNT_EXCEED\n" +
                    "- 415 = FILE_TYPE_UNSUPPORTED\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "413", description = "PAYLOAD TOO LARGE"),
            @ApiResponse(responseCode = "415", description = "UNSUPPORTED MEDIA TYPE"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PostMapping(value = "/portfolio/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DefaultResDto<Object>> updateFilePortfolio(
            HttpServletRequest servletRequest,
            @RequestParam(value = "create-portfolio-names", required = false)
            List<String> createPortfolioNames,
            @RequestPart(name = "create-portfolio-files", required = false)
            List<MultipartFile> createPortfolioFiles,
            @RequestParam(value = "update-portfolio-ids", required = false)
            List<Long> updatePortfolioIds,
            @RequestParam(value = "update-portfolio-names", required = false)
            List<String> updatePortfolioNames,
            @RequestPart(name = "update-portfolio-files", required = false)
            List<MultipartFile> updatePortfolioFiles,
            @RequestParam(value = "delete-portfolio-ids", required = false)
            List<Long> deletePortfolioIds
    ) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        portfolioService.createUpdateDeleteFile(user,
                createPortfolioNames,
                createPortfolioFiles,
                updatePortfolioIds,
                updatePortfolioNames,
                updatePortfolioFiles,
                deletePortfolioIds);

        return ResponseEntity.status(FILE_PORTFOLIO_UPDATED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(FILE_PORTFOLIO_UPDATED.name())
                        .responseMessage(FILE_PORTFOLIO_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "팀을 찾는 회원 다건 조회",
            notes = "<옵션>\n" +
                    "- position[default: none] = designer(디자이너만) || backend(백엔드) || frontend(프론트엔드만) || " +
                    "manager(매니저만) || none(전체)\n" +
                    "- profile-order[default: active] = active(활동순) || popularity(인기순) || rating(평점순)\n\n" +
                    "<검증>\n" +
                    "- position = NotBlank && Pattern(regex = ^(designer|backend|frontend|manager|none))\n" +
                    "- profile-order = NotBlank && Pattern(regex = ^(active|popularity|rating))\n" +
                    "- page-from = NotNull && PositiveOrZero\n" +
                    "- page-size = Positive\n\n" +
                    "<응답 코드>\n" +
                    "- 200 = USERS_SEEKING_TEAM_FOUND\n" +
                    "- 400 = POSITION_FIELD_REQUIRED || PROFILE_ORDER_FIELD_REQUIRED || PAGE_FROM_FIELD_REQUIRED || " +
                    "POSITION_TYPE_INVALID || PROFILE_ORDER_TYPE_INVALID || PAGE_FROM_POSITIVE_OR_ZERO_ONLY || " +
                    "|| PAGE_SIZE_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ProfileSeekResDto.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/seeking-team")
    public ResponseEntity<DefaultResDto<Object>> findUsersLookingForTeam(
            HttpServletRequest servletRequest,
            @RequestParam(value = "position", required = false)
            @NotBlank(message = "포지션은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
            @Pattern(regexp = "^(designer|backend|frontend|manager|none)",
                    message = "포지션은 'designer', 'backend', 'frontend', 'manager', 또는 'none' 중 하나여야 됩니다.",
                    groups = ValidationSequence.Format.class)
            String position,
            @RequestParam(value = "profile-order", required = false)
            @NotBlank(message = "프로필 정렬 기준은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
            @Pattern(regexp = "^(active|popularity|rating)",
                    message = "정렬 기준은 'active', 'popularity', 'rating' 중 하나여야 됩니다.",
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
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        ProfileSeekPageDto response = userService.findManyUsersByPositionWithProfileOrder(position,
                profileOrder,
                pageFrom,
                pageSize,
                user);

        return ResponseEntity.status(USERS_SEEKING_TEAM_FOUND.getHttpStatus())
                .body(DefaultResDto.multiDataBuilder()
                        .responseCode(USERS_SEEKING_TEAM_FOUND.name())
                        .responseMessage(USERS_SEEKING_TEAM_FOUND.getMessage())
                        .data(response.getProfileSeekResDtos())
                        .size(response.getTotalPages())
                        .build());
    }

    @ApiOperation(value = "팀 탈퇴",
            notes = "<응답 코드>\n" +
                    "- 200 = USER_LEFT_TEAM\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = CURRENT_TEAM_NOT_FOUND" +
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
    public ResponseEntity<DefaultResDto<Object>> leaveTeam(HttpServletRequest servletRequest) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        teamService.leaveTeam(user);

        return ResponseEntity.status(USER_LEFT_TEAM.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(USER_LEFT_TEAM.name())
                        .responseMessage(USER_LEFT_TEAM.getMessage())
                        .build());
    }
}
