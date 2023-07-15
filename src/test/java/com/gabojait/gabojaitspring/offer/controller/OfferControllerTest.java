package com.gabojait.gabojaitspring.offer.controller;

import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.WebMvc;
import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.offer.domain.type.OfferedBy;
import com.gabojait.gabojaitspring.offer.dto.req.OfferCreateReqDto;
import com.gabojait.gabojaitspring.offer.dto.req.OfferUpdateReqDto;
import com.gabojait.gabojaitspring.offer.service.OfferService;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.domain.type.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;
import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(OfferController.class)
@AutoConfigureMockMvc(addFilters = false)
class OfferControllerTest extends WebMvc {

    @MockBean
    private OfferService offerService;

    @MockBean
    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        doReturn(1L)
                .when(this.jwtProvider)
                .getId(any());

        User tester = User.testOnlyBuilder()
                .id(1L)
                .role(Role.USER)
                .build();

        Team team = Team.builder()
                .projectName("가보자잇")
                .projectDescription("가보자잇 프로젝트 설명입니다.")
                .designerTotalRecruitCnt((byte) 2)
                .backendTotalRecruitCnt((byte) 2)
                .frontendTotalRecruitCnt((byte) 2)
                .managerTotalRecruitCnt((byte) 2)
                .expectation("열정적인 팀원을 원합니다.")
                .openChatUrl("https://open.kakao.com/o")
                .build();

        Offer offer = Offer.builder()
                .user(tester)
                .team(team)
                .offeredBy(OfferedBy.TEAM)
                .position(Position.FRONTEND)
                .build();

        Page<Offer> offers = new PageImpl<>(List.of(offer));

        doReturn(offers)
                .when(this.offerService)
                .findManyOffersByUser(anyLong(), any(), any());

