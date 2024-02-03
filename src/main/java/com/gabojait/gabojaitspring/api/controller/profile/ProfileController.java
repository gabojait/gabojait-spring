package com.gabojait.gabojaitspring.api.controller.profile;

import com.gabojait.gabojaitspring.common.dto.response.DefaultMultiResponse;
import com.gabojait.gabojaitspring.common.dto.response.DefaultNoResponse;
import com.gabojait.gabojaitspring.common.dto.response.DefaultSingleResponse;
import com.gabojait.gabojaitspring.common.dto.response.PageData;
import com.gabojait.gabojaitspring.api.dto.profile.request.ProfileDescriptionRequest;
import com.gabojait.gabojaitspring.api.dto.profile.request.ProfileIsSeekRequest;
import com.gabojait.gabojaitspring.api.dto.profile.request.ProfileUpdateRequest;
import com.gabojait.gabojaitspring.api.dto.profile.response.*;
import com.gabojait.gabojaitspring.api.service.profile.ProfileService;
import com.gabojait.gabojaitspring.config.auth.JwtProvider;
import com.gabojait.gabojaitspring.domain.user.Position;
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

import javax.validation.Valid;
import javax.validation.constraints.*;

import java.util.List;

import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "프로필")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class ProfileController {

    private final ProfileService profileService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "본인 프로필 조회",
            notes = "<응답 코드>\n" +
                    "- 200 = SELF_PROFILE_FOUND\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ProfileFindMyselfResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/profile")
    public ResponseEntity<DefaultSingleResponse<Object>> findMyself(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization
    ) {
        long userId = jwtProvider.getUserId(authorization);

        ProfileFindMyselfResponse response = profileService.findMyProfile(userId);

        return ResponseEntity.status(SELF_PROFILE_FOUND.getHttpStatus())
                .body(DefaultSingleResponse.singleDataBuilder()
                        .responseCode(SELF_PROFILE_FOUND.name())
                        .responseMessage(SELF_PROFILE_FOUND.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "프로필 단건 조회",
            notes = "<검증>\n" +
                    "- user-id = Positive\n\n" +
                    "<응답 코드>\n" +
                    "- 200 = PROFILE_FOUND\n" +
                    "- 400 = USER_ID_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ProfileFindOtherResponse.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/{user-id}/profile")
    public ResponseEntity<DefaultSingleResponse<Object>> findOther(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @PathVariable(value = "user-id")
            @Positive(message = "회원 식별자는 양수만 가능합니다.")
            Long userId
    ) {
        long myUserId = jwtProvider.getUserId(authorization);

        ProfileFindOtherResponse response = profileService.findOtherProfile(myUserId, userId);

        return ResponseEntity.status(PROFILE_FOUND.getHttpStatus())
                .body(DefaultSingleResponse.singleDataBuilder()
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
                    content = @Content(schema = @Schema(implementation = ProfileImageResponse.class))),
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
    public ResponseEntity<DefaultSingleResponse<Object>> uploadProfileImage(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @RequestPart(value = "image", required = false)
            MultipartFile image
    ) {
        long userId = jwtProvider.getUserId(authorization);

        ProfileImageResponse response = profileService.uploadProfileImage(userId, image);

        return ResponseEntity.status(PROFILE_IMAGE_UPLOADED.getHttpStatus())
                .body(DefaultSingleResponse.singleDataBuilder()
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
                    content = @Content(schema = @Schema(implementation = ProfileImageResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @DeleteMapping("/image")
    public ResponseEntity<DefaultSingleResponse<Object>> deleteProfileImage(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization
    ) {
        long userId = jwtProvider.getUserId(authorization);

        ProfileImageResponse response = profileService.deleteProfileImage(userId);

        return ResponseEntity.status(PROFILE_IMAGE_DELETED.getHttpStatus())
                .body(DefaultSingleResponse.singleDataBuilder()
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
    public ResponseEntity<DefaultNoResponse> updateIsSeekingTeam(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @RequestBody @Valid ProfileIsSeekRequest request
    ) {
        long userId = jwtProvider.getUserId(authorization);

        profileService.updateIsSeekingTeam(userId, request.getIsSeekingTeam());

        return ResponseEntity.status(PROFILE_SEEKING_TEAM_UPDATED.getHttpStatus())
                .body(DefaultNoResponse.noDataBuilder()
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
    public ResponseEntity<DefaultNoResponse> updateDescription(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @RequestBody @Valid ProfileDescriptionRequest request
    ) {
        long userId = jwtProvider.getUserId(authorization);

        profileService.updateProfileDescription(userId, request.getProfileDescription());

        return ResponseEntity.status(PROFILE_DESCRIPTION_UPDATED.getHttpStatus())
                .body(DefaultNoResponse.noDataBuilder()
                        .responseCode(PROFILE_DESCRIPTION_UPDATED.name())
                        .responseMessage(PROFILE_DESCRIPTION_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "프로필 업데이트",
            notes = "<응답 코드>\n" +
                    "- 200 = PROFILE_UPDATED\n" +
                    "- 400 = POSITION_TYPE_INVALID || SKILL_NAME_LENGTH_INVALID || IS_EXPERIENCED_FIELD_REQUIRED || " +
                    "LEVEL_FIELD_REQUIRED || LEVEL_TYPE_INVALID || INSTITUTION_NAME_LENGTH_INVALID || " +
                    "STARTED_AT_FIELD_REQUIRED || IS_CURRENT_FIELD_REQUIRED || CORPORATION_NAME_LENGTH_INVALID || " +
                    "WORK_DESCRIPTION_LENGTH_INVALID || PORTFOLIO_NAME_LENGTH_INVALID || " +
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
                    content = @Content(schema = @Schema(implementation = ProfileUpdateResponse.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PostMapping("/profile")
    public ResponseEntity<DefaultSingleResponse<Object>> updateProfile(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @RequestBody @Valid ProfileUpdateRequest request
    ) {
        long userId = jwtProvider.getUserId(authorization);

        ProfileUpdateResponse response = profileService.updateProfile(userId, request);

        return ResponseEntity.status(PROFILE_UPDATED.getHttpStatus())
                .body(DefaultSingleResponse.singleDataBuilder()
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
                    content = @Content(schema = @Schema(implementation = PortfolioUrlResponse.class))),
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
    public ResponseEntity<DefaultSingleResponse<Object>> uploadPortfolioFile(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @RequestPart(value = "file", required = false)
            MultipartFile file
    ) {
        long userId = jwtProvider.getUserId(authorization);

        PortfolioUrlResponse response = profileService.uploadPortfolioFile(userId, file);

        return ResponseEntity.status(PORTFOLIO_FILE_UPLOADED.getHttpStatus())
                .body(DefaultSingleResponse.singleDataBuilder()
                        .responseCode(PORTFOLIO_FILE_UPLOADED.name())
                        .responseMessage(PORTFOLIO_FILE_UPLOADED.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "팀을 찾는 회원 페이징 조회",
            notes = "<검증>\n" +
                    "- position[default: NONE] = NotBlank && Pattern(regex = ^(DESIGNER|BACKEND|FRONTEND|MANAGER|NONE))\n" +
                    "- page-from[default: 9223372036854775806] = Positive\n" +
                    "- page-size[default: 20] = Positive && Max(value = 100)\n\n" +
                    "<응답 코드>\n" +
                    "- 200 = USERS_SEEKING_TEAM_FOUND\n" +
                    "- 400 = POSITION_TYPE_INVALID || PAGE_FROM_POSITIVE_ONLY || PAGE_SIZE_POSITIVE_ONLY || " +
                    "PAGE_SIZE_RANGE_INVALID\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ProfilePageResponse.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/seeking-team")
    public ResponseEntity<DefaultMultiResponse<Object>> findUsersLookingForTeam(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @RequestParam(value = "position", required = false, defaultValue = "NONE")
            @Pattern(regexp = "^(DESIGNER|BACKEND|FRONTEND|MANAGER|NONE)",
                    message = "포지션은 'DESIGNER', 'BACKEND', 'FRONTEND', 'MANAGER', 또는 'NONE' 중 하나여야 됩니다.")
            String position,
            @RequestParam(value = "page-from", required = false, defaultValue = "9223372036854775806")
            @Positive(message = "페이지 시작점은 양수만 가능합니다.")
            Long pageFrom,
            @RequestParam(value = "page-size", required = false, defaultValue = "20")
            @Positive(message = "페이지 사이즈는 양수만 가능합니다.")
            @Max(value = 100, message = "페이지 사이즈는 100까지의 수만 가능합니다.")
            Integer pageSize
    ) {
        long userId = jwtProvider.getUserId(authorization);

        PageData<List<ProfilePageResponse>> responses = profileService.findPageUser(userId, Position.valueOf(position),
                 pageFrom, pageSize);

        return ResponseEntity.status(USERS_SEEKING_TEAM_FOUND.getHttpStatus())
                .body(DefaultMultiResponse.multiDataBuilder()
                        .responseCode(USERS_SEEKING_TEAM_FOUND.name())
                        .responseMessage(USERS_SEEKING_TEAM_FOUND.getMessage())
                        .responseData(responses)
                        .build());
    }
}
