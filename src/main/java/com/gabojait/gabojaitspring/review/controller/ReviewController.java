package com.gabojait.gabojaitspring.review.controller;

import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.dto.DefaultResDto;
import com.gabojait.gabojaitspring.review.domain.Review;
import com.gabojait.gabojaitspring.review.dto.req.ReviewCreateReqDto;
import com.gabojait.gabojaitspring.review.service.ReviewService;
import com.gabojait.gabojaitspring.team.domain.Team;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
    private final TeamService teamService;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "리뷰 생성",
            notes = "<응답 코드>\n" +
                    "- 201 = USER_REVIEWED\n" +
                    "- 400 = REVIEWEE_ID_FIELD_REQUIRED || RATE_FIELD_REQUIRED || RATE_RANGE_INVALID || " +
                    "POSTSCRIPT_FIELD_REQUIRED || POSTSCRIPT_LENGTH_INVALID || TEAM_MEMBER_INVALID || " +
                    "ID_CONVERT_INVALID\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = TEAM_NOT_FOUND || USER_NOT_FOUND\n" +
                    "- 409 = REVIEW_UNAVAILABLE\n" +
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
                                                        @PathVariable(value = "team-id") String teamId,
                                                        @RequestBody @Valid ReviewCreateReqDto request) {
        // auth
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // sub
        Team team = teamService.findOneById(teamId);
        // main
        List<Review> reviews = reviewService.create(user, team, request);
        userService.updateRating(reviews);

        return ResponseEntity.status(USER_REVIEWED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(USER_REVIEWED.name())
                        .responseMessage(USER_REVIEWED.getMessage())
                        .build());
    }
}
