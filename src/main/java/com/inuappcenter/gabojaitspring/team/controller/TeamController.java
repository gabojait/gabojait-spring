package com.inuappcenter.gabojaitspring.team.controller;

import com.inuappcenter.gabojaitspring.auth.JwtProvider;
import com.inuappcenter.gabojaitspring.auth.JwtType;
import com.inuappcenter.gabojaitspring.common.DefaultResDto;
import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.type.Position;
import com.inuappcenter.gabojaitspring.profile.domain.type.TeamMemberStatus;
import com.inuappcenter.gabojaitspring.profile.dto.res.UserProfileAbstractResDto;
import com.inuappcenter.gabojaitspring.team.domain.Team;
import com.inuappcenter.gabojaitspring.team.dto.req.*;
import com.inuappcenter.gabojaitspring.team.dto.res.TeamDefaultResDto;
import com.inuappcenter.gabojaitspring.team.dto.res.TeamDetailResDto;
import com.inuappcenter.gabojaitspring.team.service.TeamService;
import com.inuappcenter.gabojaitspring.user.domain.User;
import com.inuappcenter.gabojaitspring.user.domain.type.Role;
import com.inuappcenter.gabojaitspring.user.service.UserService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.inuappcenter.gabojaitspring.common.SuccessCode.*;
import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "팀")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/team")
public class TeamController {

