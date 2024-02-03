package com.gabojait.gabojaitspring.api.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabojait.gabojaitspring.api.dto.user.request.ContactCreateRequest;
import com.gabojait.gabojaitspring.api.dto.user.request.ContactVerifyRequest;
import com.gabojait.gabojaitspring.api.service.user.ContactService;
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

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;
import static com.gabojait.gabojaitspring.common.code.SuccessCode.EMAIL_VERIFIED;
import static com.gabojait.gabojaitspring.common.code.SuccessCode.VERIFICATION_CODE_SENT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(ContactController.class)
@AutoConfigureMockMvc(addFilters = false)
class ContactControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    @MockBean private JwtProvider jwtProvider;
    @MockBean private ContactService contactService;

    @Test
    @DisplayName("인증코드 전송 요청을 하면 201을 반환한다.")
    void givenContactCreateRequest_whenSendVerificationCode_thenReturn201() throws Exception {
        // given
        ContactCreateRequest request = createValidContactCreateRequest();

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/contact")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.responseCode")
                        .value(VERIFICATION_CODE_SENT.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(VERIFICATION_CODE_SENT.getMessage()));
    }

    @Test
    @DisplayName("이메일 미입력시 인증코드 전송 요청을 하면 400을 반환한다.")
    void givenBlankEmail_whenSendVerificationCode_thenReturn400() throws Exception {
        // given
        ContactCreateRequest request = createValidContactCreateRequest();
        request.setEmail("");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/contact")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(EMAIL_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(EMAIL_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("잘못된 이메일 포맷시 인증코드 전송 요청을 하면 400을 반환한다.")
    void givenFormatEmail_whenSendVerificationCode_thenReturn400() throws Exception {
        // given
        ContactCreateRequest request = createValidContactCreateRequest();
        request.setEmail("testergabojait.com");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/contact")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(EMAIL_FORMAT_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(EMAIL_FORMAT_INVALID.getMessage()));
    }

    @Test
    @DisplayName("인증코드 확인 요청을 하면 200을 반환한다.")
    void givenContactVerifyRequest_whenVerifyCode_thenReturn200() throws Exception {
        // given
        ContactVerifyRequest request = createContactVerifyRequest();

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/contact")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(EMAIL_VERIFIED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(EMAIL_VERIFIED.getMessage()));
    }

    @Test
    @DisplayName("이메일 미입력시 인증코드 확인 요청을 하면 400을 반환한다.")
    void givenBlankEmail_whenVerifyCode_thenReturn400() throws Exception {
        // given
        ContactVerifyRequest request = createContactVerifyRequest();
        request.setEmail("");

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/contact")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(EMAIL_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(EMAIL_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("인증코드 인증코드 확인 요청을 하면 400을 반환한다.")
    void givenBlankVerificationCode_whenVerifyCode_thenReturn400() throws Exception {
        // given
        ContactVerifyRequest request = createContactVerifyRequest();
        request.setVerificationCode("");

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/contact")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(VERIFICATION_CODE_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(VERIFICATION_CODE_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("잘못된 이메일 포맷시 인증코드 확인 요청을 하면 400을 반환한다.")
    void givenFormatEmail_whenVerifyCode_thenReturn400() throws Exception {
        // given
        ContactVerifyRequest request = createContactVerifyRequest();
        request.setEmail("testergabojait.com");

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/contact")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(EMAIL_FORMAT_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(EMAIL_FORMAT_INVALID.getMessage()));
    }

    private ContactCreateRequest createValidContactCreateRequest() {
        return ContactCreateRequest.builder()
                .email("tester@gabojait.com")
                .build();
    }

    private ContactVerifyRequest createContactVerifyRequest() {
        return ContactVerifyRequest.builder()
                .email("tester@gabojait.com")
                .verificationCode("000000")
                .build();
    }
}