package com.gabojait.gabojaitspring.api.controller.document;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(DocumentController.class)
@AutoConfigureMockMvc(addFilters = false)
class DocumentControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    @MockBean private JwtProvider jwtProvider;

    @Test
    @DisplayName("개인정보처리방침을 요청하면 200을 반환한다.")
    void givenValid_whenPrivacy_thenReturn200() throws Exception {
        // given & when
        ResultActions actions = mockMvc.perform(
                get("/docs/privacy")
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("서비스이용약관을 요청하면 200을 반환한다.")
    void givenValid_whenService_thenReturn200() throws Exception {
        // given & when
        ResultActions actions = mockMvc.perform(
                get("/docs/service")
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk());
    }
}