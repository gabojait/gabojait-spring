package com.gabojait.gabojaitspring.api.controller.offer;

import com.gabojait.gabojaitspring.common.response.DefaultMultiResponse;
import com.gabojait.gabojaitspring.common.response.DefaultNoResponse;
import com.gabojait.gabojaitspring.common.response.PageData;
import com.gabojait.gabojaitspring.api.dto.offer.request.OfferCreateRequest;
import com.gabojait.gabojaitspring.api.dto.offer.request.OfferDecideRequest;
import com.gabojait.gabojaitspring.api.dto.offer.response.OfferPageResponse;
import com.gabojait.gabojaitspring.api.service.offer.OfferService;
import com.gabojait.gabojaitspring.config.auth.JwtProvider;
import com.gabojait.gabojaitspring.domain.offer.OfferedBy;
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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.util.List;

import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "제안")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OfferController {

    private final OfferService offerService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "회원이 팀에 지원",
            notes = "<응답 코드>\n" +
                    "- 201 = OFFERED_BY_USER\n" +
                    "- 400 = OFFER_POSITION_TYPE_INVALID || TEAM_ID_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND || TEAM_NOT_FOUND\n" +
                    "- 409 = TEAM_POSITION_UNAVAILABLE\n" +
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
    public ResponseEntity<DefaultNoResponse> userOffer(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @PathVariable(value = "team-id")
            @Positive(message = "팀 식별자는 양수만 가능합니다.")
            Long teamId,
            @RequestBody @Valid OfferCreateRequest request
    ) {
        long userId = jwtProvider.getUserId(authorization);

        offerService.offerByUser(userId, teamId, request);

        return ResponseEntity.status(OFFERED_BY_USER.getHttpStatus())
                .body(DefaultNoResponse.noDataBuilder()
                        .responseCode(OFFERED_BY_USER.name())
                        .responseMessage(OFFERED_BY_USER.getMessage())
                        .build());
    }

    @ApiOperation(value = "팀이 회원에게 스카웃",
            notes = "<응답 코드>\n" +
                    "- 201 = OFFERED_BY_TEAM\n" +
                    "- 400 = OFFER_POSITION_TYPE_INVALID || USER_ID_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED || REQUEST_FORBIDDEN\n" +
                    "- 404 = USER_NOT_FOUND || CURRENT_TEAM_NOT_FOUND\n" +
                    "- 409 = TEAM_POSITION_UNAVAILABLE\n" +
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
    public ResponseEntity<DefaultNoResponse> teamOffer(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @PathVariable(value = "user-id")
            @Positive(message = "회원 식별자는 양수만 가능합니다.")
            Long userId,
            @RequestBody @Valid OfferCreateRequest request
    ) {
        long myUserId = jwtProvider.getUserId(authorization);

        offerService.offerByTeam(myUserId, userId, request);

        return ResponseEntity.status(OFFERED_BY_TEAM.getHttpStatus())
                .body(DefaultNoResponse.noDataBuilder()
                        .responseCode(OFFERED_BY_TEAM.name())
                        .responseMessage(OFFERED_BY_TEAM.getMessage())
                        .build());
    }

    @ApiOperation(value = "회원이 받은 제안 페이징 조희",
            notes = "<검증>\n" +
                    "- page-from[default: 9223372036854775806] = Positive\n" +
                    "- page-size[default: 20] = Positive && Max(value = 100)\n\n" +
                    "<응답 코드>\n" +
                    "- 200 = USER_RECEIVED_OFFER_FOUND\n" +
                    "- 400 = PAGE_FROM_POSITIVE_ONLY || PAGE_SIZE_POSITIVE_ONLY || PAGE_SIZE_RANGE_INVALID\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = OfferPageResponse.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/user/offer/received")
    public ResponseEntity<DefaultMultiResponse<Object>> findPageUserReceivedOffer(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @RequestParam(value = "page-from", required = false, defaultValue = "9223372036854775806")
            @Positive(message = "페이지 시작점은 양수만 가능합니다.")
            Long pageFrom,
            @RequestParam(value = "page-size", required = false, defaultValue = "20")
            @Positive(message = "페이지 사이즈는 양수만 가능합니다.")
            @Max(value = 100, message = "페이지 사이즈는 100까지의 수만 가능합니다.")
            Integer pageSize
    ) {
        long userId = jwtProvider.getUserId(authorization);

        PageData<List<OfferPageResponse>> responses = offerService.findPageUserOffer(userId, OfferedBy.LEADER,
                pageFrom, pageSize);

        return ResponseEntity.status(USER_RECEIVED_OFFER_FOUND.getHttpStatus())
                .body(DefaultMultiResponse.multiDataBuilder()
                        .responseCode(USER_RECEIVED_OFFER_FOUND.name())
                        .responseMessage(USER_RECEIVED_OFFER_FOUND.getMessage())
                        .responseData(responses)
                        .build());
    }

    @ApiOperation(value = "팀이 받은 제안 페이징 조회",
            notes = "<옵션>\n" +
                    "- position = DESIGNER(디자이너) || BACKEND(백엔드) || FRONTEND(프론트엔드) || MANAGER(매니저)\n\n" +
                    "<검증>\n" +
                    "- page-from[default: 9223372036854775806] = Positive\n" +
                    "- page-size[default: 20] = Positive && Max(value = 100)\n" +
                    "- position = Pattern\n\n" +
                    "<응답 코드>\n" +
                    "- 200 = TEAM_RECEIVED_OFFER_FOUND\n" +
                    "- 400 = OFFER_POSITION_FIELD_REQUIRED || PAGE_FROM_POSITIVE_ONLY || PAGE_SIZE_POSITIVE_ONLY || " +
                    "OFFER_POSITION_TYPE_INVALID || PAGE_SIZE_RANGE_INVALID\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED || REQUEST_FORBIDDEN\n" +
                    "- 404 = USER_NOT_FOUND || CURRENT_TEAM_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = OfferPageResponse.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/team/offer/received")
    public ResponseEntity<DefaultMultiResponse<Object>> findPageTeamReceivedOffer(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @RequestParam(value = "page-from", required = false, defaultValue = "9223372036854775806")
            @Positive(message = "페이지 시작점은 양수만 가능합니다.")
            Long pageFrom,
            @RequestParam(value = "page-size", required = false, defaultValue = "20")
            @Positive(message = "페이지 사이즈는 양수만 가능합니다.")
            @Max(value = 100, message = "페이지 사이즈는 100까지의 수만 가능합니다.")
            Integer pageSize,
            @RequestParam(value = "position", required = false)
            @NotBlank(message = "제안할 포지션은 필수 입력입니다.")
            @Pattern(regexp = "^(DESIGNER|BACKEND|FRONTEND|MANAGER)",
                    message = "제안할 포지션은 'DESIGNER', 'BACKEND', 'FRONTEND', 또는 'MANAGER' 중 하나여야 됩니다.")
            String offerPosition
    ) {
        long userId = jwtProvider.getUserId(authorization);

        PageData<List<OfferPageResponse>> responses = offerService.findPageTeamOffer(userId,
                Position.valueOf(offerPosition), OfferedBy.USER, pageFrom, pageSize);

        return ResponseEntity.status(TEAM_RECEIVED_OFFER_FOUND.getHttpStatus())
                .body(DefaultMultiResponse.multiDataBuilder()
                        .responseCode(TEAM_RECEIVED_OFFER_FOUND.name())
                        .responseMessage(TEAM_RECEIVED_OFFER_FOUND.getMessage())
                        .responseData(responses)
                        .build());
    }

    @ApiOperation(value = "회원이 보낸 제안 페이징 조희",
            notes = "<검증>\n" +
                    "- page-from[default: 9223372036854775806] = Positive\n" +
                    "- page-size[default: 20] = Positive && Max(value = 100)\n\n" +
                    "<응답 코드>\n" +
                    "- 200 = USER_SENT_OFFER_FOUND\n" +
                    "- 400 = PAGE_FROM_POSITIVE_ONLY || PAGE_SIZE_POSITIVE_ONLY || PAGE_SIZE_RANGE_INVALID\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = OfferPageResponse.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/user/offer/sent")
    public ResponseEntity<DefaultMultiResponse<Object>> findPageUserSentOffer(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @RequestParam(value = "page-from", required = false, defaultValue = "9223372036854775806")
            @Positive(message = "페이지 시작점은 양수만 가능합니다.")
            Long pageFrom,
            @RequestParam(value = "page-size", required = false, defaultValue = "20")
            @Positive(message = "페이지 사이즈는 양수만 가능합니다.")
            @Max(value = 100, message = "페이지 사이즈는 100까지의 수만 가능합니다.")
            Integer pageSize
    ) {
        long userId = jwtProvider.getUserId(authorization);

        PageData<List<OfferPageResponse>> responses = offerService.findPageUserOffer(userId, OfferedBy.USER,
                pageFrom, pageSize);

        return ResponseEntity.status(USER_SENT_OFFER_FOUND.getHttpStatus())
                .body(DefaultMultiResponse.multiDataBuilder()
                        .responseCode(USER_SENT_OFFER_FOUND.name())
                        .responseMessage(USER_SENT_OFFER_FOUND.getMessage())
                        .responseData(responses)
                        .build());
    }

    @ApiOperation(value = "팀이 보낸 제안 페이징 조희",
            notes = "<옵션>\n" +
                    "- position = DESIGNER(디자이너) || BACKEND(백엔드) || FRONTEND(프론트엔드) || MANAGER(매니저)\n\n" +
                    "<검증>\n" +
                    "- page-from[default: 9223372036854775806] = Positive\n" +
                    "- page-size[default: 20] = Positive && Max\n" +
                    "- position = Pattern\n\n" +
                    "<응답 코드>\n" +
                    "- 200 = TEAM_SENT_OFFER_FOUND\n" +
                    "- 400 = OFFER_POSITION_FIELD_REQUIRED || PAGE_FROM_POSITIVE_ONLY || PAGE_SIZE_POSITIVE_ONLY || " +
                    "OFFER_POSITION_TYPE_INVALID || PAGE_SIZE_RANGE_INVALID\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED || REQUEST_FORBIDDEN\n" +
                    "- 404 = USER_NOT_FOUND || CURRENT_TEAM_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = OfferPageResponse.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/team/offer/sent")
    public ResponseEntity<DefaultMultiResponse<Object>> findPageTeamSentOffer(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @RequestParam(value = "page-from", required = false, defaultValue = "9223372036854775806")
            @Positive(message = "페이지 시작점은 양수만 가능합니다.")
            Long pageFrom,
            @RequestParam(value = "page-size", required = false, defaultValue = "20")
            @Positive(message = "페이지 사이즈는 양수만 가능합니다.")
            @Max(value = 100, message = "페이지 사이즈는 100까지의 수만 가능합니다.")
            Integer pageSize,
            @RequestParam(value = "position", required = false)
            @NotBlank(message = "제안할 포지션은 필수 입력입니다.")
            @Pattern(regexp = "^(DESIGNER|BACKEND|FRONTEND|MANAGER)",
                    message = "제안할 포지션은 'DESIGNER', 'BACKEND', 'FRONTEND', 또는 'MANAGER' 중 하나여야 됩니다.")
            String offerPosition
    ) {
        long userId = jwtProvider.getUserId(authorization);

        PageData<List<OfferPageResponse>> responses = offerService.findPageTeamOffer(userId,
                Position.valueOf(offerPosition), OfferedBy.LEADER, pageFrom, pageSize);

        return ResponseEntity.status(TEAM_SENT_OFFER_FOUND.getHttpStatus())
                .body(DefaultMultiResponse.multiDataBuilder()
                        .responseCode(TEAM_SENT_OFFER_FOUND.name())
                        .responseMessage(TEAM_SENT_OFFER_FOUND.getMessage())
                        .responseData(responses)
                        .build());
    }

    @ApiOperation(value = "회원이 받은 제안 결정",
            notes = "<응답 코드>\n" +
                    "- 200 = USER_DECIDED_OFFER\n" +
                    "- 400 = IS_ACCEPTED_FIELD_REQUIRED || OFFER_ID_POSITIVE_ONLY\n" +
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
    public ResponseEntity<DefaultNoResponse> userDecideOffer(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @PathVariable(value = "offer-id")
            @Positive(message = "제안 식별자는 양수만 가능합니다.")
            Long offerId,
            @RequestBody @Valid OfferDecideRequest request
    ) {
        long userId = jwtProvider.getUserId(authorization);

        offerService.userDecideOffer(userId, offerId, request);

        return ResponseEntity.status(USER_DECIDED_OFFER.getHttpStatus())
                .body(DefaultNoResponse.noDataBuilder()
                        .responseCode(USER_DECIDED_OFFER.name())
                        .responseMessage(USER_DECIDED_OFFER.getMessage())
                        .build());
    }

    @ApiOperation(value = "팀이 받은 제안 결정",
            notes = "<응답 코드>\n" +
                    "- 200 = TEAM_DECIDED_OFFER\n" +
                    "- 400 = IS_ACCEPTED_FIELD_REQUIRED || OFFER_ID_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED || REQUEST_FORBIDDEN\n" +
                    "- 404 = USER_NOT_FOUND || CURRENT_TEAM_NOT_FOUND || OFFER_NOT_FOUND\n" +
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
    @PatchMapping("/team/offer/{offer-id}")
    public ResponseEntity<DefaultNoResponse> teamDecideOffer(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @PathVariable(value = "offer-id")
            @Positive(message = "제안 식별자는 양수만 가능합니다.")
            Long offerId,
            @RequestBody @Valid OfferDecideRequest request
    ) {
        long userId = jwtProvider.getUserId(authorization);

        offerService.teamDecideOffer(userId, offerId, request);

        return ResponseEntity.status(TEAM_DECIDED_OFFER.getHttpStatus())
                .body(DefaultNoResponse.noDataBuilder()
                        .responseCode(TEAM_DECIDED_OFFER.name())
                        .responseMessage(TEAM_DECIDED_OFFER.getMessage())
                        .build());
    }

    @ApiOperation(value = "회원이 보낸 제안 취소",
            notes = "<응답 코드>\n" +
                    "- 200 = OFFER_CANCEL_BY_USER\n" +
                    "- 400 = OFFER_ID_POSITIVE_ONLY\n" +
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
    public ResponseEntity<DefaultNoResponse> cancelUserOffer(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @PathVariable(value = "offer-id")
            @Positive(message = "제안 식별자는 양수만 가능합니다.")
            Long offerId
    ) {
        long userId = jwtProvider.getUserId(authorization);

        offerService.cancelByUser(userId, offerId);

        return ResponseEntity.status(OFFER_CANCEL_BY_USER.getHttpStatus())
                .body(DefaultNoResponse.noDataBuilder()
                        .responseCode(OFFER_CANCEL_BY_USER.name())
                        .responseMessage(OFFER_CANCEL_BY_USER.getMessage())
                        .build());
    }

    @ApiOperation(value = "팀이 보낸 제안 취소",
            notes = "<응답 코드>\n" +
                    "- 200 = OFFER_CANCEL_BY_TEAM\n" +
                    "- 400 = OFFER_ID_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED || REQUEST_FORBIDDEN\n" +
                    "- 404 = USER_NOT_FOUND || CURRENT_TEAM_NOT_FOUND || OFFER_NOT_FOUND\n" +
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
    public ResponseEntity<DefaultNoResponse> cancelTeamOffer(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @PathVariable(value = "offer-id")
            @Positive(message = "제안 식별자는 양수만 가능합니다.")
            Long offerId
    ) {
        long userId = jwtProvider.getUserId(authorization);

        offerService.cancelByTeam(userId, offerId);

        return ResponseEntity.status(OFFER_CANCEL_BY_TEAM.getHttpStatus())
                .body(DefaultNoResponse.noDataBuilder()
                        .responseCode(OFFER_CANCEL_BY_TEAM.name())
                        .responseMessage(OFFER_CANCEL_BY_TEAM.getMessage())
                        .build());
    }
}
