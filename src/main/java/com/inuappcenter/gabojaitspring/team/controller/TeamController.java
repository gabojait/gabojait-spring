package com.inuappcenter.gabojaitspring.team.controller;

import com.inuappcenter.gabojaitspring.auth.JwtProvider;
import com.inuappcenter.gabojaitspring.auth.JwtType;
import com.inuappcenter.gabojaitspring.common.DefaultResDto;
import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.type.Position;
import com.inuappcenter.gabojaitspring.profile.dto.res.UserProfileAbstractResDto;
import com.inuappcenter.gabojaitspring.team.domain.Offer;
import com.inuappcenter.gabojaitspring.team.domain.Team;
import com.inuappcenter.gabojaitspring.team.dto.req.*;
import com.inuappcenter.gabojaitspring.team.dto.res.OfferDefaultResDto;
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

    @ApiOperation(value = "팀원 다건 조회", notes = "position = designer || backend || frontend || pm")
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
                    description = "FIELD_REQUIRED / *_LENGTH_INVALID / *_POS_ZERO_ONLY / OPENCHATURL_FORMAT_INVALID / " +
                            "POSITION_UNSELECTED"),
            @ApiResponse(responseCode = "401", description = " TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "409", description = "EXISTING_CURRENT_TEAM / *_POSITION_UNAVAILABLE"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/team")
    public ResponseEntity<DefaultResDto<Object>> createTeam(HttpServletRequest servletRequest,
                                                            @RequestBody @Valid TeamSaveReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User user = userService.findOneByUserId(token.get(0));

        userService.validateCurrentTeam(user);
        userService.isPositionSelected(user);
        Team team = request.toEntity(user.getId());
        teamService.validatePositionAvailability(team, Position.toEnum(user.getPosition()).getType());

        teamService.save(team);
        teamService.join(team, user, user.getPosition());

        TeamDefaultResDto responseBody = new TeamDefaultResDto(team);

        return ResponseEntity.status(TEAM_CREATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(TEAM_CREATED.name())
                        .responseMessage(TEAM_CREATED.getMessage())
                        .data(responseBody)
                        .build());
    }

    @ApiOperation(value = "팀 단건 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TEAM_FOUND",
                    content = @Content(schema = @Schema(implementation = TeamDefaultResDto.class))),
            @ApiResponse(responseCode = "401", description = " TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @GetMapping("/team/{team-id}")
    public ResponseEntity<DefaultResDto<Object>> findOneTeam(HttpServletRequest servletRequest,
                                                             @PathVariable(value = "team-id") String teamId) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        userService.findOneByUserId(token.get(0));

        Team team = teamService.findOne(teamId);

        TeamDefaultResDto responseBody = new TeamDefaultResDto(team);

        return ResponseEntity.status(TEAM_FOUND.getHttpStatus())
                .body(DefaultResDto.builder()
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
    @GetMapping("/team/find")
    public ResponseEntity<DefaultResDto<Object>> findManyTeams(HttpServletRequest servletRequest,
                                                               @RequestParam Integer pageFrom,
                                                               @RequestParam(required = false) Integer pageNum) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        userService.findOneByUserId(token.get(0));
        Page<Team> teams = teamService.findMany(pageFrom, pageNum);

        if (teams.getNumberOfElements() == 0) {

            return ResponseEntity.status(TEAM_ZERO.getHttpStatus())
                    .body(DefaultResDto.builder()
                            .responseCode(TEAM_ZERO.name())
                            .responseMessage(TEAM_ZERO.getMessage())
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

    @ApiOperation("팀 지원")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "OFFER_CREATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / POSITION_FORMAT_INVALID"),
            @ApiResponse(responseCode = "401", description = " TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "409", description = "EXISTING_CURRENT_TEAM / EXISTING_OFFER")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/user/team/{team-id}/offer")
    public ResponseEntity<DefaultResDto<Object>> userOffer(HttpServletRequest servletRequest,
                                                           @PathVariable(value = "team-id") String teamId,
                                                           @RequestBody @Valid OfferSaveReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User user = userService.findOneByUserId(token.get(0));
        Team team = teamService.findOne(teamId);
        userService.validateCurrentTeam(user);
        teamService.validatePositionAvailability(team, Position.fromString(request.getPosition()).getType());
        offerService.isExistingOffer(user.getId(), team.getId());

        Offer offer = offerService.save(request.userOfferToEntity(user.getId(), team.getId()));
        userService.addApplication(user, offer.getId());
        teamService.addApplication(team, offer.getId());

        return ResponseEntity.status(OFFER_CREATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(OFFER_CREATED.name())
                        .responseMessage(OFFER_CREATED.getMessage())
                        .build());
    }

    @ApiOperation("팀원 스카웃 제안")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "OFFER_CREATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / POSITION_FORMAT_INVALID"),
            @ApiResponse(responseCode = "401", description = " TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / TEAM_NOT_FOUND / CURRENT_TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "409", description = "EXISTING_CURRENT_TEAM")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/team/user/{user-id}/offer")
    public ResponseEntity<DefaultResDto<Object>> teamOffer(HttpServletRequest servletRequest,
                                                           @PathVariable(value = "user-id") String userId,
                                                           @RequestBody @Valid OfferSaveReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User leader = userService.findOneByUserId(token.get(0));
        userService.isNonExistingCurrentTeam(leader);
        Team team = teamService.findOne(leader.getCurrentTeamId().toString());
        User user = userService.findOneByUserId(userId);
        userService.validateCurrentTeam(user);
        teamService.validatePositionAvailability(team, Position.fromString(request.getPosition()).getType());
        teamService.validateLeader(team, leader);

        Offer offer = offerService.save(request.teamOfferToEntity(user.getId(), team.getId()));
        userService.addRecruit(user, offer.getId());
        teamService.addRecruit(team, offer.getId());

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
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / TEAM_NOT_FOUND / CURRENT_TEAM_NOT_FOUND"),
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
        userService.isNonExistingCurrentTeam(leader);
        Team team = teamService.findOne(leader.getCurrentTeamId().toString());
        teamService.validateLeader(team, leader);

        teamService.updateIsPublic(team, request.getIsPublic());

        return ResponseEntity.status(TEAM_VISIBILITY_UPDATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(TEAM_VISIBILITY_UPDATED.name())
                        .responseMessage(TEAM_VISIBILITY_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "지원자에게 받은 제안 다건 조회", notes = "position = designer || backend || frontend || pm")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OFFERS_FOUND / OFFER_ZERO",
                    content = @Content(schema = @Schema(implementation = OfferDefaultResDto.class))),
            @ApiResponse(responseCode = "400", description = "POSITION_FORMAT_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED / ROLE_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / TEAM_NOT_FOUND / CURRENT_TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @GetMapping("/team/offer")
    public ResponseEntity<DefaultResDto<Object>> findManyApplicantOffer(HttpServletRequest servletRequest,
                                                                        @RequestParam Integer pageFrom,
                                                                        @RequestParam(required = false) Integer pageNum,
                                                                        @RequestParam String position) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User leader = userService.findOneByUserId(token.get(0));
        userService.isNonExistingCurrentTeam(leader);
        Team team = teamService.findOne(leader.getCurrentTeamId().toString());
        teamService.validateLeader(team, leader);

        Page<Offer> offers = offerService.findManyByTeamAndPosition(team.getId(),
                Position.fromString(position),
                true,
                pageFrom,
                pageNum);

        if (offers.getNumberOfElements() == 0) {

            return ResponseEntity.status(OFFER_ZERO.getHttpStatus())
                    .body(DefaultResDto.builder()
                            .responseCode(OFFER_ZERO.name())
                            .responseMessage(OFFER_ZERO.getMessage())
                            .totalPageNum(offers.getTotalPages())
                            .build());
        } else {

            List<OfferDefaultResDto> responseBodies = new ArrayList<>();
            for (Offer o : offers)
                responseBodies.add(new OfferDefaultResDto(o));

            return ResponseEntity.status(OFFERS_FOUND.getHttpStatus())
                    .body(DefaultResDto.builder()
                            .responseCode(OFFERS_FOUND.name())
                            .responseMessage(OFFERS_FOUND.getMessage())
                            .data(responseBodies)
                            .totalPageNum(offers.getTotalPages())
                            .build());
        }
    }

    @ApiOperation(value = "팀에게 받은 제안 다건 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OFFERS_FOUND / OFFER_ZERO",
                    content = @Content(schema = @Schema(implementation = OfferDefaultResDto.class))),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED / ROLE_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @GetMapping("/user/offer")
    public ResponseEntity<DefaultResDto<Object>> findManyTeamOffer(HttpServletRequest servletRequest,
                                                                   @RequestParam Integer pageFrom,
                                                                   @RequestParam(required = false) Integer pageNum) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User user = userService.findOneByUserId(token.get(0));

        Page<Offer> offers = offerService.findManyByApplicant(user.getId(),false, pageFrom, pageNum);

        if (offers.getNumberOfElements() == 0) {

            return ResponseEntity.status(OFFER_ZERO.getHttpStatus())
                    .body(DefaultResDto.builder()
                            .responseCode(OFFER_ZERO.name())
                            .responseMessage(OFFER_ZERO.getMessage())
                            .totalPageNum(offers.getTotalPages())
                            .build());
        } else {

            List<OfferDefaultResDto> responseBodies = new ArrayList<>();
            for (Offer o : offers)
                responseBodies.add(new OfferDefaultResDto(o));

            return ResponseEntity.status(OFFERS_FOUND.getHttpStatus())
                    .body(DefaultResDto.builder()
                            .responseCode(OFFERS_FOUND.name())
                            .responseMessage(OFFERS_FOUND.getMessage())
                            .data(responseBodies)
                            .totalPageNum(offers.getTotalPages())
                            .build());
        }
    }

    @ApiOperation(value = "지원자로부터 온 제안 결정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OFFER_RESULT_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED / ROLE_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404",
                    description = "USER_NOT_FOUND / TEAM_NOT_FOUND / CURRENT_TEAM_NOT_FOUND / OFFER_NOT_FOUND"),
            @ApiResponse(responseCode = "409", description = "EXISTING_CURRENT_TEAM"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PatchMapping("/team/offer/{offer-id}")
    public ResponseEntity<DefaultResDto<Object>> decideApplicantOffer(HttpServletRequest servletRequest,
                                                                      @PathVariable(value = "offer-id") String offerId,
                                                                      @RequestBody @Valid OfferUpdateReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User leader = userService.findOneByUserId(token.get(0));
        userService.isNonExistingCurrentTeam(leader);
        Team team = teamService.findOne(leader.getCurrentTeamId().toString());
        teamService.validateLeader(team, leader);
        Offer offer = offerService.findOne(offerId);
        offerService.validateOfferRole(offer.getId(), team.getApplicationIds());
        User applicant = userService.findOneByUserId(offer.getApplicantId().toString());

        offerService.updateIsAccepted(offer, request.getIsAccepted());

        if (request.getIsAccepted()) {

            userService.validateCurrentTeam(applicant);
            teamService.validatePositionAvailability(team, offer.getPosition());

            teamService.join(team, applicant, offer.getPosition());
        }

        userService.removeApplication(applicant, offer.getId());
        teamService.removeApplication(team, offer.getId());

        return ResponseEntity.status(OFFER_RESULT_UPDATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(OFFER_RESULT_UPDATED.name())
                        .responseMessage(OFFER_RESULT_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "팀으로부터 온 제안 결정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OFFER_RESULT_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / TEAM_NOT_FOUND / OFFER_NOT_FOUND"),
            @ApiResponse(responseCode = "409", description = "EXISTING_CURRENT_TEAM"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PatchMapping("/user/offer/{offer-id}")
    public ResponseEntity<DefaultResDto<Object>> decideTeamOffer(HttpServletRequest servletRequest,
                                                                 @PathVariable(value = "offer-id") String offerId,
                                                                 @RequestBody @Valid OfferUpdateReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User applicant = userService.findOneByUserId(token.get(0));
        Offer offer = offerService.findOne(offerId);
        Team team = teamService.findOne(offer.getTeamId().toString());

        offerService.updateIsAccepted(offer, request.getIsAccepted());
        if (request.getIsAccepted()) {

            userService.validateCurrentTeam(applicant);
            teamService.validatePositionAvailability(team, offer.getPosition());

            teamService.join(team, applicant, offer.getPosition());
        }

        userService.removeRecruit(applicant, offer.getId());
        teamService.removeRecruit(team, offer.getId());

        return ResponseEntity.status(OFFER_RESULT_UPDATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(OFFER_RESULT_UPDATED.name())
                        .responseMessage(OFFER_RESULT_UPDATED.getMessage())
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
    @DeleteMapping("/team")
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
                .body(DefaultResDto.builder()
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
    @PatchMapping("/team/user/{user-id}/fire")
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
        Position position = teamService.validatePositionInCurrentTeam(team, teammate);

        teamService.leaveTeam(team, teammate, position);

        return ResponseEntity.status(TEAMMATE_FIRED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(TEAMMATE_FIRED.name())
                        .responseMessage(TEAMMATES_FOUND.getMessage())
                        .build());
    }

    @ApiOperation(value = "팀 탈퇴")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TEAM_LEFT",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "409", description = "TEAM_LEADER_CONFLICT"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PatchMapping("/user/team/leave")
    public ResponseEntity<DefaultResDto<Object>> leaveTeam(HttpServletRequest servletRequest) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User user = userService.findOneByUserId(token.get(0));
        userService.isNonExistingCurrentTeam(user);
        Team team = teamService.findOne(user.getCurrentTeamId().toString());
        teamService.validateNonLeader(team, user);
        Position position = teamService.validatePositionInCurrentTeam(team, user);

        teamService.leaveTeam(team, user, position);

        return ResponseEntity.status(TEAM_LEFT.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(TEAM_LEFT.name())
                        .responseMessage(TEAM_LEFT.getMessage())
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
    @PatchMapping("/team/complete")
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
                .body(DefaultResDto.builder()
                        .responseCode(TEAM_PROJECT_COMPLETE.name())
                        .responseMessage(TEAM_VISIBILITY_UPDATED.getMessage())
                        .build());
    }
}
