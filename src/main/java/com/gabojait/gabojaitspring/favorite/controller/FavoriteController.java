package com.gabojait.gabojaitspring.favorite.controller;

import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.dto.DefaultResDto;
import com.gabojait.gabojaitspring.favorite.domain.FavoriteTeam;
import com.gabojait.gabojaitspring.favorite.domain.FavoriteUser;
import com.gabojait.gabojaitspring.favorite.dto.req.FavoriteUpdateReqDto;
import com.gabojait.gabojaitspring.favorite.service.FavoriteTeamService;
import com.gabojait.gabojaitspring.favorite.service.FavoriteUserService;
import com.gabojait.gabojaitspring.profile.dto.res.ProfileAbstractResDto;
import com.gabojait.gabojaitspring.team.dto.res.TeamAbstractResDto;
import com.gabojait.gabojaitspring.user.domain.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "찜")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FavoriteController {

    private final FavoriteTeamService favoriteTeamService;
    private final FavoriteUserService favoriteUserService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "회원이 찜한 팀 업데이트",
            notes = "<응답 코드>\n" +
                    "- 200 = FAVORITE_TEAM_UPDATED\n" +
                    "- 400 = TEAM_ID_FIELD_REQUIRED IS_ADD_FAVORITE_FIELD_REQUIRED || TEAM_ID_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = TEAM_NOT_FOUND\n" +
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
    @PatchMapping(value = "/user/favorite/team/{team-id}")
    public ResponseEntity<DefaultResDto<Object>> updateFavoriteTeam(
            HttpServletRequest servletRequest,
            @PathVariable(value = "team-id")
            @NotNull(message = "팀 식별자는 필수 입력입니다.")
            @Positive(message = "팀 식별자는 양수만 가능합니다.")
            Long teamId,
            @RequestBody
            @Valid
            FavoriteUpdateReqDto request
    ) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        favoriteTeamService.update(user, teamId, request.getIsAddFavorite());

        return ResponseEntity.status(FAVORITE_TEAM_UPDATED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(FAVORITE_TEAM_UPDATED.name())
                        .responseMessage(FAVORITE_TEAM_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "회원이 찜한 팀 다건 조회",
            notes = "<응답 코드>\n" +
                    "- 200 = FAVORITE_TEAMS_FOUND\n" +
                    "- 400 = PAGE_FROM_FIELD_REQUIRED || PAGE_FROM_POSITIVE_OR_ZERO_ONLY || PAGE_SIZE_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = TeamAbstractResDto.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/user/favorite/team")
    public ResponseEntity<DefaultResDto<Object>> findAllFavoriteTeams(
            HttpServletRequest servletRequest,
            @RequestParam(value = "page-from")
            @NotNull(message = "페이지 시작점은 필수 입력입니다.")
            @PositiveOrZero(message = "페이지 시작점은 0 또는 양수만 가능합니다.")
            Integer pageFrom,
            @RequestParam(value = "page-size", required = false)
            @Positive(message = "페이지 사이즈는 양수만 가능합니다.")
            Integer pageSize
    ) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        Page<FavoriteTeam> favoriteTeams = favoriteTeamService.findManyFavoriteTeams(user, pageFrom, pageSize);

        List<TeamAbstractResDto> responses = new ArrayList<>();
        for (FavoriteTeam favoriteTeam : favoriteTeams)
            responses.add(new TeamAbstractResDto(favoriteTeam.getTeam()));

        return ResponseEntity.status(FAVORITE_TEAMS_FOUND.getHttpStatus())
                .body(DefaultResDto.multiDataBuilder()
                        .responseCode(FAVORITE_TEAMS_FOUND.name())
                        .responseMessage(FAVORITE_TEAMS_FOUND.getMessage())
                        .data(responses)
                        .size(responses.size())
                        .build());
    }

    @ApiOperation(value = "팀의 회원 찜 업데이트",
            notes = "<응답 코드>\n" +
                    "- 200 = USER_FAVORITE_UPDATED\n" +
                    "- 400 = USER_ID_FIELD_REQUIRED || USER_ID_POSITIVE_ONLY || IS_ADD_FAVORITE_FIELD_REQUIRED\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED || REQUEST_FORBIDDEN\n" +
                    "- 404 = CURRENT_TEAM_NOT_FOUND || FAVORITE_USER_NOT_FOUND || USER_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "409", description = "CONFLICT"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PatchMapping("/team/favorite/user/{user-id}")
    public ResponseEntity<DefaultResDto<Object>> updateFavoriteUser(
            HttpServletRequest servletRequest,
            @PathVariable(value = "user-id")
            @NotNull(message = "팀 식별자는 필수 입력입니다.")
            @Positive(message = "팀 식별자는 양수만 가능합니다.")
            Long userId,
            @RequestBody
            @Valid
            FavoriteUpdateReqDto request
    ) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        favoriteUserService.update(user, userId, request.getIsAddFavorite());

        return ResponseEntity.status(FAVORITE_USER_UPDATED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(FAVORITE_USER_UPDATED.name())
                        .responseMessage(FAVORITE_USER_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "찜한 회원 전체 조회",
            notes = "<응답 코드>\n" +
                    "- 200 = FAVORITE_USERS_FOUND\n" +
                    "- 400 = PAGE_FROM_FIELD_REQUIRED || PAGE_FROM_POSITIVE_OR_ZERO_ONLY || PAGE_SIZE_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED || REQUEST_FORBIDDEN\n" +
                    "- 404 = CURRENT_TEAM_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ProfileAbstractResDto.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/team/favorite/user")
    public ResponseEntity<DefaultResDto<Object>> findAllFavoriteUsers(
            HttpServletRequest servletRequest,
            @RequestParam(value = "page-from")
            @NotNull(message = "페이지 시작점은 필수 입력입니다.")
            @PositiveOrZero(message = "페이지 시작점은 0 또는 양수만 가능합니다.")
            Integer pageFrom,
            @RequestParam(value = "page-size", required = false)
            @Positive(message = "페이지 사이즈는 양수만 가능합니다.")
            Integer pageSize
    ) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        Page<FavoriteUser> favoriteUsers = favoriteUserService.findManyFavoriteUsers(user, pageFrom, pageSize);

        List<ProfileAbstractResDto> responses = new ArrayList<>();
        for(FavoriteUser favoriteUser : favoriteUsers)
            responses.add(new ProfileAbstractResDto(favoriteUser.getUser()));

        return ResponseEntity.status(FAVORITE_USERS_FOUND.getHttpStatus())
                .body(DefaultResDto.multiDataBuilder()
                        .responseCode(FAVORITE_USERS_FOUND.name())
                        .responseMessage(FAVORITE_USERS_FOUND.getMessage())
                        .data(responses)
                        .size(responses.size())
                        .build());
    }
}