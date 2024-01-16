package com.gabojait.gabojaitspring.api.controller.favorite;

import com.gabojait.gabojaitspring.api.dto.common.response.DefaultMultiResponse;
import com.gabojait.gabojaitspring.api.dto.common.response.DefaultNoResponse;
import com.gabojait.gabojaitspring.api.dto.common.response.PageData;
import com.gabojait.gabojaitspring.api.dto.favorite.request.FavoriteUpdateRequest;
import com.gabojait.gabojaitspring.api.dto.favorite.response.FavoriteTeamPageResponse;
import com.gabojait.gabojaitspring.api.dto.favorite.response.FavoriteUserPageResponse;
import com.gabojait.gabojaitspring.api.service.favorite.FavoriteService;
import com.gabojait.gabojaitspring.auth.JwtProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import java.util.List;

import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "찜")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "회원 찜하기 및 찜 취소하기",
            notes = "<응답 코드>\n" +
                    "- 200 = FAVORITE_USER_DELETED\n" +
                    "- 201 = FAVORITE_USER_ADDED\n" +
                    "- 400 = IS_ADD_FAVORITE_FIELD_REQUIRED || USER_ID_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "201", description = "CREATED"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PostMapping(value = "/user/{user-id}")
    public ResponseEntity<DefaultNoResponse> updateFavoriteUser(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @PathVariable(value = "user-id")
            @Positive(message = "회원 식별자는 양수만 가능합니다.")
            Long userId,
            @RequestBody @Valid FavoriteUpdateRequest request
    ) {
        long myUserId = jwtProvider.getUserId(authorization);

        favoriteService.updateFavoriteUser(myUserId, userId, request);

        if (request.getIsAddFavorite())
            return ResponseEntity.status(FAVORITE_USER_ADDED.getHttpStatus())
                    .body(DefaultNoResponse.noDataBuilder()
                            .responseCode(FAVORITE_USER_ADDED.name())
                            .responseMessage(FAVORITE_USER_ADDED.getMessage())
                            .build());
        else
            return ResponseEntity.status(FAVORITE_USER_DELETED.getHttpStatus())
                    .body(DefaultNoResponse.noDataBuilder()
                            .responseCode(FAVORITE_USER_DELETED.name())
                            .responseMessage(FAVORITE_USER_DELETED.getMessage())
                            .build());
    }

    @ApiOperation(value = "팀 찜하기 및 찜 취소하기",
            notes = "<응답 코드>\n" +
                    "- 200 = FAVORITE_TEAM_DELETED\n" +
                    "- 201 = FAVORITE_TEAM_ADDED\n" +
                    "- 400 = IS_ADD_FAVORITE_FIELD_REQUIRED || TEAM_ID_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND || TEAM_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "201", description = "CREATED"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PostMapping(value = "/team/{team-id}")
    public ResponseEntity<DefaultNoResponse> updateFavoriteTeam(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @PathVariable(value = "team-id")
            @Positive(message = "팀 식별자는 양수만 가능합니다.")
            Long teamId,
            @RequestBody @Valid FavoriteUpdateRequest request
    ) {
        long userId = jwtProvider.getUserId(authorization);

        favoriteService.updateFavoriteTeam(userId, teamId, request);

        if (request.getIsAddFavorite())
            return ResponseEntity.status(FAVORITE_TEAM_ADDED.getHttpStatus())
                    .body(DefaultNoResponse.noDataBuilder()
                            .responseCode(FAVORITE_TEAM_ADDED.name())
                            .responseMessage(FAVORITE_TEAM_ADDED.getMessage())
                            .build());
        else
            return ResponseEntity.status(FAVORITE_TEAM_DELETED.getHttpStatus())
                    .body(DefaultNoResponse.noDataBuilder()
                            .responseCode(FAVORITE_TEAM_DELETED.name())
                            .responseMessage(FAVORITE_TEAM_DELETED.getMessage())
                            .build());
    }

    @ApiOperation(value = "찜한 회원 페이징 조회",
            notes = "<검증>\n" +
                    "- page-from[default: 9223372036854775806] = Positive\n" +
                    "- page-size[default: 20] = Positive && Max(value = 100)\n\n" +
                    "<응답 코드>\n" +
                    "- 200 = FAVORITE_USERS_FOUND\n" +
                    "- 400 = PAGE_FROM_POSITIVE_ONLY || PAGE_SIZE_POSITIVE_ONLY || PAGE_SIZE_RANGE_INVALID\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = FavoriteUserPageResponse.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/user")
    public ResponseEntity<DefaultMultiResponse<Object>> findPageFavoriteUser(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @RequestParam(value = "page-from", required = false, defaultValue = "9223372036854775806")
            @Positive(message = "페이지 시작점은 양수만 가능합니다.")
            Long pageFrom,
            @RequestParam(value = "page-size", required = false, defaultValue = "20")
            @Positive(message = "페이지 사이즈는 양수만 가능합니다.")
            @Max(value = 100, message = "페이지 사이즈는 100까지의 수만 가능합니다.")
            Integer pageSize
    ) {
        long userId = jwtProvider.getUserId(authorization);

        PageData<List<FavoriteUserPageResponse>> responses = favoriteService.findPageFavoriteUser(userId, pageFrom,
                pageSize);

        return ResponseEntity.status(FAVORITE_USERS_FOUND.getHttpStatus())
                .body(DefaultMultiResponse.multiDataBuilder()
                        .responseCode(FAVORITE_USERS_FOUND.name())
                        .responseMessage(FAVORITE_USERS_FOUND.getMessage())
                        .responseData(responses)
                        .build());
    }

    @ApiOperation(value = "찜한 팀 페이징 조회",
            notes = "<검증>\n" +
                    "- page-from[default: 9223372036854775806] = Positive\n" +
                    "- page-size[default: 20] = Positive && Max(value = 100)\n\n" +
                    "<응답 코드>\n" +
                    "- 200 = FAVORITE_TEAMS_FOUND\n" +
                    "- 400 = PAGE_FROM_POSITIVE_ONLY || PAGE_SIZE_POSITIVE_ONLY || PAGE_SIZE_RANGE_INVALID\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = FavoriteTeamPageResponse.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/team")
    public ResponseEntity<DefaultMultiResponse<Object>> findPageFavoriteTeam(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @RequestParam(value = "page-from", required = false, defaultValue = "9223372036854775806")
            @Positive(message = "페이지 시작점은 양수만 가능합니다.")
            Long pageFrom,
            @RequestParam(value = "page-size", required = false, defaultValue = "20")
            @Positive(message = "페이지 사이즈는 양수만 가능합니다.")
            @Max(value = 100, message = "페이지 사이즈는 100까지의 수만 가능합니다.")
            Integer pageSize
    ) {
        long userId = jwtProvider.getUserId(authorization);

        PageData<List<FavoriteTeamPageResponse>> responses = favoriteService.findPageFavoriteTeam(userId, pageFrom,
                pageSize);

        return ResponseEntity.status(FAVORITE_TEAMS_FOUND.getHttpStatus())
                .body(DefaultMultiResponse.multiDataBuilder()
                        .responseCode(FAVORITE_TEAMS_FOUND.name())
                        .responseMessage(FAVORITE_TEAMS_FOUND.getMessage())
                        .responseData(responses)
                        .build());
    }
}
