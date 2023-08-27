package com.gabojait.gabojaitspring.team.controller;

import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.WebMvc;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.team.dto.req.TeamCompleteReqDto;
import com.gabojait.gabojaitspring.team.dto.req.TeamDefaultReqDto;
import com.gabojait.gabojaitspring.team.dto.req.TeamIsRecruitingUpdateReqDto;
import com.gabojait.gabojaitspring.team.dto.req.TeamMemberRecruitCntReqDto;
import com.gabojait.gabojaitspring.team.service.TeamService;
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

import java.util.ArrayList;
import java.util.List;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;
import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(TeamController.class)
@AutoConfigureMockMvc(addFilters = false)
class TeamControllerTest extends WebMvc {

    @MockBean
    private TeamService teamService;

    @MockBean
    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        doReturn(1L)
                .when(this.jwtProvider)
                .getId(any());

        Team team = Team.builder()
                .projectName("가보자잇")
                .projectDescription("가보자잇 프로젝트 설명입니다.")
                .designerCnt((byte) 2)
                .backendCnt((byte) 2)
                .frontendCnt((byte) 2)
                .managerCnt((byte) 2)
                .expectation("열정적인 팀원을 원합니다.")
                .openChatUrl("https://open.kakao.com/o")
                .build();

        Page<Team> teams = new PageImpl<>(List.of(team));

        doReturn(team)
                .when(this.teamService)
                .create(anyLong(), any());

        doReturn(team)
                .when(this.teamService)
                .update(anyLong(), any());

        doReturn(team)
                .when(this.teamService)
                .findOneCurrentTeam(anyLong());

