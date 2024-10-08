package com.gabojait.gabojaitspring.api.controller.review;

import com.gabojait.gabojaitspring.common.response.DefaultMultiResponse;
import com.gabojait.gabojaitspring.common.response.DefaultNoResponse;
import com.gabojait.gabojaitspring.common.response.DefaultSingleResponse;
import com.gabojait.gabojaitspring.common.response.PageData;
import com.gabojait.gabojaitspring.api.dto.review.request.ReviewCreateManyRequest;
import com.gabojait.gabojaitspring.api.dto.review.response.ReviewFindAllTeamResponse;
import com.gabojait.gabojaitspring.api.dto.review.response.ReviewFindTeamResponse;
import com.gabojait.gabojaitspring.api.dto.review.response.ReviewPageResponse;
import com.gabojait.gabojaitspring.api.service.review.ReviewService;
import com.gabojait.gabojaitspring.config.auth.JwtProvider;
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
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

import static com.gabojait.gabojaitspring.common.constant.code.SuccessCode.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "리뷰")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "리뷰 작성 가능한 팀 전체 조회",
            notes = "<설명>\n" +
                    "- 리뷰 작성 기간 = 4주이내\n\n" +
                    "<응답 코드>\n" +
                    "- 200 = REVIEWABLE_TEAMS_FOUND\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ReviewFindAllTeamResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/user/team/review")
    public ResponseEntity<DefaultMultiResponse<Object>> findAllReviewableTeams(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization
    ) {
        long userId = jwtProvider.getUserId(authorization);

        PageData<List<ReviewFindAllTeamResponse>> responses = reviewService.findAllReviewableTeams(userId,
                LocalDateTime.now());

        return ResponseEntity.status(REVIEWABLE_TEAMS_FOUND.getHttpStatus())
                .body(DefaultMultiResponse.multiDataBuilder()
                        .responseCode(REVIEWABLE_TEAMS_FOUND.name())
                        .responseMessage(REVIEWABLE_TEAMS_FOUND.getMessage())
                        .responseData(responses)
                        .build());
    }

    @ApiOperation(value = "리뷰 작성 가능한 팀 단건 조회",
            notes = "<설명>\n" +
                    "- 리뷰 작성 기간 = 4주이내\n\n" +
                    "<응답 코드>\n" +
                    "- 200 = REVIEWABLE_TEAM_FOUND\n" +
                    "- 400 = TEAM_ID_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = TEAM_MEMBER_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ReviewFindTeamResponse.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/user/team/{team-id}/review")
    public ResponseEntity<DefaultSingleResponse<Object>> findReviewableTeam(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @PathVariable(value = "team-id")
            @Positive(message = "팀 식별자는 양수만 가능합니다.")
            Long teamId
    ) {
        long userId = jwtProvider.getUserId(authorization);

        ReviewFindTeamResponse response = reviewService.findReviewableTeam(userId, teamId, LocalDateTime.now());

        return ResponseEntity.status(REVIEWABLE_TEAM_FOUND.getHttpStatus())
                .body(DefaultSingleResponse.singleDataBuilder()
                        .responseCode(REVIEWABLE_TEAM_FOUND.name())
                        .responseMessage(REVIEWABLE_TEAM_FOUND.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "리뷰 작성",
            notes = "<응답 코드>\n" +
                    "- 201 = USER_REVIEWED\n" +
                    "- 400 = TEAM_MEMBER_ID_FIELD_REQUIRED || RATING_FIELD_REQUIRED || POST_LENGTH_INVALID || " +
                    "TEAM_MEMBER_ID_POSITIVE_ONLY || RATING_RANGE_INVALID || TEAM_ID_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = TEAM_MEMBER_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "CREATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/user/team/{team-id}/review")
    public ResponseEntity<DefaultNoResponse> createReview(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @PathVariable(value = "team-id")
            @Positive(message = "팀 식별자는 양수만 가능합니다.")
            Long teamId,
            @RequestBody @Valid ReviewCreateManyRequest request
    ) {
        long userId = jwtProvider.getUserId(authorization);

        reviewService.createReview(userId, teamId, request);

        return ResponseEntity.status(USER_REVIEWED.getHttpStatus())
                .body(DefaultNoResponse.noDataBuilder()
                        .responseCode(USER_REVIEWED.name())
                        .responseMessage(USER_REVIEWED.getMessage())
                        .build());
    }

    @ApiOperation(value = "회원 리뷰 페이징 조회",
            notes = "<검증>\n" +
                    "- page-from[default: 9223372036854775806] = Positive\n" +
                    "- page-size[default: 20] = Positive && Max(value = 100)\n\n" +
                    "<응답 코드>\n" +
                    "- 200 = USER_REVIEWS_FOUND\n" +
                    "- 400 = USER_ID_POSITIVE_ONLY || PAGE_FROM_POSITIVE_ONLY || PAGE_SIZE_POSITIVE_ONLY || " +
                    "PAGE_SIZE_RANGE_INVALID\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ReviewPageResponse.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/user/{user-id}/review")
    public ResponseEntity<DefaultMultiResponse<Object>> findPageReview(
            @PathVariable(value = "user-id")
            @Positive(message = "회원 식별자는 양수만 가능합니다.")
            Long userId,
            @RequestParam(value = "page-from", required = false, defaultValue = "9223372036854775806")
            @Positive(message = "페이지 시작점은 양수만 가능합니다.")
            Long pageFrom,
            @RequestParam(value = "page-size", required = false, defaultValue = "20")
            @Positive(message = "페이지 사이즈는 양수만 가능합니다.")
            @Max(value = 100, message = "페이지 사이즈는 100까지의 수만 가능합니다.")
            Integer pageSize
    ) {
        PageData<List<ReviewPageResponse>> responses = reviewService.findPageReviews(userId, pageFrom, pageSize);

        return ResponseEntity.status(USER_REVIEWS_FOUND.getHttpStatus())
                .body(DefaultMultiResponse.multiDataBuilder()
                        .responseCode(USER_REVIEWS_FOUND.name())
                        .responseMessage(USER_REVIEWS_FOUND.getMessage())
                        .responseData(responses)
                        .build());
    }
}
