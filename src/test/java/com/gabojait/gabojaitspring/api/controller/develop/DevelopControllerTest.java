package com.gabojait.gabojaitspring.api.controller.develop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabojait.gabojaitspring.api.service.develop.DevelopService;
import com.gabojait.gabojaitspring.auth.CustomAuthenticationEntryPoint;
import com.gabojait.gabojaitspring.auth.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.SERVER_ERROR;
import static com.gabojait.gabojaitspring.common.code.ErrorCode.TESTER_ID_POSITIVE_ONLY;
import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(DevelopController.class)
@AutoConfigureMockMvc(addFilters = false)
class DevelopControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    @MockBean private JwtProvider jwtProvider;
    @MockBean private DevelopService developService;

    @Test
    @DisplayName("헬스 체크를 하면 200을 반환한다.")
    void givenValid_whenHealthCheck_thenReturn200() throws Exception {
        // given
        String serverName = "Gabojait Test";
        when(developService.getServerName())
                .thenReturn(serverName);

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/health")
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(SERVER_OK.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(serverName + " " + SERVER_OK.getMessage()));
    }

    @Test
    @DisplayName("모니터링 체크를 하면 500을 반환한다.")
    void givenValid_whenMonitorCheck_thenReturn500() throws Exception {
        // given & when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/monitor")
        );

        // then
        actions.andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.responseCode")
                        .value(SERVER_ERROR.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(SERVER_ERROR.getMessage()));
    }

    @Test
    @DisplayName("데이터베이스 초기화를 하면 200을 반환한다.")
    void givenValid_whenResetAndInjectTest_thenReturn200() throws Exception {
        // given & when
        ResultActions actions = mockMvc.perform(
                delete("/api/v1/test")
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(DATABASE_RESET.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(DATABASE_RESET.getMessage()));
    }

    @Test
    @DisplayName("테스트 계정 토큰 발급을 하면 200을 반환한다.")
    void givenValid_whenTestDataToken_thenReturn200() throws Exception {
        // given
        long testerId = 1;

        when(jwtProvider.createJwt(anyLong()))
                .thenReturn(HttpHeaders.EMPTY);

        when(developService.findTester(anyLong()))
                .thenReturn("tester");

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/test/user/{tester-id}", testerId)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(TESTER_TOKEN_ISSUED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(TESTER_TOKEN_ISSUED.getMessage()));
    }

    @Test
    @DisplayName("테스터 식별자가 양수가 아닐시 테스트 계정 토큰 발급을 하면 400을 반환한다.")
    void givenNonPositiveTesterId_whenTestDataToken_thenReturn400() throws Exception {
        // given
        long testerId = 0;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/test/user/{tester-id}", testerId)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(TESTER_ID_POSITIVE_ONLY.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(TESTER_ID_POSITIVE_ONLY.getMessage()));
    }
}