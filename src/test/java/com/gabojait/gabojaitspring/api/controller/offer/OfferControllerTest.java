package com.gabojait.gabojaitspring.api.controller.offer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabojait.gabojaitspring.api.dto.offer.request.OfferCreateRequest;
import com.gabojait.gabojaitspring.api.dto.offer.request.OfferDecideRequest;
import com.gabojait.gabojaitspring.api.service.offer.OfferService;
import com.gabojait.gabojaitspring.config.auth.CustomAuthenticationEntryPoint;
import com.gabojait.gabojaitspring.config.auth.JwtProvider;
import com.gabojait.gabojaitspring.domain.user.Position;
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
import static com.gabojait.gabojaitspring.common.constant.code.SuccessCode.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(OfferController.class)
@AutoConfigureMockMvc(addFilters = false)
class OfferControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    @MockBean private JwtProvider jwtProvider;
    @MockBean private OfferService offerService;

    @Test
    @DisplayName("회원이 팀에 지원을 하면 201을 반환한다.")
    void givenValid_whenUserOffer_thenReturn201() throws Exception {
        // given
        OfferCreateRequest request = createValidOfferCreateRequest();
        long teamId = 1L;

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/team/{team-id}/offer", teamId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.responseCode")
                        .value(OFFERED_BY_USER.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(OFFERED_BY_USER.getMessage()));
    }

    @Test
    @DisplayName("잘못된 포지션 타입시 회원이 팀에 지원을 하면 400을 반환한다.")
    void givenFormatPosition_whenUserOffer_thenReturn400() throws Exception {
        // given
        OfferCreateRequest request = createValidOfferCreateRequest();
        request.setOfferPosition("WRITER");
        long teamId = 1L;

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/team/{team-id}/offer", teamId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(OFFER_POSITION_TYPE_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(OFFER_POSITION_TYPE_INVALID.getMessage()));
    }

    @Test
    @DisplayName("팀 식별자가 양수가 아닐시 회원이 팀에 지원을 하면 400을 반환한다.")
    void givenNonPositiveTeamId_whenUserOffer_thenReturn400() throws Exception {
        // given
        OfferCreateRequest request = createValidOfferCreateRequest();
        long teamId = 0L;

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/team/{team-id}/offer", teamId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(TEAM_ID_POSITIVE_ONLY.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(TEAM_ID_POSITIVE_ONLY.getMessage()));
    }

    @Test
    @DisplayName("팀이 회원에게 스카웃을 하면 201을 반환한다.")
    void givenValid_whenTeamOffer_thenReturn201() throws Exception {
        // given
        OfferCreateRequest request = createValidOfferCreateRequest();
        long userId = 1L;

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/team/user/{user-id}/offer", userId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.responseCode")
                        .value(OFFERED_BY_TEAM.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(OFFERED_BY_TEAM.getMessage()));
    }

    @Test
    @DisplayName("잘못된 포지션 포맷일시 팀이 회원에게 스카웃을 하면 400을 반환한다.")
    void givenFormatPosition_whenTeamOffer_thenReturn400() throws Exception {
        // given
        OfferCreateRequest request = createValidOfferCreateRequest();
        request.setOfferPosition("WRITER");
        long userId = 1L;

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/team/user/{user-id}/offer", userId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(OFFER_POSITION_TYPE_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(OFFER_POSITION_TYPE_INVALID.getMessage()));
    }

    @Test
    @DisplayName("회원 식별자가 양수가 아닐시 팀이 회원에게 스카웃을 하면 400을 반환한다.")
    void givenNonPositiveUserId_whenTeamOffer_thenReturn400() throws Exception {
        // given
        OfferCreateRequest request = createValidOfferCreateRequest();
        long userId = 0L;

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/team/user/{user-id}/offer", userId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(USER_ID_POSITIVE_ONLY.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(USER_ID_POSITIVE_ONLY.getMessage()));
    }

    @Test
    @DisplayName("회원이 받은 제안 페이징 조회를 하면 200을 반환한다.")
    void givenValid_whenFindPageUserReceivedOffer_thenReturn200() throws Exception {
        // given & when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/offer/received")
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(USER_RECEIVED_OFFER_FOUND.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(USER_RECEIVED_OFFER_FOUND.getMessage()));
    }

    @Test
    @DisplayName("페이지 시작점이 양수가 아닐시 회원이 받은 제안 페이징 조회를 하면 400을 반환한다.")
    void givenNonPositivePageFrom_whenFindPageUserReceivedOffer_thenReturn400() throws Exception {
        // given
        Long pageFrom = 0L;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/offer/received")
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
    @DisplayName("페이지 크기가 양수가 아닐시 회원이 받은 제안 페이징 조회를 하면 400을 반환한다.")
    void givenNonPositivePageSize_whenFindPageUserReceivedOffer_thenReturn400() throws Exception {
        // given
        Long pageSize = 0L;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/offer/received")
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
    @DisplayName("페이지 크기가 100을 초과할시 회원이 받은 제안 페이징 조회를 하면 400을 반환한다.")
    void givenGreaterThan100PageSize_whenFindPageUserReceivedOffer_thenReturn400() throws Exception {
        // given
        Long pageSize = 101L;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/offer/received")
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
    @DisplayName("팀이 받은 제안 페이징 조회를 하면 200을 반환한다.")
    void givenValid_whenFindPageTeamReceivedOffer_thenReturn200() throws Exception {
        // given
        Position position = Position.DESIGNER;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/team/offer/received")
                        .param("position", position.toString())
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(TEAM_RECEIVED_OFFER_FOUND.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(TEAM_RECEIVED_OFFER_FOUND.getMessage()));
    }

    @Test
    @DisplayName("페이지 시작점이 양수가 아닐시 팀이 받은 제안 페이징 조회를 하면 400을 반환한다.")
    void givenNonPositivePageFrom_whenFindPageTeamReceivedOffer_thenReturn400() throws Exception {
        // given
        Long pageFrom = 0L;
        Position position = Position.DESIGNER;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/team/offer/received")
                        .param("position", position.toString())
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
    @DisplayName("페이지 크기가 양수가 아닐시 팀이 받은 제안 페이징 조회를 하면 400을 반환한다.")
    void givenNonPositivePageSize_whenFindPageTeamReceivedOffer_thenReturn400() throws Exception {
        // given
        Long pageSize = 0L;
        Position position = Position.DESIGNER;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/team/offer/received")
                        .param("position", position.toString())
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
    @DisplayName("잘못된 포지션 타입일시 팀이 받은 제안 페이징 조회를 하면 400을 반환한다.")
    void givenFormatPosition_whenFindPageTeamReceivedOffer_thenReturn400() throws Exception {
        // given
        String position = "WRITER";

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/team/offer/received")
                        .param("position", position)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(OFFER_POSITION_TYPE_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(OFFER_POSITION_TYPE_INVALID.getMessage()));
    }

    @Test
    @DisplayName("페이지 크기가 100을 초과할시 팀이 받은 제안 페이징 조회를 하면 400을 반환한다.")
    void givenGreaterThan100PageSize_whenFindPageTeamReceivedOffer_thenReturn400() throws Exception {
        // given
        Long pageSize = 101L;
        Position position = Position.DESIGNER;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/team/offer/received")
                        .param("position", position.toString())
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
    @DisplayName("회원이 보낸 제안 페이징 조회를 하면 200을 반환한다.")
    void givenValid_whenFindPageUserSentOffer_thenReturn200() throws Exception {
        // given & when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/offer/sent")
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(USER_SENT_OFFER_FOUND.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(USER_SENT_OFFER_FOUND.getMessage()));
    }

    @Test
    @DisplayName("페이지 시작점이 양수가 아닐시 회원이 보낸 제안 페이징 조회를 하면 400을 반환한다.")
    void givenNonPositivePageFrom_whenFindPageUserSentOffer_thenReturn400() throws Exception {
        // given
        Long pageFrom = 0L;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/offer/sent")
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
    @DisplayName("페이지 크기가 양수가 아닐시 회원이 보낸 제안 페이징 조회를 하면 400을 반환한다.")
    void givenNonPositivePageSize_whenFindPageUserSentOffer_thenReturn400() throws Exception {
        // given
        Long pageSize = 0L;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/offer/sent")
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
    @DisplayName("페이지 크기가 100을 초과할시 회원이 보낸 제안 페이징 조회를 하면 400을 반환한다.")
    void givenGreaterThan100PageSize_whenFindPageUserSentOffer_thenReturn400() throws Exception {
        // given
        Long pageSize = 101L;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/offer/sent")
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
    @DisplayName("팀이 보낸 제안 페이징 조회를 하면 200을 반환한다.")
    void givenValid_whenFindPageTeamSentOffer_thenReturn200() throws Exception {
        // given
        Position position = Position.DESIGNER;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/team/offer/sent")
                        .param("position", position.toString())
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(TEAM_SENT_OFFER_FOUND.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(TEAM_SENT_OFFER_FOUND.getMessage()));
    }

    @Test
    @DisplayName("페이지 시작점이 양수가 아닐시 팀이 보낸 제안 페이징 조회를 하면 400을 반환한다.")
    void givenNonPositivePageFrom_whenFindPageTeamSentOffer_thenReturn400() throws Exception {
        // given
        Long pageFrom = 0L;
        Position position = Position.DESIGNER;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/team/offer/sent")
                        .param("page-from", pageFrom.toString())
                        .param("position", position.toString())
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
    @DisplayName("페이지 크기가 양수가 아닐시 팀이 보낸 제안 페이징 조회를 하면 400을 반환한다.")
    void givenNonPositivePageSize_whenFindPageTeamSentOffer_thenReturn400() throws Exception {
        // given
        Long pageSize = 0L;
        Position position = Position.DESIGNER;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/team/offer/sent")
                        .param("page-size", pageSize.toString())
                        .param("position", position.toString())
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
    @DisplayName("잘못된 포지션 타입시 팀이 보낸 제안 페이징 조회를 하면 400을 반환한다.")
    void givenFormatPosition_whenFindPageTeamSentOffer_thenReturn400() throws Exception {
        // given
        String position = "WRITER";

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/team/offer/sent")
                        .param("position", position)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(OFFER_POSITION_TYPE_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(OFFER_POSITION_TYPE_INVALID.getMessage()));
    }

    @Test
    @DisplayName("페이지 크기가 100을 초과할시 팀이 보낸 제안 페이징 조회를 하면 400을 반환한다.")
    void givenGreaterThan100PageSize_whenFindPageTeamSentOffer_thenReturn400() throws Exception {
        // given
        Position position = Position.DESIGNER;
        Long pageSize = 101L;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/team/offer/sent")
                        .param("page-size", pageSize.toString())
                        .param("position", position.toString())
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
    @DisplayName("회원이 받은 제안 결정을 하면 200을 반환한다.")
    void givenValid_whenUserDecideOffer_thenReturn200() throws Exception {
        // given
        long offerId = 1L;
        OfferDecideRequest request = createValidOfferDecideRequest();

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/user/offer/{offer-id}", offerId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(USER_DECIDED_OFFER.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(USER_DECIDED_OFFER.getMessage()));
    }

    @Test
    @DisplayName("수락 여부 미입력시 회원이 받은 제안 결정을 하면 400을 반환한다.")
    void givenBlankIsAccepted_whenUserDecideOffer_thenReturn400() throws Exception {
        // given
        long offerId = 1L;
        OfferDecideRequest request = createValidOfferDecideRequest();
        request.setIsAccepted(null);

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/user/offer/{offer-id}", offerId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(IS_ACCEPTED_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(IS_ACCEPTED_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("제안 식별자가 양삭 아닐시 회원이 받은 제안 결정을 하면 400을 반환한다.")
    void givenNonPositiveOfferId_whenUserDecideOffer_thenReturn400() throws Exception {
        // given
        long offerId = 0L;
        OfferDecideRequest request = createValidOfferDecideRequest();

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/user/offer/{offer-id}", offerId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(OFFER_ID_POSITIVE_ONLY.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(OFFER_ID_POSITIVE_ONLY.getMessage()));
    }

    @Test
    @DisplayName("팀이 받은 제안 결정을 하면 200을 반환한다.")
    void givenValid_whenTeamDecideOffer_thenReturn200() throws Exception {
        // given
        long offerId = 1L;
        OfferDecideRequest request = createValidOfferDecideRequest();

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/team/offer/{offer-id}", offerId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(TEAM_DECIDED_OFFER.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(TEAM_DECIDED_OFFER.getMessage()));
    }

    @Test
    @DisplayName("수락 여부 미입력시 팀이 받은 제안 결정을 하면 400을 반환한다.")
    void givenBlankIsAccepted_whenTeamDecideOffer_thenReturn400() throws Exception {
        // given
        long offerId = 1L;
        OfferDecideRequest request = createValidOfferDecideRequest();
        request.setIsAccepted(null);

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/team/offer/{offer-id}", offerId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(IS_ACCEPTED_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(IS_ACCEPTED_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("제안 식별자가 양수가 아닐시 팀이 받은 제안 결정을 하면 400을 반환한다.")
    void givenNonPositiveOfferId_whenTeamDecideOffer_thenReturn400() throws Exception {
        // given
        long offerId = 0L;
        OfferDecideRequest request = createValidOfferDecideRequest();

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/team/offer/{offer-id}", offerId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(OFFER_ID_POSITIVE_ONLY.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(OFFER_ID_POSITIVE_ONLY.getMessage()));
    }

    @Test
    @DisplayName("회원이 보낸 제안를 취소하면 200을 반환한다.")
    void givenValid_whenCancelUserOffer_thenReturn200() throws Exception {
        // given
        long offerId = 1L;

        // when
        ResultActions actions = mockMvc.perform(
                delete("/api/v1/user/offer/{offer-id}", offerId)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(OFFER_CANCEL_BY_USER.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(OFFER_CANCEL_BY_USER.getMessage()));
    }

    @Test
    @DisplayName("제안 식별자가 양수가 아닐시 회원이 보낸 제안를 취소하면 400을 반환한다.")
    void givenNonPositiveOfferId_whenCancelUserOffer_thenReturn400() throws Exception {
        // given
        long offerId = 0L;

        // when
        ResultActions actions = mockMvc.perform(
                delete("/api/v1/user/offer/{offer-id}", offerId)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(OFFER_ID_POSITIVE_ONLY.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(OFFER_ID_POSITIVE_ONLY.getMessage()));
    }

    @Test
    @DisplayName("팀이 보낸 제안을 취소하면 200을 반환한다.")
    void givenValid_whenCancelTeamOffer_thenReturn200() throws Exception {
        // given
        long offerId = 1L;

        // when
        ResultActions actions = mockMvc.perform(
                delete("/api/v1/team/offer/{offer-id}", offerId)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(OFFER_CANCEL_BY_TEAM.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(OFFER_CANCEL_BY_TEAM.getMessage()));
    }

    @Test
    @DisplayName("제안 식별자가 양수가 아닐시 팀이 보낸 제안을 취소하면 400을 반환한다.")
    void givenNonPositiveOfferId_whenCancelTeamOffer_thenReturn400() throws Exception {
        // given
        long offerId = 0L;

        // when
        ResultActions actions = mockMvc.perform(
                delete("/api/v1/team/offer/{offer-id}", offerId)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(OFFER_ID_POSITIVE_ONLY.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(OFFER_ID_POSITIVE_ONLY.getMessage()));
    }

    private OfferDecideRequest createValidOfferDecideRequest() {
        return OfferDecideRequest.builder()
                .isAccepted(true)
                .build();
    }

    private OfferCreateRequest createValidOfferCreateRequest() {
        return OfferCreateRequest.builder()
                .position(Position.BACKEND.name())
                .build();
    }
}