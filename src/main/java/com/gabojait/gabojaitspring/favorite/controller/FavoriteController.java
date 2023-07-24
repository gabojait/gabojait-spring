package com.gabojait.gabojaitspring.favorite.controller;

import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.dto.DefaultMultiResDto;
import com.gabojait.gabojaitspring.common.dto.DefaultNoResDto;
import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import com.gabojait.gabojaitspring.favorite.domain.FavoriteTeam;
import com.gabojait.gabojaitspring.favorite.domain.FavoriteUser;
import com.gabojait.gabojaitspring.favorite.dto.req.FavoriteUpdateReqDto;
import com.gabojait.gabojaitspring.favorite.service.FavoriteTeamService;
import com.gabojait.gabojaitspring.favorite.service.FavoriteUserService;
import com.gabojait.gabojaitspring.profile.dto.res.ProfileAbstractResDto;
import com.gabojait.gabojaitspring.team.dto.res.TeamAbstractResDto;
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
import javax.validation.GroupSequence;
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
@GroupSequence({FavoriteController.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FavoriteController {

    private final FavoriteTeamService favoriteTeamService;
    private final FavoriteUserService favoriteUserService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "회원이 팀 찜하기 및 찜 취소하기",
            notes = "<응답 코드>\n" +
                    "- 200 = FAVORITE_TEAM_DELETED\n" +
                    "- 201 = FAVORITE_TEAM_ADDED\n" +
                    "- 400 = TEAM_ID_FIELD_REQUIRED || IS_ADD_FAVORITE_FIELD_REQUIRED || TEAM_ID_POSITIVE_ONLY\n" +
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
    @PostMapping(value = "/user/favorite/team/{team-id}")
    public ResponseEntity<DefaultNoResDto> addOrDeleteFavoriteTeam(
            HttpServletRequest servletRequest,
            @PathVariable(value = "team-id", required = false)
            @NotNull(message = "팀 식별자는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
            @Positive(message = "팀 식별자는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
            Long teamId,
            @RequestBody @Valid
            FavoriteUpdateReqDto request
    ) {
        long userId = jwtProvider.getId(servletRequest.getHeader(AUTHORIZATION));

        favoriteTeamService.update(userId, teamId, request.getIsAddFavorite());

        if (request.getIsAddFavorite())
            return ResponseEntity.status(FAVORITE_TEAM_ADDED.getHttpStatus())
                    .body(DefaultNoResDto.noDataBuilder()
                            .responseCode(FAVORITE_TEAM_ADDED.name())
                            .responseMessage(FAVORITE_TEAM_ADDED.getMessage())
                            .build());
        else
            return ResponseEntity.status(FAVORITE_TEAM_DELETED.getHttpStatus())
                    .body(DefaultNoResDto.noDataBuilder()
                            .responseCode(FAVORITE_TEAM_DELETED.name())
                            .responseMessage(FAVORITE_TEAM_DELETED.getMessage())
                            .build());
    }

    @ApiOperation(value = "회원이 찜한 팀 다건 조회",
            notes = "<응답 코드>\n" +
                    "- 200 = FAVORITE_TEAMS_FOUND\n" +
                    "- 400 = PAGE_FROM_FIELD_REQUIRED || PAGE_FROM_POSITIVE_OR_ZERO_ONLY || PAGE_SIZE_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = TeamAbstractResDto.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/user/favorite/team")
    public ResponseEntity<DefaultMultiResDto<Object>> findAllFavoriteTeams(
            HttpServletRequest servletRequest,
            @RequestParam(value = "page-from", required = false)
            @NotNull(message = "페이지 시작점은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
            @PositiveOrZero(message = "페이지 시작점은 0 또는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
            Integer pageFrom,
            @RequestParam(value = "page-size", required = false)
            @Positive(message = "페이지 사이즈는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
            Integer pageSize
    ) {
        long userId = jwtProvider.getId(servletRequest.getHeader(AUTHORIZATION));

        Page<FavoriteTeam> favoriteTeams = favoriteTeamService.findManyFavoriteTeams(userId, pageFrom, pageSize);

        List<TeamAbstractResDto> responses = new ArrayList<>();
        for (FavoriteTeam favoriteTeam : favoriteTeams)
            responses.add(new TeamAbstractResDto(favoriteTeam.getTeam()));

        return ResponseEntity.status(FAVORITE_TEAMS_FOUND.getHttpStatus())
                .body(DefaultMultiResDto.multiDataBuilder()
                        .responseCode(FAVORITE_TEAMS_FOUND.name())
                        .responseMessage(FAVORITE_TEAMS_FOUND.getMessage())
                        .data(responses)
                        .size(favoriteTeams.getTotalElements())
                        .build());
    }

    @ApiOperation(value = "팀이 회원 찜하기 및 찜 취소하기",
            notes = "<응답 코드>\n" +
                    "- 200 = FAVORITE_USER_DELETED\n" +
                    "- 201 = FAVORITE_USER_ADDED\n" +
                    "- 400 = USER_ID_FIELD_REQUIRED || USER_ID_POSITIVE_ONLY || IS_ADD_FAVORITE_FIELD_REQUIRED\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED || REQUEST_FORBIDDEN\n" +
                    "- 404 = USER_NOT_FOUND || CURRENT_TEAM_NOT_FOUND || FAVORITE_USER_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "201", description = "CREATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "409", description = "CONFLICT"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PostMapping("/team/favorite/user/{user-id}")
    public ResponseEntity<DefaultNoResDto> addOrDeleteFavoriteUser(
            HttpServletRequest servletRequest,
            @PathVariable(value = "user-id", required = false)
            @NotNull(message = "회원 식별자는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
            @Positive(message = "회원 식별자는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
            Long userId,
            @RequestBody
            @Valid
            FavoriteUpdateReqDto request
    ) {
        long uId = jwtProvider.getId(servletRequest.getHeader(AUTHORIZATION));

        favoriteUserService.update(uId, userId, request.getIsAddFavorite());

        if (request.getIsAddFavorite())
            return ResponseEntity.status(FAVORITE_USER_ADDED.getHttpStatus())
                    .body(DefaultNoResDto.noDataBuilder()
                            .responseCode(FAVORITE_USER_ADDED.name())
                            .responseMessage(FAVORITE_USER_ADDED.getMessage())
                            .build());
        else
            return ResponseEntity.status(FAVORITE_USER_DELETED.getHttpStatus())
                    .body(DefaultNoResDto.noDataBuilder()
                            .responseCode(FAVORITE_USER_DELETED.name())
                            .responseMessage(FAVORITE_USER_DELETED.getMessage())
                            .build());
    }

    @ApiOperation(value = "찜한 회원 전체 조회",
            notes = "<응답 코드>\n" +
                    "- 200 = FAVORITE_USERS_FOUND\n" +
                    "- 400 = PAGE_FROM_FIELD_REQUIRED || PAGE_FROM_POSITIVE_OR_ZERO_ONLY || PAGE_SIZE_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED || REQUEST_FORBIDDEN\n" +
                    "- 404 = USER_NOT_FOUND || CURRENT_TEAM_NOT_FOUND\n" +
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
    public ResponseEntity<DefaultMultiResDto<Object>> findAllFavoriteUsers(
            HttpServletRequest servletRequest,
            @RequestParam(value = "page-from", required = false)
            @NotNull(message = "페이지 시작점은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
            @PositiveOrZero(message = "페이지 시작점은 0 또는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
            Integer pageFrom,
            @RequestParam(value = "page-size", required = false)
            @Positive(message = "페이지 사이즈는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
            Integer pageSize
    ) {
        long userId = jwtProvider.getId(servletRequest.getHeader(AUTHORIZATION));

        Page<FavoriteUser> favoriteUsers = favoriteUserService.findManyFavoriteUsers(userId, pageFrom, pageSize);

        List<ProfileAbstractResDto> responses = new ArrayList<>();
        for(FavoriteUser favoriteUser : favoriteUsers)
            responses.add(new ProfileAbstractResDto(favoriteUser.getUser()));

        return ResponseEntity.status(FAVORITE_USERS_FOUND.getHttpStatus())
                .body(DefaultMultiResDto.multiDataBuilder()
                        .responseCode(FAVORITE_USERS_FOUND.name())
                        .responseMessage(FAVORITE_USERS_FOUND.getMessage())
                        .data(responses)
                        .size(responses.size())
                        .build());
    }
}
