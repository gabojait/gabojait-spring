package com.inuappcenter.gabojaitspring.offer.controller;

import com.inuappcenter.gabojaitspring.auth.JwtProvider;
import com.inuappcenter.gabojaitspring.auth.JwtType;
import com.inuappcenter.gabojaitspring.common.DefaultResDto;
import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.type.Position;
import com.inuappcenter.gabojaitspring.offer.domain.Offer;
import com.inuappcenter.gabojaitspring.profile.domain.type.TeamMemberStatus;
import com.inuappcenter.gabojaitspring.team.domain.Team;
import com.inuappcenter.gabojaitspring.offer.dto.req.OfferSaveReqDto;
import com.inuappcenter.gabojaitspring.offer.dto.req.OfferUpdateReqDto;
import com.inuappcenter.gabojaitspring.offer.dto.res.OfferDefaultResDto;
import com.inuappcenter.gabojaitspring.offer.service.OfferService;
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
import static com.inuappcenter.gabojaitspring.common.SuccessCode.OFFER_RESULT_UPDATED;
import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.TOKEN_NOT_ALLOWED;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "제안")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OfferController {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final TeamService teamService;
    private final OfferService offerService;

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
                .body(DefaultResDto.NoDataBuilder()
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
                .body(DefaultResDto.NoDataBuilder()
                        .responseCode(OFFER_CREATED.name())
                        .responseMessage(OFFER_CREATED.getMessage())
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

            teamService.join(team, applicant, offer.getPosition(), TeamMemberStatus.MEMBER);
        }

        userService.removeRecruit(applicant, offer.getId());
        teamService.removeRecruit(team, offer.getId());

        return ResponseEntity.status(OFFER_RESULT_UPDATED.getHttpStatus())
                .body(DefaultResDto.NoDataBuilder()
                        .responseCode(OFFER_RESULT_UPDATED.name())
                        .responseMessage(OFFER_RESULT_UPDATED.getMessage())
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
                                                                        @RequestParam(required = false) Integer pageSize,
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
                pageSize);

        if (offers.getNumberOfElements() == 0) {

            return ResponseEntity.status(OFFER_ZERO.getHttpStatus())
                    .body(DefaultResDto.MultiDataBuilder()
                            .responseCode(OFFER_ZERO.name())
                            .responseMessage(OFFER_ZERO.getMessage())
                            .data(null)
                            .size(offers.getTotalPages())
                            .build());
        } else {

            List<OfferDefaultResDto> responseBodies = new ArrayList<>();
            for (Offer o : offers)
                responseBodies.add(new OfferDefaultResDto(o));

            return ResponseEntity.status(OFFERS_FOUND.getHttpStatus())
                    .body(DefaultResDto.MultiDataBuilder()
                            .responseCode(OFFERS_FOUND.name())
                            .responseMessage(OFFERS_FOUND.getMessage())
                            .data(responseBodies)
                            .size(offers.getTotalPages())
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
                                                                   @RequestParam(required = false) Integer pageSize) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User user = userService.findOneByUserId(token.get(0));

        Page<Offer> offers = offerService.findManyByApplicant(user.getId(),false, pageFrom, pageSize);

        if (offers.getNumberOfElements() == 0) {

            return ResponseEntity.status(OFFER_ZERO.getHttpStatus())
                    .body(DefaultResDto.MultiDataBuilder()
                            .responseCode(OFFER_ZERO.name())
                            .responseMessage(OFFER_ZERO.getMessage())
                            .data(null)
                            .size(offers.getTotalPages())
                            .build());
        } else {

            List<OfferDefaultResDto> responseBodies = new ArrayList<>();
            for (Offer o : offers)
                responseBodies.add(new OfferDefaultResDto(o));

            return ResponseEntity.status(OFFERS_FOUND.getHttpStatus())
                    .body(DefaultResDto.MultiDataBuilder()
                            .responseCode(OFFERS_FOUND.name())
                            .responseMessage(OFFERS_FOUND.getMessage())
                            .data(responseBodies)
                            .size(offers.getTotalPages())
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

            teamService.join(team, applicant, offer.getPosition(), TeamMemberStatus.MEMBER);
        }

        userService.removeApplication(applicant, offer.getId());
        teamService.removeApplication(team, offer.getId());

        return ResponseEntity.status(OFFER_RESULT_UPDATED.getHttpStatus())
                .body(DefaultResDto.NoDataBuilder()
                        .responseCode(OFFER_RESULT_UPDATED.name())
                        .responseMessage(OFFER_RESULT_UPDATED.getMessage())
                        .build());
    }
}
