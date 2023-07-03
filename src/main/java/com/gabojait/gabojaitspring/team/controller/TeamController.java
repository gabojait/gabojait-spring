package com.gabojait.gabojaitspring.team.controller;

import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.dto.DefaultResDto;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.team.dto.req.TeamCompleteReqDto;
import com.gabojait.gabojaitspring.team.dto.req.TeamDefaultReqDto;
import com.gabojait.gabojaitspring.team.dto.req.TeamIsRecruitingUpdateReqDto;
import com.gabojait.gabojaitspring.team.dto.res.TeamAbstractResDto;
import com.gabojait.gabojaitspring.team.dto.res.TeamDefaultResDto;
import com.gabojait.gabojaitspring.team.dto.res.TeamFavoriteResDto;
import com.gabojait.gabojaitspring.team.service.TeamService;
import com.gabojait.gabojaitspring.user.domain.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/v1")
public class TeamController {

    private final TeamService teamService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "팀 생성",
            notes = "<응답 코드>\n" +
                    "- 201 = TEAM_CREATED\n" +
                    "- 400 = PROJECT_NAME_FIELD_REQUIRED || PROJECT_DESCRIPTION_FIELD_REQUIRED || " +
                    "TOTAL_RECRUIT_CNT_FIELD_REQUIRED || POSITION_FIELD_REQUIRED || EXCEPTION_FIELD_REQUIRED || " +
                    "OPEN_CHAT_URL_FIELD_REQUIRED || PROJECT_NAME_LENGTH_INVALID || " +
                    "PROJECT_DESCRIPTION_LENGTH_INVALID || EXPECTATION_LENGTH_INVALID || " +
                    "OPEN_CHAT_URL_LENGTH_INVALID || TOTAL_CNT_POSITIVE__ONLY || POSITION_TYPE_INVALID || " +
                    "OPEN_CHAT_URL_FORMAT_INVALID\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 409 = EXISTING_CURRENT_TEAM || NON_EXISTING_POSITION\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "CREATED",
                    content = @Content(schema = @Schema(implementation = TeamDefaultResDto.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "409", description = "CONFLICT"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/team")
    public ResponseEntity<DefaultResDto<Object>> createTeam(HttpServletRequest servletRequest,
                                                            @RequestBody @Valid TeamDefaultReqDto request) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        Team team = teamService.create(request, user);

        TeamDefaultResDto response = new TeamDefaultResDto(team);

        return ResponseEntity.status(TEAM_CREATED.getHttpStatus())
                .body(DefaultResDto.singleDataBuilder()
                        .responseCode(TEAM_CREATED.name())
                        .responseMessage(TEAM_CREATED.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "팀 정보 수정",
            notes = "<응답 코드>\n" +
                    "- 200 = TEAM_UPDATED\n" +
                    "- 400 = PROJECT_NAME_FIELD_REQUIRED || PROJECT_DESCRIPTION_FIELD_REQUIRED || " +
                    "TOTAL_RECRUIT_CNT_FIELD_REQUIRED || POSITION_FIELD_REQUIRED || EXCEPTION_FIELD_REQUIRED || " +
                    "OPEN_CHAT_URL_FIELD_REQUIRED || PROJECT_NAME_LENGTH_INVALID || " +
                    "PROJECT_DESCRIPTION_LENGTH_INVALID || EXPECTATION_LENGTH_INVALID || " +
                    "OPEN_CHAT_URL_LENGTH_INVALID || TOTAL_CNT_POSITIVE__ONLY || POSITION_TYPE_INVALID || " +
                    "OPEN_CHAT_URL_FORMAT_INVALID\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED || REQUEST_FORBIDDEN\n" +
                    "- 404 = CURRENT_TEAM_NOT_FOUND" +
                    "- 409 = DESIGNER_CNT_UPDATE_UNAVAILABLE || BACKEND_CNT_UPDATE_UNAVAILABLE || " +
                    "FRONTEND_CNT_UPDATE_UNAVAILABLE || MANAGER_CNT_UPDATE_AVAILABLE\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = TeamDefaultResDto.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "409", description = "CONFLICT"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PutMapping("/team")
    public ResponseEntity<DefaultResDto<Object>> updateTeam(HttpServletRequest servletRequest,
                                                            @RequestBody @Valid TeamDefaultReqDto request) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        Team team = teamService.update(request, user);

        TeamDefaultResDto response = new TeamDefaultResDto(team);

        return ResponseEntity.status(TEAM_UPDATED.getHttpStatus())
                .body(DefaultResDto.singleDataBuilder()
                        .responseCode(TEAM_UPDATED.name())
                        .responseMessage(TEAM_UPDATED.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "본인 현재 팀 조회",
            notes = "<응답 코드>\n" +
                    "- 200 = SELF_TEAM_FOUND\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = CURRENT_TEAM_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = TeamDefaultResDto.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/user/team")
    public ResponseEntity<DefaultResDto<Object>> findMyTeam(HttpServletRequest servletRequest) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        Team team = teamService.findOneCurrentTeam(user);

        TeamDefaultResDto response = new TeamDefaultResDto(team);

        return ResponseEntity.status(SELF_TEAM_FOUND.getHttpStatus())
                .body(DefaultResDto.singleDataBuilder()
                        .responseCode(SELF_TEAM_FOUND.name())
                        .responseMessage(SELF_TEAM_FOUND.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "팀 단건 조회",
            notes = "<응답 코드>\n" +
                    "- 200 = TEAM_FOUND\n" +
                    "- 400 = TEAM_ID_FIELD_REQUIRED || TEAM_ID_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = TEAM_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = TeamFavoriteResDto.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/team/{team-id}")
    public ResponseEntity<DefaultResDto<Object>> findOneTeam(HttpServletRequest servletRequest,
                                                             @PathVariable(value = "team-id")
                                                             @NotNull(message = "팀 식별자는 필수 입력입니다.")
                                                             @Positive(message = "팀 식별자는 양수만 가능합니다.")
                                                             Long teamId) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        TeamFavoriteResDto response = teamService.findOneOtherTeam(teamId, user);

        return ResponseEntity.status(TEAM_FOUND.getHttpStatus())
                .body(DefaultResDto.singleDataBuilder()
                        .responseCode(TEAM_FOUND.name())
                        .responseMessage(TEAM_FOUND.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "팀원을 찾는 팀 다건 조회",
            notes = "<옵션>\n" +
                    "- position[default: none] = designer(디자이너만) || backend(백엔드만) || frontend(프론트엔드만) || " +
                    "manager(매니저만) || none(전체)\n" +
                    "- team-order[default: created] = created(생성순) || active(활동순) || popularity(인기순)\n\n" +
                    "<검증>\n" +
                    "- position = NotBlank && Pattern(regex = ^(designer|backend|frontend|manager|none))\n" +
                    "- team-order = NotBlank && Pattern(regex = ^(created|active|popularity))\n" +
                    "- page-from = NotNull && PositiveOrZero\n" +
                    "- page-size = Positive\n\n" +
                    "<응답 코드>\n" +
                    "- 200 = TEAMS_FINDING_USERS_FOUND\n" +
                    "- 400 = POSITION_FIELD_REQUIRED || TEAM_ORDER_FIELD_REQUIRED || PAGE_FROM_FIELD_REQUIRED || " +
                    "POSITION_TYPE_INVALID || TEAM_ORDER_TYPE_INVALID || PAGE_FROM_POSITIVE_OR_ZERO_ONLY || " +
                    "PAGE_SIZE_POSITIVE_ONLY\n" +
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
    @GetMapping("/team/recruiting")
    public ResponseEntity<DefaultResDto<Object>> findTeamsLookingForUsers(
            HttpServletRequest servletRequest,
            @RequestParam(value = "position")
            @NotBlank(message = "포지션은 필수 입력입니다.")
            @Pattern(regexp = "^(designer|backend|frontend|manager|none)",
                    message = "포지션은 'designer', 'backend', 'frontend', 'manager', 또는 'none' 중 하나여야 됩니다.")
            String position,
            @RequestParam(value = "team-order")
            @NotBlank(message = "팀 정렬 기준은 필수 입력입니다.")
            @Pattern(regexp = "^(created|active|popularity)",
                    message = "팀 정렬 기준은 'created', 'active', 또는 'popularity', 중 하나여야 됩니다.")
            String teamOrder,
            @RequestParam(value = "page-from")
            @NotNull(message = "페이지 시작점은 필수 입력입니다.")
            @PositiveOrZero(message = "페이지 시작점은 0 또는 양수만 가능합니다.")
            Integer pageFrom,
            @RequestParam(value = "page-size", required = false)
            @Positive(message = "페이지 사이즈는 양수만 가능합니다.")
            Integer pageSize
    ) {
        jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        Page<Team> teams = teamService.findManyTeamByPositionOrder(position, teamOrder, pageFrom, pageSize);

        List<TeamAbstractResDto> responses = new ArrayList<>();
        for (Team team : teams)
            responses.add(new TeamAbstractResDto(team));

        return ResponseEntity.status(TEAMS_RECRUITING_USERS_FOUND.getHttpStatus())
                .body(DefaultResDto.multiDataBuilder()
                        .responseCode(TEAMS_RECRUITING_USERS_FOUND.name())
                        .responseMessage(TEAMS_RECRUITING_USERS_FOUND.getMessage())
                        .data(responses)
                        .size(teams.getTotalPages())
                        .build());
    }

    @ApiOperation(value = "팀원 모집 여부 업데이트",
            notes = "<응답 코드>\n" +
                    "- 200 = TEAM_IS_RECRUITING_UPDATED\n" +
                    "- 400 = IS_RECRUITING_FIELD_REQUIRED\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED || REQUEST_FORBIDDEN\n" +
                    "- 404 = CURRENT_TEAM_NOT_FOUND\n" +
                    "- 409 = NON_EXISTING_CURRENT_TEAM\n" +
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
    @PatchMapping("/team/recruiting")
    public ResponseEntity<DefaultResDto<Object>> updateIsRecruiting(HttpServletRequest servletRequest,
                                                                    @RequestBody @Valid
                                                                    TeamIsRecruitingUpdateReqDto request) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        teamService.updateIsRecruiting(user, request.getIsRecruiting());

        return ResponseEntity.status(TEAM_IS_RECRUITING_UPDATED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(TEAM_IS_RECRUITING_UPDATED.name())
                        .responseMessage(TEAM_IS_RECRUITING_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "프로젝트 미완료 종료",
            notes = "<응답 코드>\n" +
                    "- 200 = PROJECT_INCOMPLETE\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED || REQUEST_FORBIDDEN\n" +
                    "- 404 = CURRENT_TEAM_NOT_FOUND\n" +
                    "- 409 = NON_EXISTING_CURRENT_TEAM\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "409", description = "CONFLICT"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @DeleteMapping("/team/incomplete")
    public ResponseEntity<DefaultResDto<Object>> projectIncomplete(HttpServletRequest servletRequest) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        teamService.quit(user, "");

        return ResponseEntity.status(PROJECT_INCOMPLETE.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(PROJECT_INCOMPLETE.name())
                        .responseMessage(PROJECT_INCOMPLETE.getMessage())
                        .build());
    }

    @ApiOperation(value = "프로젝트 완료 종료",
            notes = "<응답 코드>\n" +
                    "- 200 = PROJECT_COMPLETE\n" +
                    "- 400 = PROJECT_URL_FIELD_REQUIRED\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED || REQUEST_FORBIDDEN\n" +
                    "- 404 = CURRENT_TEAM_NOT_FOUND\n" +
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
    @PatchMapping("/team/complete")
    public ResponseEntity<DefaultResDto<Object>> quitCompleteProject(HttpServletRequest servletRequest,
                                                                     @RequestBody @Valid
                                                                     TeamCompleteReqDto request) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        teamService.quit(user, request.getProjectUrl());

        return ResponseEntity.status(PROJECT_COMPLETE.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(PROJECT_COMPLETE.name())
                        .responseMessage(PROJECT_COMPLETE.getMessage())
                        .build());
    }

    @ApiOperation(value = "팀원 추방",
            notes = "<응답 코드>\n" +
                    "- 200 = TEAMMATE_FIRED\n" +
                    "- 400 = USER_ID_FIELD_REQUIRED || USER_ID_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED || REQUEST_FORBIDDEN\n" +
                    "- 404 = USER_NOT_FOUND || TEAM_NOT_FOUND\n" +
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
    @PatchMapping("/team/user/{user-id}/fire")
    public ResponseEntity<DefaultResDto<Object>> fireTeammate(HttpServletRequest servletRequest,
                                                              @PathVariable(value = "user-id")
                                                              @NotNull(message = "회원 식별자를 입력해 주세요.")
                                                              @Positive(message = "회원 식별자는 양수만 가능합니다.")
                                                              Long userId) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        teamService.fire(user, userId);

        return ResponseEntity.status(TEAMMATE_FIRED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(TEAMMATE_FIRED.name())
                        .responseMessage(TEAMMATE_FIRED.getMessage())
                        .build());
    }
}
