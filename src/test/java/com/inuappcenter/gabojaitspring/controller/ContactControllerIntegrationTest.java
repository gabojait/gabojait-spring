package com.inuappcenter.gabojaitspring.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inuappcenter.gabojaitspring.controller.common.JsonMapping;
import com.inuappcenter.gabojaitspring.user.controller.ContactController;
import com.inuappcenter.gabojaitspring.user.dto.ContactSaveRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;

@DataMongoTest
public class ContactControllerIntegrationTest {

    @Autowired
    private ContactController contactController;

    private MockMvc mockMvc;


//    @BeforeTestExecution
//    private void setUp() {
//        this.mockMvc = MockMvcBuilders.standaloneSetup(contactController).build();
//    }
//
//    @Test
//    public void createContact() throws Exception {
//        // Given
//        String uri = "/api/v1/contact/create";
//
//        ContactSaveRequestDto request = ContactSaveRequestDto.builder()
//                .email("test@gabojait.com")
//                .build();
//        String jsonInput = super.mapToJson(request);
//
//        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(uri));
//
//
//        MvcResult mvcResult = mockMvc.perform(
//                MockMvcRequestBuilders.get(uri)
//                        .accept(MediaType.APPLICATION_JSON_VALUE))
//                .andReturn();
//
//    }

}
