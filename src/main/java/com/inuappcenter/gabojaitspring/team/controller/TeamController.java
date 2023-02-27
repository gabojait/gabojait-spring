package com.inuappcenter.gabojaitspring.team.controller;

import com.inuappcenter.gabojaitspring.auth.JwtProvider;
import com.inuappcenter.gabojaitspring.auth.JwtType;
import com.inuappcenter.gabojaitspring.common.DefaultResDto;
import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.type.Position;
import com.inuappcenter.gabojaitspring.profile.dto.res.UserProfileAbstractResDto;
import com.inuappcenter.gabojaitspring.team.domain.Team;
import com.inuappcenter.gabojaitspring.team.dto.req.OfferDefaultReqDto;
import com.inuappcenter.gabojaitspring.team.dto.req.TeamDefaultReqDto;
import com.inuappcenter.gabojaitspring.team.dto.req.TeamVisibilityUpdateReqDto;
import com.inuappcenter.gabojaitspring.team.dto.res.TeamDefaultResDto;
import com.inuappcenter.gabojaitspring.team.service.OfferService;
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
@RequestMapping("/api")
public class TeamController {

    private final UserService userService;
    private final TeamService teamService;
    private final OfferService offerService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "팀원 찾기", notes = "position = designer || backend || frontend || pm")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TEAMMATES_FOUND / TEAMMATES_ZERO",
                    content = @Content(schema = @Schema(implementation = UserProfileAbstractResDto.class))),
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

            List<UserProfileAbstractResDto> responseBodies = new ArrayList<>();
            for (User u : users)
                responseBodies.add(new UserProfileAbstractResDto(u));

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
            @ApiResponse(responseCode = "400",
                    description = "FIELD_REQUIRED / *_LENGTH_INVALID / *_POS_ZERO_ONLY / POSITION_UNSELECTED"),
            @ApiResponse(responseCode = "401", description = " TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "409", description = "EXISTING_CURRENT_TEAM / *_POSITION_UNAVAILABLE"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/team")
    public ResponseEntity<DefaultResDto<Object>> createTeam(HttpServletRequest servletRequest,
                                                            @RequestBody @Valid TeamDefaultReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User user = userService.findOneByUserId(token.get(0));

        userService.validateCurrentTeam(user);
        userService.isPositionSelected(user);
        Team team = request.toEntity(user.getId());
        teamService.validatePositionAvailability(team, Position.toEnum(user.getPosition()));

        teamService.save(team);
        teamService.join(team, user, Position.toEnum(user.getPosition()));

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
                    content = @Content(schema = @Schema(implementation = UserProfileAbstractResDto.class))),
            @ApiResponse(responseCode = "401", description = " TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @GetMapping("/team")
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

    @ApiOperation("팀 지원하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "OFFER_CREATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / POSITION_FORMAT_INVALID"),
            @ApiResponse(responseCode = "401", description = " TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "409", description = "EXISTING_CURRENT_TEAM")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/team/offer/{team-id}")
    public ResponseEntity<DefaultResDto<Object>> userOffer(HttpServletRequest servletRequest,
                                                           @PathVariable(value = "team-id") String teamId,
                                                           @RequestBody @Valid OfferDefaultReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User user = userService.findOneByUserId(token.get(0));
        Team team = teamService.findOne(teamId);
        userService.validateCurrentTeam(user);
        teamService.validatePositionAvailability(team, Position.fromString(request.getPosition()));

        offerService.save(request.userOfferToEntity(user, team));

        return ResponseEntity.status(OFFER_CREATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(OFFER_CREATED.name())
                        .responseMessage(OFFER_CREATED.getMessage())
                        .build());
    }

    @ApiOperation("팀원 스카웃하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "OFFER_CREATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / POSITION_FORMAT_INVALID"),
            @ApiResponse(responseCode = "401", description = " TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "409", description = "EXISTING_CURRENT_TEAM")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/user/offer/{user-id}")
    public ResponseEntity<DefaultResDto<Object>> teamOffer(HttpServletRequest servletRequest,
                                                           @PathVariable(value = "user-id") String userId,
                                                           @RequestBody @Valid OfferDefaultReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User leader = userService.findOneByUserId(token.get(0));
        Team team = teamService.findOne(leader.getCurrentTeamId().toString());
        User user = userService.findOneByUserId(userId);
        userService.validateCurrentTeam(user);
        teamService.validatePositionAvailability(team, Position.fromString(request.getPosition()));
        teamService.validateLeader(team, leader);

        offerService.save(request.teamOfferToEntity(user, team));

        return ResponseEntity.status(OFFER_CREATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(OFFER_CREATED.name())
                        .responseMessage(OFFER_CREATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "팀 공개여부 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TEAM_VISIBILITY_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED / ROLE_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PatchMapping("/team/visibility")
    public ResponseEntity<DefaultResDto<Object>> updateVisibility(HttpServletRequest servletRequest,
                                                                  @RequestBody @Valid
                                                                  TeamVisibilityUpdateReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User leader = userService.findOneByUserId(token.get(0));
        Team team = teamService.findOne(leader.getCurrentTeamId().toString());
        teamService.validateLeader(team, leader);

        teamService.updateIsPublic(team, request.getIsPublic());

        return ResponseEntity.status(TEAM_VISIBILITY_UPDATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(TEAM_VISIBILITY_UPDATED.name())
                        .responseMessage(TEAM_VISIBILITY_UPDATED.getMessage())
                        .build());
    }
}