        doReturn(offers)
                .when(this.offerService)
                .findManyOffersByTeam(anyLong(), any(), any());
    }

    @Test
    @DisplayName("회원이 팀에 지원 | 올바른 요청시 | 201반환")
    void userOffer_givenValidReq_return201() throws Exception {
        // given
        OfferCreateReqDto reqDto = getValidOfferCreateReqDto();
        String request = mapToJson(reqDto);
        Long teamId = getValidId();

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/team/{team-id}/offer", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(OFFERED_BY_USER.getHttpStatus().value());
        assertThat(response).contains(OFFERED_BY_USER.name());
    }

    @Test
    @DisplayName("회원이 팀에 지원 | 팀 식별자 미입력시 | 400반환")
    void userOffer_givenTeamIdFieldRequired_return400() throws Exception {
        // given TODO 식별자 미입력
        OfferCreateReqDto reqDto = getValidOfferCreateReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/team/{team-id}/offer", "")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

//        assertThat(status).isEqualTo(TEAM_ID_FIELD_REQUIRED.getHttpStatus().value());
//        assertThat(response).contains(TEAM_ID_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("회원이 팀에 지원 | 팀 식별자가 양수 아닐시 | 400반환")
    void userOffer_givenTeamIdPositiveOnly_return400() throws Exception {
        // given
        OfferCreateReqDto reqDto = getValidOfferCreateReqDto();
        String request = mapToJson(reqDto);
        Long teamId = 0L;

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/team/{team-id}/offer", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(TEAM_ID_POSITIVE_ONLY.getHttpStatus().value());
        assertThat(response).contains(TEAM_ID_POSITIVE_ONLY.name());
    }

    @Test
    @DisplayName("회원이 팀에 지원 | 포지션 미입력시 | 400반환")
    void userOffer_givenPositiveFieldRequired_return400() throws Exception {
        // given
        OfferCreateReqDto reqDto = getValidOfferCreateReqDto();
        reqDto.setPosition("");
        String request = mapToJson(reqDto);
        Long teamId = getValidId();

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/team/{team-id}/offer", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(POSITION_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(POSITION_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("회원이 팀에 지원 | 잘못된 포지션 타입시 | 400반환")
    void userOffer_givenPositiveTypeInvalid_return400() throws Exception {
        // given
        OfferCreateReqDto reqDto = getValidOfferCreateReqDto();
        reqDto.setPosition("marketer");
        String request = mapToJson(reqDto);
        Long teamId = getValidId();

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/team/{team-id}/offer", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(POSITION_TYPE_INVALID.getHttpStatus().value());
        assertThat(response).contains(POSITION_TYPE_INVALID.name());
    }

    @Test
    @DisplayName("팀이 회원에게 스카웃 | 올바른 요청시 | 201반환")
    void teamOffer_givenValidReq_return201() throws Exception {
        // given
        OfferCreateReqDto reqDto = getValidOfferCreateReqDto();
        String request = mapToJson(reqDto);
        Long userId = getValidId();

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/team/user/{user-id}/offer", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(OFFERED_BY_TEAM.getHttpStatus().value());
        assertThat(response).contains(OFFERED_BY_TEAM.name());
    }

    @Test
    @DisplayName("팀이 회원에게 스카웃 | 회원 식별자 입력시 | 400반환")
    void teamOffer_givenUserIdFieldRequired_return400() throws Exception {
        // given TODO 식별자 미입력
        OfferCreateReqDto reqDto = getValidOfferCreateReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/team/user/{user-id}/offer", "")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

//        assertThat(status).isEqualTo(USER_ID_FIELD_REQUIRED.getHttpStatus().value());
//        assertThat(response).contains(USER_ID_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("팀이 회원에게 스카웃 | 회원 식별자가 양수 아닐시 | 400반환")
    void teamOffer_givenUserIdPositiveOnly_return400() throws Exception {
        // given
        OfferCreateReqDto reqDto = getValidOfferCreateReqDto();
        String request = mapToJson(reqDto);
        Long userId = 0L;

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/team/user/{user-id}/offer", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USER_ID_POSITIVE_ONLY.getHttpStatus().value());
        assertThat(response).contains(USER_ID_POSITIVE_ONLY.name());
    }

    @Test
    @DisplayName("팀이 회원에게 스카웃 | 포지션 미입력시 | 400반환")
    void teamOffer_givenPositionFieldRequired_return400() throws Exception {
        // given
        OfferCreateReqDto reqDto = getValidOfferCreateReqDto();
        reqDto.setPosition("");
        String request = mapToJson(reqDto);
        Long userId = getValidId();

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/team/user/{user-id}/offer", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(POSITION_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(POSITION_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("팀이 회원에게 스카웃 | 잘못된 포지션 타입시 | 400반환")
    void teamOffer_givenPositionTypeInvalid_return400() throws Exception {
        // given
        OfferCreateReqDto reqDto = getValidOfferCreateReqDto();
        reqDto.setPosition("marketer");
        String request = mapToJson(reqDto);
        Long userId = getValidId();

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/team/user/{user-id}/offer", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(POSITION_TYPE_INVALID.getHttpStatus().value());
        assertThat(response).contains(POSITION_TYPE_INVALID.name());
    }

    @Test
    @DisplayName("회원이 받은 제안 다건 조희 | 올바른 요청시 | 200반환")
    void userFindOffers_givenValidReq_return200() throws Exception {
        // given
        Integer pageFrom = getValidPageFrom();
        Integer pageSize = getValidPageSize();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/offer")
                        .param("page-from", pageFrom.toString())
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(OFFER_BY_TEAM_FOUND.getHttpStatus().value());
        assertThat(response).contains(OFFER_BY_TEAM_FOUND.name());
    }

    @Test
    @DisplayName("회원이 받은 제안 다건 조희 | 페이지 시작점 미입력시 | 400반환")
    void userFindOffers_givenPageFromFieldRequired_return400() throws Exception {
        // given
        Integer pageSize = getValidPageSize();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/offer")
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PAGE_FROM_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(PAGE_FROM_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("회원이 받은 제안 다건 조희 | 페이지 시작점이 양수 또는 0 아닐시 | 400반환")
    void userFindOffers_givenPageFromPositiveOrZerOnly_return400() throws Exception {
        // given
        Integer pageFrom = -1;
        Integer pageSize = getValidPageSize();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/offer")
                        .param("page-from", pageFrom.toString())
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PAGE_FROM_POSITIVE_OR_ZERO_ONLY.getHttpStatus().value());
        assertThat(response).contains(PAGE_FROM_POSITIVE_OR_ZERO_ONLY.name());
    }

    @Test
    @DisplayName("회원이 받은 제안 다건 조희 | 페이지 사이즈가 양수 아닐시 | 400반환")
    void userFindOffers_givenPageSizePositiveOnly_return400() throws Exception {
        // given
        Integer pageFrom = getValidPageFrom();
        Integer pageSize = 0;

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/offer")
                        .param("page-from", pageFrom.toString())
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PAGE_SIZE_POSITIVE_ONLY.getHttpStatus().value());
        assertThat(response).contains(PAGE_SIZE_POSITIVE_ONLY.name());
    }

    @Test
    @DisplayName("팀이 받은 제안 다건 조회 | 올바른 요청시 | 200반환")
    void teamFindOffers_givenValidReq_return200() throws Exception {
        // given
        Integer pageFrom = getValidPageFrom();
        Integer pageSize = getValidPageSize();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/team/offer")
                        .param("page-from", pageFrom.toString())
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(OFFER_BY_USER_FOUND.getHttpStatus().value());
        assertThat(response).contains(OFFER_BY_USER_FOUND.name());
    }

    @Test
    @DisplayName("팀이 받은 제안 다건 조회 | 올바른 페이지 시작점 미입력시 | 400반환")
    void teamFindOffers_givenPageFromFieldRequired_return400() throws Exception {
        // given
        Integer pageSize = getValidPageSize();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/team/offer")
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PAGE_FROM_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(PAGE_FROM_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("팀이 받은 제안 다건 조회 | 올바른 페이지 시작점이 양수 또는 0 아닐시 | 400반환")
    void teamFindOffers_givenPageFromPositiveOrZeroOnly_return400() throws Exception {
        // given
        Integer pageFrom = -1;
        Integer pageSize = getValidPageSize();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/team/offer")
                        .param("page-from", pageFrom.toString())
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PAGE_FROM_POSITIVE_OR_ZERO_ONLY.getHttpStatus().value());
        assertThat(response).contains(PAGE_FROM_POSITIVE_OR_ZERO_ONLY.name());
    }

    @Test
    @DisplayName("팀이 받은 제안 다건 조회 | 올바른 페이지 사이즈가 양수 아닐시 | 400반환")
    void teamFindOffers_givenPageSizePositiveOnly_return400() throws Exception {
        // given
        Integer pageFrom = getValidPageFrom();
        Integer pageSize = 0;

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/team/offer")
                        .param("page-from", pageFrom.toString())
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PAGE_SIZE_POSITIVE_ONLY.getHttpStatus().value());
        assertThat(response).contains(PAGE_SIZE_POSITIVE_ONLY.name());
    }

    @Test
    @DisplayName("회원이 받은 제안 결정 | 올바른 요청시 | 200반환")
    void decideOfferByUser_givenValidReq_return200() throws Exception {
        // given
        OfferUpdateReqDto reqDto = getValidOfferUpdateReqDto();
        String request = mapToJson(reqDto);
        Long offerId = getValidId();

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/offer/{offer-id}", offerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USER_DECIDED_OFFER.getHttpStatus().value());
        assertThat(response).contains(USER_DECIDED_OFFER.name());
    }

    @Test
    @DisplayName("회원이 받은 제안 결정 | 제안 식별자 미입력시 | 400반환")
    void decideOfferByUser_givenOfferIdFieldRequired_return400() throws Exception {
        // given TODO 식별자 미입력
        OfferUpdateReqDto reqDto = getValidOfferUpdateReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/offer/{offer-id}", "")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

//        assertThat(status).isEqualTo(OFFER_ID_FIELD_REQUIRED.getHttpStatus().value());
//        assertThat(response).contains(OFFER_ID_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("회원이 받은 제안 결정 | 제안 식별자가 양수 아닐시 | 400반환")
    void decideOfferByUser_givenOfferIdPositiveOnly_return400() throws Exception {
        // given
        OfferUpdateReqDto reqDto = getValidOfferUpdateReqDto();
        String request = mapToJson(reqDto);
        Long offerId = 0L;

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/offer/{offer-id}", offerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(OFFER_ID_POSITIVE_ONLY.getHttpStatus().value());
        assertThat(response).contains(OFFER_ID_POSITIVE_ONLY.name());
    }

    @Test
    @DisplayName("회원이 받은 제안 결정 | 수락 여부 미입력시 | 400반환")
    void decideOfferByUser_givenIsAcceptedFieldRequired_return400() throws Exception {
        // given
        OfferUpdateReqDto reqDto = getValidOfferUpdateReqDto();
        reqDto.setIsAccepted(null);
        String request = mapToJson(reqDto);
        Long offerId = getValidId();

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/offer/{offer-id}", offerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(IS_ACCEPTED_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(IS_ACCEPTED_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("팀이 받은 제안 결정 | 올바른 요청시 | 200반환")
    void decideOfferByTeam_givenValidReq_return200() throws Exception {
        // given
        OfferUpdateReqDto reqDto = getValidOfferUpdateReqDto();
        String request = mapToJson(reqDto);
        Long offerId = getValidId();

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/team/offer/{offer-id}", offerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(TEAM_DECIDED_OFFER.getHttpStatus().value());
        assertThat(response).contains(TEAM_DECIDED_OFFER.name());
    }

    @Test
    @DisplayName("팀이 받은 제안 결정 | 제안 식별자 미입력시 | 400반환")
    void decideOfferByTeam_givenOfferIdFieldRequired_return400() throws Exception {
        // given TODO 식별자 미입력
        OfferUpdateReqDto reqDto = getValidOfferUpdateReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/team/offer/{offer-id}", "")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

//        assertThat(status).isEqualTo(OFFER_ID_FIELD_REQUIRED.getHttpStatus().value());
//        assertThat(response).contains(OFFER_ID_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("팀이 받은 제안 결정 | 제안 식별자가 양수 아닐시 | 400반환")
    void decideOfferByTeam_givenOfferIdPositiveOnly_return400() throws Exception {
        // given
        OfferUpdateReqDto reqDto = getValidOfferUpdateReqDto();
        String request = mapToJson(reqDto);
        Long offerId = 0L;

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/team/offer/{offer-id}", offerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(OFFER_ID_POSITIVE_ONLY.getHttpStatus().value());
        assertThat(response).contains(OFFER_ID_POSITIVE_ONLY.name());
    }

    @Test
    @DisplayName("팀이 받은 제안 결정 | 수락 여부 미입력시 | 400반환")
    void decideOfferByTeam_givenIsAcceptedFieldRequired_return400() throws Exception {
        // given
        OfferUpdateReqDto reqDto = getValidOfferUpdateReqDto();
        reqDto.setIsAccepted(null);
        String request = mapToJson(reqDto);
        Long offerId = getValidId();

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/team/offer/{offer-id}", offerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(IS_ACCEPTED_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(IS_ACCEPTED_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("회원이 보낸 제안 취소 | 올바른 요청시 | 200반환")
    void cancelOfferByUser_givenValidReq_return200() throws Exception {
        // given
        Long offerId = getValidId();

        // when
        MvcResult mvcResult = this.mockMvc.perform(delete("/api/v1/user/offer/{offer-id}", offerId))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(OFFER_CANCEL_BY_USER.getHttpStatus().value());
        assertThat(response).contains(OFFER_CANCEL_BY_USER.name());
    }

    @Test
    @DisplayName("회원이 보낸 제안 취소 | 제안 식별자 미입력시 | 400반환")
    void cancelOfferByUser_givenOfferIdFieldRequired_return400() throws Exception {
        // given & when TODO 식별자 미입력
        MvcResult mvcResult = this.mockMvc.perform(delete("/api/v1/user/offer/{offer-id}", ""))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

//        assertThat(status).isEqualTo(OFFER_ID_FIELD_REQUIRED.getHttpStatus().value());
//        assertThat(response).contains(OFFER_ID_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("회원이 보낸 제안 취소 | 제안 식별자가 양수 아닐시 | 400반환")
    void cancelOfferByUser_givenOfferIdPositiveOnly_return400() throws Exception {
        // given
        Long offerId = 0L;

        // when
        MvcResult mvcResult = this.mockMvc.perform(delete("/api/v1/user/offer/{offer-id}", offerId))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(OFFER_ID_POSITIVE_ONLY.getHttpStatus().value());
        assertThat(response).contains(OFFER_ID_POSITIVE_ONLY.name());
    }

    @Test
    @DisplayName("팀이 보낸 제안 취소 | 올바른 요청시 | 200반환")
    void cancelOfferByTeam_givenValidReq_return200() throws Exception {
        // given
        Long offerId = getValidId();

        // when
        MvcResult mvcResult = this.mockMvc.perform(delete("/api/v1/team/offer/{offer-id}", offerId))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(OFFER_CANCEL_BY_TEAM.getHttpStatus().value());
        assertThat(response).contains(OFFER_CANCEL_BY_TEAM.name());
    }

    @Test
    @DisplayName("팀이 보낸 제안 취소 | 제안 식별자 미입력시 | 400반환")
    void cancelOfferByTeam_givenOfferIdFieldRequired_return400() throws Exception {
        // given & when
        MvcResult mvcResult = this.mockMvc.perform(delete("/api/v1/team/offer/{offer-id}", ""))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

//        assertThat(status).isEqualTo(OFFER_ID_FIELD_REQUIRED.getHttpStatus().value());
//        assertThat(response).contains(OFFER_ID_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("팀이 보낸 제안 취소 | 제안 식별자가 양수 아닐시 | 400반환")
    void cancelOfferByTeam_givenOfferIdPositiveOnly_return400() throws Exception {
        // given
        Long offerId = 0L;

        // when
        MvcResult mvcResult = this.mockMvc.perform(delete("/api/v1/team/offer/{offer-id}", offerId))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(OFFER_ID_POSITIVE_ONLY.getHttpStatus().value());
        assertThat(response).contains(OFFER_ID_POSITIVE_ONLY.name());
    }

    private OfferCreateReqDto getValidOfferCreateReqDto() {
        OfferCreateReqDto reqDto = new OfferCreateReqDto();
        reqDto.setPosition(Position.DESIGNER.name().toLowerCase());
        return reqDto;
    }

    private OfferUpdateReqDto getValidOfferUpdateReqDto() {
        OfferUpdateReqDto reqDto = new OfferUpdateReqDto();
        reqDto.setIsAccepted(true);
        return reqDto;
    }

    private Long getValidId() {
        return 1L;
    }

    private Integer getValidPageFrom() {
        return 0;
    }

    private Integer getValidPageSize() {
        return 20;
    }
}