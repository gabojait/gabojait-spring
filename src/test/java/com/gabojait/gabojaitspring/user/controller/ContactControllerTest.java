package com.gabojait.gabojaitspring.user.controller;

import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.ControllerTestSetup;
import com.gabojait.gabojaitspring.user.dto.req.ContactSaveReqDto;
import com.gabojait.gabojaitspring.user.dto.req.ContactVerifyReqDto;
import com.gabojait.gabojaitspring.user.service.ContactService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;
import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(ContactController.class)
class ContactControllerTest extends ControllerTestSetup {

    @MockBean
    private ContactService contactService;

    @MockBean
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("인증코드 전송_올바른 요청시_201반환")
    void sendVerificationCode_givenValidReq_return201() throws Exception {
        ContactSaveReqDto requestDto = new ContactSaveReqDto();
        requestDto.setEmail("tester@gabojait.com");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/contact")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(VERIFICATION_CODE_SENT.getHttpStatus().value());
        assertThat(response).contains(VERIFICATION_CODE_SENT.name());
    }

    @Test
    @DisplayName("인증코드 전송_이메일 미입력시_400반환")
    void sendVerificationCode_givenEmailFieldRequired_return400() throws Exception {
        ContactSaveReqDto requestDto = new ContactSaveReqDto();
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/contact")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EMAIL_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(EMAIL_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("인증코드 전송_잘못된 이메일 포맷시_400반환")
    void sendVerificationCode_givenEmailFormatInvalid_return400() throws Exception {
        ContactSaveReqDto requestDto = new ContactSaveReqDto();
        requestDto.setEmail("testgabojait.com");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EMAIL_FORMAT_INVALID.getHttpStatus().value());
        assertThat(response).contains(EMAIL_FORMAT_INVALID.name());
    }

    @Test
    @WithMockUser(roles = "GUEST")
    @DisplayName("인증코드 확인_올바른 요청시_200반환")
    void verifyCode_givenValidReq_return200() throws Exception {
        ContactVerifyReqDto requestDto = new ContactVerifyReqDto();
        requestDto.setEmail("tester@gabojait.com");
        requestDto.setVerificationCode("000000");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EMAIL_VERIFIED.getHttpStatus().value());
        assertThat(response).contains(EMAIL_VERIFIED.name());
    }

    @Test
    @WithMockUser(roles = "GUEST")
    @DisplayName("인증코드 확인_이메일 미입력시_400반환")
    void verifyCode_givenEmailFieldRequired_return400() throws Exception {
        ContactVerifyReqDto requestDto = new ContactVerifyReqDto();
        requestDto.setVerificationCode("000000");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EMAIL_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(EMAIL_FIELD_REQUIRED.name());
    }

    @Test
    @WithMockUser(roles = "GUEST")
    @DisplayName("인증코드 확인_인증코드 미입력시_400반환")
    void verifyCode_givenVerificationCodeFieldRequired_return400() throws Exception {
        ContactVerifyReqDto requestDto = new ContactVerifyReqDto();
        requestDto.setEmail("tester@gabojait.com");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(VERIFICATION_CODE_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(VERIFICATION_CODE_FIELD_REQUIRED.name());
    }

    @Test
    @WithMockUser(roles = "GUEST")
    @DisplayName("인증코드 확인_잘못된 이메일 포맷시_400반환")
    void verifyCode_givenEmailFormatInvalid_return400() throws Exception {
        ContactVerifyReqDto requestDto = new ContactVerifyReqDto();
        requestDto.setEmail("testergabojait.com");
        requestDto.setVerificationCode("000000");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EMAIL_FORMAT_INVALID.getHttpStatus().value());
        assertThat(response).contains(EMAIL_FORMAT_INVALID.name());
    }
}