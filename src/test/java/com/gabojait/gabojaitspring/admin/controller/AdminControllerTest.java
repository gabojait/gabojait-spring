package com.gabojait.gabojaitspring.admin.controller;

import com.gabojait.gabojaitspring.admin.dto.req.AdminLoginReqDto;
import com.gabojait.gabojaitspring.admin.dto.req.AdminRegisterDecideReqDto;
import com.gabojait.gabojaitspring.admin.dto.req.AdminRegisterReqDto;
import com.gabojait.gabojaitspring.admin.service.AdminService;
import com.gabojait.gabojaitspring.admin.service.MasterService;
import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.WebMvc;
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

import java.time.LocalDate;
import java.util.List;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;
import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest extends WebMvc {

    @MockBean
    private AdminService adminService;

    @MockBean
    private MasterService masterService;

    @MockBean
    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        doReturn(1L)
                .when(this.jwtProvider)
                .getId(any());

        User adminTester = User.testOnlyBuilder()
                .id(1L)
                .role(Role.ADMIN)
                .build();

        User userTester = User.testOnlyBuilder()
                .id(2L)
                .role(Role.USER)
                .build();

        Page<User> admins = new PageImpl<>(List.of(adminTester));

        doReturn(adminTester)
                .when(this.adminService)
                .login(any());

        doReturn(admins)
                .when(this.masterService)
                .findManyUnregisteredAdmin(any(), any());

        doReturn(userTester)
                .when(this.adminService)
                .findOneUser(any());
    }

    @Test
    @DisplayName("관리자 가입 | 올바른 요청시 | 201반환")
    void register_givenValidReq_return201() throws Exception {
        // given
        AdminRegisterReqDto reqDto = getValidAdminRegisterReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(ADMIN_REGISTERED.getHttpStatus().value());
        assertThat(response).contains(ADMIN_REGISTERED.name());
    }

    @Test
    @DisplayName("관리자 가입 | 잘못된 아이디 길이시 | 400반환")
    void register_givenAdminNameLengthInvalid_return400() throws Exception {
        // given
        AdminRegisterReqDto reqDto = getValidAdminRegisterReqDto();
        reqDto.setAdminName("abc1");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(ADMIN_NAME_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(ADMIN_NAME_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("관리자 가입 | 잘못된 아이디 포맷시 | 400반환")
    void register_givenAdminNameFormatInvalid_return400() throws Exception {
        // given
        AdminRegisterReqDto reqDto = getValidAdminRegisterReqDto();
        reqDto.setAdminName("tester123");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(ADMIN_NAME_FORMAT_INVALID.getHttpStatus().value());
        assertThat(response).contains(ADMIN_NAME_FORMAT_INVALID.name());
    }

    @Test
    @DisplayName("관리자 가입 | 비밀번호 미입력시 | 400반환")
    void register_givenPasswordFieldRequired_return400() throws Exception {
        // given
        AdminRegisterReqDto reqDto = getValidAdminRegisterReqDto();
        reqDto.setPassword("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PASSWORD_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(PASSWORD_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("관리자 가입 | 잘못된 비밀번호 길이시 | 400반환")
    void register_givenPasswordLengthInvalid_return400() throws Exception {
        // given
        AdminRegisterReqDto reqDto = getValidAdminRegisterReqDto();
        reqDto.setPassword("pass1!");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PASSWORD_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(PASSWORD_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("관리자 가입 | 잘못된 비밀번호 포맷시 | 400반환")
    void register_givenPasswordFormatInvalid_return400() throws Exception {
        // given
        AdminRegisterReqDto reqDto = getValidAdminRegisterReqDto();
        reqDto.setPassword("password12345");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PASSWORD_FORMAT_INVALID.getHttpStatus().value());
        assertThat(response).contains(PASSWORD_FORMAT_INVALID.name());
    }

    @Test
    @DisplayName("관리자 가입 | 비밀번호 재입력 미입력시 | 400반환")
    void register_givenPasswordReEnteredFieldRequired_return400() throws Exception {
        // given
        AdminRegisterReqDto reqDto = getValidAdminRegisterReqDto();
        reqDto.setPasswordReEntered("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PASSWORD_RE_ENTERED_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(PASSWORD_RE_ENTERED_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("관리자 가입 | 실명 미입력시 | 400반환")
    void register_givenLegalNameFieldRequired_return400() throws Exception {
        // given
        AdminRegisterReqDto reqDto = getValidAdminRegisterReqDto();
        reqDto.setLegalName("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(LEGAL_NAME_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(LEGAL_NAME_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("관리자 가입 | 잘못된 실명 길이시 | 400반환")
    void register_givenLegalNameLengthInvalid_return400() throws Exception {
        // given
        AdminRegisterReqDto reqDto = getValidAdminRegisterReqDto();
        reqDto.setLegalName("김가보자이잇");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(LEGAL_NAME_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(LEGAL_NAME_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("관리자 가입 | 잘못된 실명 포맷시 | 400반환")
    void register_givenLegalNameFormatInvalid_return400() throws Exception {
        // given
        AdminRegisterReqDto reqDto = getValidAdminRegisterReqDto();
        reqDto.setLegalName("Smith");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(LEGAL_NAME_FORMAT_INVALID.getHttpStatus().value());
        assertThat(response).contains(LEGAL_NAME_FORMAT_INVALID.name());
    }

    @Test
    @DisplayName("관리자 가입 | 설별 미입력시 | 400반환")
    void register_givenGenderFieldRequired_return400() throws Exception {
        // given
        AdminRegisterReqDto reqDto = getValidAdminRegisterReqDto();
        reqDto.setGender("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(GENDER_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(GENDER_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("관리자 가입 | 잘못된 설별 타입시 | 400반환")
    void register_givenGenderTypeInvalid_return400() throws Exception {
        // given
        AdminRegisterReqDto reqDto = getValidAdminRegisterReqDto();
        reqDto.setGender("boy");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(GENDER_TYPE_INVALID.getHttpStatus().value());
        assertThat(response).contains(GENDER_TYPE_INVALID.name());
    }

    @Test
    @DisplayName("관리자 가입 | 생년월일 미입력시 | 400반환")
    void register_givenBirthdateFieldRequired_return400() throws Exception {
        // given
        AdminRegisterReqDto reqDto = getValidAdminRegisterReqDto();
        reqDto.setBirthdate(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(BIRTHDATE_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(BIRTHDATE_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("관리자 로그인 | 올바른 요청시 | 200반환")
    void login_givenValidReq_return200() throws Exception {
        // given
        AdminLoginReqDto reqDto = getValidAdminLoginReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(ADMIN_LOGIN.getHttpStatus().value());
        assertThat(response).contains(ADMIN_LOGIN.name());
    }

    @Test
    @DisplayName("관리자 로그인 | 아이디 미입력시 | 400반환")
    void login_givenUsernameFieldRequired_return400() throws Exception {
        // given
        AdminLoginReqDto reqDto = getValidAdminLoginReqDto();
        reqDto.setUsername("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USERNAME_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(USERNAME_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("관리자 로그인 | 비밀번호 미입력시 | 400반환")
    void login_givenPasswordFieldRequired_return400() throws Exception {
        // given
        AdminLoginReqDto reqDto = getValidAdminLoginReqDto();
        reqDto.setPassword("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PASSWORD_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(PASSWORD_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("관리자 가입 대기자 다건 조회 | 올바른 요청시 | 200반환")
    void findUnregisteredAdmin_givenValidReq_return200() throws Exception {
        // given
        Integer pageFrom = getValidPageFrom();
        Integer pageSize = getValidPageSize();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/admin")
                        .param("page-from", pageFrom.toString())
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(UNREGISTERED_ADMIN_FOUND.getHttpStatus().value());
        assertThat(response).contains(UNREGISTERED_ADMIN_FOUND.name());
    }

    @Test
    @DisplayName("관리자 가입 대기자 다건 조회 | 페이지 시작점 미입력시 | 400반환")
    void findUnregisteredAdmin_givenPageFromFieldRequired_return400() throws Exception {
        // given
        Integer pageSize = getValidPageSize();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/admin")
                        .param("page-from", "")
                        .param("page-size", pageSize.toString()))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PAGE_FROM_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(PAGE_FROM_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("관리자 가입 대기자 다건 조회 | 페이지 시작점이 양수 또는 0 아닐시 | 400반환")
    void findUnregisteredAdmin_givenPageFromPositiveOrZeroOnly_return400() throws Exception {
        // given
        Integer pageFrom = -1;
        Integer pageSize = getValidPageSize();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/admin")
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
    @DisplayName("관리자 가입 대기자 다건 조회 | 페이지 사이즈가 양수 아닐시 | 400반환")
    void findUnregisteredAdmin_givenPageSizePositiveOnly_return400() throws Exception {
        // given
        Integer pageFrom = getValidPageFrom();
        Integer pageSize = 0;

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/admin")
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
    @DisplayName("관리자 가입 결정 | 올바른 요청시 | 200반환")
    void decideAdminRegistration_givenValidReq_return200() throws Exception {
        // given
        AdminRegisterDecideReqDto reqDto = getValidAdminRegisterDecideReqDto();
        String request = mapToJson(reqDto);
        Long adminId = getValidId();

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/admin/{admin-id}/decide", adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(ADMIN_REGISTER_DECIDED.getHttpStatus().value());
        assertThat(response).contains(ADMIN_REGISTER_DECIDED.name());
    }

    @Test
    @DisplayName("관리자 가입 결정 | 관리자 식별자 미입력시 | 400반환")
    void decideAdminRegistration_givenAdminIdFieldRequired_return400() throws Exception {
        // given TODO 식별자 미입력
        AdminRegisterDecideReqDto reqDto = getValidAdminRegisterDecideReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/admin/{admin-id}/decide", "")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

//        assertThat(status).isEqualTo(ADMIN_ID_FIELD_REQUIRED.getHttpStatus().value());
//        assertThat(response).contains(ADMIN_ID_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("관리자 가입 결정 | 관리자 식별자가 양수 아닐시 | 400반환")
    void decideAdminRegistration_givenAdminIdPositiveOnly_return400() throws Exception {
        // given TODO 식별자 미입력
        AdminRegisterDecideReqDto reqDto = getValidAdminRegisterDecideReqDto();
        String request = mapToJson(reqDto);
        Long adminId = 0L;

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/admin/{admin-id}/decide", adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(ADMIN_ID_POSITIVE_ONLY.getHttpStatus().value());
        assertThat(response).contains(ADMIN_ID_POSITIVE_ONLY.name());
    }

    @Test
    @DisplayName("관리자 가입 결정 | 승인 여부 미입력시 | 400반환")
    void decideAdminRegistration_givenIsApprovedFieldRequired_return400() throws Exception {
        // given
        AdminRegisterDecideReqDto reqDto = getValidAdminRegisterDecideReqDto();
        reqDto.setIsApproved(null);
        String request = mapToJson(reqDto);
        Long adminId = getValidId();

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/admin/{admin-id}/decide", adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(IS_APPROVED_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(IS_APPROVED_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("회원 단건 조회 | 올바른 요청시 | 200 반환")
    void findOneUser_givenValidReq_return200() throws Exception {
        // given
        Long userId = getValidId();

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/admin/user/{user-id}", userId))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USER_FOUND.getHttpStatus().value());
        assertThat(response).contains(USER_FOUND.name());
    }

    @Test
    @DisplayName("회원 단건 조회 | 회원 식별자 미입력시 | 400 반환")
    void findOneUser_givenUserIdFieldRequired_return400() throws Exception {
        // given TODO 식별자 미입력
        Long userId = null;

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/admin/user/{user-id}", userId))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

//        assertThat(status).isEqualTo(USER_ID_FIELD_REQUIRED.getHttpStatus().value());
//        assertThat(response).contains(USER_ID_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("회원 단건 조회 | 회원 식별자가 양수 아닐시 | 400 반환")
    void findOneUser_givenUserIdPositiveOnly_return400() throws Exception {
        // given
        Long userId = 0L;

        // when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/admin/user/{user-id}", userId))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USER_ID_POSITIVE_ONLY.getHttpStatus().value());
        assertThat(response).contains(USER_ID_POSITIVE_ONLY.name());
    }

    private AdminRegisterReqDto getValidAdminRegisterReqDto() {
        AdminRegisterReqDto reqDto = new AdminRegisterReqDto();
        reqDto.setAdminName("test_admin");
        reqDto.setPassword("password1!");
        reqDto.setPasswordReEntered("password1!");
        reqDto.setLegalName("김가보자잇");
        reqDto.setGender("male");
        reqDto.setBirthdate(LocalDate.of(1997, 2, 11));
        return reqDto;
    }

    private AdminLoginReqDto getValidAdminLoginReqDto() {
        AdminLoginReqDto reqDto = new AdminLoginReqDto();
        reqDto.setUsername("test_admin");
        reqDto.setPassword("password1!");
        return reqDto;
    }

    private Integer getValidPageFrom() {
        return 0;
    }

    private Integer getValidPageSize() {
        return 20;
    }

    private Long getValidId() {
        return 1L;
    }

    private AdminRegisterDecideReqDto getValidAdminRegisterDecideReqDto() {
        AdminRegisterDecideReqDto reqDto = new AdminRegisterDecideReqDto();
        reqDto.setIsApproved(true);
        return reqDto;
    }
}