package com.inuappcenter.gabojaitspring.team.controller;

import com.inuappcenter.gabojaitspring.auth.JwtProvider;
import com.inuappcenter.gabojaitspring.auth.JwtType;
import com.inuappcenter.gabojaitspring.common.DefaultResDto;
import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.type.Position;
import com.inuappcenter.gabojaitspring.profile.dto.res.UserAbstractDefaultResDto;
import com.inuappcenter.gabojaitspring.team.domain.Team;
import com.inuappcenter.gabojaitspring.team.dto.req.TeamCreateReqDto;
import com.inuappcenter.gabojaitspring.team.dto.res.TeamDefaultResDto;
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
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

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

    @ApiOperation(value = "팀원 찾기", notes = "position = designer || backend || frontend || pm")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TEAMMATES_FOUND / TEAMMATES_ZERO",
                    content = @Content(schema = @Schema(implementation = UserAbstractDefaultResDto.class))),
            @ApiResponse(responseCode = "401", description = " TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @GetMapping("/user/{position}")
    public ResponseEntity<DefaultResDto<Object>> findTeammates(HttpServletRequest servletRequest,
                                                               @PathVariable String position,
                                                               @RequestParam Integer pageFrom,
                                                               @RequestParam(required = false) Integer pageNum) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        userService.findOneByUserId(token.get(0));

        Page<User> users = userService.findManyByPosition(Position.fromString(position), pageFrom, pageNum);

        if (users.getNumberOfElements() == 0) {

            return ResponseEntity.status(TEAMMATES_ZERO.getHttpStatus())
                    .body(DefaultResDto.builder()
                            .responseCode(TEAMMATES_ZERO.name())
                            .responseMessage(TEAMMATES_ZERO.getMessage())
                            .totalPageNum(users.getTotalPages())
                            .build());
        } else {

            List<UserAbstractDefaultResDto> responseBodies = new ArrayList<>();
            for (User u : users)
                responseBodies.add(new UserAbstractDefaultResDto(u));

            return ResponseEntity.status(TEAMMATES_FOUND.getHttpStatus())
                    .body(DefaultResDto.builder()
                            .responseCode(TEAMMATES_FOUND.name())
                            .responseMessage(TEAMMATES_FOUND.getMessage())
                            .data(responseBodies)
                            .totalPageNum(users.getTotalPages())
                            .build());
        }
    }

    @ApiOperation(value = "팀 생성하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "TEAM_CREATED",
                    content = @Content(schema = @Schema(implementation = TeamDefaultResDto.class))),
            @ApiResponse(responseCode = "400", description = "POSITION_UNSELECTED"),
            @ApiResponse(responseCode = "401", description = " TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "409",
                    description = "EXISTING_CURRENT_TEAM / DESIGNER_POSITION_UNAVAILABLE " +
                            "/ BACKEND_POSITION_UNAVAILABLE / FRONTEND_POSITION_UNAVAILABLE " +
                            "/ PROJECT_MANAGER_POSITION_UNAVAILABLE"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<DefaultResDto<Object>> createTeam(HttpServletRequest servletRequest,
                                                            @RequestBody @Valid TeamCreateReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User user = userService.findOneByUserId(token.get(0));

        userService.validateCurrentTeam(user);
        userService.isPositionSelected(user);
        Team team = request.toEntity(user.getId());
        teamService.validatePositionAvailability(team, user);

        teamService.join(team, user);
        teamService.save(team);

        TeamDefaultResDto responseBody = new TeamDefaultResDto(team);

        return ResponseEntity.status(TEAM_CREATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(TEAM_CREATED.name())
                        .responseMessage(TEAM_CREATED.getMessage())
                        .data(responseBody)
                        .build());
    }

    @ApiOperation(value = "팀 찾기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TEAMS_FOUND / TEAMS_ZERO",
                    content = @Content(schema = @Schema(implementation = UserAbstractDefaultResDto.class))),
            @ApiResponse(responseCode = "401", description = " TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @GetMapping
    public ResponseEntity<DefaultResDto<Object>> findTeams(HttpServletRequest servletRequest,
                                                           @RequestParam Integer pageFrom,
                                                           @RequestParam(required = false) Integer pageNum) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        userService.findOneByUserId(token.get(0));
        Page<Team> teams = teamService.findMany(pageFrom, pageNum);

        if (teams.getNumberOfElements() == 0) {

            return ResponseEntity.status(TEAMS_ZERO.getHttpStatus())
                    .body(DefaultResDto.builder()
                            .responseCode(TEAMS_ZERO.name())
                            .responseMessage(TEAMS_ZERO.getMessage())
                            .totalPageNum(teams.getTotalPages())
                            .build());
        } else {

            List<TeamDefaultResDto> responseBodies = new ArrayList<>();
            for (Team t : teams)
                responseBodies.add(new TeamDefaultResDto(t));

            return ResponseEntity.status(TEAMS_FOUND.getHttpStatus())
                    .body(DefaultResDto.builder()
                            .responseCode(TEAM_CREATED.name())
                            .responseMessage(TEAM_CREATED.getMessage())
                            .data(responseBodies)
                            .totalPageNum(teams.getTotalPages())
                            .build());
        }
    }
}