        doReturn(teams)
                .when(this.teamService)
                .findManyTeamByPositionOrder(any(), anyLong(), any());
    }

    @Test
    @DisplayName("팀 생성 | 올바른 요청시 | 201반환")
    void createTeam_givenValidReq_return201() throws Exception {
        // given
        TeamDefaultReqDto reqDto = getValidTeamDefaultReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(TEAM_CREATED.getHttpStatus().value());
        assertThat(response).contains(TEAM_CREATED.name());
    }

    @Test
    @DisplayName("팀 생성 | 프로젝트명 미입력시 | 400반환")
    void createTeam_givenProjectNameFieldRequired_return400() throws Exception {
        // given
        TeamDefaultReqDto reqDto = getValidTeamDefaultReqDto();
        reqDto.setProjectName("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PROJECT_NAME_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(PROJECT_NAME_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("팀 생성 | 잘못된 프로젝트명 길이시 | 400반환")
    void createTeam_givenProjectNameLengthInvalid_return400() throws Exception {
        // given
        TeamDefaultReqDto reqDto = getValidTeamDefaultReqDto();
        reqDto.setProjectName("가".repeat(21));
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PROJECT_NAME_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(PROJECT_NAME_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("팀 생성 | 프로젝트 설명 미입력시 | 400반환")
    void createTeam_givenProjectDescriptionFieldRequired_return400() throws Exception {
        // given
        TeamDefaultReqDto reqDto = getValidTeamDefaultReqDto();
        reqDto.setProjectDescription("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PROJECT_DESCRIPTION_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(PROJECT_DESCRIPTION_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("팀 생성 | 잘못된 프로젝트 설명 길이시 | 400반환")
    void createTeam_givenProjectDescriptionLengthInvalid_return400() throws Exception {
        // given
        TeamDefaultReqDto reqDto = getValidTeamDefaultReqDto();
        reqDto.setProjectDescription("가".repeat(501));
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PROJECT_DESCRIPTION_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(PROJECT_DESCRIPTION_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("팀 생성 | 총 팀원 수 미입력시 | 400반환")
    void createTeam_givenTotalRecruitCntFieldRequired_return400() throws Exception {
        // given
        TeamDefaultReqDto reqDto = getValidTeamDefaultReqDto();
        reqDto.getTeamMemberRecruitCnts().get(0).setTotalRecruitCnt(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(TOTAL_RECRUIT_CNT_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(TOTAL_RECRUIT_CNT_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("팀 생성 | 총 팀원 수가 양수 또는 0 아닐시 | 400반환")
    void createTeam_givenTotalRecruitCntPositiveOnly_return400() throws Exception {
        // given
        TeamDefaultReqDto reqDto = getValidTeamDefaultReqDto();
        reqDto.getTeamMemberRecruitCnts().get(0).setTotalRecruitCnt((byte) -1);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(TOTAL_RECRUIT_CNT_POSITIVE_OR_ZERO_ONLY.getHttpStatus().value());
        assertThat(response).contains(TOTAL_RECRUIT_CNT_POSITIVE_OR_ZERO_ONLY.name());
    }

    @Test
    @DisplayName("팀 생성 | 포지션 미입력시 | 400반환")
    void createTeam_givenPositionFieldRequired_return400() throws Exception {
        // given
        TeamDefaultReqDto reqDto = getValidTeamDefaultReqDto();
        reqDto.getTeamMemberRecruitCnts().get(0).setPosition("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/team")
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
    @DisplayName("팀 생성 | 잘못된 포지션 타입시 | 400반환")
    void createTeam_givenPositionTypeInvalid_return400() throws Exception {
        // given
        TeamDefaultReqDto reqDto = getValidTeamDefaultReqDto();
        reqDto.getTeamMemberRecruitCnts().get(0).setPosition("marketer");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/team")
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
    @DisplayName("팀 생성 | 바라는 점 미입력시 | 400반환")
    void createTeam_givenExpectationFieldRequired_return400() throws Exception {
        // given
        TeamDefaultReqDto reqDto = getValidTeamDefaultReqDto();
        reqDto.setExpectation("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EXPECTATION_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(EXPECTATION_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("팀 생성 | 잘못된 바라는 점 길이시 | 400반환")
    void createTeam_givenExpectationLengthInvalid_return400() throws Exception {
        // given
        TeamDefaultReqDto reqDto = getValidTeamDefaultReqDto();
        reqDto.setExpectation("가".repeat(201));
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EXPECTATION_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(EXPECTATION_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("팀 생성 | 오픈 채팅 링크 미입력시 | 400반환")
    void createTeam_givenOpenCharUrlFieldRequired_return400() throws Exception {
        // given
        TeamDefaultReqDto reqDto = getValidTeamDefaultReqDto();
        reqDto.setOpenChatUrl("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(OPEN_CHAT_URL_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(OPEN_CHAT_URL_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("팀 생성 | 잘못된 오픈 채팅 링크 길이시 | 400반환")
    void createTeam_givenOpenChatUrlLengthInvalid_return400() throws Exception {
        // given
        TeamDefaultReqDto reqDto = getValidTeamDefaultReqDto();
        reqDto.setOpenChatUrl("a".repeat(24));
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(OPEN_CHAT_URL_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(OPEN_CHAT_URL_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("팀 생성 | 잘못된 오픈 채팅 포맷시 | 400반환")
    void createTeam_givenOpenChatUrlFormatInvalid_return400() throws Exception {
        // given
        TeamDefaultReqDto reqDto = getValidTeamDefaultReqDto();
        reqDto.setOpenChatUrl("a".repeat(25));
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(OPEN_CHAT_URL_FORMAT_INVALID.getHttpStatus().value());
        assertThat(response).contains(OPEN_CHAT_URL_FORMAT_INVALID.name());
    }

    @Test
    @DisplayName("팀 정보 수정 | 올바른 요청시 | 200반환")
    void updateTeam_givenValidReq_return200() throws Exception {
        // given
        TeamDefaultReqDto reqDto = getValidTeamDefaultReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(put("/api/v1/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(TEAM_UPDATED.getHttpStatus().value());
        assertThat(response).contains(TEAM_UPDATED.name());
    }

    @Test
    @DisplayName("팀 정보 수정 | 프로젝트명 미입력시 | 400반환")
    void updateTeam_givenProjectNameFieldRequired_return400() throws Exception {
        // given
        TeamDefaultReqDto reqDto = getValidTeamDefaultReqDto();
        reqDto.setProjectName("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(put("/api/v1/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PROJECT_NAME_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(PROJECT_NAME_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("팀 정보 수정 | 잘못된 프로젝트명 길이시 | 400반환")
    void updateTeam_givenProjectNameLengthInvalid_return400() throws Exception {
        // given
        TeamDefaultReqDto reqDto = getValidTeamDefaultReqDto();
        reqDto.setProjectName("가".repeat(21));
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(put("/api/v1/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PROJECT_NAME_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(PROJECT_NAME_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("팀 정보 수정 | 프로젝트 설명 미입력시 | 400반환")
    void updateTeam_givenProjectDescriptionFieldRequired_return400() throws Exception {
        // given
        TeamDefaultReqDto reqDto = getValidTeamDefaultReqDto();
        reqDto.setProjectDescription("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(put("/api/v1/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PROJECT_DESCRIPTION_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(PROJECT_DESCRIPTION_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("팀 정보 수정 | 잘못된 프로젝트 설명 길이시 | 400반환")
    void updateTeam_givenProjectDescriptionLengthInvalid_return400() throws Exception {
        // given
        TeamDefaultReqDto reqDto = getValidTeamDefaultReqDto();
        reqDto.setProjectDescription("가".repeat(501));
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(put("/api/v1/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PROJECT_DESCRIPTION_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(PROJECT_DESCRIPTION_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("팀 정보 수정 | 총 팀원 수 미입력시 | 400반환")
    void updateTeam_givenTotalRecruitCntFieldRequired_return400() throws Exception {
        // given
        TeamDefaultReqDto reqDto = getValidTeamDefaultReqDto();
        reqDto.getTeamMemberRecruitCnts().get(0).setTotalRecruitCnt(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(put("/api/v1/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(TOTAL_RECRUIT_CNT_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(TOTAL_RECRUIT_CNT_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("팀 정보 수정 | 총 팀원 수가 양수 또는 0 아닐시 | 400반환")
    void updateTeam_givenTotalRecruitCntPositiveOnly_return400() throws Exception {
        // given
        TeamDefaultReqDto reqDto = getValidTeamDefaultReqDto();
        reqDto.getTeamMemberRecruitCnts().get(0).setTotalRecruitCnt((byte) -1);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(put("/api/v1/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(TOTAL_RECRUIT_CNT_POSITIVE_OR_ZERO_ONLY.getHttpStatus().value());
        assertThat(response).contains(TOTAL_RECRUIT_CNT_POSITIVE_OR_ZERO_ONLY.name());
    }

    @Test
    @DisplayName("팀 정보 수정 | 포지션 미입력시 | 400반환")
    void updateTeam_givenPositionFieldRequired_return400() throws Exception {
        // given
        TeamDefaultReqDto reqDto = getValidTeamDefaultReqDto();
        reqDto.getTeamMemberRecruitCnts().get(0).setPosition("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(put("/api/v1/team")
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
    @DisplayName("팀 정보 수정 | 잘못된 포지션 타입시 | 400반환")
    void updateTeam_givenPositionTypeInvalid_return400() throws Exception {
        // given
        TeamDefaultReqDto reqDto = getValidTeamDefaultReqDto();
        reqDto.getTeamMemberRecruitCnts().get(0).setPosition("marketer");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(put("/api/v1/team")
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
    @DisplayName("팀 정보 수정 | 바라는 점 미입력시 | 400반환")
    void updateTeam_givenExpectationFieldRequired_return400() throws Exception {
        // given
        TeamDefaultReqDto reqDto = getValidTeamDefaultReqDto();
        reqDto.setExpectation("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(put("/api/v1/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EXPECTATION_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(EXPECTATION_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("팀 정보 수정 | 잘못된 바라는 점 길이시 | 400반환")
    void updateTeam_givenExpectationLengthInvalid_return400() throws Exception {
        // given
        TeamDefaultReqDto reqDto = getValidTeamDefaultReqDto();
        reqDto.setExpectation("가".repeat(201));
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(put("/api/v1/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EXPECTATION_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(EXPECTATION_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("팀 정보 수정 | 오픈 채팅 링크 미입력시 | 400반환")
    void updateTeam_givenOpenCharUrlFieldRequired_return400() throws Exception {
        // given
        TeamDefaultReqDto reqDto = getValidTeamDefaultReqDto();
        reqDto.setOpenChatUrl("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(put("/api/v1/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(OPEN_CHAT_URL_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(OPEN_CHAT_URL_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("팀 정보 수정 | 잘못된 오픈 채팅 링크 길이시 | 400반환")
    void updateTeam_givenOpenChatUrlLengthInvalid_return400() throws Exception {
        // given
        TeamDefaultReqDto reqDto = getValidTeamDefaultReqDto();
        reqDto.setOpenChatUrl("a".repeat(24));
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(put("/api/v1/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(OPEN_CHAT_URL_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(OPEN_CHAT_URL_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("팀 정보 수정 | 잘못된 오픈 채팅 포맷시 | 400반환")
    void updateTeam_givenOpenChatUrlFormatInvalid_return400() throws Exception {
        // given
        TeamDefaultReqDto reqDto = getValidTeamDefaultReqDto();
        reqDto.setOpenChatUrl("a".repeat(25));
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(put("/api/v1/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(OPEN_CHAT_URL_FORMAT_INVALID.getHttpStatus().value());
        assertThat(response).contains(OPEN_CHAT_URL_FORMAT_INVALID.name());
    }

    @Test
    @DisplayName("본인 현재 팀 조회 | 올바른 요청시 | 200반환")
    void findMyTeam_givenValidReq_return200() throws Exception {
        // given & when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/team"))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(SELF_TEAM_FOUND.getHttpStatus().value());
        assertThat(response).contains(SELF_TEAM_FOUND.name());
    }

    @Test
    @DisplayName("팀 단건 조회 | 올바른 요청시 | 200반환")
    void findOneTeam_givenValidReq_return200() throws Exception {
        // given & when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/team/{team-id}", 1L))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(TEAM_FOUND.getHttpStatus().value());
        assertThat(response).contains(TEAM_FOUND.name());
    }

    @Test
    @DisplayName("팀 단건 조회 | 팀 식별자가 양수 아닐시 | 400반환")
    void findOneTeam_givenTeamIdPositiveOnly_return400() throws Exception {
        // given & when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/team/{team-id}", 0L))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(TEAM_ID_POSITIVE_ONLY.getHttpStatus().value());
        assertThat(response).contains(TEAM_ID_POSITIVE_ONLY.name());
    }

    @Test
    @DisplayName("팀원을 찾는 팀 다건 조회 | 올바른 요청시 | 200반환")
    void findTeamsLookingForUsers_givenValidReq_return200() throws Exception {
        // given
        String position = getValidPosition();
        Integer pageFrom = getValidPageFrom();
        Integer pageSize = getValidPageSize();

        //when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/team/recruiting")
                        .param("position", position)
                        .param("page-from", pageFrom.toString())
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(TEAMS_RECRUITING_USERS_FOUND.getHttpStatus().value());
        assertThat(response).contains(TEAMS_RECRUITING_USERS_FOUND.name());
    }

    @Test
    @DisplayName("팀원을 찾는 팀 다건 조회 | 포지션 미입력시 | 400반환")
    void findTeamsLookingForUsers_givenPositionFieldRequired_return400() throws Exception {
        // given
        String position = "";
        Integer pageFrom = getValidPageFrom();
        Integer pageSize = getValidPageSize();

        //when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/team/recruiting")
                        .param("position", position)
                        .param("page-from", pageFrom.toString())
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(POSITION_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(POSITION_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("팀원을 찾는 팀 다건 조회 | 잘못된 포지션 타입시 | 400반환")
    void findTeamsLookingForUsers_givenPositionTypeInvalid_return400() throws Exception {
        // given
        String position = "marketer";
        Integer pageFrom = getValidPageFrom();
        Integer pageSize = getValidPageSize();

        //when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/team/recruiting")
                        .param("position", position)
                        .param("page-from", pageFrom.toString())
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(POSITION_TYPE_INVALID.getHttpStatus().value());
        assertThat(response).contains(POSITION_TYPE_INVALID.name());
    }

    @Test
    @DisplayName("팀원을 찾는 팀 다건 조회 | 페이지 시작점 미입력시 | 400반환")
    void findTeamsLookingForUsers_givenPageFromFieldRequired_return400() throws Exception {
        // given
        String position = getValidPosition();
        Integer pageSize = getValidPageSize();

        //when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/team/recruiting")
                        .param("position", position)
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PAGE_FROM_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(PAGE_FROM_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("팀원을 찾는 팀 다건 조회 | 페이지 시작점이 양수 또는 0 아닐시 | 400반환")
    void findTeamsLookingForUsers_givenPageFromPositiveOrZeroOnly_return400() throws Exception {
        // given
        String position = getValidPosition();
        Integer pageFrom = -1;
        Integer pageSize = getValidPageSize();

        //when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/team/recruiting")
                        .param("position", position)
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
    @DisplayName("팀원을 찾는 팀 다건 조회 | 페이지 사이즈가 양수 아닐시 | 400반환")
    void findTeamsLookingForUsers_givenPageSizePositiveOnly_return400() throws Exception {
        // given
        String position = getValidPosition();
        Integer pageFrom = getValidPageFrom();
        Integer pageSize = 0;

        //when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/team/recruiting")
                        .param("position", position)
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
    @DisplayName("팀원 모집 여부 업데이트 | 올바른 요청시 | 200반환")
    void updateIsRecruiting_givenValidReq_return200() throws Exception {
        // given
        TeamIsRecruitingUpdateReqDto reqDto = getValidTeamIsRecruitingUpdateReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/team/recruiting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(TEAM_IS_RECRUITING_UPDATED.getHttpStatus().value());
        assertThat(response).contains(TEAM_IS_RECRUITING_UPDATED.name());
    }

    @Test
    @DisplayName("팀원 모집 여부 업데이트 | 팀원 모집 여부 미입력시 | 400반환")
    void updateIsRecruiting_givenIsRecruitingFieldRequired_return400() throws Exception {
        // given
        TeamIsRecruitingUpdateReqDto reqDto = getValidTeamIsRecruitingUpdateReqDto();
        reqDto.setIsRecruiting(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/team/recruiting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(IS_RECRUITING_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(IS_RECRUITING_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("프로젝트 미완료 종료 | 올바른 요청시 | 200반환")
    void projectIncomplete_givenValidReq_return200() throws Exception {
        // given & when
        MvcResult mvcResult = this.mockMvc.perform(delete("/api/v1/team/incomplete"))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PROJECT_INCOMPLETE.getHttpStatus().value());
        assertThat(response).contains(PROJECT_INCOMPLETE.name());
    }

    @Test
    @DisplayName("프로젝트 완료 종료 | 올바른 요청시 | 200반환")
    void quitCompleteProject_givenValidReq_return200() throws Exception {
        // given
        TeamCompleteReqDto reqDto = getValidTeamCompleteReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/team/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PROJECT_COMPLETE.getHttpStatus().value());
        assertThat(response).contains(PROJECT_COMPLETE.name());
    }

    @Test
    @DisplayName("프로젝트 완료 종료 | 완료한 프로젝트 URL 미입력시 | 400반환")
    void quitCompleteProject_givenProjectUrlFieldRequired_return400() throws Exception {
        // given
        TeamCompleteReqDto reqDto = getValidTeamCompleteReqDto();
        reqDto.setProjectUrl("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/team/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PROJECT_URL_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(PROJECT_URL_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("팀원 추방 | 올바른 요청시 | 200반환")
    void fireTeammate_givenValidReq_return200() throws Exception {
        // given & when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/team/user/{user-id}/fire", 1L))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(TEAMMATE_FIRED.getHttpStatus().value());
        assertThat(response).contains(TEAMMATE_FIRED.name());
    }

    @Test
    @DisplayName("팀원 추방 | 회원 식별자가 양수 아닐시 | 400반환")
    void fireTeammate_givenUserIdPositiveOnly_return400() throws Exception {
        // given & when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/team/user/{user-id}/fire", 0L))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USER_ID_POSITIVE_ONLY.getHttpStatus().value());
        assertThat(response).contains(USER_ID_POSITIVE_ONLY.name());
    }

    private TeamDefaultReqDto getValidTeamDefaultReqDto() {
        List<TeamMemberRecruitCntReqDto> teamMemberRecruitCntReqDto = new ArrayList<>();
        teamMemberRecruitCntReqDto.add(TeamMemberRecruitCntReqDto.builder()
                .totalRecruitCnt((byte) 2)
                .position(Position.DESIGNER.name())
                .build());
        teamMemberRecruitCntReqDto.add(TeamMemberRecruitCntReqDto.builder()
                .totalRecruitCnt((byte) 2)
                .position(Position.BACKEND.name())
                .build());
        teamMemberRecruitCntReqDto.add(TeamMemberRecruitCntReqDto.builder()
                .totalRecruitCnt((byte) 2)
                .position(Position.FRONTEND.name())
                .build());
        teamMemberRecruitCntReqDto.add(TeamMemberRecruitCntReqDto.builder()
                .totalRecruitCnt((byte) 2)
                .position(Position.MANAGER.name())
                .build());

        return TeamDefaultReqDto.builder()
                .projectName("가보자잇")
                .projectDescription("가보자잇 프로젝트 설명입니다.")
                .teamMemberRecruitCnts(teamMemberRecruitCntReqDto)
                .expectation("열정적인 팀원을 구합니다.")
                .openChatUrl("https://open.kakao.com/o/")
                .build();
    }

    private String getValidPosition() {
        return Position.NONE.name();
    }

    private Integer getValidPageFrom() {
        return 0;
    }

    private Integer getValidPageSize() {
        return 20;
    }

    private TeamIsRecruitingUpdateReqDto getValidTeamIsRecruitingUpdateReqDto() {
        return TeamIsRecruitingUpdateReqDto.builder()
                .isRecruiting(true)
                .build();
    }

    private TeamCompleteReqDto getValidTeamCompleteReqDto() {
        return TeamCompleteReqDto.builder()
                .projectUrl("github.com/gabojait")
                .build();
    }
}