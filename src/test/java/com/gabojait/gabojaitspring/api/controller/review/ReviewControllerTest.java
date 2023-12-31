package com.gabojait.gabojaitspring.api.controller.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabojait.gabojaitspring.api.dto.review.request.ReviewCreateManyRequest;
import com.gabojait.gabojaitspring.api.dto.review.request.ReviewCreateOneRequest;
import com.gabojait.gabojaitspring.api.service.review.ReviewService;
import com.gabojait.gabojaitspring.auth.CustomAuthenticationEntryPoint;
import com.gabojait.gabojaitspring.auth.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.stream.Collectors;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;
import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    @MockBean private JwtProvider jwtProvider;
    @MockBean private ReviewService reviewService;

    @Test
    @DisplayName("리뷰 작성 가능한 팀 전체 조회를 하면 200을 반환한다.")
    void givenValid_whenFindAllReviewableTeams_thenReturn200() throws Exception {
        // given & when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/team/review")
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(REVIEWABLE_TEAMS_FOUND.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(REVIEWABLE_TEAMS_FOUND.getMessage()));
    }

    @Test
    @DisplayName("리뷰 작성 가능한 팀 조회를 하면 200을 반환한다.")
    void givenValid_whenFindReviewableTeam_thenReturn200() throws Exception {
        // given
        long teamId = 1L;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/team/{team-id}/review", teamId)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(REVIEWABLE_TEAM_FOUND.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(REVIEWABLE_TEAM_FOUND.getMessage()));
    }

    @Test
    @DisplayName("양수가 아닌 팀 식별자로 리뷰 작성 가능한 팀 조회를 하면 400을 반환한다.")
    void givenNonPositiveTeamId_whenFindReviewableTeam_thenReturn400() throws Exception {
        // given
        long teamId = 0L;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/team/{team-id}/review", teamId)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(TEAM_ID_POSITIVE_ONLY.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(TEAM_ID_POSITIVE_ONLY.getMessage()));
    }

    @Test
    @DisplayName("리뷰 작성을 하면 201을 반환한다.")
    void givenValid_whenCreateReview_thenReturn201() throws Exception {
        // given
        ReviewCreateManyRequest request = createValidReviewCreateManyRequest(List.of(1L, 2L, 3L));
        long teamId = 1L;

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/team/{team-id}/review", teamId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.responseCode")
                        .value(USER_REVIEWED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(USER_REVIEWED.getMessage()));
    }

    @Test
    @DisplayName("팀원 식별자 미입력시 리뷰 작성을 하면 400을 반환한다.")
    void givenBlankTeamMemberId_whenCreateReview_thenReturn400() throws Exception {
        // given
        ReviewCreateManyRequest request = createValidReviewCreateManyRequest(List.of(1L, 2L, 3L));
        request.getReviews().get(0).setTeamMemberId(null);
        long teamId = 1L;

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/team/{team-id}/review", teamId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(TEAM_MEMBER_ID_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(TEAM_MEMBER_ID_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("평점 미입력시 리뷰 작성을 하면 400을 반환한다.")
    void givenBlankRating_whenCreateReview_thenReturn400() throws Exception {
        // given
        ReviewCreateManyRequest request = createValidReviewCreateManyRequest(List.of(1L, 2L, 3L));
        request.getReviews().get(0).setRating(null);
        long teamId = 1L;

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/team/{team-id}/review", teamId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(RATING_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(RATING_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("후기 200자 초과일시 리뷰 작성을 하면 400을 반환한다.")
    void givenGreaterThan200SizePost_whenCreateReview_thenReturn400() throws Exception {
        // given
        ReviewCreateManyRequest request = createValidReviewCreateManyRequest(List.of(1L, 2L, 3L));
        request.getReviews().get(0).setPost("가".repeat(201));
        long teamId = 1L;

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/team/{team-id}/review", teamId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(POST_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(POST_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("팀원 식별자가 양수가 아닐시 리뷰 작성을 하면 400을 반환한다.")
    void givenNonPositiveTeamMemberId_whenCreateReview_thenReturn400() throws Exception {
        // given
        ReviewCreateManyRequest request = createValidReviewCreateManyRequest(List.of(1L, 2L, 3L));
        request.getReviews().get(0).setTeamMemberId(0L);
        long teamId = 1L;

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/team/{team-id}/review", teamId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(TEAM_MEMBER_ID_POSITIVE_ONLY.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(TEAM_MEMBER_ID_POSITIVE_ONLY.getMessage()));
    }

    @Test
    @DisplayName("평점이 1 미만일시 리뷰 작성을 하면 400을 반환한다.")
    void givenLessThan1Rating_whenCreateReview_thenReturn400() throws Exception {
        // given
        ReviewCreateManyRequest request = createValidReviewCreateManyRequest(List.of(1L, 2L, 3L));
        request.getReviews().get(0).setRating((byte) 0);
        long teamId = 1L;

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/team/{team-id}/review", teamId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(RATING_RANGE_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(RATING_RANGE_INVALID.getMessage()));
    }

    @Test
    @DisplayName("평점이 5 초과일시 리뷰 작성을 하면 400을 반환한다.")
    void givenGreaterThan5Rating_whenCreateReview_thenReturn400() throws Exception {
        // given
        ReviewCreateManyRequest request = createValidReviewCreateManyRequest(List.of(1L, 2L, 3L));
        request.getReviews().get(0).setRating((byte) 6);
        long teamId = 1L;

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/team/{team-id}/review", teamId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(RATING_RANGE_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(RATING_RANGE_INVALID.getMessage()));
    }

    @Test
    @DisplayName("팀 식별자가 양수가 아닐시 리뷰 작성을 하면 400을 반환한다.")
    void givenNonPositiveTeamId_whenCreateReview_thenReturn400() throws Exception {
        // given
        ReviewCreateManyRequest request = createValidReviewCreateManyRequest(List.of(1L, 2L, 3L));
        long teamId = 0L;

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/team/{team-id}/review", teamId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(TEAM_ID_POSITIVE_ONLY.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(TEAM_ID_POSITIVE_ONLY.getMessage()));
    }

    @Test
    @DisplayName("회원 리뷰 페이징 조회를 하면 200을 반환한다.")
    void givenValid_whenFindPageReview_thenReturn200() throws Exception {
        // given
        long userId = 1L;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/{user-id}/review", userId)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(USER_REVIEWS_FOUND.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(USER_REVIEWS_FOUND.getMessage()));
    }

    @Test
    @DisplayName("회원 식별자가 양수가 아닐시 회원 리뷰 페이징 조회를 하면 400을 반환한다.")
    void givenNonPositiveUserId_whenFindPageReview_thenReturn400() throws Exception {
        // given
        long userId = 0L;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/{user-id}/review", userId)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(USER_ID_POSITIVE_ONLY.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(USER_ID_POSITIVE_ONLY.getMessage()));
    }

    @Test
    @DisplayName("페이지 시작점이 양수가 아닐시 회원 리뷰 페이징 조회를 하면 400을 반환한다.")
    void givenNonPositivePageFrom_whenFindPageReview_thenReturn400() throws Exception {
        // given
        long userId = 1L;
        Long pageFrom = 0L;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/{user-id}/review", userId)
                        .param("page-from", pageFrom.toString())
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(PAGE_FROM_POSITIVE_ONLY.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PAGE_FROM_POSITIVE_ONLY.getMessage()));
    }

    @Test
    @DisplayName("페이지 크기가 양수가 아닐시 회원 리뷰 페이징 조회를 하면 400을 반환한다.")
    void givenNonPositivePageSize_whenFindPageReview_thenReturn400() throws Exception {
        // given
        long userId = 1L;
        Long pageSize = 0L;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/{user-id}/review", userId)
                        .param("page-size", pageSize.toString())
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(PAGE_SIZE_POSITIVE_ONLY.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PAGE_SIZE_POSITIVE_ONLY.getMessage()));
    }

    @Test
    @DisplayName("페이지 크기가 100 초과일시 회원 리뷰 페이징 조회를 하면 400을 반환한다.")
    void givenGraterThan100PageSize_whenFindPageReview_thenReturn400() throws Exception {
        // given
        long userId = 1L;
        Long pageSize = 101L;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/{user-id}/review", userId)
                        .param("page-size", pageSize.toString())
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(PAGE_SIZE_RANGE_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PAGE_SIZE_RANGE_INVALID.getMessage()));
    }

    private ReviewCreateManyRequest createValidReviewCreateManyRequest(List<Long> revieweeMemberIds) {
        List<ReviewCreateOneRequest> reviews = revieweeMemberIds.stream()
                .map(revieweeMemberId -> ReviewCreateOneRequest.builder()
                        .teamMemberId(revieweeMemberId)
                        .rating((byte) 3)
                        .post("열정적인 팀원입니다.")
                        .build())
                .collect(Collectors.toList());

        return ReviewCreateManyRequest.builder()
                .reviews(reviews)
                .build();
    }
}