package com.gabojait.gabojaitspring.api.controller.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabojait.gabojaitspring.api.service.notification.NotificationService;
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

import static com.gabojait.gabojaitspring.common.constant.code.ErrorCode.*;
import static com.gabojait.gabojaitspring.common.constant.code.SuccessCode.NOTIFICATIONS_FOUND;
import static com.gabojait.gabojaitspring.common.constant.code.SuccessCode.NOTIFICATION_READ;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    @MockBean private JwtProvider jwtProvider;
    @MockBean private NotificationService notificationService;

    @Test
    @DisplayName("알림 페이징 조회를 하면 200을 반환한다.")
    void givenValid_whenFindPageNotification_thenReturn200() throws Exception {
        // given & when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/notification")
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(NOTIFICATIONS_FOUND.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(NOTIFICATIONS_FOUND.getMessage()));
    }

    @Test
    @DisplayName("페이지 시작점이 양수가 아닐시 알림 페이징 조회를 하면 400을 반환한다.")
    void givenNonPositivePageFrom_whenFindPageNotification_thenReturn400() throws Exception {
        // given
        Long pageFrom = 0L;

        // given & when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/notification")
                        .param("page-from", pageFrom.toString())
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(PAGE_FROM_POSITIVE_ONLY.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PAGE_FROM_POSITIVE_ONLY.getMessage()));
    }

    @Test
    @DisplayName("페이지 크기가 양수가 아닐시 알림 페이징 조회를 하면 400을 반환한다.")
    void givenNonPositivePageSize_whenFindPageNotification_thenReturn400() throws Exception {
        // given
        Integer pageSize = 0;

        // given & when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/notification")
                        .param("page-size", pageSize.toString())
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(PAGE_SIZE_POSITIVE_ONLY.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PAGE_SIZE_POSITIVE_ONLY.getMessage()));
    }

    @Test
    @DisplayName("페이지 크기가 100 초과일시 알림 페이징 조회를 하면 400을 반환한다.")
    void givenGreaterThan100PageSize_whenFindPageNotification_thenReturn400() throws Exception {
        // given
        Integer pageSize = 101;

        // given & when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/notification")
                        .param("page-size", pageSize.toString())
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(PAGE_SIZE_RANGE_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PAGE_SIZE_RANGE_INVALID.getMessage()));
    }

    @Test
    @DisplayName("알림 읽기를 하면 200을 반환한다.")
    void givenValid_whenReadNotification_thenReturn200() throws Exception {
        // given
        long notificationId = 1L;

        // given & when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/user/notification/{notification-id}", notificationId)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(NOTIFICATION_READ.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(NOTIFICATION_READ.getMessage()));
    }

    @Test
    @DisplayName("양수가 아닌 알림 식별자로 알림 읽기를 하면 400을 반환한다.")
    void givenNonPositiveNotificationId_whenReadNotification_thenReturn400() throws Exception {
        // given
        long notificationId = 0L;

        // given & when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/user/notification/{notification-id}", notificationId)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(NOTIFICATION_ID_POSITIVE_ONLY.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(NOTIFICATION_ID_POSITIVE_ONLY.getMessage()));
    }
}