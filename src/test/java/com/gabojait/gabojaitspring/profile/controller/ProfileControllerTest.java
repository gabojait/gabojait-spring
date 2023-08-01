package com.gabojait.gabojaitspring.profile.controller;

import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.WebMvc;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.favorite.service.FavoriteUserService;
import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.offer.domain.type.OfferedBy;
import com.gabojait.gabojaitspring.profile.domain.type.Level;
import com.gabojait.gabojaitspring.profile.domain.type.Media;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.profile.domain.type.ProfileOrder;
import com.gabojait.gabojaitspring.profile.dto.ProfileSeekPageDto;
import com.gabojait.gabojaitspring.profile.dto.req.*;
import com.gabojait.gabojaitspring.profile.dto.res.ProfileSeekResDto;
import com.gabojait.gabojaitspring.profile.service.ProfileService;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.team.service.TeamService;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.domain.type.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;
import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProfileControllerTest extends WebMvc {

    @MockBean
    private ProfileService profileService;

    @MockBean
    private TeamService teamService;

    @MockBean
    private FavoriteUserService favoriteUserService;

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
                .offeredBy(OfferedBy.USER)
                .position(Position.FRONTEND)
                .build();

        String url = "https://google.com";

        ProfileSeekResDto profileSeekResDto = new ProfileSeekResDto(tester, List.of(offer));

        ProfileSeekPageDto profileSeekPageDto = new ProfileSeekPageDto(List.of(profileSeekResDto), 15);

        doReturn(profileSeekPageDto)
                .when(this.profileService)
                .findManyUsersByPositionWithProfileOrder(anyLong(), any(), any(), any(), any());

        doReturn(tester)
                .when(this.profileService)
                .updateProfile(anyLong(), any());

        doReturn(tester)
                .when(this.profileService)
                .uploadProfileImage(anyLong(), any());

        doReturn(tester)
                .when(this.profileService)
                .findOneUser(anyLong());

        doReturn(tester)
                .when(this.profileService)
                .deleteProfileImage(anyLong());

        doReturn(url)
                .when(this.profileService)
                .uploadPortfolioFile(anyLong(), any());
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
        MockMultipartFile image = getValidMultipartFile();

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
                .when(this.profileService)
                .uploadProfileImage(anyLong(), any());

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
    @DisplayName("프로필 업데이트 | 올바른 요청시 | 200반환")
    void updateProfile_givenValidReq_return200() throws Exception {
        // given
        ProfileDefaultReqDto reqDto = getValidProfileDefaultReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PROFILE_UPDATED.getHttpStatus().value());
        assertThat(response).contains(PROFILE_UPDATED.name());
    }

    @Test
    @DisplayName("프로필 업데이트 | 포지션 미입력시 | 400반환")
    void updateProfile_givenPositionFieldRequired_return400() throws Exception {
        // given
        ProfileDefaultReqDto reqDto = getValidProfileDefaultReqDto();
        reqDto.setPosition("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/profile")
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
    @DisplayName("프로필 업데이트 | 잘못된 포지션 타입시 | 400반환")
    void updateProfile_givenPositionTypeInvalid_return400() throws Exception {
        // given
        ProfileDefaultReqDto reqDto = getValidProfileDefaultReqDto();
        reqDto.setPosition("person");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/profile")
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
    @DisplayName("프로필 업데이트 | 기술명 미입력시 | 400반환")
    void updateProfile_givenSkillNameFieldRequired_return400() throws Exception {
        // given
        ProfileDefaultReqDto reqDto = getValidProfileDefaultReqDto();
        reqDto.getSkills().get(0).setSkillName("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/profile")
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
    @DisplayName("프로필 업데이트 | 잘못된 기술명 길이시 | 400반환")
    void updateProfile_givenSkillNameLengthInvalid_return400() throws Exception {
        // given
        ProfileDefaultReqDto reqDto = getValidProfileDefaultReqDto();
        reqDto.getSkills().get(0).setSkillName("A".repeat(21));
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/profile")
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
    @DisplayName("프로필 업데이트 | 경험 여부 미입력시 | 400반환")
    void updateProfile_givenIsExperiencedFieldRequired_return400() throws Exception {
        // given
        ProfileDefaultReqDto reqDto = getValidProfileDefaultReqDto();
        reqDto.getSkills().get(0).setIsExperienced(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/profile")
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
    @DisplayName("프로필 업데이트 | 레벨 미입력시 | 400반환")
    void updateProfile_givenLevelFieldRequired_return400() throws Exception {
        // given
        ProfileDefaultReqDto reqDto = getValidProfileDefaultReqDto();
        reqDto.getSkills().get(0).setLevel(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/profile")
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
    @DisplayName("프로필 업데이트 | 잘못된 레벨 타입시 | 400반환")
    void updateProfile_givenLevelTypeInvalid_return400() throws Exception {
        // given
        ProfileDefaultReqDto reqDto = getValidProfileDefaultReqDto();
        reqDto.getSkills().get(0).setLevel("nice");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/profile")
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
    @DisplayName("프로필 업데이트 | 학교명 미입력시 | 400반환")
    void updateProfile_givenInstitutionNameFieldRequired_return400() throws Exception {
        // given
        ProfileDefaultReqDto reqDto = getValidProfileDefaultReqDto();
        reqDto.getEducations().get(0).setInstitutionName("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/profile")
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
    @DisplayName("프로필 업데이트 | 잘못된 학교명 길이시 | 400반환")
    void updateProfile_givenInstitutionNameLengthInvalid_return400() throws Exception {
        // given
        ProfileDefaultReqDto reqDto = getValidProfileDefaultReqDto();
        reqDto.getEducations().get(0).setInstitutionName("가".repeat(2));
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/profile")
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
    @DisplayName("프로필 업데이트 | 학력 시작일 미입력시 | 400반환")
    void updateProfile_givenEducationStartedAtFieldRequired_return400() throws Exception {
        // given
        ProfileDefaultReqDto reqDto = getValidProfileDefaultReqDto();
        reqDto.getEducations().get(0).setStartedAt(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/profile")
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
    @DisplayName("프로필 업데이트 | 학력 현재 여부 미입력시 | 400반환")
    void updateProfile_givenEducationIsCurrentFieldRequired_return400() throws Exception {
        // given
        ProfileDefaultReqDto reqDto = getValidProfileDefaultReqDto();
        reqDto.getEducations().get(0).setIsCurrent(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/profile")
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
    @DisplayName("프로필 업데이트 | 기관명 미입력시 | 400반환")
    void updateProfile_givenCorporationNameFieldRequired_return400() throws Exception {
        // given
        ProfileDefaultReqDto reqDto = getValidProfileDefaultReqDto();
        reqDto.getWorks().get(0).setCorporationName("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/profile")
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
    @DisplayName("프로필 업데이트 | 잘못된 기관명 길이시 | 400반환")
    void updateProfile_givenCorporationNameLengthInvalid_return400() throws Exception {
        // given
        ProfileDefaultReqDto reqDto = getValidProfileDefaultReqDto();
        reqDto.getWorks().get(0).setCorporationName("가".repeat(21));
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/profile")
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
    @DisplayName("프로필 업데이트 | 경력 설명 길이시 | 400반환")
    void updateProfile_givenWorkDescriptionLengthInvalid_return400() throws Exception {
        // given
        ProfileDefaultReqDto reqDto = getValidProfileDefaultReqDto();
        reqDto.getWorks().get(0).setWorkDescription("가".repeat(101));
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/profile")
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
    @DisplayName("프로필 업데이트 | 시작일 미입력시 | 400반환")
    void updateProfile_givenWorkStartedAtFieldRequired_return400() throws Exception {
        // given
        ProfileDefaultReqDto reqDto = getValidProfileDefaultReqDto();
        reqDto.getWorks().get(0).setStartedAt(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/profile")
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
    @DisplayName("프로필 업데이트 | 경력 현재 여부 미입력시 | 400반환")
    void updateProfile_givenWorkIsCurrent_return400() throws Exception {
        // given
        ProfileDefaultReqDto reqDto = getValidProfileDefaultReqDto();
        reqDto.getWorks().get(0).setIsCurrent(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/profile")
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
    @DisplayName("프로필 업데이트 | 포트폴리오명 미입력시 | 400반환")
    void updateProfile_givenPortfolioNameFieldRequired_return400() throws Exception {
        // given
        ProfileDefaultReqDto reqDto = getValidProfileDefaultReqDto();
        reqDto.getPortfolios().get(0).setPortfolioName("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/profile")
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
    @DisplayName("프로필 업데이트 | 잘못된 포트폴리오명 길이시 | 400반환")
    void updateProfile_givenPortfolioNameLengthInvalid_return400() throws Exception {
        // given
        ProfileDefaultReqDto reqDto = getValidProfileDefaultReqDto();
        reqDto.getPortfolios().get(0).setPortfolioName("가".repeat(11));
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/profile")
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
    @DisplayName("프로필 업데이트 | 포트폴리오 URL 미입력시 | 400반환")
    void updateProfile_givenPortfolioUrlFieldRequired_return400() throws Exception {
        // given
        ProfileDefaultReqDto reqDto = getValidProfileDefaultReqDto();
        reqDto.getPortfolios().get(0).setPortfolioUrl("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/profile")
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
    @DisplayName("프로필 업데이트 | 잘못된 포트폴리오 URL 길이시 | 400반환")
    void updateProfile_givenPortfolioUrlLengthInvalid_return400() throws Exception {
        // given
        ProfileDefaultReqDto reqDto = getValidProfileDefaultReqDto();
        reqDto.getPortfolios().get(0).setPortfolioUrl("w".repeat(1001));
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/profile")
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
    @DisplayName("프로필 업데이트 | 미디어 종류 미입력시 | 400반환")
    void updateProfile_givenMediaFieldRequired_return400() throws Exception {
        // given
        ProfileDefaultReqDto reqDto = getValidProfileDefaultReqDto();
        reqDto.getPortfolios().get(0).setMedia("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(MEDIA_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(MEDIA_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("프로필 업데이트 | 잘못된 미디어 타입시 | 400반환")
    void updateProfile_givenMediaTypeInvalid_return400() throws Exception {
        // given
        ProfileDefaultReqDto reqDto = getValidProfileDefaultReqDto();
        reqDto.getPortfolios().get(0).setMedia("music");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(MEDIA_TYPE_INVALID.getHttpStatus().value());
        assertThat(response).contains(MEDIA_TYPE_INVALID.name());
    }

    @Test
    @DisplayName("파일 포트폴리오 업로드 | 올바른 요청시 | 201반환")
    void uploadPortfolioFile_givenValidReq_return201() throws Exception {
        // given
        MockMultipartFile file = getValidMultipartFile();

        // when
        MvcResult mvcResult = this.mockMvc.perform(multipart("/api/v1/user/portfolio/file")
                        .file(file))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PORTFOLIO_FILE_UPLOADED.getHttpStatus().value());
        assertThat(response).contains(PORTFOLIO_FILE_UPLOADED.name());
    }

    @Test
    @DisplayName("파일 포트폴리오 업로드 | 파일 미입력시 | 400반환")
    void uploadPortfolioFile_givenFileFieldRequired_return400() throws Exception {
        // given
        doThrow(new CustomException(FILE_FIELD_REQUIRED))
                .when(this.profileService)
                .uploadPortfolioFile(anyLong(), any());

        // when
        MvcResult mvcResult = this.mockMvc.perform(multipart("/api/v1/user/portfolio/file"))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(FILE_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(FILE_FIELD_REQUIRED.name());
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

    private ProfileDefaultReqDto getValidProfileDefaultReqDto() {
        String positionReqDto = Position.BACKEND.name();

        SkillDefaultReqDto skillDefaultReqDto = new SkillDefaultReqDto();
        skillDefaultReqDto.setSkillId(null);
        skillDefaultReqDto.setSkillName("스프링");
        skillDefaultReqDto.setIsExperienced(true);
        skillDefaultReqDto.setLevel(Level.MID.name());

        EducationDefaultReqDto educationDefaultReqDto = new EducationDefaultReqDto();
        educationDefaultReqDto.setEducationId(null);
        educationDefaultReqDto.setInstitutionName("하버드대학교");
        educationDefaultReqDto.setStartedAt(LocalDate.of(2019, 3, 1));
        educationDefaultReqDto.setEndedAt(LocalDate.of(2023, 8, 1));
        educationDefaultReqDto.setIsCurrent(true);

        WorkDefaultReqDto workDefaultReqDto = new WorkDefaultReqDto();
        workDefaultReqDto.setWorkId(null);
        workDefaultReqDto.setCorporationName("가보자잇사");
        workDefaultReqDto.setWorkDescription("가보자잇에서 백엔드 개발");
        workDefaultReqDto.setStartedAt(LocalDate.of(2023, 9, 1));
        workDefaultReqDto.setEndedAt(null);
        workDefaultReqDto.setIsCurrent(true);

        PortfolioDefaultReqDto portfolioDefaultReqDto = new PortfolioDefaultReqDto();
        portfolioDefaultReqDto.setPortfolioId(null);
        portfolioDefaultReqDto.setPortfolioName("깃허브");
        portfolioDefaultReqDto.setPortfolioUrl("github.com/gabojait");
        portfolioDefaultReqDto.setMedia(Media.LINK.name());

        ProfileDefaultReqDto reqDto = new ProfileDefaultReqDto();
        reqDto.setPosition(positionReqDto);
        reqDto.setSkills(List.of(skillDefaultReqDto));
        reqDto.setEducations(List.of(educationDefaultReqDto));
        reqDto.setWorks(List.of(workDefaultReqDto));
        reqDto.setPortfolios(List.of(portfolioDefaultReqDto));

        return reqDto;
    }

    private MockMultipartFile getValidMultipartFile() {
        return new MockMultipartFile(
                "image",
                "test.png",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                "test image".getBytes()
        );
    }

    private String getValidPosition() {
        return Position.NONE.name();
    }

    private String getValidProfileOrder() {
        return ProfileOrder.ACTIVE.name();
    }

    private Integer getValidPageFrom() {
        return 0;
    }

    private Integer getValidPageSize() {
        return 20;
    }
}