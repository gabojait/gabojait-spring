package com.gabojait.gabojaitspring.profile.controller;

import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.WebMvc;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.favorite.service.FavoriteUserService;
import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.offer.domain.type.OfferedBy;
import com.gabojait.gabojaitspring.profile.domain.type.Level;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.profile.dto.ProfileSeekPageDto;
import com.gabojait.gabojaitspring.profile.dto.req.*;
import com.gabojait.gabojaitspring.profile.dto.res.ProfileSeekResDto;
import com.gabojait.gabojaitspring.profile.service.EducationAndWorkService;
import com.gabojait.gabojaitspring.profile.service.PortfolioService;
import com.gabojait.gabojaitspring.profile.service.PositionAndSkillService;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.team.service.TeamService;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.domain.type.Role;
import com.gabojait.gabojaitspring.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;
import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProfileControllerTest extends WebMvc {

    @MockBean
    private UserService userService;

    @MockBean
    private PositionAndSkillService positionAndSkillService;

    @MockBean
    private EducationAndWorkService educationAndWorkService;

    @MockBean
    private PortfolioService portfolioService;

    @MockBean
    private TeamService teamService;

    @MockBean
    private FavoriteUserService favoriteUserService;

    @MockBean
    private JwtProvider jwtProvider;


    @BeforeEach
    void setUp() {
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
                .offeredBy(OfferedBy.USER)
                .position(Position.FRONTEND)
                .build();

        ProfileSeekResDto profileSeekResDto = new ProfileSeekResDto(tester, List.of(offer));

        ProfileSeekPageDto profileSeekPageDto = new ProfileSeekPageDto(List.of(profileSeekResDto), 15);

        doReturn(tester)
                .when(this.jwtProvider)
                .authorizeUserAccessJwt(any());

        doReturn(profileSeekPageDto)
                .when(this.userService)
                .findManyUsersByPositionWithProfileOrder(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("본인 프로필 조회 | 올바른 요청시 | 200반환")
    void findMyself_givenValidReq_return200() throws Exception {
        // given & when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/profile"))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(SELF_PROFILE_FOUND.getHttpStatus().value());
        assertThat(response).contains(SELF_PROFILE_FOUND.name());
    }

    @Test
    @DisplayName("프로필 단건 조회 | 올바른 요청시 | 200반환")
    void findOther_givenValidReq_return200() throws Exception {
        // given & when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/{user-id}/profile", 1))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PROFILE_FOUND.getHttpStatus().value());
        assertThat(response).contains(PROFILE_FOUND.name());
    }

    @Test
    @DisplayName("프로필 단건 조회 | 회원 식별자 미입력시 | 400반환")
    void findOther_givenUserIdFieldRequired_return400() throws Exception {
        // given & when TODO 식별자 미입력
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/{user-id}/profile", ""))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

//        assertThat(status).isEqualTo(USER_ID_FIELD_REQUIRED.getHttpStatus().value());
//        assertThat(response).contains(USER_ID_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("프로필 단건 조회 | 회원 식별가 양수 아닐시 | 400반환")
    void findOther_givenUserIdPositiveOnly_return400() throws Exception {
        // given & when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/{user-id}/profile", -1))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USER_ID_POSITIVE_ONLY.getHttpStatus().value());
        assertThat(response).contains(USER_ID_POSITIVE_ONLY.name());
    }

    @Test
    @DisplayName("프로필 사진 업로드 또는 수정 | 올바른 요청시 | 200반환")
    void uploadProfileImage_givenValidReq_return200() throws Exception {
        // given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test.png",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                "test image".getBytes()
        );

        // when
        MvcResult mvcResult = this.mockMvc.perform(multipart("/api/v1/user/image")
                        .file(image))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PROFILE_IMAGE_UPLOADED.getHttpStatus().value());
        assertThat(response).contains(PROFILE_IMAGE_UPLOADED.name());
    }

    @Test
    @DisplayName("프로필 사진 업로드 또는 수정 | 이미지 미입력시 | 400반환")
    void uploadProfileImage_givenFileFieldRequired_return400() throws Exception {
        // given
        doThrow(new CustomException(FILE_FIELD_REQUIRED))
                .when(this.userService)
                .uploadProfileImage(any(), any());

        // when
        MvcResult mvcResult = this.mockMvc.perform(multipart("/api/v1/user/image"))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(FILE_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(FILE_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("프로필 사진 삭제 | 올바른 요청시 | 200반환")
    void deleteProfileImage_givenValidReq_return200() throws Exception {
        // given & when
        MvcResult mvcResult = this.mockMvc.perform(delete("/api/v1/user/image"))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PROFILE_IMAGE_DELETED.getHttpStatus().value());
        assertThat(response).contains(PROFILE_IMAGE_DELETED.name());
    }

    @Test
    @DisplayName("팀 찾기 여부 수정 | 올바른 요청시 | 200반환")
    void updateIsSeekingTeam_givenValidReq_return200() throws Exception {
        // given
        ProfileIsSeekingTeamUpdateReqDto reqDto = getValidProfileIsSeekingTeamUpdateReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/seeking-team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PROFILE_SEEKING_TEAM_UPDATED.getHttpStatus().value());
        assertThat(response).contains(PROFILE_SEEKING_TEAM_UPDATED.name());
    }

    @Test
    @DisplayName("팀 찾기 여부 수정 | 팀 찾기 여부 수정 미입력시 | 400반환")
    void updateIsSeekingTeam_givenIsPublicFieldRequired_return400() throws Exception {
        // given
        ProfileIsSeekingTeamUpdateReqDto reqDto = getValidProfileIsSeekingTeamUpdateReqDto();
        reqDto.setIsSeekingTeam(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/seeking-team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(IS_SEEKING_TEAM_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(IS_SEEKING_TEAM_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("자기소개 업데이트 | 올바른 요청시 | 200반환")
    void updateDescription_givenValidReq_return200() throws Exception {
        // given
        ProfileDescriptionUpdateReqDto reqDto = getValidProfileDescriptionUpdateReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/description")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PROFILE_DESCRIPTION_UPDATED.getHttpStatus().value());
        assertThat(response).contains(PROFILE_DESCRIPTION_UPDATED.name());
    }

    @Test
    @DisplayName("자기소개 업데이트 | 잘못된 자기소개 길이시 | 200반환")
    void updateDescription_givenProfileDescriptionLengthInvalid_return400() throws Exception {
        // given
        ProfileDescriptionUpdateReqDto reqDto = getValidProfileDescriptionUpdateReqDto();
        reqDto.setProfileDescription("가".repeat(121));
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/description")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PROFILE_DESCRIPTION_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(PROFILE_DESCRIPTION_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("포지션과 기술 생성, 수정, 삭제 | 올바른 요청시 | 200반환")
    void updatePositionAndSkill_givenValidReq_return200() throws Exception {
        // given
        PositionAndSkillDefaultReqDto requestDto = getValidPositionAndSkillDefaultReqDto();
        String request = mapToJson(requestDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/position-and-skill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(POSITION_AND_SKILL_UPDATED.getHttpStatus().value());
        assertThat(response).contains(POSITION_AND_SKILL_UPDATED.name());
    }

    @Test
    @DisplayName("포지션과 기술 생성, 수정, 삭제 | 포지션 미입력시 | 400반환")
    void updatePositionAndSkill_givenPositionFieldRequired_return400() throws Exception {
        // given
        PositionAndSkillDefaultReqDto reqDto = getValidPositionAndSkillDefaultReqDto();
        reqDto.setPosition("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/position-and-skill")
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
    @DisplayName("포지션과 기술 생성, 수정, 삭제 | 잘못된 포지션 타입시 | 400반환")
    void updatePositionAndSkill_givenPositionTypeInvalid_return400() throws Exception {
        // given
        PositionAndSkillDefaultReqDto reqDto = getValidPositionAndSkillDefaultReqDto();
        reqDto.setPosition("gamer");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/position-and-skill")
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
    @DisplayName("포지션과 기술 생성, 수정, 삭제 | 생성 기술명 미입력시 | 400반환")
    void updatePositionAndSkill_givenCreateSkillNameFieldRequired_return400() throws Exception {
        // given
        PositionAndSkillDefaultReqDto reqDto = getValidPositionAndSkillDefaultReqDto();
        reqDto.getCreateSkills().get(0).setSkillName("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/position-and-skill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(SKILL_NAME_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(SKILL_NAME_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("포지션과 기술 생성, 수정, 삭제 | 잘못된 생성 기술명 길이시 | 400반환")
    void updatePositionAndSkill_givenCreateSkillNameLengthInvalid_return400() throws Exception {
        // given
        PositionAndSkillDefaultReqDto reqDto = getValidPositionAndSkillDefaultReqDto();
        reqDto.getCreateSkills().get(0).setSkillName("스프링부트AWS노드스프링부트AWS노드임");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/position-and-skill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(SKILL_NAME_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(SKILL_NAME_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("포지션과 기술 생성, 수정, 삭제 | 생성 경험 여부 미입력시 | 400반환")
    void updatePositionAndSkill_givenCreateIsExperiencedFieldRequired_return400() throws Exception {
        // given
        PositionAndSkillDefaultReqDto reqDto = getValidPositionAndSkillDefaultReqDto();
        reqDto.getCreateSkills().get(0).setIsExperienced(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/position-and-skill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(IS_EXPERIENCED_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(IS_EXPERIENCED_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("포지션과 기술 생성, 수정, 삭제 | 생성 레벨 미입력시 | 400반환")
    void updatePositionAndSkill_givenCreateLevelFieldRequired_return400() throws Exception {
        // given
        PositionAndSkillDefaultReqDto reqDto = getValidPositionAndSkillDefaultReqDto();
        reqDto.getCreateSkills().get(0).setLevel(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/position-and-skill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(LEVEL_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(LEVEL_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("포지션과 기술 생성, 수정, 삭제 | 잘못된 생성 레벨 타입시 | 400반환")
    void updatePositionAndSkill_givenCreateLevelTypeInvalid_return400() throws Exception {
        // given
        PositionAndSkillDefaultReqDto reqDto = getValidPositionAndSkillDefaultReqDto();
        reqDto.getCreateSkills().get(0).setLevel("good");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/position-and-skill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(LEVEL_TYPE_INVALID.getHttpStatus().value());
        assertThat(response).contains(LEVEL_TYPE_INVALID.name());
    }

    @Test
    @DisplayName("포지션과 기술 생성, 수정, 삭제 | 수정 기술 식별자 미입력시 | 400반환")
    void updatePositionAndSkill_givenUpdateSkillIdFieldRequired_return400() throws Exception {
        // given
        PositionAndSkillDefaultReqDto reqDto = getValidPositionAndSkillDefaultReqDto();
        reqDto.getUpdateSkills().get(0).setSkillId(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/position-and-skill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(SKILL_ID_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(SKILL_ID_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("포지션과 기술 생성, 수정, 삭제 | 수정 기술 식별자가 양수 아닐시 | 400반환")
    void updatePositionAndSkill_givenUpdateSkillIdPositiveOnly_return400() throws Exception {
        // given
        PositionAndSkillDefaultReqDto reqDto = getValidPositionAndSkillDefaultReqDto();
        reqDto.getUpdateSkills().get(0).setSkillId(-1L);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/position-and-skill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(SKILL_ID_POSITIVE_ONLY.getHttpStatus().value());
        assertThat(response).contains(SKILL_ID_POSITIVE_ONLY.name());
    }

    @Test
    @DisplayName("포지션과 기술 생성, 수정, 삭제 | 수정 기술명 미입력시 | 400반환")
    void updatePositionAndSkill_givenUpdateSkillNameFieldRequired_return400() throws Exception {
        // given
        PositionAndSkillDefaultReqDto reqDto = getValidPositionAndSkillDefaultReqDto();
        reqDto.getUpdateSkills().get(0).setSkillName("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/position-and-skill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(SKILL_NAME_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(SKILL_NAME_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("포지션과 기술 생성, 수정, 삭제 | 잘못된 수정 기술명 길이시 | 400반환")
    void updatePositionAndSkill_givenUpdateSkillNameLengthInvalid_return400() throws Exception {
        // given
        PositionAndSkillDefaultReqDto reqDto = getValidPositionAndSkillDefaultReqDto();
        reqDto.getUpdateSkills().get(0).setSkillName("스프링부트AWS노드스프링부트AWS노드임");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/position-and-skill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(SKILL_NAME_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(SKILL_NAME_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("포지션과 기술 생성, 수정, 삭제 | 수정 경험 여부 미입력시 | 400반환")
    void updatePositionAndSkill_givenUpdateIsExperiencedFieldRequired_return400() throws Exception {
        // given
        PositionAndSkillDefaultReqDto reqDto = getValidPositionAndSkillDefaultReqDto();
        reqDto.getUpdateSkills().get(0).setIsExperienced(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/position-and-skill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(IS_EXPERIENCED_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(IS_EXPERIENCED_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("포지션과 기술 생성, 수정, 삭제 | 수정 레벨 미입력시 | 400반환")
    void updatePositionAndSkill_givenUpdateLevelFieldRequired_return400() throws Exception {
        // given
        PositionAndSkillDefaultReqDto reqDto = getValidPositionAndSkillDefaultReqDto();
        reqDto.getUpdateSkills().get(0).setLevel(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/position-and-skill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(LEVEL_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(LEVEL_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("포지션과 기술 생성, 수정, 삭제 | 잘못된 수정 레벨 타입시 | 400반환")
    void updatePositionAndSkill_givenUpdateLevelTypeInvalid_return400() throws Exception {
        // given
        PositionAndSkillDefaultReqDto reqDto = getValidPositionAndSkillDefaultReqDto();
        reqDto.getUpdateSkills().get(0).setLevel("good");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/position-and-skill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(LEVEL_TYPE_INVALID.getHttpStatus().value());
        assertThat(response).contains(LEVEL_TYPE_INVALID.name());
    }

    @Test
    @DisplayName("학력과 경력 생성, 수정, 삭제 | 올바른 요청시 | 200반환")
    void updateEducationAndWork_givenValidReq_return200() throws Exception {
        // given
        EducationAndWorkDefaultReqDto reqDto = getValidEducationAndWorkDefaultReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/education-and-work")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EDUCATION_AND_WORK_UPDATED.getHttpStatus().value());
        assertThat(response).contains(EDUCATION_AND_WORK_UPDATED.name());
    }

    @Test
    @DisplayName("학력과 경력 생성, 수정, 삭제 | 학력 생성 학교명 미입력시 | 400반환")
    void updateEducationAndWork_givenEducationCreateInstitutionNameFieldRequired_return200() throws Exception {
        // given
        EducationAndWorkDefaultReqDto reqDto = getValidEducationAndWorkDefaultReqDto();
        reqDto.getCreateEducations().get(0).setInstitutionName("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/education-and-work")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(INSTITUTION_NAME_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(INSTITUTION_NAME_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("학력과 경력 생성, 수정, 삭제 | 잘못된 학력 생성 학교명 길이시 | 400반환")
    void updateEducationAndWork_givenEducationCreateInstitutionNameLengthInvalid_return400() throws Exception {
        // given
        EducationAndWorkDefaultReqDto reqDto = getValidEducationAndWorkDefaultReqDto();
        reqDto.getCreateEducations().get(0).setInstitutionName("대학");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/education-and-work")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(INSTITUTION_NAME_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(INSTITUTION_NAME_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("학력과 경력 생성, 수정, 삭제 | 학력 생성 시작일 미입력시 | 400반환")
    void updateEducationAndWork_givenEducationCreateStartedAtFieldRequired_return400() throws Exception {
        // given
        EducationAndWorkDefaultReqDto reqDto = getValidEducationAndWorkDefaultReqDto();
        reqDto.getCreateEducations().get(0).setStartedAt(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/education-and-work")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(STARTED_AT_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(STARTED_AT_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("학력과 경력 생성, 수정, 삭제 | 학력 생성 현재 여부 미입력시 | 400반환")
    void updateEducationAndWork_givenEducationCreateIsCurrentFieldRequired_return400() throws Exception {
        // given
        EducationAndWorkDefaultReqDto reqDto = getValidEducationAndWorkDefaultReqDto();
        reqDto.getCreateEducations().get(0).setIsCurrent(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/education-and-work")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(IS_CURRENT_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(IS_CURRENT_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("학력과 경력 생성, 수정, 삭제 | 학력 수정 학력 식별자 미입력시 | 400반환")
    void updateEducationAndWork_givenEducationUpdateEducationIdFieldRequired_return400() throws Exception {
        // given
        EducationAndWorkDefaultReqDto reqDto = getValidEducationAndWorkDefaultReqDto();
        reqDto.getUpdateEducations().get(0).setEducationId(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/education-and-work")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EDUCATION_ID_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(EDUCATION_ID_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("학력과 경력 생성, 수정, 삭제 | 학력 수정 학력 식별자가 양수 아닐시 | 400반환")
    void updateEducationAndWork_givenEducationUpdateEducationIdPositiveOnly_return400() throws Exception {
        // given
        EducationAndWorkDefaultReqDto reqDto = getValidEducationAndWorkDefaultReqDto();
        reqDto.getUpdateEducations().get(0).setEducationId(-1L);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/education-and-work")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EDUCATION_ID_POSITIVE_ONLY.getHttpStatus().value());
        assertThat(response).contains(EDUCATION_ID_POSITIVE_ONLY.name());
    }

    @Test
    @DisplayName("학력과 경력 생성, 수정, 삭제 | 학력 수정 학력명 미입력시 | 400반환")
    void updateEducationAndWork_givenEducationUpdateInstitutionNameFieldRequired_return400() throws Exception {
        // given
        EducationAndWorkDefaultReqDto reqDto = getValidEducationAndWorkDefaultReqDto();
        reqDto.getUpdateEducations().get(0).setInstitutionName("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/education-and-work")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(INSTITUTION_NAME_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(INSTITUTION_NAME_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("학력과 경력 생성, 수정, 삭제 | 잘못된 학력 수정 학력명 길이시 | 400반환")
    void updateEducationAndWork_givenEducationUpdateInstitutionNameLengthInvalid_return400() throws Exception {
        // given
        EducationAndWorkDefaultReqDto reqDto = getValidEducationAndWorkDefaultReqDto();
        reqDto.getUpdateEducations().get(0).setInstitutionName("HarvardMIT University");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/education-and-work")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(INSTITUTION_NAME_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(INSTITUTION_NAME_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("학력과 경력 생성, 수정, 삭제 | 학력 수정 시작일 미입력시 | 400반환")
    void updateEducationAndWork_givenEducationUpdateStartedAtFieldRequired_return400() throws Exception {
        // given
        EducationAndWorkDefaultReqDto reqDto = getValidEducationAndWorkDefaultReqDto();
        reqDto.getUpdateEducations().get(0).setStartedAt(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/education-and-work")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(STARTED_AT_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(STARTED_AT_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("학력과 경력 생성, 수정, 삭제 | 학력 수정 현재 여부 미입력시 | 400반환")
    void updateEducationAndWork_givenEducationUpdateIsCurrentFieldRequired_return400() throws Exception {
        // given
        EducationAndWorkDefaultReqDto reqDto = getValidEducationAndWorkDefaultReqDto();
        reqDto.getUpdateEducations().get(0).setIsCurrent(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/education-and-work")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(IS_CURRENT_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(IS_CURRENT_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("학력과 경력 생성, 수정, 삭제 | 경력 생성 기관명 미입력시 | 400반환")
    void updateEducationAndWork_givenWorkCreateCorporationNameFieldRequired_return400() throws Exception {
        // given
        EducationAndWorkDefaultReqDto reqDto = getValidEducationAndWorkDefaultReqDto();
        reqDto.getCreateWorks().get(0).setCorporationName("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/education-and-work")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(CORPORATION_NAME_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(CORPORATION_NAME_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("학력과 경력 생성, 수정, 삭제 | 잘못된 경력 생성 기관명 길이시 | 400반환")
    void updateEducationAndWork_givenWorkCreateCorporationNameLengthInvalid_return400() throws Exception {
        // given
        EducationAndWorkDefaultReqDto reqDto = getValidEducationAndWorkDefaultReqDto();
        reqDto.getCreateWorks().get(0).setCorporationName("가".repeat(21));
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/education-and-work")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(CORPORATION_NAME_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(CORPORATION_NAME_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("학력과 경력 생성, 수정, 삭제 | 잘못된 경력 생성 경력 설명 길이시 | 400반환")
    void updateEducationAndWork_givenWorkCreateWorkDescriptionLengthInvalid_return400() throws Exception {
        // given
        EducationAndWorkDefaultReqDto reqDto = getValidEducationAndWorkDefaultReqDto();
        reqDto.getCreateWorks().get(0).setWorkDescription("가".repeat(101));
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/education-and-work")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(WORK_DESCRIPTION_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(WORK_DESCRIPTION_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("학력과 경력 생성, 수정, 삭제 | 경력 생성 시작일 미입력시 | 400반환")
    void updateEducationAndWork_givenWorkCreateStartedAtFieldRequired_return400() throws Exception {
        // given
        EducationAndWorkDefaultReqDto reqDto = getValidEducationAndWorkDefaultReqDto();
        reqDto.getCreateWorks().get(0).setStartedAt(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/education-and-work")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(STARTED_AT_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(STARTED_AT_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("학력과 경력 생성, 수정, 삭제 | 경력 생성 현재 여부 미입력시 | 400반환")
    void updateEducationAndWork_givenWorkCreateIsCurrentFieldRequired_return400() throws Exception {
        // given
        EducationAndWorkDefaultReqDto reqDto = getValidEducationAndWorkDefaultReqDto();
        reqDto.getCreateWorks().get(0).setIsCurrent(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/education-and-work")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(IS_CURRENT_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(IS_CURRENT_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("학력과 경력 생성, 수정, 삭제 | 경력 수정 경력 식별자 미입력시 | 400반환")
    void updateEducationAndWork_givenWorkUpdateWorkIdFieldRequired_return400() throws Exception {
        // given
        EducationAndWorkDefaultReqDto reqDto = getValidEducationAndWorkDefaultReqDto();
        reqDto.getUpdateWorks().get(0).setWorkId(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/education-and-work")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(WORK_ID_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(WORK_ID_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("학력과 경력 생성, 수정, 삭제 | 경력 수정 경력 식별자가 양수 아닐시 | 400반환")
    void updateEducationAndWork_givenWorkUpdateWorkIdPositiveOnly_return400() throws Exception {
        // given
        EducationAndWorkDefaultReqDto reqDto = getValidEducationAndWorkDefaultReqDto();
        reqDto.getUpdateWorks().get(0).setWorkId(-1L);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/education-and-work")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(WORK_ID_POSITIVE_ONLY.getHttpStatus().value());
        assertThat(response).contains(WORK_ID_POSITIVE_ONLY.name());
    }

    @Test
    @DisplayName("학력과 경력 생성, 수정, 삭제 | 경력 수정 기관명 미입력시 | 400반환")
    void updateEducationAndWork_givenWorkUpdateCorporationNameFieldRequired_return400() throws Exception {
        // given
        EducationAndWorkDefaultReqDto reqDto = getValidEducationAndWorkDefaultReqDto();
        reqDto.getUpdateWorks().get(0).setCorporationName("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/education-and-work")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(CORPORATION_NAME_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(CORPORATION_NAME_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("학력과 경력 생성, 수정, 삭제 | 잘못된 경력 수정 기관명 길이시 | 400반환")
    void updateEducationAndWork_givenWorkUpdateCorporationNameLengthInvalid_return400() throws Exception {
        // given
        EducationAndWorkDefaultReqDto reqDto = getValidEducationAndWorkDefaultReqDto();
        reqDto.getUpdateWorks().get(0).setCorporationName("가".repeat(21));
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/education-and-work")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(CORPORATION_NAME_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(CORPORATION_NAME_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("학력과 경력 생성, 수정, 삭제 | 잘못된 경력 생성 경력 설명 길이시 | 400반환")
    void updateEducationAndWork_givenWorkUpdateWorkDescriptionLengthInvalid_return400() throws Exception {
        // given
        EducationAndWorkDefaultReqDto reqDto = getValidEducationAndWorkDefaultReqDto();
        reqDto.getUpdateWorks().get(0).setWorkDescription("가".repeat(101));
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/education-and-work")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(WORK_DESCRIPTION_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(WORK_DESCRIPTION_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("학력과 경력 생성, 수정, 삭제 | 경력 생성 시작일 미입력시 | 400반환")
    void updateEducationAndWork_givenWorkUpdateStartedAtFieldRequired_return400() throws Exception {
        // given
        EducationAndWorkDefaultReqDto reqDto = getValidEducationAndWorkDefaultReqDto();
        reqDto.getUpdateWorks().get(0).setStartedAt(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/education-and-work")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(STARTED_AT_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(STARTED_AT_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("학력과 경력 생성, 수정, 삭제 | 경력 생성 현재 여부 미입력시 | 400반환")
    void updateEducationAndWork_givenWorkUpdateIsCurrentFieldRequired_return400() throws Exception {
        // given
        EducationAndWorkDefaultReqDto reqDto = getValidEducationAndWorkDefaultReqDto();
        reqDto.getUpdateWorks().get(0).setIsCurrent(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/education-and-work")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(IS_CURRENT_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(IS_CURRENT_FIELD_REQUIRED.name());
    }


    @Test
    @DisplayName("링크 포트폴리오 생성, 수정, 삭제 | 올바른 요청시 | 200반환")
    void updateLinkPortfolio_givenValidReq_return200() throws Exception {
        // given
        PortfolioLinkDefaultReqDto reqDto = getValidPortfolioLinkDefaultReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/portfolio/link")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(LINK_PORTFOLIO_UPDATED.getHttpStatus().value());
        assertThat(response).contains(LINK_PORTFOLIO_UPDATED.name());
    }

    @Test
    @DisplayName("링크 포트폴리오 생성, 수정, 삭제 | 링크 포트폴리오 생성 포트폴리오명 미입력시 | 400반환")
    void updateLinkPortfolio_givenPortfolioLinkCreatePortfolioNameFieldRequired_return400() throws Exception {
        // given
        PortfolioLinkDefaultReqDto reqDto = getValidPortfolioLinkDefaultReqDto();
        reqDto.getCreateLinkPortfolios().get(0).setPortfolioName("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/portfolio/link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PORTFOLIO_NAME_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(PORTFOLIO_NAME_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("링크 포트폴리오 생성, 수정, 삭제 | 잘못된 링크 포트폴리오 생성 포트폴리오명 길이시 | 400반환")
    void updateLinkPortfolio_givenPortfolioLinkCreatePortfolioNameLengthInvalid_return400() throws Exception {
        // given
        PortfolioLinkDefaultReqDto reqDto = getValidPortfolioLinkDefaultReqDto();
        reqDto.getCreateLinkPortfolios().get(0).setPortfolioName("가".repeat(11));
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/portfolio/link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PORTFOLIO_NAME_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(PORTFOLIO_NAME_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("링크 포트폴리오 생성, 수정, 삭제 | 링크 포트폴리오 생성 포트폴리오 URL 미입력시 | 400반환")
    void updateLinkPortfolio_givenPortfolioLinkCreatePortfolioUrlFieldRequired_return400() throws Exception {
        // given
        PortfolioLinkDefaultReqDto reqDto = getValidPortfolioLinkDefaultReqDto();
        reqDto.getCreateLinkPortfolios().get(0).setPortfolioUrl("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/portfolio/link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PORTFOLIO_URL_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(PORTFOLIO_URL_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("링크 포트폴리오 생성, 수정, 삭제 | 잘못된 링크 포트폴리오 생성 포트폴리요 URL 길이시 | 400반환")
    void updateLinkPortfolio_givenPortfolioLinkCreatePortfolioUrlLengthInvalid_return400() throws Exception {
        // given
        PortfolioLinkDefaultReqDto reqDto = getValidPortfolioLinkDefaultReqDto();
        reqDto.getCreateLinkPortfolios().get(0).setPortfolioUrl("a".repeat(1001));
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/portfolio/link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PORTFOLIO_URL_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(PORTFOLIO_URL_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("링크 포트폴리오 생성, 수정, 삭제 | 링크 포트폴리오 수정 포트폴리오 식별자 미입력시 | 400반환")
    void updateLinkPortfolio_givenPortfolioLinkUpdatePortfolioIdFieldRequired_return400() throws Exception {
        // given
        PortfolioLinkDefaultReqDto reqDto = getValidPortfolioLinkDefaultReqDto();
        reqDto.getUpdateLinkPortfolios().get(0).setPortfolioId(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/portfolio/link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PORTFOLIO_ID_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(PORTFOLIO_ID_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("링크 포트폴리오 생성, 수정, 삭제 | 링크 포트폴리오 수정 포트폴리오 식별자가 양수 아닐시 | 400반환")
    void updateLinkPortfolio_givenPortfolioLinkUpdatePortfolioIdPositiveOnly_return400() throws Exception {
        // given
        PortfolioLinkDefaultReqDto reqDto = getValidPortfolioLinkDefaultReqDto();
        reqDto.getUpdateLinkPortfolios().get(0).setPortfolioId(-1L);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/portfolio/link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PORTFOLIO_ID_POSITIVE_ONLY.getHttpStatus().value());
        assertThat(response).contains(PORTFOLIO_ID_POSITIVE_ONLY.name());
    }

    @Test
    @DisplayName("링크 포트폴리오 생성, 수정, 삭제 | 링크 포트폴리오 수정 포트폴리오명 미입력시 | 400반환")
    void updateLinkPortfolio_givenPortfolioLinkUpdatePortfolioNameFieldRequired_return400() throws Exception {
        // given
        PortfolioLinkDefaultReqDto reqDto = getValidPortfolioLinkDefaultReqDto();
        reqDto.getUpdateLinkPortfolios().get(0).setPortfolioName("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/portfolio/link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PORTFOLIO_NAME_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(PORTFOLIO_NAME_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("링크 포트폴리오 생성, 수정, 삭제 | 잘못된 링크 포트폴리오 수정 포트폴리오명 길이시 | 400반환")
    void updateLinkPortfolio_givenPortfolioLinkUpdatePortfolioNameLengthInvalid_return400() throws Exception {
        // given
        PortfolioLinkDefaultReqDto reqDto = getValidPortfolioLinkDefaultReqDto();
        reqDto.getUpdateLinkPortfolios().get(0).setPortfolioName("a".repeat(1001));
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/portfolio/link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PORTFOLIO_NAME_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(PORTFOLIO_NAME_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("링크 포트폴리오 생성, 수정, 삭제 | 링크 포트폴리오 수정 포트폴리오 URL 미입력시 | 400반환")
    void updateLinkPortfolio_givenPortfolioLinkUpdatePortfolioUrlFieldRequired_return400() throws Exception {
        // given
        PortfolioLinkDefaultReqDto reqDto = getValidPortfolioLinkDefaultReqDto();
        reqDto.getUpdateLinkPortfolios().get(0).setPortfolioUrl("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/portfolio/link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PORTFOLIO_URL_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(PORTFOLIO_URL_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("링크 포트폴리오 생성, 수정, 삭제 | 잘못된 링크 포트폴리오 수정 포트폴리오 URL 길이시 | 400반환")
    void updateLinkPortfolio_givenPortfolioLinkUpdatePortfolioUrlLengthInvalid_return400() throws Exception {
        // given
        PortfolioLinkDefaultReqDto reqDto = getValidPortfolioLinkDefaultReqDto();
        reqDto.getUpdateLinkPortfolios().get(0).setPortfolioUrl("a".repeat(1001));
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/portfolio/link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PORTFOLIO_URL_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(PORTFOLIO_URL_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("파일 포트폴리오 생성, 수정, 삭제 | 올바른 요청시 | 200반환")
    void updateFilePortfolio_givenValidReq_return200() throws Exception{
        // given
        String createPortfolioName = getValidPortfolioName();
        MultipartFile createPortfolioFile = getValidPortfolioFiles();
        Long updatePortfolioId = getValidId();
        String updatePortfolioName = getValidPortfolioName();
        MultipartFile updatePortfolioFile = getValidPortfolioFiles();
        Long deletePortfolioId = getValidId();

        // when
        MvcResult mvcResult = this.mockMvc.perform(multipart("/api/v1/user/portfolio/file")
                        .file("create-portfolio-files", createPortfolioFile.getBytes())
                        .file("update-portfolio-files", updatePortfolioFile.getBytes())
                        .param("create-portfolio-names", createPortfolioName)
                        .param("update-portfolio-ids", updatePortfolioId.toString())
                        .param("update-portfolio-names", updatePortfolioName)
                        .param("delete-portfolio-ids", deletePortfolioId.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(FILE_PORTFOLIO_UPDATED.getHttpStatus().value());
        assertThat(response).contains(FILE_PORTFOLIO_UPDATED.name());
    }

    @Test
    @DisplayName("팀을 찾는 회원 다건 조회 | 올바른 요청시 | 200반환")
    void findUsersLookingForTeam_givenValidReq_return200() throws Exception {
        // given
        String position = getValidPosition();
        String profileOrder = getValidProfileOrder();
        Integer pageFrom = getValidPageFrom();
        Integer pageSize = getValidPageSize();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/seeking-team")
                        .param("position", position)
                        .param("profile-order", profileOrder)
                        .param("page-from", pageFrom.toString())
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USERS_SEEKING_TEAM_FOUND.getHttpStatus().value());
        assertThat(response).contains(USERS_SEEKING_TEAM_FOUND.name());
    }

    @Test
    @DisplayName("팀을 찾는 회원 다건 조회 | 포지션 미입력시 | 400반환")
    void findUsersLookingForTeam_givenPositionFieldRequired_return400() throws Exception {
        // given
        String position = "";
        String profileOrder = getValidProfileOrder();
        Integer pageFrom = getValidPageFrom();
        Integer pageSize = getValidPageSize();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/seeking-team")
                        .param("position", position)
                        .param("profile-order", profileOrder)
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
    @DisplayName("팀을 찾는 회원 다건 조회 | 잘못된 포지션 타입시 | 400반환")
    void findUsersLookingForTeam_givenPositionTypeInvalid_return400() throws Exception {
        // given
        String position = "marketer";
        String profileOrder = getValidProfileOrder();
        Integer pageFrom = getValidPageFrom();
        Integer pageSize = getValidPageSize();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/seeking-team")
                        .param("position", position)
                        .param("profile-order", profileOrder)
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
    @DisplayName("팀을 찾는 회원 다건 조회 | 프로필 정렬 기준 미입력시 | 400반환")
    void findUsersLookingForTeam_givenProfileOrderFieldRequired_return400() throws Exception {
        // given
        String position = getValidPosition();
        String profileOrder = "";
        Integer pageFrom = getValidPageFrom();
        Integer pageSize = getValidPageSize();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/seeking-team")
                        .param("position", position)
                        .param("profile-order", profileOrder)
                        .param("page-from", pageFrom.toString())
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PROFILE_ORDER_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(PROFILE_ORDER_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("팀을 찾는 회원 다건 조회 | 잘못된 프로필 정렬 기준 타입시 | 400반환")
    void findUsersLookingForTeam_givenProfileOrderTypeInvalid_return400() throws Exception {
        // given
        String position = getValidPosition();
        String profileOrder = "update";
        Integer pageFrom = getValidPageFrom();
        Integer pageSize = getValidPageSize();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/seeking-team")
                        .param("position", position)
                        .param("profile-order", profileOrder)
                        .param("page-from", pageFrom.toString())
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PROFILE_ORDER_TYPE_INVALID.getHttpStatus().value());
        assertThat(response).contains(PROFILE_ORDER_TYPE_INVALID.name());
    }

    @Test
    @DisplayName("팀을 찾는 회원 다건 조회 | 페이지 시작점 미입력시 | 400반환")
    void findUsersLookingForTeam_givenPageFromFieldRequired_return400() throws Exception {
        // given
        String position = getValidPosition();
        String profileOrder = getValidProfileOrder();
        Integer pageSize = getValidPageSize();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/seeking-team")
                        .param("position", position)
                        .param("profile-order", profileOrder)
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PAGE_FROM_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(PAGE_FROM_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("팀을 찾는 회원 다건 조회 | 페이지 시작점이 양수 또는 0 아닐시 | 400반환")
    void findUsersLookingForTeam_givenPageFromPositiveOrZeroOnly_return400() throws Exception {
        // given
        String position = getValidPosition();
        String profileOrder = getValidProfileOrder();
        Integer pageFrom = -1;
        Integer pageSize = getValidPageSize();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/seeking-team")
                        .param("position", position)
                        .param("profile-order", profileOrder)
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
    @DisplayName("팀을 찾는 회원 다건 조회 | 페이지 사이즈가 양수 아닐시 | 400반환")
    void findUsersLookingForTeam_givenPageSizePositiveOnly_return400() throws Exception {
        // given
        String position = getValidPosition();
        String profileOrder = getValidProfileOrder();
        Integer pageFrom = getValidPageFrom();
        Integer pageSize = 0;

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/seeking-team")
                        .param("position", position)
                        .param("profile-order", profileOrder)
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
    @DisplayName("팀을 찾는 회원 다건 조회 | 올바른 요청시 | 200반환")
    void leaveTeam_giveValidReq_return200() throws Exception {
        // given & when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/team/leave"))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USER_LEFT_TEAM.getHttpStatus().value());
        assertThat(response).contains(USER_LEFT_TEAM.name());
    }

    private ProfileIsSeekingTeamUpdateReqDto getValidProfileIsSeekingTeamUpdateReqDto() {
        ProfileIsSeekingTeamUpdateReqDto reqDto = new ProfileIsSeekingTeamUpdateReqDto();
        reqDto.setIsSeekingTeam(false);
        return reqDto;
    }

    private ProfileDescriptionUpdateReqDto getValidProfileDescriptionUpdateReqDto() {
        ProfileDescriptionUpdateReqDto reqDto = new ProfileDescriptionUpdateReqDto();
        reqDto.setProfileDescription("테스트 자기소개입니다.");
        return reqDto;
    }

    private PositionAndSkillDefaultReqDto getValidPositionAndSkillDefaultReqDto() {
        SkillCreateReqDto skillCreateReqDto = new SkillCreateReqDto();
        skillCreateReqDto.setSkillName("스프링");
        skillCreateReqDto.setIsExperienced(true);
        skillCreateReqDto.setLevel(Level.MID.name().toLowerCase());

        SkillUpdateReqDto skillUpdateReqDto = new SkillUpdateReqDto();
        skillUpdateReqDto.setSkillId(1L);
        skillUpdateReqDto.setSkillName("노드");
        skillUpdateReqDto.setIsExperienced(false);
        skillUpdateReqDto.setLevel(Level.LOW.name().toLowerCase());

        PositionAndSkillDefaultReqDto reqDto = new PositionAndSkillDefaultReqDto();
        reqDto.setPosition(Position.BACKEND.name().toLowerCase());
        reqDto.setCreateSkills(List.of(skillCreateReqDto));
        reqDto.setUpdateSkills(List.of(skillUpdateReqDto));
        reqDto.setDeleteSkillIds(List.of(1L));

        return reqDto;
    }

    private EducationAndWorkDefaultReqDto getValidEducationAndWorkDefaultReqDto() {
        EducationCreateReqDto educationCreateReqDto = new EducationCreateReqDto();
        educationCreateReqDto.setInstitutionName("하버드대학교");
        educationCreateReqDto.setStartedAt(LocalDate.of(2019, 3, 1));
        educationCreateReqDto.setEndedAt(LocalDate.of(2023, 8, 1));
        educationCreateReqDto.setIsCurrent(false);

        EducationUpdateReqDto educationUpdateReqDto = new EducationUpdateReqDto();
        educationUpdateReqDto.setEducationId(1L);
        educationUpdateReqDto.setInstitutionName("MIT");
        educationUpdateReqDto.setStartedAt(LocalDate.of(2019, 3, 1));
        educationUpdateReqDto.setEndedAt(LocalDate.of(2023, 8, 1));
        educationUpdateReqDto.setIsCurrent(false);

        WorkCreateReqDto workCreateReqDto = new WorkCreateReqDto();
        workCreateReqDto.setCorporationName("가보자잇사");
        workCreateReqDto.setWorkDescription("가보자잇에서 백엔드 개발");
        workCreateReqDto.setStartedAt(LocalDate.of(2023, 9, 1));
        workCreateReqDto.setEndedAt(null);
        workCreateReqDto.setIsCurrent(true);

        WorkUpdateReqDto workUpdateReqDto = new WorkUpdateReqDto();
        workUpdateReqDto.setWorkId(1L);
        workUpdateReqDto.setCorporationName("가볼까잇사");
        workUpdateReqDto.setWorkDescription("가볼까잇에서 백엔드 개발");
        workUpdateReqDto.setStartedAt(LocalDate.of(2022, 9, 1));
        workUpdateReqDto.setEndedAt(LocalDate.of(2023, 9, 1));
        workUpdateReqDto.setIsCurrent(true);

        EducationAndWorkDefaultReqDto reqDto = new EducationAndWorkDefaultReqDto();
        reqDto.setCreateEducations(List.of(educationCreateReqDto));
        reqDto.setUpdateEducations(List.of(educationUpdateReqDto));
        reqDto.setDeleteEducationIds(List.of(1L));
        reqDto.setCreateWorks(List.of(workCreateReqDto));
        reqDto.setUpdateWorks(List.of(workUpdateReqDto));
        reqDto.setDeleteWorkIds(List.of(1L));

        return reqDto;
    }

    private PortfolioLinkDefaultReqDto getValidPortfolioLinkDefaultReqDto() {
        PortfolioLinkCreateReqDto linkCreateReqDto = new PortfolioLinkCreateReqDto();
        linkCreateReqDto.setPortfolioName("깃허브");
        linkCreateReqDto.setPortfolioUrl("github.com/gabojait");

        PortfolioLinkUpdateReqDto linkUpdateReqDto = new PortfolioLinkUpdateReqDto();
        linkUpdateReqDto.setPortfolioId(1L);
        linkUpdateReqDto.setPortfolioName("깃허브");
        linkUpdateReqDto.setPortfolioUrl("github.com/gabojait");

        PortfolioLinkDefaultReqDto reqDto = new PortfolioLinkDefaultReqDto();
        reqDto.setCreateLinkPortfolios(List.of(linkCreateReqDto));
        reqDto.setUpdateLinkPortfolios(List.of(linkUpdateReqDto));
        reqDto.setDeletePortfolioIds(List.of(1L));

        return reqDto;
    }

    private String getValidPortfolioName() {
        return "깃허브";
    }

    private MultipartFile getValidPortfolioFiles() {
        return new MockMultipartFile(
                "image",
                "test.png",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                "test image".getBytes()
        );
    }

    private Long getValidId() {
        return 1L;
    }

    private String getValidPosition() {
        return Position.NONE.name().toLowerCase();
    }

    private String getValidProfileOrder() {
        return "active";
    }

    private Integer getValidPageFrom() {
        return 0;
    }

    private Integer getValidPageSize() {
        return 20;
    }
}