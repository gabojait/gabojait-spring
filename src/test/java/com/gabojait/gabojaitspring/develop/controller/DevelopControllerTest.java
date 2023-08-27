package com.gabojait.gabojaitspring.develop.controller;

import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.WebMvc;
import com.gabojait.gabojaitspring.develop.dto.req.DevelopFcmReqDto;
import com.gabojait.gabojaitspring.develop.service.DevelopService;
import com.gabojait.gabojaitspring.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;
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
        User tester = User.testBuilder()
                .id(1L)
                .build();

        doReturn(1L)
                .when(this.jwtProvider)
                .getId(any());

        doReturn(tester)
                .when(this.developService)
                .findOneTester(any());

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
    @DisplayName("모니터링 체크 | 올바른 요청시 | 500반환")
    void healthCheck_givenValidReq_return500() throws Exception {
        // given & when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/monitor"))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(SERVER_ERROR.getHttpStatus().value());
        assertThat(response).contains(SERVER_ERROR.name());
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

    @Test
    @DisplayName("테스트 계정 토큰 발급 | 테스터 식별자가 양수가 아닐시 | 400반환")
    void testDataToken_givenTesterIdPositiveOnly_return400() throws Exception {
        // given
        Integer testerId = 0;

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/test/user/{tester-id}", testerId))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(TESTER_ID_POSITIVE_ONLY.getHttpStatus().value());
        assertThat(response).contains(TESTER_ID_POSITIVE_ONLY.name());
    }

    @Test
    @DisplayName("테스트 FCM 전송 | 올바른 요청시 | 200반환")
    void sendTestFcm_givenValidReq_return200() throws Exception {
        // given
        DevelopFcmReqDto reqDto = getValidDevelopFcmReqDto();
        String request = mapToJson(reqDto);

        // then
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/test/fcm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // when
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(TEST_FCM_SENT.getHttpStatus().value());
        assertThat(response).contains(TEST_FCM_SENT.name());
    }

    @Test
    @DisplayName("테스트 FCM 전송 | FCM 제목 미입력시 | 400반환")
    void sendTestFcm_givenFcmTitleFieldRequired_return400() throws Exception {
        // given
        DevelopFcmReqDto reqDto = getValidDevelopFcmReqDto();
        reqDto.setFcmTitle("");
        String request = mapToJson(reqDto);

        // then
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/test/fcm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // when
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(FCM_TITLE_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(FCM_TITLE_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("테스트 FCM 전송 | FCM 메세지 미입력시 | 400반환")
    void sendTestFcm_givenFcmMessageFieldRequired_return400() throws Exception {
        // given
        DevelopFcmReqDto reqDto = getValidDevelopFcmReqDto();
        reqDto.setFcmMessage("");
        String request = mapToJson(reqDto);

        // then
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/test/fcm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // when
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(FCM_MESSAGE_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(FCM_MESSAGE_FIELD_REQUIRED.name());
    }

    private Integer getValidId() {
        return 1;
    }

    private DevelopFcmReqDto getValidDevelopFcmReqDto() {
        return DevelopFcmReqDto.builder()
                .fcmTitle("테스트 제목")
                .fcmMessage("테스트 메세지")
                .build();
    }
}