package com.gabojait.gabojaitspring.user.controller;

import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.ControllerTestSetup;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.dto.req.*;
import com.gabojait.gabojaitspring.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;
import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest extends ControllerTestSetup {

    @MockBean
    private UserService userService;

    @MockBean
    private JwtProvider jwtProvider;

    @BeforeEach
    void setup() {
        User tester = User.testOnlyBuilder()
                .id(1L)
                .build();

        doReturn(tester)
                .when(this.jwtProvider)
                .authorizeUserAccessJwt(any());

        doReturn(tester)
                .when(this.jwtProvider)
                .authorizeUserRefreshJwt(any());

        doReturn(tester)
                .when(this.userService)
                .register(any());

        doReturn(tester)
                .when(this.userService)
                .login(any());
    }

    @Test
    @DisplayName("아이디 중복여부 확인 | 올바른 요청시 | 200응답")
    void duplicateUsername_givenValidReq_return200() throws Exception {
        // given & when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/username")
                        .param("username", "test1"))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USERNAME_AVAILABLE.getHttpStatus().value());
        assertThat(response).contains(USERNAME_AVAILABLE.name());
    }

    @Test
    @DisplayName("아이디 중복여부 확인 | 아이디 미입력시 | 400응답")
    void duplicateUsername_givenUsernameFieldRequired_return400() throws Exception {
        // given & when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/username")
                        .param("username", ""))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USERNAME_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(USERNAME_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("아이디 중복여부 확인 | 잘못된 아이디 길이시 | 400응답")
    void duplicateUsername_givenUsernameLengthInvalid_return400() throws Exception {
        // given & when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/username")
                        .param("username", "test"))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USERNAME_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(USERNAME_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("아이디 중복여부 확인 | 잘못된 아이디 포맷시 | 400응답")
    void duplicateUsername_givenUsernameFormatInvalid_return400() throws Exception {
        // given & when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/username")
                        .param("username", "테스트아이"))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USERNAME_FORMAT_INVALID.getHttpStatus().value());
        assertThat(response).contains(USERNAME_FORMAT_INVALID.name());
    }

    @Test
    @DisplayName("닉네임 중복여부 확인 | 올바른 요청시 | 200응답")
    void duplicateNickname_givenValidReq_return200() throws Exception {
        // given & when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/nickname")
                        .param("nickname", "닉네임"))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(NICKNAME_AVAILABLE.getHttpStatus().value());
        assertThat(response).contains(NICKNAME_AVAILABLE.name());
    }

    @Test
    @DisplayName("닉네임 중복여부 확인 | 닉네임 미입력시 | 400응답")
    void duplicateNickname_givenNicknameFieldRequired_return400() throws Exception {
        // given & when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/nickname")
                        .param("nickname", ""))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(NICKNAME_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(NICKNAME_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("닉네임 중복여부 확인 | 잘못된 닉네임 길이시 | 400응답")
    void duplicateNickname_givenNicknameLengthInvalid_return400() throws Exception {
        // given & when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/nickname")
                        .param("nickname", "테"))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(NICKNAME_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(NICKNAME_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("닉네임 중복여부 확인 | 잘못된 닉네임 포맷시 | 400응답")
    void duplicateNickname_givenNicknameFormatInvalid_return400() throws Exception {
        // given & when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/nickname")
                        .param("nickname", "test"))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(NICKNAME_FORMAT_INVALID.getHttpStatus().value());
        assertThat(response).contains(NICKNAME_FORMAT_INVALID.name());
    }

    @Test
    @DisplayName("회원 가입 | 올바른 요청시 | 201응답")
    void register_givenValidReq_return201() throws Exception {
        // given
        UserRegisterReqDto reqDto = getValidUserRegisterReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USER_REGISTERED.getHttpStatus().value());
        assertThat(response).contains(USER_REGISTERED.name());
    }

    @Test
    @DisplayName("회원 가입 | 아이디 미입력시 | 400응답")
    void register_givenUsernameFieldRequired_return400() throws Exception {
        // given
        UserRegisterReqDto reqDto = getValidUserRegisterReqDto();
        reqDto.setUsername("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
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
    @DisplayName("회원 가입 | 잘못된 아이디 길이시 | 400응답")
    void register_givenUsernameLengthInvalid_return400() throws Exception {
        // given
        UserRegisterReqDto reqDto = getValidUserRegisterReqDto();
        reqDto.setUsername("test");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USERNAME_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(USERNAME_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("회원 가입 | 잘못된 아이디 포맷시 | 400응답")
    void register_givenUsernameFormatInvalid_return400() throws Exception {
        // given
        UserRegisterReqDto reqDto = getValidUserRegisterReqDto();
        reqDto.setUsername("tester가");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USERNAME_FORMAT_INVALID.getHttpStatus().value());
        assertThat(response).contains(USERNAME_FORMAT_INVALID.name());
    }

    @Test
    @DisplayName("회원 가입 | 비밀번호 미입력시 | 400응답")
    void register_givenPasswordFieldRequired_return400() throws Exception {
        // given
        UserRegisterReqDto reqDto = getValidUserRegisterReqDto();
        reqDto.setPassword("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
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
    @DisplayName("회원 가입 | 잘못된 비밀번호 길이시 | 400응답")
    void register_givenPasswordLengthInvalid_return400() throws Exception {
        // given
        UserRegisterReqDto reqDto = getValidUserRegisterReqDto();
        reqDto.setPassword("pass12!");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
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
    @DisplayName("회원 가입 | 잘못된 비밀번호 포맷시 | 400응답")
    void register_givenPasswordFormatInvalid_return400() throws Exception {
        // given
        UserRegisterReqDto reqDto = getValidUserRegisterReqDto();
        reqDto.setPassword("password123");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
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
    @DisplayName("회원 가입 | 비밀번호 재입력 미입력시 | 400응답")
    void register_givenPasswordReEnterFieldRequired_return400() throws Exception {
        // given
        UserRegisterReqDto requestDto = getValidUserRegisterReqDto();
        requestDto.setPasswordReEntered("");
        String request = mapToJson(requestDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
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
    @DisplayName("회원 가입 | 닉네임 미입력시 | 400응답")
    void register_givenNicknameFieldRequired_return400() throws Exception {
        // given
        UserRegisterReqDto reqDto = getValidUserRegisterReqDto();
        reqDto.setNickname("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(NICKNAME_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(NICKNAME_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("회원 가입_잘못된 닉네임 길이시_400응답")
    void register_givenNicknameLengthInvalid_return400() throws Exception {
        // given
        UserRegisterReqDto reqDto = getValidUserRegisterReqDto();
        reqDto.setNickname("테스터닉네임입니다");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(NICKNAME_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(NICKNAME_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("회원 가입 | 잘못된 닉네임 포맷시 | 400응답")
    void register_givenNicknameFormatInvalid_return400() throws Exception {
        // given
        UserRegisterReqDto reqDto = getValidUserRegisterReqDto();
        reqDto.setNickname("tester");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(NICKNAME_FORMAT_INVALID.getHttpStatus().value());
        assertThat(response).contains(NICKNAME_FORMAT_INVALID.name());
    }

    @Test
    @DisplayName("회원 가입 | 성별 미입력시 | 400응답")
    void register_givenGenderFieldRequired_return400() throws Exception {
        // given
        UserRegisterReqDto reqDto = getValidUserRegisterReqDto();
        reqDto.setGender("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
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
    @DisplayName("회원 가입 | 잘못된 성별 타입시 | 400응답")
    void register_givenGenderTypeInvalid_return400() throws Exception {
        // given
        UserRegisterReqDto reqDto = getValidUserRegisterReqDto();
        reqDto.setGender("boy");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
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
    @DisplayName("회원 가입 | 이메일 미입력시 | 400응답")
    void register_givenEmailFieldRequired_return400() throws Exception {
        // given
        UserRegisterReqDto reqDto = getValidUserRegisterReqDto();
        reqDto.setEmail("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EMAIL_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(EMAIL_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("회원 가입 | 잘못된 이메일 포맷시 | 400응답")
    void register_givenEmailFormatInvalid_return400() throws Exception {
        // given
        UserRegisterReqDto reqDto = getValidUserRegisterReqDto();
        reqDto.setEmail("testgabojait.com");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EMAIL_FORMAT_INVALID.getHttpStatus().value());
        assertThat(response).contains(EMAIL_FORMAT_INVALID.name());
    }

    @Test
    @DisplayName("로그인 | 올바른 요청시 | 200응답")
    void login_givenValidReq_return200() throws Exception {
        // given
        UserLoginReqDto reqDto = getValidUserLoginReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USER_LOGIN.getHttpStatus().value());
        assertThat(response).contains(USER_LOGIN.name());
    }

    @Test
    @DisplayName("로그인 | 아이디 미입력시 | 400응답")
    void login_givenUsernameFieldRequired_return400() throws Exception {
        // given
        UserLoginReqDto reqDto = getValidUserLoginReqDto();
        reqDto.setUsername("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/login")
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
    @DisplayName("로그인 | 비밀번호 미입력시 | 400응답")
    void login_givenPasswordFieldRequired_return400() throws Exception {
        // given
        UserLoginReqDto reqDto = getValidUserLoginReqDto();
        reqDto.setPassword("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/login")
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
    @DisplayName("로그아웃 | 올바른 요청시 | 200응답")
    void logout_givenValidReq_return200() throws Exception {
        // given
        UserLogoutReqDto reqDto = getValidUserLogoutReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USER_LOGOUT.getHttpStatus().value());
        assertThat(response).contains(USER_LOGOUT.name());
    }

    @Test
    @DisplayName("본인 조회 | 올바른 요청시 | 200응답")
    void findMyself_givenValidReq_return200() throws Exception {
        // given & when
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user"))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(SELF_USER_FOUND.getHttpStatus().value());
        assertThat(response).contains(SELF_USER_FOUND.name());
    }

    @Test
    @DisplayName("토큰 재발급 | 올바른 요청시 | 200응답")
    void renewToken_givenValidReq_return200() throws Exception {
        // given
        UserRenewTokenReqDto reqDto = getValidUserRenewTokenReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(TOKEN_RENEWED.getHttpStatus().value());
        assertThat(response).contains(TOKEN_RENEWED.name());
    }

    @Test
    @DisplayName("아이디 찾기 | 올바른 요청시 | 200응답")
    void forgotUsername_givenValidReq_return200() throws Exception {
        // given
        UserFindUsernameReqDto reqDto = getValidUserFindUsernameReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USERNAME_EMAIL_SENT.getHttpStatus().value());
        assertThat(response).contains(USERNAME_EMAIL_SENT.name());
    }

    @Test
    @DisplayName("아이디 찾기 | 이메일 미입력시 | 400응답")
    void forgotUsername_givenEmailFieldRequired_return400() throws Exception {
        // given
        UserFindUsernameReqDto reqDto = getValidUserFindUsernameReqDto();
        reqDto.setEmail("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EMAIL_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(EMAIL_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("아이디 찾기 | 잘못된 이메일 포맷시 | 200응답")
    void forgotUsername_givenEmailFormatInvalid_return400() throws Exception {
        // given
        UserFindUsernameReqDto reqDto = getValidUserFindUsernameReqDto();
        reqDto.setEmail("testergabojait.com");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EMAIL_FORMAT_INVALID.getHttpStatus().value());
        assertThat(response).contains(EMAIL_FORMAT_INVALID.name());
    }

    @Test
    @DisplayName("비밀번호 찾기 | 올바른 요청시 | 200응답")
    void forgotPassword_givenValidReq_return400() throws Exception {
        // given
        UserFindPasswordReqDto reqDto = getValidUserFindPasswordReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PASSWORD_EMAIL_SENT.getHttpStatus().value());
        assertThat(response).contains(PASSWORD_EMAIL_SENT.name());
    }

    @Test
    @DisplayName("비밀번호 찾기 | 이메일 미입력시 | 400응답")
    void forgotPassword_givenEmailFieldRequired_return400() throws Exception {
        // given
        UserFindPasswordReqDto reqDto = getValidUserFindPasswordReqDto();
        reqDto.setEmail("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EMAIL_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(EMAIL_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("비밀번호 찾기 | 아이디 미입력시 | 400응답")
    void forgotPassword_givenUsernameFieldRequired_return400() throws Exception {
        // given
        UserFindPasswordReqDto reqDto = getValidUserFindPasswordReqDto();
        reqDto.setUsername("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/password")
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
    @DisplayName("비밀번호 찾기 | 잘못된 이메일 포맷시 | 400응답")
    void forgotPassword_givenEmailFormatInvalid_return400() throws Exception {
        // given
        UserFindPasswordReqDto reqDto = getValidUserFindPasswordReqDto();
        reqDto.setEmail("testergabojait.com");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EMAIL_FORMAT_INVALID.getHttpStatus().value());
        assertThat(response).contains(EMAIL_FORMAT_INVALID.name());
    }

    @Test
    @DisplayName("비밀번호 검증 | 올바른 요청시 | 200응답")
    void verifyPassword_givenValidReq_return200() throws Exception {
        // given
        UserVerifyReqDto reqDto = getValidUserVerifyReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/password/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PASSWORD_VERIFIED.getHttpStatus().value());
        assertThat(response).contains(PASSWORD_VERIFIED.name());
    }

    @Test
    @DisplayName("비밀번호 검증 | 비밀번호 미입력시 | 400응답")
    void verifyPassword_givenPasswordFieldRequired_return400() throws Exception {
        // given
        UserVerifyReqDto reqDto = getValidUserVerifyReqDto();
        reqDto.setPassword("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/password/verify")
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
    @DisplayName("닉네임 업데이트 | 올바른 요청시 | 200응답")
    void updateNickname_givenValidReq_return200() throws Exception {
        // given
        UserNicknameUpdateReqDto reqDto = getValidUserNicknameUpdateReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(NICKNAME_UPDATED.getHttpStatus().value());
        assertThat(response).contains(NICKNAME_UPDATED.name());
    }

    @Test
    @DisplayName("닉네임 업데이트 | 닉네임 미입력시 | 400응답")
    void updateNickname_givenNicknameFieldRequired_return400() throws Exception {
        // given
        UserNicknameUpdateReqDto reqDto = getValidUserNicknameUpdateReqDto();
        reqDto.setNickname("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(NICKNAME_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(NICKNAME_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("닉네임 업데이트 | 잘못된 닉네임 길이시 | 400응답")
    void updateNickname_givenNicknameLengthInvalid_return400() throws Exception {
        // given
        UserNicknameUpdateReqDto reqDto = getValidUserNicknameUpdateReqDto();
        reqDto.setNickname("가");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(NICKNAME_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(NICKNAME_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("닉네임 업데이트 | 잘못된 닉네임 포맷시 | 400응답")
    void updateNickname_givenNicknameFormatInvalid_return400() throws Exception {
        // given
        UserNicknameUpdateReqDto reqDto = getValidUserNicknameUpdateReqDto();
        reqDto.setNickname("tester");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(NICKNAME_FORMAT_INVALID.getHttpStatus().value());
        assertThat(response).contains(NICKNAME_FORMAT_INVALID.name());
    }

    @Test
    @DisplayName("비밀번호 업데이트 | 올바른 요청시 | 200응답")
    void updatePassword_givenValidReq_return200() throws Exception {
        // given
        UserUpdatePasswordReqDto reqDto = getValidUserUpdatePasswordReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PASSWORD_UPDATED.getHttpStatus().value());
        assertThat(response).contains(PASSWORD_UPDATED.name());
    }

    @Test
    @DisplayName("비밀번호 업데이트 | 비밀번호 미입력시 | 400응답")
    void updatePassword_givenPasswordFieldRequired_return400() throws Exception {
        // given
        UserUpdatePasswordReqDto reqDto = getValidUserUpdatePasswordReqDto();
        reqDto.setPassword("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/password")
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
    @DisplayName("비밀번호 업데이트 | 잘못된 비밀번호 길이시 | 400응답")
    void updatePassword_givenPasswordLengthInvalid_return400() throws Exception {
        // given
        UserUpdatePasswordReqDto reqDto = getValidUserUpdatePasswordReqDto();
        reqDto.setPassword("pas123!");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/password")
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
    @DisplayName("비밀번호 업데이트 | 잘못된 비밀번호 포맷시 | 400응답")
    void updatePassword_givenPasswordFormatInvalid_return400() throws Exception {
        // given
        UserUpdatePasswordReqDto reqDto = getValidUserUpdatePasswordReqDto();
        reqDto.setPassword("password!@#$");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/password")
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
    @DisplayName("비밀번호 업데이트 | 비밀번호 재입력 미입력시 | 400응답")
    void updatePassword_givenPasswordReEnteredFieldRequired_return400() throws Exception {
        // given
        UserUpdatePasswordReqDto reqDto = getValidUserUpdatePasswordReqDto();
        reqDto.setPasswordReEntered("");
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/password")
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
    @DisplayName("알림 업데이트 | 올바른 요청시 | 200응답")
    void updateIsNotified_givenValidReq_return200() throws Exception {
        // given
        UserIsNotifiedUpdateReqDto reqDto = getValidUserIsNotifiedUpdateReqDto();
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/notified")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(IS_NOTIFIED_UPDATED.getHttpStatus().value());
        assertThat(response).contains(IS_NOTIFIED_UPDATED.name());
    }

    @Test
    @DisplayName("알림 업데이트 | 알림 여부 미입력시 | 400응답")
    void updateIsNotified_givenIsNotifiedFieldRequired_return400() throws Exception {
        // given
        UserIsNotifiedUpdateReqDto reqDto = getValidUserIsNotifiedUpdateReqDto();
        reqDto.setIsNotified(null);
        String request = mapToJson(reqDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/notified")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(IS_NOTIFIED_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(IS_NOTIFIED_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("회원 탈퇴 | 올바른 요청시 | 200응답")
    void deactivate_givenValidReq_return200() throws Exception {
        // given & when
        MvcResult mvcResult = this.mockMvc.perform(delete("/api/v1/user"))
                .andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USER_DELETED.getHttpStatus().value());
        assertThat(response).contains(USER_DELETED.name());
    }

    private UserRegisterReqDto getValidUserRegisterReqDto() {
        UserRegisterReqDto reqDto = new UserRegisterReqDto();
        reqDto.setUsername("tester");
        reqDto.setPassword("password123!");
        reqDto.setPasswordReEntered("password123!");
        reqDto.setNickname("테스터");
        reqDto.setGender("none");
        reqDto.setBirthdate(LocalDate.of(1997, 2, 11));
        reqDto.setEmail("test@gabojait.com");
        return reqDto;
    }

    private UserLoginReqDto getValidUserLoginReqDto() {
        UserLoginReqDto reqDto = new UserLoginReqDto();
        reqDto.setUsername("tester");
        reqDto.setPassword("password123!");
        return reqDto;
    }

    private UserLogoutReqDto getValidUserLogoutReqDto() {
        UserLogoutReqDto reqDto = new UserLogoutReqDto();
        reqDto.setFcmToken("fcm-token");
        return reqDto;
    }

    private UserRenewTokenReqDto getValidUserRenewTokenReqDto() {
        UserRenewTokenReqDto reqDto = new UserRenewTokenReqDto();
        reqDto.setFcmToken("fcm-token");
        return reqDto;
    }

    private UserFindUsernameReqDto getValidUserFindUsernameReqDto() {
        UserFindUsernameReqDto reqDto = new UserFindUsernameReqDto();
        reqDto.setEmail("tester@gabojait.com");
        return reqDto;
    }

    private UserFindPasswordReqDto getValidUserFindPasswordReqDto() {
        UserFindPasswordReqDto reqDto = new UserFindPasswordReqDto();
        reqDto.setUsername("tester");
        reqDto.setEmail("tester@gabojait.com");
        return reqDto;
    }

    private UserVerifyReqDto getValidUserVerifyReqDto() {
        UserVerifyReqDto reqDto = new UserVerifyReqDto();
        reqDto.setPassword("password123!");
        return reqDto;
    }

    private UserNicknameUpdateReqDto getValidUserNicknameUpdateReqDto() {
        UserNicknameUpdateReqDto reqDto = new UserNicknameUpdateReqDto();
        reqDto.setNickname("테스터");
        return reqDto;
    }

    private UserUpdatePasswordReqDto getValidUserUpdatePasswordReqDto() {
        UserUpdatePasswordReqDto reqDto = new UserUpdatePasswordReqDto();
        reqDto.setPassword("password123!");
        reqDto.setPasswordReEntered("password123!");
        return reqDto;
    }

    private UserIsNotifiedUpdateReqDto getValidUserIsNotifiedUpdateReqDto() {
        UserIsNotifiedUpdateReqDto reqDto = new UserIsNotifiedUpdateReqDto();
        reqDto.setIsNotified(true);
        return reqDto;
    }
}