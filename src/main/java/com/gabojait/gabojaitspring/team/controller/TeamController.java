package com.gabojait.gabojaitspring.team.controller;

import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.dto.DefaultResDto;
import com.gabojait.gabojaitspring.profile.dto.res.ProfileAbstractResDto;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.team.dto.req.TeamCompleteUpdateReqDto;
import com.gabojait.gabojaitspring.team.dto.req.TeamDefaultReqDto;
import com.gabojait.gabojaitspring.team.dto.req.TeamFavoriteUpdateReqDto;
import com.gabojait.gabojaitspring.team.dto.req.TeamIsRecruitingUpdateReqDto;
import com.gabojait.gabojaitspring.team.dto.res.TeamDefaultResDto;
import com.gabojait.gabojaitspring.team.dto.res.TeamDetailResDto;
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
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "팀")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/team")
public class TeamController {

    private final TeamService teamService;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "팀 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "TEAM_CREATED",
                    content = @Content(schema = @Schema(implementation = TeamDefaultResDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "*_FIELD_REQUIRED / *_LENGTH_INVALID / OPEN_CHAT_URL_FORMAT_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN"),
            @ApiResponse(responseCode = "409",
                    description = "EXISTING_CURRENT_TEAM / NON_EXISTING_POSITION / TEAM_POSITION_UNAVAILABLE"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<DefaultResDto<Object>> createTeam(HttpServletRequest servletRequest,
                                                            @RequestBody @Valid TeamDefaultReqDto request) {
        // auth
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // sub
        userService.validatePreCreateTeam(user);
        // main
        Team team = teamService.create(request.toEntity(user.getId(), user.getPosition()), user);
        userService.joinTeam(user, team);

        // response
        TeamDefaultResDto response = new TeamDefaultResDto(team);

        return ResponseEntity.status(TEAM_CREATED.getHttpStatus())
                .body(DefaultResDto.singleDataBuilder()
                        .responseCode(TEAM_CREATED.name())
                        .responseMessage(TEAM_CREATED.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "팀 정보 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TEAM_UPDATED",
                    content = @Content(schema = @Schema(implementation = TeamDefaultResDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "*_FIELD_REQUIRED / *_LENGTH_INVALID / OPEN_CHAT_URL_FORMAT_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN / REQUEST_FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "409",
                    description = "NON_EXISTING_CURRENT_TEAM / DESIGNER_CNT_UPDATE_UNAVAILABLE / " +
                            "BACKEND_CNT_UPDATE_UNAVAILABLE / FRONTEND_CNT_UPDATE_UNAVAILABLE"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @PutMapping
    public ResponseEntity<DefaultResDto<Object>> updateTeam(HttpServletRequest servletRequest,
                                                            @RequestBody @Valid TeamDefaultReqDto request) {
        // auth
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // sub
        userService.hasCurrentTeam(user);
        // main
        Team team = teamService.update(request, user);

        // response
        TeamDefaultResDto response = new TeamDefaultResDto(team);

        return ResponseEntity.status(TEAM_UPDATED.getHttpStatus())
                .body(DefaultResDto.singleDataBuilder()
                        .responseCode(TEAM_UPDATED.name())
                        .responseMessage(TEAM_UPDATED.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "팀 단건 조회", notes = "* team-id = NotBlank")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TEAM_FOUND",
                    content = @Content(schema = @Schema(implementation = TeamDetailResDto.class))),
            @ApiResponse(responseCode = "400", description = "TEAM_ID_FIELD_REQUIRED"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @GetMapping("/{team-id}")
    public ResponseEntity<DefaultResDto<Object>> findOneTeam(HttpServletRequest servletRequest,
                                                             @PathVariable(value = "team-id")
                                                             @NotBlank(message = "팀 식별자를 입력해 주세요.")
                                                             String teamId) {
        // auth
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // main
        Team team = teamService.findOther(teamId, user);
        Boolean isFavorite = userService.isFavoriteTeam(user, team.getId());

        // response
        TeamDetailResDto response = new TeamDetailResDto(team, isFavorite);

        return ResponseEntity.status(TEAM_FOUND.getHttpStatus())
                .body(DefaultResDto.singleDataBuilder()
                        .responseCode(TEAM_FOUND.name())
                        .responseMessage(TEAM_FOUND.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "팀원을 찾는 팀 다건 조회",
            notes = "* position = NotBlank && Pattern(regex = ^(designer|backend|frontend|manager|none))\n" +
                    "* team-order = NotBlank && Pattern(regex = ^(created|active|popularity))\n" +
                    "* page-from = NotNull && PositiveOrZero\n" +
                    "* page-size = Positive")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TEAMS_FINDING_USERS_FOUND",
                    content = @Content(schema = @Schema(implementation = TeamDefaultResDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "*_FIELD_REQUIRED / *_TYPE_INVALID / PAGE_FROM_POSITIVE_OR_ZER_ONLY / " +
                            "PAGE_SIZE_POSITIVE_ONLY"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @GetMapping("/recruiting")
    public ResponseEntity<DefaultResDto<Object>> findTeamsLookingForUsers(
            HttpServletRequest servletRequest,
            @RequestParam(value = "position")
            @NotBlank(message = "포지션을 입력해 주세요.")
            @Pattern(regexp = "^(designer|backend|frontend|manager|none)",
                    message = "포지션은 'designer', 'backend', 'frontend', 'manager', 또는 'none' 중 하나여야 됩니다.")
            String position,
            @RequestParam(value = "team-order")
            @NotBlank(message = "팀 정렬 기준을 입력해 주세요.")
            @Pattern(regexp = "^(created|active|popularity)",
                    message = "팀 정렬 기준은 'created', 'active', 또는 'popularity', 중 하나여야 됩니다.")
            String teamOrder,
            @RequestParam(value = "page-from")
            @NotNull(message = "페이지 시작점을 입력해 주세요.")
            @PositiveOrZero(message = "페이지 시작점은 0 또는 양수만 가능합니다.")
            Integer pageFrom,
            @RequestParam(value = "page-size", required = false)
            @Positive(message = "페이지 사이즈는 양수만 가능합니다.")
            Integer pageSize
    ) {
        // auth
        jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // main
        Page<Team> teams = teamService.findPagePositionOrder(position, teamOrder, pageFrom, pageSize);

        // response
        List<TeamDefaultResDto> responses = new ArrayList<>();
        for (Team team : teams)
            responses.add(new TeamDefaultResDto(team));

        return ResponseEntity.status(TEAMS_FINDING_USERS_FOUND.getHttpStatus())
                .body(DefaultResDto.multiDataBuilder()
                        .responseCode(TEAMS_FINDING_USERS_FOUND.name())
                        .responseMessage(TEAMS_FINDING_USERS_FOUND.getMessage())
                        .data(responses)
                        .size(teams.getTotalPages())
                        .build());
    }

    @ApiOperation(value = "팀원 모집 여부 업데이트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TEAM_IS_RECRUITING_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "IS_RECRUITING_FIELD_REQUIRED"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN / REQUEST_FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "409", description = "NON_EXISTING_CURRENT_TEAM"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @PatchMapping("/recruiting")
    public ResponseEntity<DefaultResDto<Object>> updateIsRecruiting(HttpServletRequest servletRequest,
                                                                    @RequestBody @Valid
                                                                    TeamIsRecruitingUpdateReqDto request) {
        // auth
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // sub
        userService.hasCurrentTeam(user);
        // main
        teamService.updateIsRecruiting(user, request.getIsRecruiting());

        return ResponseEntity.status(TEAM_IS_RECRUITING_UPDATED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(TEAM_IS_RECRUITING_UPDATED.name())
                        .responseMessage(TEAM_IS_RECRUITING_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "프로젝트 미완료 종료")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PROJECT_INCOMPLETE",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "IS_RECRUITING_FIELD_REQUIRED"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN / REQUEST_FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "409", description = "NON_EXISTING_CURRENT_TEAM"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @DeleteMapping("/incomplete")
    public ResponseEntity<DefaultResDto<Object>> projectIncomplete(HttpServletRequest servletRequest) {
        // auth
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // sub
        userService.hasCurrentTeam(user);
        // main
        List<User> teamMembers = teamService.quit(user, "");
        userService.exitCurrentTeam(teamMembers, false);

        return ResponseEntity.status(PROJECT_INCOMPLETE.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(PROJECT_INCOMPLETE.name())
                        .responseMessage(PROJECT_INCOMPLETE.getMessage())
                        .build());
    }

    @ApiOperation(value = "프로젝트 완료 종료")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PROJECT_COMPLETE",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "PROJECT_URL_FIELD_REQUIRED"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN / REQUEST_FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "409", description = "NON_EXISTING_CURRENT_TEAM"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")

    })
    @DeleteMapping("/complete")
    public ResponseEntity<DefaultResDto<Object>> quitCompleteProject(HttpServletRequest servletRequest,
                                                                     @RequestBody @Valid
                                                                     TeamCompleteUpdateReqDto request) {
        // auth
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // sub
        userService.hasCurrentTeam(user);
        // main
        List<User> teamMembers = teamService.quit(user, request.getProjectUrl());
        userService.exitCurrentTeam(teamMembers, true);
        // TODO set FCM for review

        return ResponseEntity.status(PROJECT_COMPLETE.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(PROJECT_COMPLETE.name())
                        .responseMessage(PROJECT_COMPLETE.getMessage())
                        .build());
    }

    @ApiOperation(value = "팀원 추방", notes = "* user-id = NotBlank")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TEAMMATE_FIRED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "USER_ID_FIELD_REQUIRED"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN / REQUEST_FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "409", description = "NON_EXISTING_CURRENT_TEAM"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @PatchMapping("/user/{user-id}/fire")
    public ResponseEntity<DefaultResDto<Object>> fireTeammate(HttpServletRequest servletRequest,
                                                              @PathVariable(value = "user-id")
                                                              @NotBlank(message = "회원 식별자를 입력해 주세요.")
                                                              String userId) {
        // auth
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // sub
        User teammate = userService.findOne(userId);
        userService.hasCurrentTeam(user);
        // main
        teamService.fire(user, teammate);
        userService.exitCurrentTeam(List.of(teammate), false);

        return ResponseEntity.status(TEAMMATE_FIRED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(TEAMMATE_FIRED.name())
                        .responseMessage(TEAMMATE_FIRED.getMessage())
                        .build());
    }

    @ApiOperation(value = "팀의 회원 찜 업데이트", notes = "* user-id = NotBlank")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "USER_FAVORITE_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "*_FIELD_REQUIRED"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN / REQUEST_FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "409", description = "NON_EXISTING_CURRENT_TEAM"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @PatchMapping("/user/{user-id}/favorite")
    public ResponseEntity<DefaultResDto<Object>> updateFavoriteUser(
            HttpServletRequest servletRequest,
            @PathVariable(value = "user-id")
            @NotBlank(message = "회원 식별자를 입력해 주세요.")
            String userId,
            @RequestBody
            @Valid
            TeamFavoriteUpdateReqDto request
    ) {
        // auth
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // sub
        User otherUser = userService.findOne(userId);
        userService.hasCurrentTeam(user);
        // main
        teamService.updateFavoriteUser(user, otherUser, request.getIsAddFavorite());

        return ResponseEntity.status(USER_FAVORITE_UPDATED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(USER_FAVORITE_UPDATED.name())
                        .responseMessage(USER_FAVORITE_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "찜한 회원 전체 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FAVORITE_USERS_FOUND",
                    content = @Content(schema = @Schema(implementation = ProfileAbstractResDto.class))),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN / REQUEST_FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "409", description = "NON_EXISTING_CURRENT_TEAM"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @GetMapping("/user/favorite")
    public ResponseEntity<DefaultResDto<Object>> findAllFavoriteUsers(HttpServletRequest servletRequest) {
        // auth
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // sub
        userService.hasCurrentTeam(user);
        // main
        List<ObjectId> favoriteUserIds = teamService.findAllFavorite(user);
        List<User> users = userService.findAllId(favoriteUserIds);

        // response
        List<ProfileAbstractResDto> responses = new ArrayList<>();
        for (User u : users)
            responses.add(new ProfileAbstractResDto(u));

        return ResponseEntity.status(FAVORITE_USERS_FOUND.getHttpStatus())
                .body(DefaultResDto.multiDataBuilder()
                        .responseCode(FAVORITE_USERS_FOUND.name())
                        .responseMessage(FAVORITE_USERS_FOUND.getMessage())
                        .data(responses)
                        .size(responses.size() > 0 ? 1 : 0)
                        .build());
    }
}
