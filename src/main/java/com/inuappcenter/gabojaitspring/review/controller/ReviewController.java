package com.inuappcenter.gabojaitspring.review.controller;

import com.inuappcenter.gabojaitspring.auth.JwtProvider;
import com.inuappcenter.gabojaitspring.auth.JwtType;
import com.inuappcenter.gabojaitspring.common.DefaultResDto;
import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.review.domain.Question;
import com.inuappcenter.gabojaitspring.review.domain.Review;
import com.inuappcenter.gabojaitspring.review.dto.req.ReviewSaveManyReqDto;
import com.inuappcenter.gabojaitspring.review.dto.req.ReviewSaveOneReqDto;
import com.inuappcenter.gabojaitspring.review.dto.res.QuestionDefaultResDto;
import com.inuappcenter.gabojaitspring.review.dto.res.ReviewDefaultResDto;
import com.inuappcenter.gabojaitspring.review.service.QuestionService;
import com.inuappcenter.gabojaitspring.review.service.ReviewService;
import com.inuappcenter.gabojaitspring.team.domain.Team;
import com.inuappcenter.gabojaitspring.team.dto.res.TeamAbstractResDto;
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
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.inuappcenter.gabojaitspring.common.SuccessCode.*;
import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "리뷰")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final ReviewService reviewService;
    private final QuestionService questionService;
    private final TeamService teamService;

    @ApiOperation(value = "한 회원 리뷰 전체 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ALL_REVIEWS_FOUND / ZERO_REVIEW_FOUND",
                    content = @Content(schema = @Schema(implementation = ReviewDefaultResDto.class))),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @GetMapping("/user/{user-id}/reviews")
    public ResponseEntity<DefaultResDto<Object>> findAllReviews(HttpServletRequest servletRequest,
                                                                @PathVariable(value = "user-id") String userId) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        userService.findOneByUserId(token.get(0));
        User user = userService.findOneByUserId(userId);

        List<Review> reviews = reviewService.findAll(user.getId().toString());

        if (reviews != null) {

            List<ReviewDefaultResDto> responseBodies = new ArrayList<>();
            for (Review review : reviews)
                responseBodies.add(new ReviewDefaultResDto(review));

            return ResponseEntity.status(ALL_REVIEWS_FOUND.getHttpStatus())
                    .body(DefaultResDto.builder()
                            .responseCode(ALL_REVIEWS_FOUND.name())
                            .responseMessage(ALL_REVIEWS_FOUND.getMessage())
                            .data(responseBodies)
                            .build());
        } else {
            return ResponseEntity.status(ZERO_REVIEW_FOUND.getHttpStatus())
                    .body(DefaultResDto.builder()
                            .responseCode(ZERO_REVIEW_FOUND.name())
                            .responseMessage(ZERO_REVIEW_FOUND.getMessage())
                            .build());
        }
    }

    @ApiOperation(value = "현재 리뷰 질문 전체 조회", notes = "reviewType = RATING || ANSWER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CURRENT_REVIEW_QUESTIONS_FOUND",
                    content = @Content(schema = @Schema(implementation = QuestionDefaultResDto.class))),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @GetMapping("/review/questions")
    public ResponseEntity<DefaultResDto<Object>> findAllQuestions(HttpServletRequest servletRequest) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        userService.findOneByUserId(token.get(0));

        List<Question> questions = questionService.findAllCurrentQuestions();

        List<QuestionDefaultResDto> responseBodies = new ArrayList<>();
        for (Question question : questions)
            responseBodies.add(new QuestionDefaultResDto(question));

        return ResponseEntity.status(CURRENT_REVIEW_QUESTIONS_FOUND.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(CURRENT_REVIEW_QUESTIONS_FOUND.name())
                        .responseMessage(CURRENT_REVIEW_QUESTIONS_FOUND.getMessage())
                        .data(responseBodies)
                        .build());
    }

    @ApiOperation(value = "리뷰 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "REVIEWS_CREATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400",
                    description = "FIELD_REQUIRED / REVIEWTYPE_FORMAT_INVALID / REVIEW_RATING_FORMAT_INVALID / " +
                            "REVIEW_ANSWER_FORMAT_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404",
                    description = "USER_NOT_FOUND / TEAM_NOT_FOUND / QUESTION_NOT_FOUND / REVIEWEE_NOT_FOUND"),
            @ApiResponse(responseCode = "409", description = "EXISTING_REVIEW"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PostMapping("/user/team/{team-id}/review")
    public ResponseEntity<DefaultResDto<Object>> create(HttpServletRequest servletRequest,
                                                        @PathVariable(value = "team-id") String teamId,
                                                        @RequestBody @Valid ReviewSaveManyReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User reviewer = userService.findOneByUserId(token.get(0));
        Team team = teamService.findOne(teamId);
        List<ObjectId> revieweeUserIds = userService.findAllByTeam(reviewer, team);

        for (ReviewSaveOneReqDto r : request.getReviews()) {
            reviewService.validateReviewee(revieweeUserIds, r.getRevieweeUserId());
            Question question = questionService.findById(r.getQuestionId());
            questionService.validateReviewType(question, r.getRate(), r.getAnswer());
            reviewService.isExistingReview(reviewer.getId(), r.getRevieweeUserId(), team.getId(), question);
        }

        for (ReviewSaveOneReqDto r : request.getReviews()) {
            Question question = questionService.findById(r.getQuestionId());
            Review review = reviewService.save(r.toEntity(reviewer.getId(), team.getId(), question));
            User reviewee = userService.findOneByUserId(review.getRevieweeUserId().toString());
            userService.addReview(reviewee, review);
        }

        return ResponseEntity.status(REVIEWS_CREATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(REVIEWS_CREATED.name())
                        .responseMessage(REVIEWS_CREATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "본인 리뷰 작성 가능한 팀 조회", notes = "리뷰 작성 가능 기간 = 프로젝트 완료 후 4주 동안")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "AVAILABLE_REVIEWS",
                    content = @Content(schema = @Schema(implementation = TeamAbstractResDto.class))),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @GetMapping("/user/reviews")
    public ResponseEntity<DefaultResDto<Object>> findAvailableReview(HttpServletRequest servletRequest) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));
        List<Team> completedTeams = teamService.findAllPrevious(user);
        List<Team> undoneTeams = reviewService.findUndoneTeam(completedTeams);

        if (undoneTeams.isEmpty()) {

            return ResponseEntity.status(ZERO_AVAILABLE_REVIEW_FOUND.getHttpStatus())
                    .body(DefaultResDto.builder()
                            .responseCode(ZERO_AVAILABLE_REVIEW_FOUND.name())
                            .responseMessage(ZERO_AVAILABLE_REVIEW_FOUND.getMessage())
                            .build());
        } else {

            List<TeamAbstractResDto> responseBodies = new ArrayList<>();
            for (Team team : undoneTeams)
                responseBodies.add(new TeamAbstractResDto(team));

            return ResponseEntity.status(AVAILABLE_REVIEWS_FOUND.getHttpStatus())
                    .body(DefaultResDto.builder()
                            .responseCode(AVAILABLE_REVIEWS_FOUND.name())
                            .responseMessage(AVAILABLE_REVIEWS_FOUND.getMessage())
                            .data(responseBodies)
                            .build());
        }
    }
}
