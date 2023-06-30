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
    @DisplayName("인증코드 전송_201VERIFICATION_CODE_SENT")
    void sendVerificationCode_return201VerificationCodeSent() throws Exception {
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
    @DisplayName("인증코드 전송_400EMAIL_FIELD_REQUIRED")
    void sendVerificationCode_return400EmailFieldRequired() throws Exception {
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
    @DisplayName("인증코드 전송_400EMAIL_FORMAT_INVALID")
    void sendVerificationCode_return400EmailFormatInvalid() throws Exception {
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
    @DisplayName("인증코드 확인_200EMAIL_VERIFIED")
    void verifyCode_return200EmailVerified() throws Exception {
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
    @DisplayName("인증코드 확인_400EMAIL_FIELD_REQUIRED")
    void verifyCode_return400EmailFieldRequired() throws Exception {
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
    @DisplayName("인증코드 확인_400VERIFICATION_FIELD_REQUIRED")
    void verifyCode_return400VerificationCodeFieldRequired() throws Exception {
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
    @DisplayName("인증코드 확인_400EMAIL_FORMAT_INVALID")
    void verifyCode_return400EmailFormatInvalid() throws Exception {
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