package com.gabojait.gabojaitspring.review.controller;

import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.WebMvc;
import com.gabojait.gabojaitspring.review.domain.Review;
import com.gabojait.gabojaitspring.review.dto.req.ReviewCreateReqDto;
import com.gabojait.gabojaitspring.review.dto.req.ReviewDefaultReqDto;
import com.gabojait.gabojaitspring.review.dto.res.ReviewDefaultResDto;
import com.gabojait.gabojaitspring.review.service.ReviewService;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.domain.type.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;
import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest extends WebMvc {

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        doReturn(1L)
                .when(this.jwtProvider)
                .getId(any());

        User tester = User.testOnlyBuilder()
                .id(1L)
                .role(Role.USER)
                .build();

        Team team = Team.builder()
                .projectName("가보자잇")
                .projectDescription("가보자잇 프로젝트 설명입니다.")
                .designerCnt((byte) 2)
                .backendCnt((byte) 2)
                .frontendCnt((byte) 2)
                .managerCnt((byte) 2)
                .expectation("열정적인 팀원을 원합니다.")
                .openChatUrl("https://open.kakao.com/o")
                .build();

        Review review = Review.builder()
                .reviewer(tester)
                .reviewee(tester)
                .rate((byte) 1)
                .team(team)
                .post("협조적이에요.")
                .build();

        Page<ReviewDefaultResDto> reviewDtos = new PageImpl<>(
                List.of(new ReviewDefaultResDto(review, 1)),
                (Pageable) PageRequest.of(0, 1),
                1
        );

        doReturn(List.of(team))
                .when(this.reviewService)
                .findAllReviewableTeams(anyLong());

        doReturn(reviewDtos)
                .when(this.reviewService)
                .findManyReviews(anyLong(), anyLong(), any());

        doReturn(team)
                .when(this.reviewService)
                .findOneReviewableTeam(anyLong(), anyLong());
    }

    @Test
    @DisplayName("리뷰 작성 | 올바른 요청시 | 201반환")
    void createReview_givenValidReq_return201() throws Exception {
        // given
        ReviewCreateReqDto reqDto = getValidReviewCreateReqDto();
        String request = mapToJson(reqDto);
        Long teamId = getValidId();

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/team/{team-id}/review", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USER_REVIEWED.getHttpStatus().value());
        assertThat(response).contains(USER_REVIEWED.name());
    }

    @Test
    @DisplayName("리뷰 작성 | 팀 식별자가 양수 아닐시 | 400반환")
    void createReview_givenTeamIdPositiveOnly_return400() throws Exception {
        // given
        ReviewCreateReqDto reqDto = getValidReviewCreateReqDto();
        String request = mapToJson(reqDto);
        Long teamId = 0L;

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/team/{team-id}/review", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(TEAM_ID_POSITIVE_ONLY.getHttpStatus().value());
        assertThat(response).contains(TEAM_ID_POSITIVE_ONLY.name());
    }

    @Test
    @DisplayName("리뷰 작성 | 회원 식별자 미존재시 | 400반환")
    void createReview_givenUserIdIsRequired_return400() throws Exception {
        // given
        ReviewCreateReqDto reqDto = getValidReviewCreateReqDto();
        reqDto.getReviews().get(0).setUserId(null);
        String request = mapToJson(reqDto);
        Long teamId = 1L;

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/team/{team-id}/review", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USER_ID_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(USER_ID_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("리뷰 작성 | 회원 식별자가 양수 아닐시 | 400반환")
    void createReview_givenUserIdPositiveOnly_return400() throws Exception {
        // given
        ReviewCreateReqDto reqDto = getValidReviewCreateReqDto();
        reqDto.getReviews().get(0).setUserId(0L);
        String request = mapToJson(reqDto);
        Long teamId = 1L;

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/team/{team-id}/review", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USER_ID_POSITIVE_ONLY.getHttpStatus().value());
        assertThat(response).contains(USER_ID_POSITIVE_ONLY.name());
    }

    @Test
    @DisplayName("리뷰 작성 | 평점 미입력시 | 400반환")
    void createReview_givenRateFieldRequired_return400() throws Exception {
        // given
        ReviewCreateReqDto reqDto = getValidReviewCreateReqDto();
        reqDto.getReviews().get(0).setRate(null);
        String request = mapToJson(reqDto);
        Long teamId = getValidId();

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/team/{team-id}/review", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(RATE_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(RATE_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("리뷰 작성 | 잘못된 평점 범위시 | 400반환")
    void createReview_givenRateRangeInvalid_return400() throws Exception {
        // given
        ReviewCreateReqDto reqDto = getValidReviewCreateReqDto();
        reqDto.getReviews().get(0).setRate((byte) 6);
        String request = mapToJson(reqDto);
        Long teamId = getValidId();

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/team/{team-id}/review", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(RATE_RANGE_INVALID.getHttpStatus().value());
        assertThat(response).contains(RATE_RANGE_INVALID.name());
    }

    @Test
    @DisplayName("리뷰 작성 | 후기 미입력시 | 400반환")
    void createReview_givenPostFieldRequired_return400() throws Exception {
        // given
        ReviewCreateReqDto reqDto = getValidReviewCreateReqDto();
        reqDto.getReviews().get(0).setPost("");
        String request = mapToJson(reqDto);
        Long teamId = getValidId();

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/team/{team-id}/review", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(POST_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(POST_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("리뷰 작성 | 잘못된 후기 길이시 | 400반환")
    void createReview_givenPostLengthInvalid_return400() throws Exception {
        // given
        ReviewCreateReqDto reqDto = getValidReviewCreateReqDto();
        reqDto.getReviews().get(0).setPost("가".repeat(201));
        String request = mapToJson(reqDto);
        Long teamId = getValidId();

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/team/{team-id}/review", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(POST_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(POST_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("본인이 리뷰 작성 가능한 팀 전체 조회 | 올바른 요청시 | 200반환")
    void findReviewableTeams_givenValidReq_return200() throws Exception {
        // given & when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/team/review"))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(REVIEWABLE_TEAMS_FOUND.getHttpStatus().value());
        assertThat(response).contains(REVIEWABLE_TEAMS_FOUND.name());
    }

    @Test
    @DisplayName("본인이 리뷰 작성 가능한 팀 단건 조회 | 올바른 요청시 | 200반환")
    void findReviewableTeam_givenValidReq_return200() throws Exception {
        // given
        Long teamId = getValidId();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/team/{team-id}/review", teamId))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(REVIEWABLE_TEAM_FOUND.getHttpStatus().value());
        assertThat(response).contains(REVIEWABLE_TEAM_FOUND.name());
    }

    @Test
    @DisplayName("본인이 리뷰 작성 가능한 팀 단건 조회 | 팀 식별자 양수 아닐시 | 400반환")
    void findReviewableTeam_givenTeamIdPositiveOnly_return400() throws Exception {
        // given
        Long teamId = 0L;

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/team/{team-id}/review", teamId))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(TEAM_ID_POSITIVE_ONLY.getHttpStatus().value());
        assertThat(response).contains(TEAM_ID_POSITIVE_ONLY.name());
    }

    @Test
    @DisplayName("회원 리뷰 다건 조회 | 올바른 요청시 | 200반환")
    void findManyReview_givenValidReq_return200() throws Exception {
        // given
        Long userId = getValidId();
        Integer pageFrom = getValidPageFrom();
        Integer pageSize = getValidPageSize();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/{user-id}/review", userId)
                        .param("page-from", pageFrom.toString())
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USER_REVIEWS_FOUND.getHttpStatus().value());
        assertThat(response).contains(USER_REVIEWS_FOUND.name());
    }

    @Test
    @DisplayName("회원 리뷰 다건 조회 | 회원 식별자가 양수 아닐시 | 400반환")
    void findManyReview_givenUserIdPositiveOnly_return400() throws Exception {
        // given
        Long userId = 0L;
        Integer pageFrom = getValidPageFrom();
        Integer pageSize = getValidPageSize();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/{user-id}/review", userId)
                        .param("page-from", pageFrom.toString())
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USER_ID_POSITIVE_ONLY.getHttpStatus().value());
        assertThat(response).contains(USER_ID_POSITIVE_ONLY.name());
    }

    @Test
    @DisplayName("회원 리뷰 다건 조회 | 페이지 시작점 미입력시 | 400반환")
    void findManyReview_givenPageFromFieldRequired_return400() throws Exception {
        // given
        Long userId = getValidId();
        Integer pageSize = getValidPageSize();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/{user-id}/review", userId)
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PAGE_FROM_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(PAGE_FROM_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("회원 리뷰 다건 조회 | 페이지 시작점이 양수 또는 0 아닐시 | 400반환")
    void findManyReview_givenPageFromPositiveOrZeroOnly_return400() throws Exception {
        // given
        Long userId = getValidId();
        Integer pageFrom = -1;
        Integer pageSize = getValidPageSize();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/{user-id}/review", userId)
                        .param("page-from", pageFrom.toString())
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PAGE_FROM_POSITIVE_OR_ZERO_ONLY.getHttpStatus().value());
        assertThat(response).contains(PAGE_FROM_POSITIVE_OR_ZERO_ONLY.name());
    }

    @Test
    @DisplayName("회원 리뷰 다건 조회 | 페이지 사이즈가 양수 아닐시 | 400반환")
    void findManyReview_givenPageSizePositiveOnly_return400() throws Exception {
        // given
        Long userId = getValidId();
        Integer pageFrom = getValidPageFrom();
        Integer pageSize = 0;

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/{user-id}/review", userId)
                        .param("page-from", pageFrom.toString())
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PAGE_SIZE_POSITIVE_ONLY.getHttpStatus().value());
        assertThat(response).contains(PAGE_SIZE_POSITIVE_ONLY.name());
    }

    private ReviewCreateReqDto getValidReviewCreateReqDto() {
        ReviewDefaultReqDto reviewDefaultReqDto = new ReviewDefaultReqDto();
        reviewDefaultReqDto.setUserId(1L);
        reviewDefaultReqDto.setRate((byte) 4);
        reviewDefaultReqDto.setPost("협조적입니다.");

        ReviewCreateReqDto reqDto = new ReviewCreateReqDto();
        reqDto.setReviews(List.of(reviewDefaultReqDto));

        return reqDto;
    }

    private Long getValidId() {
        return 1L;
    }

    private Integer getValidPageFrom() {
        return 0;
    }

    private Integer getValidPageSize() {
        return 20;
    }
}