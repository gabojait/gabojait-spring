package com.gabojait.gabojaitspring.develop.controller;

import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.WebMvc;
import com.gabojait.gabojaitspring.develop.service.DevelopService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;

import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(DevelopController.class)
@AutoConfigureMockMvc(addFilters = false)
class DevelopControllerTest extends WebMvc {

    @MockBean
    private DevelopService developService;

    @MockBean
    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        doReturn(1L)
                .when(this.jwtProvider)
                .getId(any());

        doReturn("서버 1")
                .when(this.developService)
                .getServerName();
    }

    @Test
    @DisplayName("헬스 체크 | 올바른 요청시 | 200반환")
    void healthCheck_givenValidReq_return200() throws Exception {
        // given & when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/health"))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(SERVER_OK.getHttpStatus().value());
        assertThat(response).contains(SERVER_OK.name());
    }

    @Test
    @DisplayName("데이터베이스 초기화 후 테스트 데이터 주입 | 올바른 요청시 | 200반환")
    void resetAndInjectTest_givenValidReq_return200() throws Exception {
        // given & when
        MvcResult mvcResult = this.mockMvc.perform(delete("/api/v1/test"))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(TEST_DATA_INJECTED.getHttpStatus().value());
        assertThat(response).contains(TEST_DATA_INJECTED.name());
    }

    @Test
    @DisplayName("테스트 계정 토큰 발급 | 올바른 요청시 | 200반환")
    void testDataToken_givenValidReq_return200() throws Exception {
        // given
        Integer testerId = getValidId();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/test/user/{tester-id}", testerId))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(TESTER_TOKEN_ISSUED.getHttpStatus().value());
        assertThat(response).contains(TESTER_TOKEN_ISSUED.name());
    }
    private Integer getValidId() {
        return 1;
    }
}