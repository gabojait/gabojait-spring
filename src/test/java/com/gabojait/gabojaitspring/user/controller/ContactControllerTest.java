package com.gabojait.gabojaitspring.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.user.dto.req.ContactSaveReqDto;
import com.gabojait.gabojaitspring.user.service.ContactService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ContactController.class)
@TestPropertySource(locations = {"classpath:application-test.yml"})
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContactService contactService;

    @MockBean
    private JwtProvider jwtProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSendVerificationCode() throws Exception {
        ContactSaveReqDto request = new ContactSaveReqDto();
        request.setEmail("test@example.com");

//        mockMvc.perform(post("/api/v1/contact")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .body(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.responseCode").value("VERIFICATION"))
//                .andExpect(jsonPath("$.responseMessage").value("Verification code sent"));
//
//        verify(contactService).sendRegisterVerificationCode(request);
    }

}