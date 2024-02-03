package com.gabojait.gabojaitspring.api.controller.team;

import com.gabojait.gabojaitspring.common.response.DefaultMultiResponse;
import com.gabojait.gabojaitspring.common.response.DefaultNoResponse;
import com.gabojait.gabojaitspring.common.response.DefaultSingleResponse;
import com.gabojait.gabojaitspring.common.response.PageData;
import com.gabojait.gabojaitspring.api.dto.team.request.TeamCompleteRequest;
import com.gabojait.gabojaitspring.api.dto.team.request.TeamCreateRequest;
import com.gabojait.gabojaitspring.api.dto.team.request.TeamIsRecruitingUpdateRequest;
import com.gabojait.gabojaitspring.api.dto.team.request.TeamUpdateRequest;
import com.gabojait.gabojaitspring.api.dto.team.response.*;
import com.gabojait.gabojaitspring.api.service.team.TeamService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
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
                    "- 400 = DESIGNER_MAX_CNT_FIELD_REQUIRED || BACKEND_MAX_CNT_FIELD_REQUIRED || " +
                    "FRONTEND_MAX_CNT_FIELD_REQUIRED || MANAGER_MAX_CNT_FIELD_REQUIRED || " +
                    "PROJECT_NAME_LENGTH_INVALID || PROJECT_DESCRIPTION_LENGTH_INVALID || " +
                    "EXPECTATION_LENGTH_INVALID || OPEN_CHAT_URL_LENGTH_INVALID || OPEN_CHAT_URL_FORMAT_INVALID || " +
                    "DESIGNER_MAX_CNT_POSITIVE_OR_ZERO_ONLY || BACKEND_MAX_CNT_POSITIVE_OR_ZERO_ONLY || " +
                    "FRONTEND_MAX_CNT_POSITIVE_OR_ZERO_ONLY || MANAGER_MAX_CNT_POSITIVE_OR_ZERO_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND\n" +
                    "- 409 = EXISTING_CURRENT_TEAM || NON_EXISTING_POSITION || TEAM_LEADER_POSITION_UNAVAILABLE\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "CREATED",
                    content = @Content(schema = @Schema(implementation = TeamCreateResponse.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "409", description = "CONFLICT"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/team")
    public ResponseEntity<DefaultSingleResponse<Object>> createTeam(
            @RequestHeader(value = AUTHORIZATION, required = false) String token,
            @RequestBody @Valid TeamCreateRequest request
    ) {
        long userId = jwtProvider.getUserId(token);

        TeamCreateResponse response = teamService.createTeam(userId, request);

        return ResponseEntity.status(TEAM_CREATED.getHttpStatus())
                .body(DefaultSingleResponse.singleDataBuilder()
                        .responseCode(TEAM_CREATED.name())
                        .responseMessage(TEAM_CREATED.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "팀 수정",
            notes = "<응답 코드>\n" +
                    "- 200 = TEAM_UPDATED\n" +
                    "- 400 = DESIGNER_MAX_CNT_FIELD_REQUIRED || BACKEND_MAX_CNT_FIELD_REQUIRED || " +
                    "FRONTEND_MAX_CNT_FIELD_REQUIRED || MANAGER_MAX_CNT_FIELD_REQUIRED || " +
                    "PROJECT_NAME_LENGTH_INVALID || PROJECT_DESCRIPTION_LENGTH_INVALID || " +
                    "EXPECTATION_LENGTH_INVALID || OPEN_CHAT_URL_LENGTH_INVALID || OPEN_CHAT_URL_FORMAT_INVALID || " +
                    "DESIGNER_MAX_CNT_POSITIVE_OR_ZERO_ONLY || BACKEND_MAX_CNT_POSITIVE_OR_ZERO_ONLY || " +
                    "FRONTEND_MAX_CNT_POSITIVE_OR_ZERO_ONLY || MANAGER_MAX_CNT_POSITIVE_OR_ZERO_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED || REQUEST_FORBIDDEN\n" +
                    "- 404 = USER_NOT_FOUND || CURRENT_TEAM_NOT_FOUND\n" +
                    "- 409 = DESIGNER_CNT_UPDATE_UNAVAILABLE || BACKEND_CNT_UPDATE_UNAVAILABLE || " +
                    "FRONTEND_CNT_UPDATE_UNAVAILABLE || MANAGER_CNT_UPDATE_AVAILABLE\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = TeamUpdateResponse.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "409", description = "CONFLICT"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PutMapping("/team")
    public ResponseEntity<DefaultSingleResponse<Object>> updateTeam(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @RequestBody @Valid TeamUpdateRequest request
    ) {
        long userId = jwtProvider.getUserId(authorization);

        TeamUpdateResponse response = teamService.updateTeam(userId, request);

        return ResponseEntity.status(TEAM_UPDATED.getHttpStatus())
                .body(DefaultSingleResponse.singleDataBuilder()
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
                    "- 404 = USER_NOT_FOUND || CURRENT_TEAM_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = TeamMyCurrentResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/user/team")
    public ResponseEntity<DefaultSingleResponse<Object>> findMyCurrentTeam(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization
    ) {
        long userId = jwtProvider.getUserId(authorization);

        TeamMyCurrentResponse response = teamService.findCurrentTeam(userId);

        return ResponseEntity.status(SELF_TEAM_FOUND.getHttpStatus())
                .body(DefaultSingleResponse.singleDataBuilder()
                        .responseCode(SELF_TEAM_FOUND.name())
                        .responseMessage(SELF_TEAM_FOUND.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "팀 단건 조회",
            notes = "<검증>\n" +
                    "- team-id = Positive\n\n" +
                    "<응답 코드>\n" +
                    "- 200 = TEAM_FOUND\n" +
                    "- 400 = TEAM_ID_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND || TEAM_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = TeamFindResponse.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/team/{team-id}")
    public ResponseEntity<DefaultSingleResponse<Object>> findTeam(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @PathVariable(value = "team-id")
            @Positive(message = "팀 식별자는 양수만 가능합니다.")
            Long teamId
    ) {
        long userId = jwtProvider.getUserId(authorization);

        TeamFindResponse response = teamService.findOtherTeam(userId, teamId);

        return ResponseEntity.status(TEAM_FOUND.getHttpStatus())
                .body(DefaultSingleResponse.singleDataBuilder()
                        .responseCode(TEAM_FOUND.name())
                        .responseMessage(TEAM_FOUND.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "팀원을 찾는 팀 페이징 조회",
            notes = "<검증>\n" +
                    "- position[default: NONE] = Pattern(regex = ^(DESIGNER|BACKEND|FRONTEND|MANAGER|NONE))\n" +
                    "- page-from[default: 9223372036854775806] = Positive\n" +
                    "- page-size[default: 20] = Positive && Max(value = 100)\n\n" +
                    "<응답 코드>\n" +
                    "- 200 = TEAMS_RECRUITING_USERS_FOUND\n" +
                    "- 400 = POSITION_TYPE_INVALID || PAGE_FROM_POSITIVE_ONLY || PAGE_SIZE_POSITIVE_ONLY || " +
                    "PAGE_SIZE_RANGE_INVALID\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = TeamPageResponse.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/team/recruiting")
    public ResponseEntity<DefaultMultiResponse<Object>> findTeamsLookingForUsers(
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
        PageData<List<TeamPageResponse>> responses = teamService.findPageTeam(Position.valueOf(position),
                pageFrom, pageSize);

        return ResponseEntity.status(TEAMS_RECRUITING_USERS_FOUND.getHttpStatus())
                .body(DefaultMultiResponse.multiDataBuilder()
                        .responseCode(TEAMS_RECRUITING_USERS_FOUND.name())
                        .responseMessage(TEAMS_RECRUITING_USERS_FOUND.getMessage())
                        .responseData(responses)
                        .build());
    }

    @ApiOperation(value = "팀원 모집 여부 업데이트",
            notes = "<응답 코드>\n" +
                    "- 200 = TEAM_IS_RECRUITING_UPDATED\n" +
                    "- 400 = IS_RECRUITING_FIELD_REQUIRED\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED || REQUEST_FORBIDDEN\n" +
                    "- 404 = USER_NOT_FOUND || CURRENT_TEAM_NOT_FOUND\n" +
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
    @PatchMapping("/team/recruiting")
    public ResponseEntity<DefaultNoResponse> updateIsRecruiting(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @RequestBody @Valid TeamIsRecruitingUpdateRequest request
    ) {
        long userId = jwtProvider.getUserId(authorization);

        teamService.updateIsRecruiting(userId, request.getIsRecruiting());

        return ResponseEntity.status(TEAM_IS_RECRUITING_UPDATED.getHttpStatus())
                .body(DefaultNoResponse.noDataBuilder()
                        .responseCode(TEAM_IS_RECRUITING_UPDATED.name())
                        .responseMessage(TEAM_IS_RECRUITING_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "프로젝트 미완료 종료",
            notes = "<응답 코드>\n" +
                    "- 200 = PROJECT_INCOMPLETE\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED || REQUEST_FORBIDDEN\n" +
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
    @DeleteMapping("/team/incomplete")
    public ResponseEntity<DefaultNoResponse> projectIncomplete(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization
    ) {
        long userId = jwtProvider.getUserId(authorization);

        teamService.endProject(userId, "", LocalDateTime.now());

        return ResponseEntity.status(PROJECT_INCOMPLETE.getHttpStatus())
                .body(DefaultNoResponse.noDataBuilder()
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
                    "- 404 = USER_NOT_FOUND || CURRENT_TEAM_NOT_FOUND\n" +
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
    public ResponseEntity<DefaultNoResponse> quitCompleteProject(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @RequestBody @Valid TeamCompleteRequest request
    ) {
        long userId = jwtProvider.getUserId(authorization);

        teamService.endProject(userId, request.getProjectUrl(), LocalDateTime.now());

        return ResponseEntity.status(PROJECT_COMPLETE.getHttpStatus())
                .body(DefaultNoResponse.noDataBuilder()
                        .responseCode(PROJECT_COMPLETE.name())
                        .responseMessage(PROJECT_COMPLETE.getMessage())
                        .build());
    }

    @ApiOperation(value = "팀원 추방",
            notes = "<검증>\n" +
                    "- user-id = Positive\n\n" +
                    "<응답 코드>\n" +
                    "- 200 = TEAMMATE_FIRED\n" +
                    "- 400 = USER_ID_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED || REQUEST_FORBIDDEN\n" +
                    "- 404 = USER_NOT_FOUND || CURRENT_TEAM_NOT_FOUND\n" +
                    "- 409 = TEAM_LEADER_UNAVAILABLE\n" +
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
    @PatchMapping("/team/user/{user-id}/fire")
    public ResponseEntity<DefaultNoResponse> fireTeamMember(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @PathVariable(value = "user-id")
            @Positive(message = "회원 식별자는 양수만 가능합니다.")
            Long userId
    ) {
        long myUserId = jwtProvider.getUserId(authorization);

        teamService.fire(myUserId, userId);

        return ResponseEntity.status(TEAMMATE_FIRED.getHttpStatus())
                .body(DefaultNoResponse.noDataBuilder()
                        .responseCode(TEAMMATE_FIRED.name())
                        .responseMessage(TEAMMATE_FIRED.getMessage())
                        .build());
    }

    @ApiOperation(value = "팀 탈퇴",
            notes = "<응답 코드>\n" +
                    "- 200 = USER_LEFT_TEAM\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND || CURRENT_TEAM_NOT_FOUND\n" +
                    "- 409 = TEAM_LEADER_UNAVAILABLE\n" +
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
    @PatchMapping("/team/leave")
    public ResponseEntity<DefaultNoResponse> leaveTeam(@RequestHeader(value = AUTHORIZATION, required = false)
                                                       String authorization) {
        long userId = jwtProvider.getUserId(authorization);

        teamService.leave(userId);

        return ResponseEntity.status(USER_LEFT_TEAM.getHttpStatus())
                .body(DefaultNoResponse.noDataBuilder()
                        .responseCode(USER_LEFT_TEAM.name())
                        .responseMessage(USER_LEFT_TEAM.getMessage())
                        .build());
    }
}
