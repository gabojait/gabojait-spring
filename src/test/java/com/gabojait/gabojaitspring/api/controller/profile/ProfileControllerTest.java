package com.gabojait.gabojaitspring.api.controller.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabojait.gabojaitspring.api.dto.profile.request.*;
import com.gabojait.gabojaitspring.api.service.profile.ProfileService;
import com.gabojait.gabojaitspring.config.auth.CustomAuthenticationEntryPoint;
import com.gabojait.gabojaitspring.config.auth.JwtProvider;
import com.gabojait.gabojaitspring.domain.profile.Level;
import com.gabojait.gabojaitspring.domain.profile.Media;
import com.gabojait.gabojaitspring.domain.user.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

import static com.gabojait.gabojaitspring.common.constant.code.ErrorCode.*;
import static com.gabojait.gabojaitspring.common.constant.code.SuccessCode.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProfileControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    @MockBean private JwtProvider jwtProvider;
    @MockBean private ProfileService profileService;

    @Test
    @DisplayName("본인 조회를 하면 200을 반환한다.")
    void givenValid_whenFindMyself_thenReturn200() throws Exception {
        // given & when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/profile")
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(SELF_PROFILE_FOUND.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(SELF_PROFILE_FOUND.getMessage()));
    }

    @Test
    @DisplayName("프로필 단건 조회를 하면 200을 반환한다.")
    void givenValid_whenFindOther_thenReturn200() throws Exception {
        // given
        long userId = 1;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/{user-id}/profile", userId)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(PROFILE_FOUND.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PROFILE_FOUND.getMessage()));
    }

    @Test
    @DisplayName("회원 식별자가 양수가 아닐시 프로필 단건 조회를 하면 400을 반환한다.")
    void givenNonPositiveUserId_whenFindOther_thenReturn400() throws Exception {
        // given
        long userId = 0;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/{user-id}/profile", userId)
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
    @DisplayName("프로필 사진 업로드 또는 수정을 하면 200을 반환한다.")
    void givenValid_whenUploadProfileImage_thenReturn200() throws Exception {
        // given
        MultipartFile image = new MockMultipartFile("a", "a".getBytes());

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/image")
                        .param("image", image.toString())
                        .contentType(MULTIPART_FORM_DATA_VALUE)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(PROFILE_IMAGE_UPLOADED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PROFILE_IMAGE_UPLOADED.getMessage()));
    }

    @Test
    @DisplayName("프로필 사진을 삭제하면 200을 반환한다.")
    void givenValid_whenDeleteProfileImage_thenReturn200() throws Exception {
        // given & when
        ResultActions actions = mockMvc.perform(
                delete("/api/v1/user/image")
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(PROFILE_IMAGE_DELETED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PROFILE_IMAGE_DELETED.getMessage()));
    }

    @Test
    @DisplayName("팀 찾기 여부 수정을 하면 200을 반환한다.")
    void givenValid_whenUpdateIsSeekingTeam_thenReturn200() throws Exception {
        // given
        ProfileIsSeekRequest request = createValidProfileIsSeekRequest();

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/user/seeking-team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(PROFILE_SEEKING_TEAM_UPDATED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PROFILE_SEEKING_TEAM_UPDATED.getMessage()));
    }

    @Test
    @DisplayName("팀 찾기 여부 미입력시 팀 찾기 여부 수정을 하면 400을 반환한다.")
    void givenBlankIsSeekingTeam_whenUpdateIsSeekingTeam_thenReturn400() throws Exception {
        // given
        ProfileIsSeekRequest request = createValidProfileIsSeekRequest();
        request.setIsSeekingTeam(null);

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/user/seeking-team")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(IS_SEEKING_TEAM_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(IS_SEEKING_TEAM_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("자기소개 업데이트를 하면 200을 반환한다.")
    void givenValid_whenUpdateDescription_thenReturn200() throws Exception {
        // given
        ProfileDescriptionRequest request = createValidProfileDescriptionRequest();

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/user/description")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(PROFILE_DESCRIPTION_UPDATED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PROFILE_DESCRIPTION_UPDATED.getMessage()));
    }

    @Test
    @DisplayName("자기소개 200자 초과일시 자기소개 업데이트를 하면 400을 반환한다.")
    void givenMoreThan200SizeProfileDescription_whenUpdateDescription_thenReturn400() throws Exception {
        // given
        ProfileDescriptionRequest request = createValidProfileDescriptionRequest();
        request.setProfileDescription("가".repeat(200));

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/user/description")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(PROFILE_DESCRIPTION_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PROFILE_DESCRIPTION_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("프로필 업데이트를 하면 200을 반환한다.")
    void givenValid_whenUpdateProfile_thenReturn200() throws Exception {
        // given
        ProfileUpdateRequest request = createValidProfileUpdateRequest();

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/profile")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(PROFILE_UPDATED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PROFILE_UPDATED.getMessage()));
    }

    @Test
    @DisplayName("잘못된 포지션 타입일시 프로필 업데이트를 하면 400을 반환한다.")
    void givenFormatPosition_whenUpdateProfile_thenReturn400() throws Exception {
        // given
        ProfileUpdateRequest request = createValidProfileUpdateRequest();
        request.setPosition("OPERATOR");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/profile")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(POSITION_TYPE_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(POSITION_TYPE_INVALID.getMessage()));
    }

    @Test
    @DisplayName("기술에 기술명 20자 초과일시 프로필 업데이트를 하면 400을 반환한다.")
    void givenSkillGreaterThan20SizeSkillName_whenUpdateProfile_thenReturn400() throws Exception {
        // given
        ProfileUpdateRequest request = createValidProfileUpdateRequest();
        request.getSkills().get(0).setSkillName("가".repeat(21));

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/profile")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(SKILL_NAME_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(SKILL_NAME_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("기술에 경험 여부 미입력시 프로필 업데이트를 하면 400을 반환한다.")
    void givenSkillBlankIsExperienced_whenUpdateProfile_thenReturn400() throws Exception {
        // given
        ProfileUpdateRequest request = createValidProfileUpdateRequest();
        request.getSkills().get(0).setIsExperienced(null);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/profile")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(IS_EXPERIENCED_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(IS_EXPERIENCED_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("기술에 올바르지 않은 레벨로 프로필 업데이트를 하면 400을 반환한다.")
    void givenSkillFormatLevel_whenUpdateProfile_thenReturn400() throws Exception {
        // given
        ProfileUpdateRequest request = createValidProfileUpdateRequest();
        request.getSkills().get(0).setLevel("GOOD");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/profile")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(LEVEL_TYPE_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(LEVEL_TYPE_INVALID.getMessage()));
    }

    @Test
    @DisplayName("학력에 학교명 3자 미만일시 프로필 업데이트를 하면 400을 반환한다.")
    void givenEducationLessThan3SizeInstitutionName_whenUpdateProfile_thenReturn400() throws Exception {
        // given
        ProfileUpdateRequest request = createValidProfileUpdateRequest();
        request.getEducations().get(0).setInstitutionName("대학");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/profile")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(INSTITUTION_NAME_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(INSTITUTION_NAME_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("학력에 학교명 3자 미만일시 프로필 업데이트를 하면 400을 반환한다.")
    void givenEducationGreaterThan20SizeInstitutionName_whenUpdateProfile_thenReturn400() throws Exception {
        // given
        ProfileUpdateRequest request = createValidProfileUpdateRequest();
        request.getEducations().get(0).setInstitutionName("대".repeat(21));

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/profile")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(INSTITUTION_NAME_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(INSTITUTION_NAME_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("학력에 시작일 미입력시 프로필 업데이트를 하면 400을 반환한다.")
    void givenEducationBlankStartedAt_whenUpdateProfile_thenReturn400() throws Exception {
        // given
        ProfileUpdateRequest request = createValidProfileUpdateRequest();
        request.getEducations().get(0).setStartedAt(null);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/profile")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(STARTED_AT_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(STARTED_AT_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("학력에 현재 여부 미입력시 프로필 업데이트를 하면 400을 반환한다.")
    void givenEducationBlankIsCurrent_whenUpdateProfile_thenReturn400() throws Exception {
        // given
        ProfileUpdateRequest request = createValidProfileUpdateRequest();
        request.getEducations().get(0).setIsCurrent(null);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/profile")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(IS_CURRENT_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(IS_CURRENT_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("포트폴리오에 포트폴리오명이 1자 미만일시 프로필 업데이트를 하면 400을 반환한다.")
    void givenPortfolioLessThan1SizePortfolioName_whenUpdateProfile_thenReturn400() throws Exception {
        // given
        ProfileUpdateRequest request = createValidProfileUpdateRequest();
        request.getPortfolios().get(0).setPortfolioName("");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/profile")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(PORTFOLIO_NAME_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PORTFOLIO_NAME_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("포트폴리오에 포트폴리오명이 10자 초과일시 프로필 업데이트를 하면 400을 반환한다.")
    void givenPortfolioGreaterThan10SizePortfolioName_whenUpdateProfile_thenReturn400() throws Exception {
        // given
        ProfileUpdateRequest request = createValidProfileUpdateRequest();
        request.getPortfolios().get(0).setPortfolioName("a".repeat(11));

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/profile")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(PORTFOLIO_NAME_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PORTFOLIO_NAME_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("포트폴리오에 포트폴리오 URL이 10자 미만일시 프로필 업데이트를 하면 400을 반환한다.")
    void givenPortfolioLessThan10SizePortfolioUrl_whenUpdateProfile_thenReturn400() throws Exception {
        // given
        ProfileUpdateRequest request = createValidProfileUpdateRequest();
        request.getPortfolios().get(0).setPortfolioUrl("https://a");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/profile")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(PORTFOLIO_URL_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PORTFOLIO_URL_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("포트폴리오에 포트폴리오 URL이 1000자 초과일시 프로필 업데이트를 하면 400을 반환한다.")
    void givenPortfolioGreaterThan1000SizePortfolioUrl_whenUpdateProfile_thenReturn400() throws Exception {
        // given
        ProfileUpdateRequest request = createValidProfileUpdateRequest();
        request.getPortfolios().get(0).setPortfolioUrl("https://" + "a".repeat(993));

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/profile")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(PORTFOLIO_URL_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PORTFOLIO_URL_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("포트폴리오에 포트폴리오 URL이 잘못된 포맷일시 프로필 업데이트를 하면 400을 반환한다.")
    void givenPortfolioFormatPortfolioUrl_whenUpdateProfile_thenReturn400() throws Exception {
        // given
        ProfileUpdateRequest request = createValidProfileUpdateRequest();
        request.getPortfolios().get(0).setPortfolioUrl("a".repeat(100));

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/profile")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(PORTFOLIO_URL_FORMAT_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PORTFOLIO_URL_FORMAT_INVALID.getMessage()));
    }

    @Test
    @DisplayName("포트폴리오에 잘못된 미디어 입력시 프로필 업데이트를 하면 400을 반환한다.")
    void givenPortfolioFormatMedia_whenUpdateProfile_thenReturn400() throws Exception {
        // given
        ProfileUpdateRequest request = createValidProfileUpdateRequest();
        request.getPortfolios().get(0).setMedia("WRONG");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/profile")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(MEDIA_TYPE_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(MEDIA_TYPE_INVALID.getMessage()));
    }

    @Test
    @DisplayName("경력에 기관명이 1자 미만일시 프로필 업데이트를 하면 400을 반환한다.")
    void givenWorkLessThan1SizeCorporationName_whenUpdateProfile_thenReturn400() throws Exception {
        // given
        ProfileUpdateRequest request = createValidProfileUpdateRequest();
        request.getWorks().get(0).setCorporationName("");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/profile")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(CORPORATION_NAME_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(CORPORATION_NAME_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("경력에 기관명이 20자 초과일시 프로필 업데이트를 하면 400을 반환한다.")
    void givenWorkGreaterThan20SizeCorporationName_whenUpdateProfile_thenReturn400() throws Exception {
        // given
        ProfileUpdateRequest request = createValidProfileUpdateRequest();
        request.getWorks().get(0).setCorporationName("가".repeat(21));

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/profile")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(CORPORATION_NAME_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(CORPORATION_NAME_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("파일 포트폴리오를 하면 200을 반환한다.")
    void givenValid_whenUploadPortfolioFile_thenReturn200() throws Exception {
        // given
        MultipartFile file = new MockMultipartFile("a", "a".getBytes());

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/portfolio/file")
                        .param("image", file.toString())
                        .contentType(MULTIPART_FORM_DATA_VALUE)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.responseCode")
                        .value(PORTFOLIO_FILE_UPLOADED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PORTFOLIO_FILE_UPLOADED.getMessage()));
    }

    @Test
    @DisplayName("팀을 찾는 회원 페이징 조회를 하면 200을 반환한다.")
    void givenValid_whenFindUsersLookingForTeam_theReturn200() throws Exception {
        // given & when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/seeking-team")
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(USERS_SEEKING_TEAM_FOUND.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(USERS_SEEKING_TEAM_FOUND.getMessage()));
    }

    @Test
    @DisplayName("올바르지 않은 포지션으로 팀을 찾는 회원 페이징 조회를 하면 400을 반환한다.")
    void givenFormatPosition_whenFindUsersLookingForTeam_theReturn400() throws Exception {
        // given
        String position = "WRITER";

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/seeking-team")
                        .param("position", position)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(POSITION_TYPE_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(POSITION_TYPE_INVALID.getMessage()));
    }

    @Test
    @DisplayName("양수가 아닌 페이지 시작점으로 팀을 찾는 회원 페이징 조회를 하면 400을 반환한다.")
    void givenNonPositivePageFrom_whenFindUsersLookingForTeam_theReturn400() throws Exception {
        // given
        Long pageFrom = 0L;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/seeking-team")
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
    @DisplayName("양수가 아닌 페이지 크기로 팀을 찾는 회원 페이징 조회를 하면 400을 반환한다.")
    void givenNonPositivePageSize_whenFindUsersLookingForTeam_theReturn400() throws Exception {
        // given
        Long pageSize = 0L;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/seeking-team")
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
    @DisplayName("100 초과의 페이지 크기로 팀을 찾는 회원 페이징 조회를 하면 400을 반환한다.")
    void givenGreaterThan100PageSize_whenFindUsersLookingForTeam_theReturn400() throws Exception {
        // given
        Long pageSize = 101L;

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/seeking-team")
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

    private ProfileUpdateRequest createValidProfileUpdateRequest() {
        return ProfileUpdateRequest.builder()
                .position(Position.BACKEND.toString())
                .educations(List.of(createValidEducationUpdateRequest()))
                .portfolios(List.of(createValidPortfolioUpdateRequest()))
                .skills(List.of(createValidSkillUpdateRequest()))
                .works(List.of(createValidWorkUpdateRequest()))
                .build();
    }

    private EducationUpdateRequest createValidEducationUpdateRequest() {
        return EducationUpdateRequest.builder()
                .institutionName("가보자잇대")
                .startedAt(LocalDate.of(2000, 1, 1))
                .endedAt(LocalDate.of(2001, 1, 1))
                .isCurrent(false)
                .build();
    }

    private PortfolioUpdateRequest createValidPortfolioUpdateRequest() {
        return PortfolioUpdateRequest.builder()
                .portfolioName("깃허브")
                .portfolioUrl("https://github.com/gabojait")
                .media(Media.LINK.toString())
                .build();
    }

    private SkillUpdateRequest createValidSkillUpdateRequest() {
        return SkillUpdateRequest.builder()
                .skillName("스프링")
                .isExperienced(true)
                .level(Level.HIGH.toString())
                .build();
    }

    private WorkUpdateRequest createValidWorkUpdateRequest() {
        return WorkUpdateRequest.builder()
                .corporationName("가보자잇사")
                .workDescription("백엔드 개발")
                .startedAt(LocalDate.of(2000, 1, 1))
                .endedAt(LocalDate.of(2001, 1, 1))
                .isCurrent(false)
                .build();
    }

    private ProfileDescriptionRequest createValidProfileDescriptionRequest() {
        return ProfileDescriptionRequest.builder()
                .profileDescription("안녕하세요.")
                .build();
    }

    private ProfileIsSeekRequest createValidProfileIsSeekRequest() {
        return ProfileIsSeekRequest.builder()
                .isSeekingTeam(false)
                .build();
    }

}