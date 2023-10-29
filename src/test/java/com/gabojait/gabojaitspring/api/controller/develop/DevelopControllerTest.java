package com.gabojait.gabojaitspring.api.controller.develop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabojait.gabojaitspring.api.dto.user.response.UserDefaultResponse;
import com.gabojait.gabojaitspring.api.service.develop.DevelopService;
import com.gabojait.gabojaitspring.auth.CustomAuthenticationEntryPoint;
import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.domain.user.Gender;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

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
}