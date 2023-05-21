package com.gabojait.gabojaitspring.offer.controller;

import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.dto.DefaultResDto;
import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.offer.dto.req.OfferDefaultReqDto;
import com.gabojait.gabojaitspring.offer.dto.res.OfferDefaultResDto;
import com.gabojait.gabojaitspring.offer.service.OfferService;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import java.util.ArrayList;
import java.util.List;

import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;
import static java.util.Map.of;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "제안")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OfferController {

    private final OfferService offerService;
    private final UserService userService;
    private final TeamService teamService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "회원이 팀에 지원",
            notes = "<응답 코드>\n" +
                    "- 201 = OFFERED_BY_USER\n" +
                    "- 400 = POSITION_FIELD_REQUIRED || POSITION_TYPE_INVALID || ID_CONVERT_INVALID\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = TEAM_NOT_FOUND || USER_NOT_FOUND\n" +
                    "- 409 = EXISTING_CURRENT_TEAM || TEAM_POSITION_UNAVAILABLE\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
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
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/user/team/{team-id}/offer")
    public ResponseEntity<DefaultResDto<Object>> userOffer(HttpServletRequest servletRequest,
                                                           @PathVariable(value = "team-id") String teamId,
                                                           @RequestBody @Valid OfferDefaultReqDto request) {
        // auth
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // sub
        userService.validateHasNoCurrentTeam(user);
        teamService.validatePreOfferByUser(teamId, request.getPosition());
        // main
        ObjectId offerId = offerService.offer(request, user.getId().toString(), teamId, true);
        teamService.offer(teamId, offerId, true);
        userService.offer(user.getId().toString(), offerId, true);

        return ResponseEntity.status(OFFERED_BY_USER.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(OFFERED_BY_USER.name())
                        .responseMessage(OFFERED_BY_USER.getMessage())
                        .build());
    }

    @ApiOperation(value = "팀이 회원에게 채용 제안",
            notes = "<응답 코드>\n" +
                    "- 201 = OFFERED_BY_TEAM\n" +
                    "- 400 = POSITION_FIELD_REQUIRED || POSITION_TYPE_INVALID || ID_CONVERT_INVALID\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED || REQUEST_FORBIDDEN\n" +
                    "- 404 = TEAM_NOT_FOUND || USER_NOT_FOUND\n" +
                    "- 409 = NON_EXISTING_CURRENT_TEAM || EXISTING_CURRENT_TEAM || TEAM_POSITION_UNAVAILABLE\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
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
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/team/user/{user-id}/offer")
    public ResponseEntity<DefaultResDto<Object>> teamOffer(HttpServletRequest servletRequest,
                                                           @PathVariable(value = "user-id") String userId,
                                                           @RequestBody @Valid OfferDefaultReqDto request) {
        // auth
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // sub
        userService.validateHasCurrentTeam(user);
        User otherUser = userService.findOneById(userId);
        userService.validateHasNoCurrentTeam(otherUser);
        teamService.validatePreOfferByTeam(user.getCurrentTeamId().toString(),
                user.getId().toString(),
                userId,
                request.getPosition());
        // main
        ObjectId offerId = offerService.offer(request, userId, user.getCurrentTeamId().toString(), false);
        teamService.offer(user.getCurrentTeamId().toString(), offerId, false);
        userService.offer(userId, offerId, false);

        return ResponseEntity.status(OFFERED_BY_TEAM.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(OFFERED_BY_TEAM.name())
                        .responseMessage(OFFERED_BY_TEAM.getMessage())
                        .build());
    }

    @ApiOperation(value = "회원이 받은 제안 다건 조희",
            notes = "<응답 코드>\n" +
                    "- 200 = OFFER_BY_TEAM_FOUND\n" +
                    "- 400 = PAGE_FROM_FIELD_REQUIRED || PAGE_FROM_POS_OR_ZERO_ONLY || PAGE_SIZE_POS_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = OfferDefaultResDto.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/user/offer")
    public ResponseEntity<DefaultResDto<Object>> decideTeamOffer(
            HttpServletRequest servletRequest,
            @RequestParam(value = "page-from")
            @NotNull(message = "페이지 시작점은 필수 입력란입니다.")
            @PositiveOrZero(message = "페이지 시작점은 0 또는 양수만 가능합니다.")
            Integer pageFrom,
            @RequestParam(value = "page-size", required = false)
            @Positive(message = "페이지 사이즈는 양수만 가능합니다.")
            Integer pageSize
    ) {
        // auth
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // sub
        userService.validateHasNoCurrentTeam(user);
        // main
        Page<Offer> offers = offerService.findPageByUserId(user.getId(), pageFrom, pageSize);

        // response
        List<OfferDefaultResDto> responses = new ArrayList<>();
        for (Offer offer : offers)
            responses.add(new OfferDefaultResDto(offer));

        return ResponseEntity.status(OFFER_BY_TEAM_FOUND.getHttpStatus())
                .body(DefaultResDto.multiDataBuilder()
                        .responseCode(OFFER_BY_TEAM_FOUND.name())
                        .responseMessage(OFFER_BY_TEAM_FOUND.getMessage())
                        .data(responses)
                        .size(offers.getTotalPages())
                        .build());
    }
}
