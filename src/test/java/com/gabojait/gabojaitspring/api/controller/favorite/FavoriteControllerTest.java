package com.gabojait.gabojaitspring.api.controller.favorite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabojait.gabojaitspring.api.dto.favorite.request.FavoriteUpdateRequest;
import com.gabojait.gabojaitspring.api.service.favorite.FavoriteService;
import com.gabojait.gabojaitspring.config.auth.CustomAuthenticationEntryPoint;
import com.gabojait.gabojaitspring.config.auth.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.gabojait.gabojaitspring.common.constant.code.ErrorCode.*;
import static com.gabojait.gabojaitspring.common.constant.code.SuccessCode.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(FavoriteController.class)
@AutoConfigureMockMvc(addFilters = false)
class FavoriteControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    @MockBean private JwtProvider jwtProvider;
    @MockBean private FavoriteService favoriteService;

    @Test
    @DisplayName("회원 찜하기를 하면 201을 반환한다.")
    void givenAdd_whenUpdateFavoriteUser_thenReturn201() throws Exception {
        // given
        long userId = 1L;
        FavoriteUpdateRequest request = createValidFavoriteUpdateRequest(true);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/favorite/user/{user-id}", userId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.responseCode")
                        .value(FAVORITE_USER_ADDED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(FAVORITE_USER_ADDED.getMessage()));
    }

    @Test
    @DisplayName("회원 찜을 제거 하면 200을 반환한다.")
    void givenDelete_whenUpdateFavoriteUser_thenReturn200() throws Exception {
        // given
        long userId = 1L;
        FavoriteUpdateRequest request = createValidFavoriteUpdateRequest(false);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/favorite/user/{user-id}", userId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(FAVORITE_USER_DELETED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(FAVORITE_USER_DELETED.getMessage()));
    }

    @Test
    @DisplayName("찜 추가 여부 미입력시 회원 찜 요청을 하면 400을 반환한다.")
    void givenBlankIsAddFavorite_whenUpdateFavoriteUser_thenReturn400() throws Exception {
        // given
        long userId = 1L;
        FavoriteUpdateRequest request = createValidFavoriteUpdateRequest(true);
        request.setIsAddFavorite(null);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/favorite/user/{user-id}", userId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(IS_ADD_FAVORITE_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(IS_ADD_FAVORITE_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("회원 식별자가 양수가 아닐시 회원 찜 요청을 하면 400을 반환한다.")
    void givenNonPositiveUserId_whenUpdateFavoriteUser_thenReturn400() throws Exception {
        // given
        long userId = 0L;
        FavoriteUpdateRequest request = createValidFavoriteUpdateRequest(true);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/favorite/user/{user-id}", userId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
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
    @DisplayName("팀 찜하기를 하면 201을 반환한다.")
    void givenAdd_whenUpdateFavoriteTeam_thenReturn201() throws Exception {
        // given
        long teamId = 1L;
        FavoriteUpdateRequest request = createValidFavoriteUpdateRequest(true);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/favorite/team/{team-id}", teamId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.responseCode")
                        .value(FAVORITE_TEAM_ADDED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(FAVORITE_TEAM_ADDED.getMessage()));
    }

    @Test
    @DisplayName("팀 찜을 제거 하면 200을 반환한다.")
    void givenDelete_whenUpdateFavoriteTeam_thenReturn200() throws Exception {
        // given
        long teamId = 1L;
        FavoriteUpdateRequest request = createValidFavoriteUpdateRequest(false);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/favorite/team/{team-id}", teamId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(FAVORITE_TEAM_DELETED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(FAVORITE_TEAM_DELETED.getMessage()));
    }

    @Test
    @DisplayName("찜 추가 여부 미입력시 회원 찜 요청을 하면 400을 반환한다.")
    void givenBlankIsAddFavorite_whenUpdateFavoriteTeam_thenReturn400() throws Exception {
        // given
        long teamId = 1L;
        FavoriteUpdateRequest request = createValidFavoriteUpdateRequest(true);
        request.setIsAddFavorite(null);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/favorite/team/{team-id}", teamId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(IS_ADD_FAVORITE_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(IS_ADD_FAVORITE_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("팀 식별자가 양수가 아닐시 회원 찜 요청을 하면 400을 반환한다.")
    void givenNonPositiveTeamId_whenUpdateFavoriteTeam_thenReturn400() throws Exception {
        // given
        long teamId = 0L;
        FavoriteUpdateRequest request = createValidFavoriteUpdateRequest(true);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/favorite/team/{team-id}", teamId)
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
    @DisplayName("찜한 회원 페이징 조회를 하면 200을 반환한다.")
    void givenValid_whenFindPageFavoriteUser_thenReturn200() throws Exception {
        // given & when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/favorite/user")
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(FAVORITE_USERS_FOUND.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(FAVORITE_USERS_FOUND.getMessage()));
    }

    @Test
    @DisplayName("페이지 시작점이 양수가 아닐시 찜한 회원 페이징 조회를 하면 400을 반환한다.")
    void givenNonPositivePageFrom_whenFindPageFavoriteUser_thenReturn400() throws Exception {
        // given
        Long pageFrom = 0L;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/favorite/user")
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
    @DisplayName("페이지 크기가 양수가 아닐시 찜한 회원 페이징 조회를 하면 400을 반환한다.")
    void givenNonPositivePageSize_whenFindPageFavoriteUser_thenReturn400() throws Exception {
        // given
        Integer pageSize = 0;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/favorite/user")
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
    @DisplayName("페이지 크기가 100을 초과할시 찜한 회원 페이징 조회를 하면 400을 반환한다.")
    void givenGreaterThan100PageSize_whenFindPageFavoriteUser_thenReturn400() throws Exception {
        // given
        Integer pageSize = 101;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/favorite/user")
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

    @Test
    @DisplayName("찜한 팀 페이징 조회를 하면 200을 반환한다.")
    void givenValid_whenFindPageFavoriteTeam_thenReturn200() throws Exception {
        // given & when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/favorite/team")
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(FAVORITE_TEAMS_FOUND.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(FAVORITE_TEAMS_FOUND.getMessage()));
    }

    @Test
    @DisplayName("페이지 시작점이 양수가 아닐시 찜한 팀 페이징 조회를 하면 400을 반환한다.")
    void givenNonPositivePageFrom_whenFindPageFavoriteTeam_thenReturn400() throws Exception {
        // given
        Long pageFrom = 0L;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/favorite/team")
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
    @DisplayName("페이지 크기가 양수가 아닐시 찜한 팀 페이징 조회를 하면 400을 반환한다.")
    void givenNonPositivePageSize_whenFindPageFavoriteTeam_thenReturn400() throws Exception {
        // given
        Integer pageSize = 0;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/favorite/team")
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
    @DisplayName("페이지 크기가 100을 초과할시 찜한 팀 페이징 조회를 하면 400을 반환한다.")
    void givenGreaterThan100PageSize_whenFindPageFavoriteTeam_thenReturn400() throws Exception {
        // given
        Integer pageSize = 101;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/favorite/team")
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

    private FavoriteUpdateRequest createValidFavoriteUpdateRequest(boolean isAddFavorite) {
        return FavoriteUpdateRequest.builder()
                .isAddFavorite(isAddFavorite)
                .build();
    }
}