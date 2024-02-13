package com.gabojait.gabojaitspring.api.controller.team;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabojait.gabojaitspring.api.dto.team.request.TeamCompleteRequest;
import com.gabojait.gabojaitspring.api.dto.team.request.TeamCreateRequest;
import com.gabojait.gabojaitspring.api.dto.team.request.TeamIsRecruitingUpdateRequest;
import com.gabojait.gabojaitspring.api.dto.team.request.TeamUpdateRequest;
import com.gabojait.gabojaitspring.api.service.team.TeamService;
import com.gabojait.gabojaitspring.config.auth.CustomAuthenticationEntryPoint;
import com.gabojait.gabojaitspring.config.auth.JwtProvider;
import com.gabojait.gabojaitspring.domain.user.Position;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(TeamController.class)
@AutoConfigureMockMvc(addFilters = false)
class TeamControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    @MockBean private JwtProvider jwtProvider;
    @MockBean private TeamService teamService;

    @Test
    @DisplayName("팀 생성을 하면 201을 반환한다.")
    void givenValid_whenCreateTeam_thenReturn201() throws Exception {
        // given
        TeamCreateRequest request = createValidTeamCreateRequest();

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.responseCode")
                        .value(TEAM_CREATED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(TEAM_CREATED.getMessage()));
    }

    @Test
    @DisplayName("프로젝트명 20자 초과일시 팀 생성을 하면 400을 반환한다.")
    void givenGreaterThan20SizeProjectName_whenCreateTeam_thenReturn400() throws Exception {
        // given
        TeamCreateRequest request = createValidTeamCreateRequest();
        request.setProjectName("가".repeat(21));

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(PROJECT_NAME_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PROJECT_NAME_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("프로젝트 설명 500자 초과일시 팀 생성을 하면 400을 반환한다.")
    void givenGreaterThan500SizeProjectDescription_whenCreateTeam_thenReturn400() throws Exception {
        // given
        TeamCreateRequest request = createValidTeamCreateRequest();
        request.setProjectDescription("가".repeat(501));

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(PROJECT_DESCRIPTION_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PROJECT_DESCRIPTION_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("바라는 점 200자 초과일시 팀 생성을 하면 400을 반환한다.")
    void givenGreaterThan200SizeExpectation_whenCreateTeam_thenReturn400() throws Exception {
        // given
        TeamCreateRequest request = createValidTeamCreateRequest();
        request.setExpectation("가".repeat(201));

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(EXPECTATION_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(EXPECTATION_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("오픈 채팅 링크 26자 미만일시 팀 생성을 하면 400을 반환한다.")
    void givenLessThan26SizeOpenChatUrl_whenCreateTeam_thenReturn400() throws Exception {
        // given
        TeamCreateRequest request = createValidTeamCreateRequest();
        request.setOpenChatUrl("https://open.kakao.com/o/");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(OPEN_CHAT_URL_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(OPEN_CHAT_URL_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("오픈 채팅 링크 100자 초과일시 팀 생성을 하면 400을 반환한다.")
    void givenGreaterThan100SizeOpenChatUrl_whenCreateTeam_thenReturn400() throws Exception {
        // given
        TeamCreateRequest request = createValidTeamCreateRequest();
        request.setOpenChatUrl("https://open.kakao.com/o/" + "a".repeat(76));

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(OPEN_CHAT_URL_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(OPEN_CHAT_URL_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("팀장 포지션 미입력시 팀 생성을 하면 400을 반환한다")
    void givenBlankLeaderPosition_whenCreateTeam_thenReturn400() throws Exception {
        // given
        TeamCreateRequest request = createValidTeamCreateRequest();
        request.setLeaderPosition(null);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(LEADER_POSITION_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(LEADER_POSITION_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("잘못된 팀장 포지션 타입으로 팀 생성을 하면 400을 반환한다")
    void givenTypeLeaderPosition_whenCreateTeam_thenReturn400() throws Exception {
        // given
        TeamCreateRequest request = createValidTeamCreateRequest();
        request.setLeaderPosition("ABC");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(LEADER_POSITION_TYPE_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(LEADER_POSITION_TYPE_INVALID.getMessage()));
    }

    @Test
    @DisplayName("디자이너 최대 수 미입력시 팀 생성을 하면 400을 반환한다.")
    void givenBlankDesignerMaxCnt_whenCreateTeam_thenReturn400() throws Exception {
        // given
        TeamCreateRequest request = createValidTeamCreateRequest();
        request.setDesignerMaxCnt(null);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(DESIGNER_MAX_CNT_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(DESIGNER_MAX_CNT_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("디자이너 최대 수가 음수일시 팀 생성을 하면 400을 반환한다.")
    void givenNegativeDesignerMaxCnt_whenCreateTeam_thenReturn400() throws Exception {
        // given
        TeamCreateRequest request = createValidTeamCreateRequest();
        request.setDesignerMaxCnt((byte) -1);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(DESIGNER_MAX_CNT_POSITIVE_OR_ZERO_ONLY.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(DESIGNER_MAX_CNT_POSITIVE_OR_ZERO_ONLY.getMessage()));
    }

    @Test
    @DisplayName("백엔드 최대 수 미입력시 팀 생성을 하면 400을 반환한다.")
    void givenBlankBackendMaxCnt_whenCreateTeam_thenReturn400() throws Exception {
        // given
        TeamCreateRequest request = createValidTeamCreateRequest();
        request.setBackendMaxCnt(null);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(BACKEND_MAX_CNT_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(BACKEND_MAX_CNT_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("백엔드 최대 수가 음수일시 팀 생성을 하면 400을 반환한다.")
    void givenNegativeBackendMaxCnt_whenCreateTeam_thenReturn400() throws Exception {
        // given
        TeamCreateRequest request = createValidTeamCreateRequest();
        request.setBackendMaxCnt((byte) -1);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(BACKEND_MAX_CNT_POSITIVE_OR_ZERO_ONLY.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(BACKEND_MAX_CNT_POSITIVE_OR_ZERO_ONLY.getMessage()));
    }

    @Test
    @DisplayName("프런트 최대 수 미입력시 팀 생성을 하면 400을 반환한다.")
    void givenBlankFrontendMaxCnt_whenCreateTeam_thenReturn400() throws Exception {
        // given
        TeamCreateRequest request = createValidTeamCreateRequest();
        request.setFrontendMaxCnt(null);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(FRONTEND_MAX_CNT_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(FRONTEND_MAX_CNT_FIELD_REQUIRED.getMessage()));
    }
    @Test
    @DisplayName("프런트 최대 수가 음수일시 팀 생성을 하면 400을 반환한다.")
    void givenNegativeFrontendMaxCnt_whenCreateTeam_thenReturn400() throws Exception {
        // given
        TeamCreateRequest request = createValidTeamCreateRequest();
        request.setFrontendMaxCnt((byte) -1);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(FRONTEND_MAX_CNT_POSITIVE_OR_ZERO_ONLY.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(FRONTEND_MAX_CNT_POSITIVE_OR_ZERO_ONLY.getMessage()));
    }

    @Test
    @DisplayName("매니저 최대 수 미입력시 팀 생성을 하면 400을 반환한다.")
    void givenBlankManagerMaxCnt_whenCreateTeam_thenReturn400() throws Exception {
        // given
        TeamCreateRequest request = createValidTeamCreateRequest();
        request.setManagerMaxCnt(null);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(MANAGER_MAX_CNT_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(MANAGER_MAX_CNT_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("매니저 최대 수가 음수일시 팀 생성을 하면 400을 반환한다.")
    void givenNegativeManagerMaxCnt_whenCreateTeam_thenReturn400() throws Exception {
        // given
        TeamCreateRequest request = createValidTeamCreateRequest();
        request.setManagerMaxCnt((byte) -1);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(MANAGER_MAX_CNT_POSITIVE_OR_ZERO_ONLY.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(MANAGER_MAX_CNT_POSITIVE_OR_ZERO_ONLY.getMessage()));
    }

    @Test
    @DisplayName("팀 수정을 하면 200을 반환한다.")
    void givenValid_whenUpdateTeam_thenReturn200() throws Exception {
        // given
        TeamUpdateRequest request = createValidTeamUpdateRequest();

        // when
        ResultActions actions = mockMvc.perform(
                put("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(TEAM_UPDATED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(TEAM_UPDATED.getMessage()));
    }

    @Test
    @DisplayName("프로젝트명 20자 초과일시 팀 수정을 하면 400을 반환한다.")
    void givenGreaterThan20SizeProjectName_whenUpdateTeam_thenReturn400() throws Exception {
        // given
        TeamUpdateRequest request = createValidTeamUpdateRequest();
        request.setProjectName("가".repeat(21));

        // when
        ResultActions actions = mockMvc.perform(
                put("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(PROJECT_NAME_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PROJECT_NAME_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("프로젝트 설명 500자 초과일시 팀 수정을 하면 400을 반환한다.")
    void givenGreaterThan500SizeProjectDescription_whenUpdateTeam_thenReturn400() throws Exception {
        // given
        TeamUpdateRequest request = createValidTeamUpdateRequest();
        request.setProjectDescription("가".repeat(501));

        // when
        ResultActions actions = mockMvc.perform(
                put("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(PROJECT_DESCRIPTION_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PROJECT_DESCRIPTION_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("바라는 점 200자 초과일시 팀 수정을 하면 400을 반환한다.")
    void givenGreaterThan200SizeExpectation_whenUpdateTeam_thenReturn400() throws Exception {
        // given
        TeamUpdateRequest request = createValidTeamUpdateRequest();
        request.setExpectation("가".repeat(201));

        // when
        ResultActions actions = mockMvc.perform(
                put("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(EXPECTATION_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(EXPECTATION_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("오픈 채팅 링크 26자 미만일시 팀 수정을 하면 400을 반환한다.")
    void givenLessThan26SizeOpenChatUrl_whenUpdateTeam_thenReturn400() throws Exception {
        // given
        TeamUpdateRequest request = createValidTeamUpdateRequest();
        request.setOpenChatUrl("https://open.kakao.com/o/");

        // when
        ResultActions actions = mockMvc.perform(
                put("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(OPEN_CHAT_URL_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(OPEN_CHAT_URL_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("오픈 채팅 링크 100자 초과일시 팀 수정을 하면 400을 반환한다.")
    void givenGreaterThan100SizeOpenChatUrl_whenUpdateTeam_thenReturn400() throws Exception {
        // given
        TeamUpdateRequest request = createValidTeamUpdateRequest();
        request.setOpenChatUrl("https://open.kakao.com/o/" + "a".repeat(76));

        // when
        ResultActions actions = mockMvc.perform(
                put("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(OPEN_CHAT_URL_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(OPEN_CHAT_URL_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("팀장 포지션 미입력시 팀 수정을 하면 400을 반환한다")
    void givenBlankLeaderPosition_whenUpdateTeam_thenReturn400() throws Exception {
        // given
        TeamUpdateRequest request = createValidTeamUpdateRequest();
        request.setLeaderPosition(null);

        // when
        ResultActions actions = mockMvc.perform(
                put("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(LEADER_POSITION_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(LEADER_POSITION_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("잘못된 팀장 포지션 타입으로 팀 수정을 하면 400을 반환한다")
    void givenTypeLeaderPosition_whenUpdateTeam_thenReturn400() throws Exception {
        // given
        TeamUpdateRequest request = createValidTeamUpdateRequest();
        request.setLeaderPosition("ABC");

        // when
        ResultActions actions = mockMvc.perform(
                put("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(LEADER_POSITION_TYPE_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(LEADER_POSITION_TYPE_INVALID.getMessage()));
    }

    @Test
    @DisplayName("디자이너 최대 수 미입력시 팀 수정을 하면 400을 반환한다.")
    void givenBlankDesignerMaxCnt_whenUpdateTeam_thenReturn400() throws Exception {
        // given
        TeamUpdateRequest request = createValidTeamUpdateRequest();
        request.setDesignerMaxCnt(null);

        // when
        ResultActions actions = mockMvc.perform(
                put("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(DESIGNER_MAX_CNT_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(DESIGNER_MAX_CNT_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("디자이너 최대 수가 음수일시 팀 수정을 하면 400을 반환한다.")
    void givenNegativeDesignerMaxCnt_whenUpdateTeam_thenReturn400() throws Exception {
        // given
        TeamUpdateRequest request = createValidTeamUpdateRequest();
        request.setDesignerMaxCnt((byte) -1);

        // when
        ResultActions actions = mockMvc.perform(
                put("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(DESIGNER_MAX_CNT_POSITIVE_OR_ZERO_ONLY.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(DESIGNER_MAX_CNT_POSITIVE_OR_ZERO_ONLY.getMessage()));
    }

    @Test
    @DisplayName("백엔드 최대 수 미입력시 팀 수정을 하면 400을 반환한다.")
    void givenBlankBackendMaxCnt_whenUpdateTeam_thenReturn400() throws Exception {
        // given
        TeamUpdateRequest request = createValidTeamUpdateRequest();
        request.setBackendMaxCnt(null);

        // when
        ResultActions actions = mockMvc.perform(
                put("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(BACKEND_MAX_CNT_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(BACKEND_MAX_CNT_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("백엔드 최대 수가 음수일시 팀 수정을 하면 400을 반환한다.")
    void givenNegativeBackendMaxCnt_whenUpdateTeam_thenReturn400() throws Exception {
        // given
        TeamUpdateRequest request = createValidTeamUpdateRequest();
        request.setBackendMaxCnt((byte) -1);

        // when
        ResultActions actions = mockMvc.perform(
                put("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(BACKEND_MAX_CNT_POSITIVE_OR_ZERO_ONLY.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(BACKEND_MAX_CNT_POSITIVE_OR_ZERO_ONLY.getMessage()));
    }

    @Test
    @DisplayName("프런트 최대 수 미입력시 팀 수정을 하면 400을 반환한다.")
    void givenBlankFrontendMaxCnt_whenUpdateTeam_thenReturn400() throws Exception {
        // given
        TeamUpdateRequest request = createValidTeamUpdateRequest();
        request.setFrontendMaxCnt(null);

        // when
        ResultActions actions = mockMvc.perform(
                put("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(FRONTEND_MAX_CNT_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(FRONTEND_MAX_CNT_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("프런트 최대 수가 음수일시 팀 수정을 하면 400을 반환한다.")
    void givenNegativeFrontendMaxCnt_whenUpdateTeam_thenReturn400() throws Exception {
        // given
        TeamUpdateRequest request = createValidTeamUpdateRequest();
        request.setFrontendMaxCnt((byte) -1);

        // when
        ResultActions actions = mockMvc.perform(
                put("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(FRONTEND_MAX_CNT_POSITIVE_OR_ZERO_ONLY.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(FRONTEND_MAX_CNT_POSITIVE_OR_ZERO_ONLY.getMessage()));
    }

    @Test
    @DisplayName("매니저 최대 수 미입력시 팀 수정을 하면 400을 반환한다.")
    void givenBlankManagerMaxCnt_whenUpdateTeam_thenReturn400() throws Exception {
        // given
        TeamUpdateRequest request = createValidTeamUpdateRequest();
        request.setManagerMaxCnt(null);

        // when
        ResultActions actions = mockMvc.perform(
                put("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(MANAGER_MAX_CNT_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(MANAGER_MAX_CNT_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("매니저 최대 수가 음수일시 팀 수정을 하면 400을 반환한다.")
    void givenNegativeManagerMaxCnt_whenUpdateTeam_thenReturn400() throws Exception {
        // given
        TeamUpdateRequest request = createValidTeamUpdateRequest();
        request.setManagerMaxCnt((byte) -1);

        // when
        ResultActions actions = mockMvc.perform(
                put("/api/v1/team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(MANAGER_MAX_CNT_POSITIVE_OR_ZERO_ONLY.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(MANAGER_MAX_CNT_POSITIVE_OR_ZERO_ONLY.getMessage()));
    }

    @Test
    @DisplayName("본인 현재 팀을 조회하면 200을 반환한다.")
    void givenValid_whenFindMyCurrentTeam_thenReturn200() throws Exception {
        // given & when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/team")
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(SELF_TEAM_FOUND.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(SELF_TEAM_FOUND.getMessage()));
    }

    @Test
    @DisplayName("팀 단건 조회를 하면 200을 반환한다.")
    void givenValid_whenFindTeam_thenReturn200() throws Exception {
        // given
        long teamId = 1L;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/team/{team-id}", teamId)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(TEAM_FOUND.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(TEAM_FOUND.getMessage()));
    }

    @Test
    @DisplayName("양수가 아닌 팀 식별자로 팀 단건 조회를 하면 400을 반환한다.")
    void givenNonPositiveTeamId_whenFindTeam_thenReturn400() throws Exception {
        // given
        long teamId = 0L;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/team/{team-id}", teamId)
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
    @DisplayName("팀원을 찾는 팀 페이징 조회를 하면 200을 반환한다.")
    void givenValid_whenFindTeamsLookingForUsers_theReturn200() throws Exception {
        // given & when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/team/recruiting")
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(TEAMS_RECRUITING_USERS_FOUND.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(TEAMS_RECRUITING_USERS_FOUND.getMessage()));
    }

    @Test
    @DisplayName("올바르지 않은 포지션으로 팀원을 찾는 팀 페이징 조회를 하면 400을 반환한다.")
    void givenFormatPosition_whenFindTeamsLookingFroUsers_theReturn400() throws Exception {
        // given
        String position = "WRITER";

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/team/recruiting")
                        .param("position", position)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(POSITION_TYPE_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(POSITION_TYPE_INVALID.getMessage()));
    }

    @Test
    @DisplayName("양수가 아닌 페이지 시작점으로 팀원을 찾는 팀 페이징 조회를 하면 400을 반환한다.")
    void givenNonPositivePageFrom_whenFindTeamsLookingFroUsers_theReturn400() throws Exception {
        // given
        Long pageFrom = 0L;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/team/recruiting")
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
    @DisplayName("양수가 아닌 페이지 크기로 팀원을 찾는 팀 페이징 조회를 하면 400을 반환한다.")
    void givenNonPositivePageSize_whenFindTeamsLookingFroUsers_theReturn400() throws Exception {
        // given
        Long pageSize = 0L;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/team/recruiting")
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
    @DisplayName("100 초과의 페이지 크기로 팀원을 찾는 팀 페이징 조회를 하면 400을 반환한다.")
    void givenGreaterThan100PageSize_whenFindTeamsLookingFroUsers_theReturn400() throws Exception {
        // given
        Long pageSize = 101L;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/team/recruiting")
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
    @DisplayName("팀원 모집 여부 업데이트를 하면 200을 반환한다.")
    void givenValid_whenUpdateIsRecruiting_thenReturn200() throws Exception {
        // given
        TeamIsRecruitingUpdateRequest request = createValidTeamIsRecruitingUpdateRequest();

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/team/recruiting")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(TEAM_IS_RECRUITING_UPDATED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(TEAM_IS_RECRUITING_UPDATED.getMessage()));
    }

    @Test
    @DisplayName("팀원 모집 여부 미입력시 팀원 모집 여부 업데이트를 하면 400을 반환한다.")
    void givenBlankIsRecruiting_whenUpdateIsRecruiting_thenReturn400() throws Exception {
        // given
        TeamIsRecruitingUpdateRequest request = createValidTeamIsRecruitingUpdateRequest();
        request.setIsRecruiting(null);

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/team/recruiting")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(IS_RECRUITING_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(IS_RECRUITING_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("프로젝트 미완료 종료를 하면 200을 반환한다.")
    void givenValid_whenProjectIncomplete_thenReturn200() throws Exception {
        // given & when
        ResultActions actions = mockMvc.perform(
                delete("/api/v1/team/incomplete")
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(PROJECT_INCOMPLETE.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PROJECT_INCOMPLETE.getMessage()));
    }

    @Test
    @DisplayName("프로젝트 완료 종료를 하면 200을 반환한다.")
    void givenValid_whenProjectComplete_thenReturn200() throws Exception {
        // given
        TeamCompleteRequest request = createValidTeamCompleteRequest();

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/team/complete")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(PROJECT_COMPLETE.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PROJECT_COMPLETE.getMessage()));
    }

    @Test
    @DisplayName("완료한 프로젝트 URL 미입력시 프로젝트 완료 종료를 하면 400을 반환한다.")
    void givenBlankProjectUrl_whenProjectComplete_thenReturn400() throws Exception {
        // given
        TeamCompleteRequest request = createValidTeamCompleteRequest();
        request.setProjectUrl("");

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/team/complete")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(PROJECT_URL_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PROJECT_URL_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("팀원 추방을 하면 200을 반환한다.")
    void givenValid_whenFireTeamMember_thenReturn200() throws Exception {
        // given
        Long userId = 1L;

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/team/user/{user-id}/fire", userId)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(TEAMMATE_FIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(TEAMMATE_FIRED.getMessage()));
    }

    @Test
    @DisplayName("양수가 아닌 회원 식별자로 팀원 추방을 하면 400을 반환한다.")
    void givenNonPositiveUserId_whenFireTeamMember_thenReturn400() throws Exception {
        // given
        Long userId = 0L;

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/team/user/{user-id}/fire", userId)
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
    @DisplayName("팀 탈퇴를 하면 200을 반환한다.")
    void givenValid_whenLeaveTeam_thenReturn200() throws Exception {
        // given & when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/team/leave")
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(USER_LEFT_TEAM.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(USER_LEFT_TEAM.getMessage()));
    }

    private TeamCompleteRequest createValidTeamCompleteRequest() {
        return TeamCompleteRequest.builder()
                .projectUrl("github.com/gabojait")
                .build();
    }

    private TeamIsRecruitingUpdateRequest createValidTeamIsRecruitingUpdateRequest() {
        return TeamIsRecruitingUpdateRequest.builder()
                .isRecruiting(false)
                .build();
    }

    private TeamCreateRequest createValidTeamCreateRequest() {
        return TeamCreateRequest.builder()
                .projectName("가보자잇")
                .projectDescription("프로젝트 설명입니다.")
                .expectation("바라는 점입니다.")
                .openChatUrl("https://open.kakao.com/o/gabojait")
                .leaderPosition(Position.MANAGER.name())
                .designerMaxCnt((byte) 2)
                .backendMaxCnt((byte) 2)
                .frontendMaxCnt((byte) 2)
                .managerMaxCnt((byte) 2)
                .build();
    }

    private TeamUpdateRequest createValidTeamUpdateRequest() {
        return TeamUpdateRequest.builder()
                .projectName("가보자잇")
                .projectDescription("프로젝트 설명입니다.")
                .expectation("바라는 점입니다.")
                .openChatUrl("https://open.kakao.com/o/gabojait")
                .leaderPosition(Position.MANAGER.name())
                .designerMaxCnt((byte) 2)
                .backendMaxCnt((byte) 2)
                .frontendMaxCnt((byte) 2)
                .managerMaxCnt((byte) 2)
                .build();
    }
}