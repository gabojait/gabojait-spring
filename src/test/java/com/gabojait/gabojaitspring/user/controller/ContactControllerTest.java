package com.gabojait.gabojaitspring.user.controller;

import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.ControllerTestSetup;
import com.gabojait.gabojaitspring.user.dto.req.ContactSaveReqDto;
import com.gabojait.gabojaitspring.user.dto.req.ContactVerifyReqDto;
import com.gabojait.gabojaitspring.user.service.ContactService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(ContactController.class)
@AutoConfigureMockMvc(addFilters = false)
class ContactControllerTest extends ControllerTestSetup {

    @MockBean
    private ContactService contactService;

    @MockBean
    private JwtProvider jwtProvider;

    private ContactSaveReqDto getValidContactSaveReqDto() {
        ContactSaveReqDto reqDto = new ContactSaveReqDto();
        reqDto.setEmail("tester@gabojait.com");
        return reqDto;
    }

    private ContactVerifyReqDto getValidContactVerifyReqDto() {
        ContactVerifyReqDto reqDto = new ContactVerifyReqDto();
        reqDto.setEmail("tester@gabojait.com");
        reqDto.setVerificationCode("000000");
        return reqDto;
    }

    @Test
    @DisplayName("인증코드 전송 | 올바른 요청시 | 201응답")
    void sendVerificationCode_givenValidReq_return201() throws Exception {
        // given
        ContactSaveReqDto reqDto = getValidContactSaveReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/contact")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(VERIFICATION_CODE_SENT.getHttpStatus().value());
        assertThat(response).contains(VERIFICATION_CODE_SENT.name());
    }

    @Test
    @DisplayName("인증코드 전송 | 이메일 미입력시 | 400응답")
    void sendVerificationCode_givenEmailFieldRequired_return400() throws Exception {
        // given
        ContactSaveReqDto reqDto = getValidContactSaveReqDto();
        reqDto.setEmail("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/contact")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EMAIL_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(EMAIL_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("인증코드 전송 | 잘못된 이메일 포맷시 | 400응답")
    void sendVerificationCode_givenEmailFormatInvalid_return400() throws Exception {
        // given
        ContactSaveReqDto reqDto = getValidContactSaveReqDto();
        reqDto.setEmail("testergabojait.com");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EMAIL_FORMAT_INVALID.getHttpStatus().value());
        assertThat(response).contains(EMAIL_FORMAT_INVALID.name());
    }

    @Test
    @DisplayName("인증코드 확인 | 올바른 요청시 | 200응답")
    void verifyCode_givenValidReq_return200() throws Exception {
        // given
        ContactVerifyReqDto reqDto = getValidContactVerifyReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EMAIL_VERIFIED.getHttpStatus().value());
        assertThat(response).contains(EMAIL_VERIFIED.name());
    }

    @Test
    @DisplayName("인증코드 확인 | 이메일 미입력시 | 400응답")
    void verifyCode_givenEmailFieldRequired_return400() throws Exception {
        // given
        ContactVerifyReqDto reqDto = getValidContactVerifyReqDto();
        reqDto.setEmail("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EMAIL_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(EMAIL_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("인증코드 확인 | 인증코드 미입력시 | 400응답")
    void verifyCode_givenVerificationCodeFieldRequired_return400() throws Exception {
        // given
        ContactVerifyReqDto reqDto = getValidContactVerifyReqDto();
        reqDto.setVerificationCode("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(VERIFICATION_CODE_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(VERIFICATION_CODE_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("인증코드 확인 | 잘못된 이메일 포맷시 | 400응답")
    void verifyCode_givenEmailFormatInvalid_return400() throws Exception {
        // given
        ContactVerifyReqDto reqDto = getValidContactVerifyReqDto();
        reqDto.setEmail("testergabojait.com");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EMAIL_FORMAT_INVALID.getHttpStatus().value());
        assertThat(response).contains(EMAIL_FORMAT_INVALID.name());
    }
}