    private final UserService userService;
    private final TeamService teamService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "팀 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "TEAM_CREATED",
                    content = @Content(schema = @Schema(implementation = TeamDefaultResDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "FIELD_REQUIRED / *_LENGTH_INVALID / *_POS_ZERO_ONLY / OPENCHATURL_FORMAT_INVALID / " +
                            "POSITION_UNSELECTED"),
            @ApiResponse(responseCode = "401", description = " TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "409", description = "EXISTING_CURRENT_TEAM"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<DefaultResDto<Object>> createTeam(HttpServletRequest servletRequest,
                                                            @RequestBody @Valid TeamDefaultReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User user = userService.findOneByUserId(token.get(0));

        userService.validateCurrentTeam(user);
        userService.isPositionSelected(user);
        Team team = request.toEntity(user.getId(), user.getPosition());
        teamService.validatePositionAvailability(team, Position.toEnum(user.getPosition()).getType());

        teamService.save(team);
        teamService.join(team, user, user.getPosition(), TeamMemberStatus.LEADER);

        TeamDefaultResDto responseBody = new TeamDefaultResDto(team);

        return ResponseEntity.status(TEAM_CREATED.getHttpStatus())
                .body(DefaultResDto.SingleDataBuilder()
                        .responseCode(TEAM_CREATED.name())
                        .responseMessage(TEAM_CREATED.getMessage())
                        .data(responseBody)
                        .build());
    }

    @ApiOperation(value = "팀 정보 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TEAM_UPDATED",
                    content = @Content(schema = @Schema(implementation = TeamDefaultResDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "FIELD_REQUIRED / *_LENGTH_INVALID / *_POS_ZERO_ONLY / *_LIMIT_INVALID / " +
                            "OPENCHATURL_FORMAT_INVALID"),
            @ApiResponse(responseCode = "401", description = " TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED / ROLE_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "409", description = "EXISTING_CURRENT_TEAM"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PatchMapping
    public ResponseEntity<DefaultResDto<Object>> updateTeam(HttpServletRequest servletRequest,
                                                            @RequestBody @Valid TeamDefaultReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User leader = userService.findOneByUserId(token.get(0));
        userService.isNonExistingCurrentTeam(leader);
        Team team = teamService.findOne(leader.getCurrentTeamId().toString());
        Position position = teamService.getPositionInCurrentTeam(team, leader);
        teamService.validateLeader(team, leader);
        teamService.validateUpdatePositionAvailability(team, request, position);

        teamService.update(team, request, position);

        TeamDefaultResDto responseBody = new TeamDefaultResDto(team);

        return ResponseEntity.status(TEAM_UPDATED.getHttpStatus())
                .body(DefaultResDto.SingleDataBuilder()
                        .responseCode(TEAM_UPDATED.name())
                        .responseMessage(TEAM_UPDATED.getMessage())
                        .data(responseBody)
                        .build());
    }

    @ApiOperation(value = "팀 단건 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TEAM_FOUND",
                    content = @Content(schema = @Schema(implementation = TeamDetailResDto.class))),
            @ApiResponse(responseCode = "401", description = " TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @GetMapping("/find/{team-id}")
    public ResponseEntity<DefaultResDto<Object>> findOneTeam(HttpServletRequest servletRequest,
                                                             @PathVariable(value = "team-id") String teamId) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User user = userService.findOneByUserId(token.get(0));

        Team team = teamService.findOne(teamId);

        boolean isLeader = false;
        boolean isFavorite = false;
        if (user.getCurrentTeamId() != null) {
            isLeader = teamService.isLeader(team, user);
            if (!isLeader)
                isFavorite = userService.isFavoriteTeam(user, team.getId());
        }

        if (!isLeader) {
            TeamDetailResDto responseBody = new TeamDetailResDto(team, isFavorite);

            return ResponseEntity.status(TEAM_FOUND.getHttpStatus())
                    .body(DefaultResDto.SingleDataBuilder()
                            .responseCode(TEAM_FOUND.name())
                            .responseMessage(TEAM_FOUND.getMessage())
                            .data(responseBody)
                            .build());
        } else {
            TeamDefaultResDto responseBody = new TeamDefaultResDto(team);

            return ResponseEntity.status(TEAM_FOUND.getHttpStatus())
                    .body(DefaultResDto.SingleDataBuilder()
                            .responseCode(TEAM_FOUND.name())
                            .responseMessage(TEAM_FOUND.getMessage())
                            .data(responseBody)
                            .build());
        }

        TeamDetailResDto responseBody = new TeamDetailResDto(team, isFavorite);

        return ResponseEntity.status(TEAM_FOUND.getHttpStatus())
                .body(DefaultResDto.SingleDataBuilder()
                        .responseCode(TEAM_FOUND.name())
                        .responseMessage(TEAM_FOUND.getMessage())
                        .data(responseBody)
                        .build());
    }

    @ApiOperation(value = "팀 다건 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TEAMS_FOUND / TEAM_ZERO",
                    content = @Content(schema = @Schema(implementation = TeamDefaultResDto.class))),
            @ApiResponse(responseCode = "401", description = " TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @GetMapping("/find")
    public ResponseEntity<DefaultResDto<Object>> findManyTeams(HttpServletRequest servletRequest,
                                                               @RequestParam Integer pageFrom,
                                                               @RequestParam(required = false) Integer pageSize) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        userService.findOneByUserId(token.get(0));
        Page<Team> teams = teamService.findMany(pageFrom, pageSize);

        if (teams.getNumberOfElements() == 0) {

            return ResponseEntity.status(TEAM_ZERO.getHttpStatus())
                    .body(DefaultResDto.MultiDataBuilder()
                            .responseCode(TEAM_ZERO.name())
                            .responseMessage(TEAM_ZERO.getMessage())
                            .data(null)
                            .size(teams.getTotalPages())
                            .build());
        } else {

            List<TeamDefaultResDto> responseBodies = new ArrayList<>();
            for (Team t : teams)
                responseBodies.add(new TeamDefaultResDto(t));

            return ResponseEntity.status(TEAMS_FOUND.getHttpStatus())
                    .body(DefaultResDto.MultiDataBuilder()
                            .responseCode(TEAM_CREATED.name())
                            .responseMessage(TEAM_CREATED.getMessage())
                            .data(responseBodies)
                            .size(teams.getTotalPages())
                            .build());
        }
    }

    @ApiOperation(value = "팀 공개여부 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TEAM_VISIBILITY_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED / ROLE_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / TEAM_NOT_FOUND / CURRENT_TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PatchMapping("/visibility")
    public ResponseEntity<DefaultResDto<Object>> updateVisibility(HttpServletRequest servletRequest,
                                                                  @RequestBody @Valid
                                                                  TeamVisibilityUpdateReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User leader = userService.findOneByUserId(token.get(0));
        userService.isNonExistingCurrentTeam(leader);
        Team team = teamService.findOne(leader.getCurrentTeamId().toString());
        teamService.validateLeader(team, leader);

        teamService.updateIsPublic(team, request.getIsPublic());

        return ResponseEntity.status(TEAM_VISIBILITY_UPDATED.getHttpStatus())
                .body(DefaultResDto.NoDataBuilder()
                        .responseCode(TEAM_VISIBILITY_UPDATED.name())
                        .responseMessage(TEAM_VISIBILITY_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "팀 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TEAM_DELETED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED / ROLE_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @DeleteMapping
    public ResponseEntity<DefaultResDto<Object>> deleteTeam(HttpServletRequest servletRequest) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User leader = userService.findOneByUserId(token.get(0));
        userService.isNonExistingCurrentTeam(leader);
        Team team = teamService.findOne(leader.getCurrentTeamId().toString());
        teamService.validateLeader(team, leader);

        teamService.delete(team);

        return ResponseEntity.status(TEAM_DELETED.getHttpStatus())
                .body(DefaultResDto.NoDataBuilder()
                        .responseCode(TEAM_DELETED.name())
                        .responseMessage(TEAM_DELETED.getMessage())
                        .build());
    }

    @ApiOperation(value = "팀원 추방")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TEAMMATE_FIRED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED / ROLE_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PatchMapping("/user/{user-id}/fire")
    public ResponseEntity<DefaultResDto<Object>> fireTeammate(HttpServletRequest servletRequest,
                                                              @PathVariable(value = "user-id") String userId) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User leader = userService.findOneByUserId(token.get(0));
        userService.isNonExistingCurrentTeam(leader);
        Team team = teamService.findOne(leader.getCurrentTeamId().toString());
        teamService.validateLeader(team, leader);
        User teammate = userService.findOneByUserId(userId);
        Position position = teamService.getPositionInCurrentTeam(team, teammate);

        teamService.leaveTeam(team, teammate, position);

        return ResponseEntity.status(TEAMMATE_FIRED.getHttpStatus())
                .body(DefaultResDto.NoDataBuilder()
                        .responseCode(TEAMMATE_FIRED.name())
                        .responseMessage(TEAMMATES_FOUND.getMessage())
                        .build());
    }

    @ApiOperation(value = "프로젝트 완료")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PROJECT_COMPLETED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / PROJECTURL_LENGTH_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED / ROLE_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PatchMapping("/complete")
    public ResponseEntity<DefaultResDto<Object>> complete(HttpServletRequest servletRequest,
                                                          @RequestBody @Valid TeamCompleteUpdateReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User leader = userService.findOneByUserId(token.get(0));
        userService.isNonExistingCurrentTeam(leader);
        Team team = teamService.findOne(leader.getCurrentTeamId().toString());
        teamService.validateLeader(team, leader);

        teamService.projectComplete(team, request.getProjectUrl());

        return ResponseEntity.status(TEAM_PROJECT_COMPLETE.getHttpStatus())
                .body(DefaultResDto.NoDataBuilder()
                        .responseCode(TEAM_PROJECT_COMPLETE.name())
                        .responseMessage(TEAM_VISIBILITY_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "유저 찜하기 추가 / 제거")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "USER_FAVORITE_ADDED / USER_FAVORITE_REMOVED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
    })
    @PatchMapping("/user/{user-id}/favorite")
    public ResponseEntity<DefaultResDto<Object>> addOrRemoveFavoriteUser(HttpServletRequest servletRequest,
                                                                         @PathVariable(value = "user-id") String userId,
                                                                         @RequestBody @Valid
                                                                         TeamUserFavoriteDefaultReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User leader = userService.findOneByUserId(token.get(0));
        userService.isNonExistingCurrentTeam(leader);
        Team team = teamService.findOne(leader.getCurrentTeamId().toString());
        teamService.validateLeader(team, leader);

        if (request.getIsAdd()) {

            User user = userService.findOneByUserId(userId);

            teamService.addFavoriteUser(team, user.getId());

            return ResponseEntity.status(USER_FAVORITE_ADDED.getHttpStatus())
                    .body(DefaultResDto.NoDataBuilder()
                            .responseCode(USER_FAVORITE_ADDED.name())
                            .responseMessage(USER_FAVORITE_ADDED.getMessage())
                            .build());
        } else {

            teamService.removeFavoriteUser(team, new ObjectId(userId));

            return ResponseEntity.status(USER_FAVORITE_REMOVED.getHttpStatus())
                    .body(DefaultResDto.NoDataBuilder()
                            .responseCode(USER_FAVORITE_REMOVED.name())
                            .responseMessage(USER_FAVORITE_REMOVED.getMessage())
                            .build());
        }
    }

    @ApiOperation(value = "유저 찜한 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FOUND_FAVORITE_USERS / ZERO_FAVORITE_USER",
                    content = @Content(schema = @Schema(implementation = UserProfileAbstractResDto.class))),
    })
    @GetMapping("/user/favorites")
    public ResponseEntity<DefaultResDto<Object>> findFavoriteUsers(HttpServletRequest servletRequest,
                                                                   @RequestParam Integer pageFrom,
                                                                   @RequestParam(required = false) Integer pageSize) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User leader = userService.findOneByUserId(token.get(0));
        userService.isNonExistingCurrentTeam(leader);
        Team team = teamService.findOne(leader.getCurrentTeamId().toString());
        teamService.validateLeader(team, leader);

        Map<String, List<User>> users = userService
                .findManyTeamFavoriteUsersAndRemoveIfDeleted(team, pageFrom, pageSize);

        if (!users.get("deletedUsers").isEmpty())
            for (User user : users.get("deletedUsers"))
                teamService.removeFavoriteUser(team, user.getId());

        if (users.get("favoriteUsers").isEmpty()) {

            return ResponseEntity.status(ZERO_FAVORITE_USER.getHttpStatus())
                    .body(DefaultResDto.MultiDataBuilder()
                            .responseCode(ZERO_FAVORITE_USER.name())
                            .responseMessage(ZERO_FAVORITE_USER.getMessage())
                            .data(null)
                            .size(team.getFavoriteUserIds().size())
                            .build());
        } else {

            List<UserProfileAbstractResDto> responseBodies = new ArrayList<>();
            for (User user : users.get("favoriteUsers"))
                responseBodies.add(new UserProfileAbstractResDto(user));

            return ResponseEntity.status(FOUND_FAVORITE_USERS.getHttpStatus())
                    .body(DefaultResDto.MultiDataBuilder()
                            .responseCode(FOUND_FAVORITE_USERS.name())
                            .responseMessage(FOUND_FAVORITE_USERS.getMessage())
                            .data(responseBodies)
                            .size(team.getFavoriteUserIds().size())
                            .build());
        }
    }
}
