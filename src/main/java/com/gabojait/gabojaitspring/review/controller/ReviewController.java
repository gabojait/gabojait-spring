package com.gabojait.gabojaitspring.review.controller;

import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.dto.DefaultResDto;
import com.gabojait.gabojaitspring.review.domain.Review;
import com.gabojait.gabojaitspring.review.dto.req.ReviewCreateReqDto;
import com.gabojait.gabojaitspring.review.dto.res.ReviewDefaultResDto;
import com.gabojait.gabojaitspring.review.service.ReviewService;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.team.dto.res.TeamAbstractResDto;
import com.gabojait.gabojaitspring.user.domain.User;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "리뷰")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "리뷰 작성",
            notes = "<응답 코드>\n" +
                    "- 201 = USER_REVIEWED\n" +
                    "- 400 = TEAM_ID_FIELD_REQUIRED || USER_ID_FIELD_REQUIRED || RATE_FIELD_REQUIRED || " +
                    "POST_FIELD_REQUIRED || POST_LENGTH_INVALID || USER_ID_POSITIVE_ONLY || RATE_RANGE_INVALID || " +
                    "TEAM_ID_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND || TEAM_NOT_FOUND || TEAM_MEMBER_NOT_FOUND\n" +
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
    @PostMapping("/user/team/{team-id}/review")
    public ResponseEntity<DefaultResDto<Object>> createReview(HttpServletRequest servletRequest,
                                                              @PathVariable(value = "team-id")
                                                              @NotNull(message = "팀 식별자는 필수 입력입니다.")
                                                              @Positive(message = "팀 식별자는 양수만 가능합니다.")
                                                              Long teamId,
                                                              @RequestBody @Valid ReviewCreateReqDto request) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        reviewService.create(user, teamId, request);

        return ResponseEntity.status(USER_REVIEWED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(USER_REVIEWED.name())
                        .responseMessage(USER_REVIEWED.getMessage())
                        .build());
    }

    @ApiOperation(value = "본인이 리뷰 작성 가능한 팀 전체 조회",
            notes = "<설명>\n" +
                    "- 리뷰 작성 기간 = 4주이내\n\n" +
                    "<응답 코드>\n" +
                    "- 200 = REVIEWABLE_TEAMS_FOUND\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = TeamAbstractResDto.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/user/team/review")
    public ResponseEntity<DefaultResDto<Object>> findReviewableTeams(HttpServletRequest servletRequest) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        List<Team> reviewableTeams = reviewService.findAllReviewableTeams(user);

        List<TeamAbstractResDto> responses = new ArrayList<>();
        for(Team reviewableTeam : reviewableTeams)
            responses.add(new TeamAbstractResDto(reviewableTeam));

        return ResponseEntity.status(REVIEWABLE_TEAMS_FOUND.getHttpStatus())
                .body(DefaultResDto.multiDataBuilder()
                        .responseCode(REVIEWABLE_TEAMS_FOUND.name())
                        .responseMessage(REVIEWABLE_TEAMS_FOUND.getMessage())
                        .data(responses)
                        .size(responses.size())
                        .build());
    }

    @ApiOperation(value = "회원 리뷰 다건 조회",
            notes = "<검증>\n" +
                    "- page-from = NotNull && PositiveOrZero\n" +
                    "- page-size = Positive\n\n" +
                    "<응답 코드>\n" +
                    "- 200 = USER_REVIEWS_FOUND\n" +
                    "- 400 = USER_ID_FIELD_REQUIRED || PAGE_FROM_FIELD_REQUIRED || USER_ID_POSITIVE_ONLY || " +
                    "PAGE_FROM_POSITIVE_OR_ZERO_ONLY || PAGE_SIZE_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ReviewDefaultResDto.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/user/{user-id}/review")
    public ResponseEntity<DefaultResDto<Object>> findManyReview(
            HttpServletRequest servletRequest,
            @PathVariable(value = "user-id")
            @NotNull(message = "회원 식별자는 필수 입력입니다.")
            @Positive(message = "회원 식별자는 양수만 가능합니다.")
            Long userId,
            @RequestParam(value = "page-from")
            @NotNull(message = "페이지 시작점은 필수 입력입니다.")
            @PositiveOrZero(message = "페이지 시작점은 0 또는 양수만 가능합니다.")
            Integer pageFrom,
            @RequestParam(value = "page-size", required = false)
            @Positive(message = "페이지 사이즈는 양수만 가능합니다.")
            Integer pageSize
    ) {
        jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        Page<Review> reviews = reviewService.findManyReviews(userId, pageFrom, pageSize);

        List<ReviewDefaultResDto> responses = new ArrayList<>();
        for (Review review: reviews)
            responses.add(new ReviewDefaultResDto(review));

        return ResponseEntity.status(USER_REVIEWS_FOUND.getHttpStatus())
                .body(DefaultResDto.multiDataBuilder()
                        .responseCode(USER_REVIEWS_FOUND.name())
                        .responseMessage(USER_REVIEWS_FOUND.getMessage())
                        .data(responses)
                        .size(reviews.getTotalPages())
                        .build());
    }
}
