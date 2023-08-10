package com.gabojait.gabojaitspring.offer.controller;

import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.dto.DefaultMultiResDto;
import com.gabojait.gabojaitspring.common.dto.DefaultNoResDto;
import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.offer.dto.req.OfferCreateReqDto;
import com.gabojait.gabojaitspring.offer.dto.req.OfferUpdateReqDto;
import com.gabojait.gabojaitspring.offer.dto.res.OfferDefaultResDto;
import com.gabojait.gabojaitspring.offer.service.OfferService;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
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
import javax.validation.GroupSequence;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;
import static com.gabojait.gabojaitspring.common.code.SuccessCode.OFFER_CANCEL_BY_USER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "제안")
@Validated
@GroupSequence({OfferController.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OfferController {

    private final OfferService offerService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "회원이 팀에 지원",
            notes = "<응답 코드>\n" +
                    "- 201 = OFFERED_BY_USER\n" +
                    "- 400 = POSITION_FIELD_REQUIRED || TEAM_ID_FIELD_REQUIRED || POSITION_TYPE_INVALID || " +
                    "TEAM_ID_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND || TEAM_NOT_FOUND\n" +
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
    public ResponseEntity<DefaultNoResDto> userOffer(
            HttpServletRequest servletRequest,
            @PathVariable(value = "team-id", required = false)
            @NotNull(message = "팀 식별자는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
            @Positive(message = "팀 식별자는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
            Long teamId,
            @RequestBody @Valid OfferCreateReqDto request) {
        long userId = jwtProvider.getId(servletRequest.getHeader(AUTHORIZATION));

        offerService.offerByUser(userId, teamId, request);

        return ResponseEntity.status(OFFERED_BY_USER.getHttpStatus())
                .body(DefaultNoResDto.noDataBuilder()
                        .responseCode(OFFERED_BY_USER.name())
                        .responseMessage(OFFERED_BY_USER.getMessage())
                        .build());
    }

    @ApiOperation(value = "팀이 회원에게 스카웃",
            notes = "<응답 코드>\n" +
                    "- 201 = OFFERED_BY_TEAM\n" +
                    "- 400 = POSITION_FIELD_REQUIRED || USER_ID_FIELD_REQUIRED || POSITION_TYPE_INVALID || " +
                    "USER_ID_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED || REQUEST_FORBIDDEN\n" +
                    "- 404 = USER_NOT_FOUND\n" +
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
    @PostMapping("/team/user/{user-id}/offer")
    public ResponseEntity<DefaultNoResDto> teamOffer(
            HttpServletRequest servletRequest,
            @PathVariable(value = "user-id", required = false)
            @NotNull(message = "회원 식별자는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
            @Positive(message = "회원 식별자는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
            Long userId,
            @RequestBody @Valid OfferCreateReqDto request
    ) {
        long uId = jwtProvider.getId(servletRequest.getHeader(AUTHORIZATION));

        offerService.offerByTeam(uId, userId, request);

        return ResponseEntity.status(OFFERED_BY_TEAM.getHttpStatus())
                .body(DefaultNoResDto.noDataBuilder()
                        .responseCode(OFFERED_BY_TEAM.name())
                        .responseMessage(OFFERED_BY_TEAM.getMessage())
                        .build());
    }

    @ApiOperation(value = "회원이 받은 제안 다건 조희",
            notes = "<응답 코드>\n" +
                    "- 200 = OFFER_BY_TEAM_FOUND\n" +
                    "- 400 = PAGE_FROM_FIELD_REQUIRED || PAGE_FROM_POSITIVE_OR_ZERO_ONLY || PAGE_SIZE_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = OfferDefaultResDto.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/user/offer")
    public ResponseEntity<DefaultMultiResDto<Object>> userFindOffers(
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

        Page<Offer> offers = offerService.findManyOffersByUser(userId, pageFrom, pageSize);

        List<OfferDefaultResDto> responses = new ArrayList<>();
        for(Offer offer : offers)
            responses.add(new OfferDefaultResDto(offer));

        return ResponseEntity.status(OFFER_BY_TEAM_FOUND.getHttpStatus())
                .body(DefaultMultiResDto.multiDataBuilder()
                        .responseCode(OFFER_BY_TEAM_FOUND.name())
                        .responseMessage(OFFER_BY_TEAM_FOUND.getMessage())
                        .data(responses)
                        .size(offers.getTotalElements())
                        .build());
    }

    @ApiOperation(value = "팀이 받은 제안 다건 조회",
            notes = "<옵션>\n" +
                    "- position[default: NONE] = DESIGNER(디자이너) || BACKEND(백엔드) || FRONTEND(프론트엔드) || " +
                    "MANAGER(매니저) || NONE(전체)\n" +
                    "<응답 코드>\n" +
                    "- 200 = OFFER_BY_USER_FOUND\n" +
                    "- 400 = PAGE_FROM_FIELD_REQUIRED || POSITION_FIELD_REQUIRED || PAGE_FROM_POSITIVE_OR_ZERO_ONLY " +
                    "|| PAGE_SIZE_POSITIVE_ONLY || POSITION_TYPE_INVALID\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED || REQUEST_FORBIDDEN\n" +
                    "- 404 = USER_NOT_FOUND\n" +
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
    @GetMapping("/team/offer")
    public ResponseEntity<DefaultMultiResDto<Object>> teamFindOffers(
            HttpServletRequest servletRequest,
            @RequestParam(value = "page-from", required = false)
            @NotNull(message = "페이지 시작점은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
            @PositiveOrZero(message = "페이지 시작점은 0 또는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
            Integer pageFrom,
            @RequestParam(value = "page-size", required = false)
            @Positive(message = "페이지 사이즈는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
            Integer pageSize,
            @RequestParam(value = "position", required = false)
            @NotBlank(message = "포지션은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
            @Pattern(regexp = "^(DESIGNER|BACKEND|FRONTEND|MANAGER|NONE)",
                    message = "포지션은 'DESIGNER', 'BACKEND', 'FRONTEND', 'MANAGER', 또는 'NONE' 중 하나여야 됩니다.",
                    groups = ValidationSequence.Format.class)
            String position
    ) {
        long userId = jwtProvider.getId(servletRequest.getHeader(AUTHORIZATION));

        Page<Offer> offers = offerService.findManyOffersByTeam(userId, pageFrom, pageSize, Position.valueOf(position));

        List<OfferDefaultResDto> responses = new ArrayList<>();
        for (Offer offer : offers)
            responses.add(new OfferDefaultResDto(offer));

        return ResponseEntity.status(OFFER_BY_USER_FOUND.getHttpStatus())
                .body(DefaultMultiResDto.multiDataBuilder()
                        .responseCode(OFFER_BY_USER_FOUND.name())
                        .responseMessage(OFFER_BY_USER_FOUND.getMessage())
                        .data(responses)
                        .size(offers.getTotalElements())
                        .build());
    }

    @ApiOperation(value = "회원이 받은 제안 결정",
            notes = "<응답 코드>\n" +
                    "- 200 = USER_DECIDED_OFFER\n" +
                    "- 400 = IS_ACCEPTED_FIELD_REQUIRED || OFFER_ID_FIELD_REQUIRED || OFFER_ID_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND || OFFER_NOT_FOUND\n" +
                    "- 409 = EXISTING_CURRENT_TEAM || TEAM_POSITION_UNAVAILABLE\n" +
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
    @PatchMapping("/user/offer/{offer-id}")
    public ResponseEntity<DefaultNoResDto> decideOfferByUser(
            HttpServletRequest servletRequest,
            @PathVariable(value = "offer-id", required = false)
            @NotNull(message = "제안 식별자는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
            @Positive(message = "제안 식별자는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
            Long offerId,
            @RequestBody @Valid
            OfferUpdateReqDto request
    ) {
        long userId = jwtProvider.getId(servletRequest.getHeader(AUTHORIZATION));

        offerService.decideByUser(userId, offerId, request.getIsAccepted());

        return ResponseEntity.status(USER_DECIDED_OFFER.getHttpStatus())
                .body(DefaultNoResDto.noDataBuilder()
                        .responseCode(USER_DECIDED_OFFER.name())
                        .responseMessage(USER_DECIDED_OFFER.getMessage())
                        .build());
    }

    @ApiOperation(value = "팀이 받은 제안 결정",
            notes = "<응답 코드>\n" +
                    "- 200 = TEAM_DECIDED_OFFER\n" +
                    "- 400 = IS_ACCEPTED_FIELD_REQUIRED || OFFER_ID_FIELD_REQUIRED || OFFER_ID_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED || REQUEST_FORBIDDEN\n" +
                    "- 404 = USER_NOT_FOUND || OFFER_NOT_FOUND\n" +
                    "- 409 = EXISTING_CURRENT_TEAM || TEAM_POSITION_UNAVAILABLE\n" +
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
    @PatchMapping("/team/offer/{offer-id}")
    public ResponseEntity<DefaultNoResDto> decideOfferByTeam(
            HttpServletRequest servletRequest,
            @PathVariable(value = "offer-id", required = false)
            @NotNull(message = "제안 식별자는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
            @Positive(message = "제안 식별자는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
            Long offerId,
            @RequestBody @Valid
            OfferUpdateReqDto request
    ) {
        long userId = jwtProvider.getId(servletRequest.getHeader(AUTHORIZATION));

        offerService.decideByTeam(userId, offerId, request.getIsAccepted());

        return ResponseEntity.status(TEAM_DECIDED_OFFER.getHttpStatus())
                .body(DefaultNoResDto.noDataBuilder()
                        .responseCode(TEAM_DECIDED_OFFER.name())
                        .responseMessage(TEAM_DECIDED_OFFER.getMessage())
                        .build());
    }

    @ApiOperation(value = "회원이 보낸 제안 취소",
            notes = "<응답 코드>\n" +
                    "- 200 = OFFER_CANCEL_BY_USER\n" +
                    "- 400 = OFFER_ID_FIELD_REQUIRED || OFFER_ID_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND || OFFER_NOT_FOUND\n" +
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
    @DeleteMapping("/user/offer/{offer-id}")
    public ResponseEntity<DefaultNoResDto> cancelOfferByUser(
            HttpServletRequest servletRequest,
            @PathVariable(value = "offer-id", required = false)
            @NotNull(message = "제안 식별자는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
            @Positive(message = "제안 식별자는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
            Long offerId
    ) {
        long userId = jwtProvider.getId(servletRequest.getHeader(AUTHORIZATION));

        offerService.cancelByUser(userId, offerId);

        return ResponseEntity.status(OFFER_CANCEL_BY_USER.getHttpStatus())
                .body(DefaultNoResDto.noDataBuilder()
                        .responseCode(OFFER_CANCEL_BY_USER.name())
                        .responseMessage(OFFER_CANCEL_BY_USER.getMessage())
                        .build());
    }

    @ApiOperation(value = "팀이 보낸 제안 취소",
            notes = "<응답 코드>\n" +
                    "- 200 = OFFER_CANCEL_BY_TEAM\n" +
                    "- 400 = OFFER_ID_FIELD_REQUIRED || OFFER_ID_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED || REQUEST_FORBIDDEN\n" +
                    "- 404 = USER_NOT_FOUND || OFFER_NOT_FOUND\n" +
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
    @DeleteMapping("/team/offer/{offer-id}")
    public ResponseEntity<DefaultNoResDto> cancelOfferByTeam(
            HttpServletRequest servletRequest,
            @PathVariable(value = "offer-id", required = false)
            @NotNull(message = "제안 식별자는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
            @Positive(message = "제안 식별자는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
            Long offerId
    ) {
        long userId = jwtProvider.getId(servletRequest.getHeader(AUTHORIZATION));

        offerService.cancelByTeam(userId, offerId);

        return ResponseEntity.status(OFFER_CANCEL_BY_TEAM.getHttpStatus())
                .body(DefaultNoResDto.noDataBuilder()
                        .responseCode(OFFER_CANCEL_BY_TEAM.name())
                        .responseMessage(OFFER_CANCEL_BY_TEAM.getMessage())
                        .build());
    }
}
