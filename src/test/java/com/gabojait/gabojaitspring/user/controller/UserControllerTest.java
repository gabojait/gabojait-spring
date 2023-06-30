package com.gabojait.gabojaitspring.user.controller;

import com.gabojait.gabojaitspring.auth.CustomAuthenticationFilter;
import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.ControllerTestSetup;
import com.gabojait.gabojaitspring.favorite.service.FavoriteUserService;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.dto.req.*;
import com.gabojait.gabojaitspring.user.service.UserService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest extends ControllerTestSetup {

    @MockBean
    private UserService userService;

    @MockBean
    private FavoriteUserService favoriteUserService;

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

        doReturn(tester)
                .when(this.favoriteUserService)
                .findOneOtherUser(any(), any());
    }

    @Test
    @DisplayName("아이디 중복여부 확인_올바른 요청시_200반환")
    void duplicateUsername_givenValidReq_return200() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/username")
                        .param("username", "test1"))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USERNAME_AVAILABLE.getHttpStatus().value());
        assertThat(response).contains(USERNAME_AVAILABLE.name());
    }

    @Test
    @DisplayName("아이디 중복여부 확인_아이디 미입력시_400반환")
    void duplicateUsername_givenUsernameFieldRequired_return400() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/username")
                        .param("username", ""))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USERNAME_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(USERNAME_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("아이디 중복여부 확인_잘못된 아이디 길이시_400반환")
    void duplicateUsername_givenUsernameLengthInvalid_return400() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/username")
                        .param("username", "test"))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USERNAME_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(USERNAME_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("아이디 중복여부 확인_잘못된 아이디 포맷시_400반환")
    void duplicateUsername_givenUsernameFormatInvalid_return400() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/username")
                        .param("username", "테스트아이"))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USERNAME_FORMAT_INVALID.getHttpStatus().value());
        assertThat(response).contains(USERNAME_FORMAT_INVALID.name());
    }

    @Test
    @DisplayName("닉네임 중복여부 확인_올바른 요청시_200반환")
    void duplicateNickname_givenValidReq_return200() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/nickname")
                        .param("nickname", "닉네임"))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(NICKNAME_AVAILABLE.getHttpStatus().value());
        assertThat(response).contains(NICKNAME_AVAILABLE.name());
    }

    @Test
    @DisplayName("닉네임 중복여부 확인_닉네임 미입력시_400반환")
    void duplicateNickname_givenNicknameFieldRequired_return400() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/nickname")
                        .param("nickname", ""))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(NICKNAME_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(NICKNAME_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("닉네임 중복여부 확인_잘못된 닉네임 길이시_400반환")
    void duplicateNickname_givenNicknameLengthInvalid_return400() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/nickname")
                        .param("nickname", "테"))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(NICKNAME_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(NICKNAME_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("닉네임 중복여부 확인_잘못된 닉네임 포맷시_400반환")
    void duplicateNickname_givenNicknameFormatInvalid_return400() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/nickname")
                        .param("nickname", "test"))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(NICKNAME_FORMAT_INVALID.getHttpStatus().value());
        assertThat(response).contains(NICKNAME_FORMAT_INVALID.name());
    }

    @Test
    @DisplayName("회원 가입_올바른 요청시_201반환")
    void register_givenValidReq_return201() throws Exception {
        UserRegisterReqDto requestDto = new UserRegisterReqDto();
        requestDto.setUsername("tester");
        requestDto.setPassword("password123!");
        requestDto.setPasswordReEntered("password123!");
        requestDto.setNickname("테스터");
        requestDto.setGender("none");
        requestDto.setBirthdate(LocalDate.of(1997, 2, 11));
        requestDto.setEmail("test@gabojait.com");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USER_REGISTERED.getHttpStatus().value());
        assertThat(response).contains(USER_REGISTERED.name());
    }

    @Test
    @DisplayName("회원 가입_아이디 미입력시_400반환")
    void register_givenUsernameFieldRequired_return400() throws Exception {
        UserRegisterReqDto requestDto = new UserRegisterReqDto();
        requestDto.setUsername("");
        requestDto.setPassword("password123!");
        requestDto.setPasswordReEntered("password123!");
        requestDto.setNickname("테스터");
        requestDto.setGender("none");
        requestDto.setBirthdate(LocalDate.of(1997, 2, 11));
        requestDto.setEmail("test@gabojait.com");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USERNAME_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(USERNAME_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("회원 가입_잘못된 아이디 길이시_400반환")
    void register_givenUsernameLengthInvalid_return400() throws Exception {
        UserRegisterReqDto requestDto = new UserRegisterReqDto();
        requestDto.setUsername("test");
        requestDto.setPassword("password123!");
        requestDto.setPasswordReEntered("password123!");
        requestDto.setNickname("테스터");
        requestDto.setGender("none");
        requestDto.setBirthdate(LocalDate.of(1997, 2, 11));
        requestDto.setEmail("test@gabojait.com");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USERNAME_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(USERNAME_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("회원 가입_잘못된 아이디 포맷시_400반환")
    void register_givenUsernameFormatInvalid_return400() throws Exception {
        UserRegisterReqDto requestDto = new UserRegisterReqDto();
        requestDto.setUsername("tester가");
        requestDto.setPassword("password123!");
        requestDto.setPasswordReEntered("password123!");
        requestDto.setNickname("테스터");
        requestDto.setGender("none");
        requestDto.setBirthdate(LocalDate.of(1997, 2, 11));
        requestDto.setEmail("test@gabojait.com");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USERNAME_FORMAT_INVALID.getHttpStatus().value());
        assertThat(response).contains(USERNAME_FORMAT_INVALID.name());
    }

    @Test
    @DisplayName("회원 가입_비밀번호 미입력시_400반환")
    void register_givenPasswordFieldRequired_return400() throws Exception {
        UserRegisterReqDto requestDto = new UserRegisterReqDto();
        requestDto.setUsername("tester");
        requestDto.setPassword("");
        requestDto.setPasswordReEntered("password123!");
        requestDto.setNickname("테스터");
        requestDto.setGender("none");
        requestDto.setBirthdate(LocalDate.of(1997, 2, 11));
        requestDto.setEmail("test@gabojait.com");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PASSWORD_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(PASSWORD_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("회원 가입_잘못된 비밀번호 길이시_400반환")
    void register_givenPasswordLengthInvalid_return400() throws Exception {
        UserRegisterReqDto requestDto = new UserRegisterReqDto();
        requestDto.setUsername("tester");
        requestDto.setPassword("pass12!");
        requestDto.setPasswordReEntered("pass12!");
        requestDto.setNickname("테스터");
        requestDto.setGender("none");
        requestDto.setBirthdate(LocalDate.of(1997, 2, 11));
        requestDto.setEmail("test@gabojait.com");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PASSWORD_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(PASSWORD_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("회원 가입_잘못된 비밀번호 포맷시_400반환")
    void register_givenPasswordFormatInvalid_return400() throws Exception {
        UserRegisterReqDto requestDto = new UserRegisterReqDto();
        requestDto.setUsername("tester");
        requestDto.setPassword("password123");
        requestDto.setPasswordReEntered("password123");
        requestDto.setNickname("테스터");
        requestDto.setGender("none");
        requestDto.setBirthdate(LocalDate.of(1997, 2, 11));
        requestDto.setEmail("test@gabojait.com");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PASSWORD_FORMAT_INVALID.getHttpStatus().value());
        assertThat(response).contains(PASSWORD_FORMAT_INVALID.name());
    }

    @Test
    @DisplayName("회원 가입_비밀번호 재입력 미입력시_400반환")
    void register_givenPasswordReEnterFieldRequired_return400() throws Exception {
        UserRegisterReqDto requestDto = new UserRegisterReqDto();
        requestDto.setUsername("tester");
        requestDto.setPassword("password123!");
        requestDto.setPasswordReEntered("");
        requestDto.setNickname("테스터");
        requestDto.setGender("none");
        requestDto.setBirthdate(LocalDate.of(1997, 2, 11));
        requestDto.setEmail("test@gabojait.com");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PASSWORD_RE_ENTERED_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(PASSWORD_RE_ENTERED_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("회원 가입_닉네임 미입력시_400반환")
    void register_givenNicknameFieldRequired_return400() throws Exception {
        UserRegisterReqDto requestDto = new UserRegisterReqDto();
        requestDto.setUsername("tester");
        requestDto.setPassword("password123!");
        requestDto.setPasswordReEntered("password123!");
        requestDto.setNickname("");
        requestDto.setGender("none");
        requestDto.setBirthdate(LocalDate.of(1997, 2, 11));
        requestDto.setEmail("test@gabojait.com");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(NICKNAME_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(NICKNAME_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("회원 가입_잘못된 닉네임 길이시_400반환")
    void register_givenNicknameLengthInvalid_return400() throws Exception {
        UserRegisterReqDto requestDto = new UserRegisterReqDto();
        requestDto.setUsername("tester");
        requestDto.setPassword("password123!");
        requestDto.setPasswordReEntered("password123!");
        requestDto.setNickname("테스터닉네임입니다");
        requestDto.setGender("none");
        requestDto.setBirthdate(LocalDate.of(1997, 2, 11));
        requestDto.setEmail("test@gabojait.com");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(NICKNAME_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(NICKNAME_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("회원 가입_잘못된 닉네임 포맷시_400반환")
    void register_givenNicknameFormatInvalid_return400() throws Exception {
        UserRegisterReqDto requestDto = new UserRegisterReqDto();
        requestDto.setUsername("tester");
        requestDto.setPassword("password123!");
        requestDto.setPasswordReEntered("password123!");
        requestDto.setNickname("tester");
        requestDto.setGender("none");
        requestDto.setBirthdate(LocalDate.of(1997, 2, 11));
        requestDto.setEmail("test@gabojait.com");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(NICKNAME_FORMAT_INVALID.getHttpStatus().value());
        assertThat(response).contains(NICKNAME_FORMAT_INVALID.name());
    }

    @Test
    @DisplayName("회원 가입_성별 미입력시_400반환")
    void register_givenGenderFieldRequired_return400() throws Exception {
        UserRegisterReqDto requestDto = new UserRegisterReqDto();
        requestDto.setUsername("tester");
        requestDto.setPassword("password123!");
        requestDto.setPasswordReEntered("password123!");
        requestDto.setNickname("테스터");
        requestDto.setGender("");
        requestDto.setBirthdate(LocalDate.of(1997, 2, 11));
        requestDto.setEmail("test@gabojait.com");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(GENDER_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(GENDER_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("회원 가입_잘못된 성별 타입시_400반환")
    void register_givenGenderTypeInvalid_return400() throws Exception {
        UserRegisterReqDto requestDto = new UserRegisterReqDto();
        requestDto.setUsername("tester");
        requestDto.setPassword("password123!");
        requestDto.setPasswordReEntered("password123!");
        requestDto.setNickname("테스터");
        requestDto.setGender("boy");
        requestDto.setBirthdate(LocalDate.of(1997, 2, 11));
        requestDto.setEmail("test@gabojait.com");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(GENDER_TYPE_INVALID.getHttpStatus().value());
        assertThat(response).contains(GENDER_TYPE_INVALID.name());
    }

    @Test
    @DisplayName("회원 가입_이메일 미입력시_400반환")
    void register_givenEmailFieldRequired_return400() throws Exception {
        UserRegisterReqDto requestDto = new UserRegisterReqDto();
        requestDto.setUsername("tester");
        requestDto.setPassword("password123!");
        requestDto.setPasswordReEntered("password123!");
        requestDto.setNickname("테스터");
        requestDto.setGender("male");
        requestDto.setEmail("");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EMAIL_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(EMAIL_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("회원 가입_잘못된 이메일 포맷시_400반환")
    void register_givenEmailFormatInvalid_return400() throws Exception {
        UserRegisterReqDto requestDto = new UserRegisterReqDto();
        requestDto.setUsername("tester");
        requestDto.setPassword("password123!");
        requestDto.setPasswordReEntered("password123!");
        requestDto.setNickname("테스터");
        requestDto.setGender("male");
        requestDto.setEmail("testgabojait.com");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EMAIL_FORMAT_INVALID.getHttpStatus().value());
        assertThat(response).contains(EMAIL_FORMAT_INVALID.name());
    }

    @Test
    @DisplayName("로그인_올바른 요청시_200반환")
    void login_givenValidReq_return200() throws Exception {
        UserLoginReqDto requestDto = new UserLoginReqDto();
        requestDto.setUsername("tester");
        requestDto.setPassword("password123!");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USER_LOGIN.getHttpStatus().value());
        assertThat(response).contains(USER_LOGIN.name());
    }

    @Test
    @DisplayName("로그인_아이디 미입력시_400반환")
    void login_givenUsernameFieldRequired_return400() throws Exception {
        UserLoginReqDto requestDto = new UserLoginReqDto();
        requestDto.setUsername("");
        requestDto.setPassword("password123!");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USERNAME_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(USERNAME_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("로그인_비밀번호 미입력시_400반환")
    void login_givenPasswordFieldRequired_return400() throws Exception {
        UserLoginReqDto requestDto = new UserLoginReqDto();
        requestDto.setUsername("tester");
        requestDto.setPassword("");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PASSWORD_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(PASSWORD_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("로그아웃_올바른 요청시_200반환")
    void logout_givenValidReq_return200() throws Exception {
        UserLogoutReqDto requestDto = new UserLogoutReqDto();
        requestDto.setFcmToken("fcmToken");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USER_LOGOUT.getHttpStatus().value());
        assertThat(response).contains(USER_LOGOUT.name());
    }

    @Test
    @DisplayName("본인 조회_올바른 요청시_200반환")
    void findMyself_givenValidReq_return200() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user"))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(SELF_USER_FOUND.getHttpStatus().value());
        assertThat(response).contains(SELF_USER_FOUND.name());
    }

    @Test
    @DisplayName("단건 조회_올바른 요청시_200반환")
    void findOther_givenValidReq_return200() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/{user-id}", 1))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USER_FOUND.getHttpStatus().value());
        assertThat(response).contains(USER_FOUND.name());
    }

    @Test
    @DisplayName("단건 조회_회원 식별자 미입력시_400반환")
    void findOther_givenUserIdFieldRequired_return400() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/{user-id}", ""))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        // TODO
//        assertThat(status).isEqualTo(USER_ID_FIELD_REQUIRED.getHttpStatus().value());
//        assertThat(response).contains(USER_ID_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("단건 조회_양수 아닌 회원 식별자시_400반환")
    void findOther_givenUserIdPositiveOnly_return400() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/v1/user/{user-id}", -1))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USER_ID_POSITIVE_ONLY.getHttpStatus().value());
        assertThat(response).contains(USER_ID_POSITIVE_ONLY.name());
    }

    @Test
    @DisplayName("토큰 재발급_올바른 요청시_200반환")
    void renewToken_givenValidReq_return200() throws Exception {
        UserRenewTokenReqDto requestDto = new UserRenewTokenReqDto();
        requestDto.setFcmToken("fcm-token");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(TOKEN_RENEWED.getHttpStatus().value());
        assertThat(response).contains(TOKEN_RENEWED.name());
    }

    @Test
    @DisplayName("아이디 찾기_올바른 요청시_200반환")
    void forgotUsername_givenValidReq_return200() throws Exception {
        UserFindUsernameReqDto requestDto = new UserFindUsernameReqDto();
        requestDto.setEmail("tester@gabojait.com");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USERNAME_EMAIL_SENT.getHttpStatus().value());
        assertThat(response).contains(USERNAME_EMAIL_SENT.name());
    }

    @Test
    @DisplayName("아이디 찾기_이메일 미입력시_400반환")
    void forgotUsername_givenEmailFieldRequired_return400() throws Exception {
        UserFindUsernameReqDto requestDto = new UserFindUsernameReqDto();
        requestDto.setEmail("");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EMAIL_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(EMAIL_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("아이디 찾기_잘못된 이메일 포맷시_200반환")
    void forgotUsername_givenEmailFormatInvalid_return400() throws Exception {
        UserFindUsernameReqDto requestDto = new UserFindUsernameReqDto();
        requestDto.setEmail("testergabojait.com");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EMAIL_FORMAT_INVALID.getHttpStatus().value());
        assertThat(response).contains(EMAIL_FORMAT_INVALID.name());
    }

    @Test
    @DisplayName("비밀번호 찾기_올바른 요청시_200반환")
    void forgotPassword_givenValidReq_return400() throws Exception {
        UserFindPasswordReqDto requestDto = new UserFindPasswordReqDto();
        requestDto.setEmail("tester@gabojait.com");
        requestDto.setUsername("tester");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PASSWORD_EMAIL_SENT.getHttpStatus().value());
        assertThat(response).contains(PASSWORD_EMAIL_SENT.name());
    }

    @Test
    @DisplayName("비밀번호 찾기_이메일 미입력시_400반환")
    void forgotPassword_givenEmailFieldRequired_return400() throws Exception {
        UserFindPasswordReqDto requestDto = new UserFindPasswordReqDto();
        requestDto.setEmail("");
        requestDto.setUsername("tester");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EMAIL_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(EMAIL_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("비밀번호 찾기_아이디 미입력시_400반환")
    void forgotPassword_givenUsernameFieldRequired_return400() throws Exception {
        UserFindPasswordReqDto requestDto = new UserFindPasswordReqDto();
        requestDto.setEmail("tester@gabojait.com");
        requestDto.setUsername("");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USERNAME_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(USERNAME_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("비밀번호 찾기_잘못된 이메일 포맷시_400반환")
    void forgotPassword_givenEmailFormatInvalid_return400() throws Exception {
        UserFindPasswordReqDto requestDto = new UserFindPasswordReqDto();
        requestDto.setEmail("testergabojait.com");
        requestDto.setUsername("tester");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(EMAIL_FORMAT_INVALID.getHttpStatus().value());
        assertThat(response).contains(EMAIL_FORMAT_INVALID.name());
    }

    @Test
    @DisplayName("비밀번호 검증_올바른 요청시_200반환")
    void verifyPassword_givenValidReq_return200() throws Exception {
        UserVerifyReqDto requestDto = new UserVerifyReqDto();
        requestDto.setPassword("password123!");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/password/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PASSWORD_VERIFIED.getHttpStatus().value());
        assertThat(response).contains(PASSWORD_VERIFIED.name());
    }

    @Test
    @DisplayName("비밀번호 검증_비밀번호 미입력시_400반환")
    void verifyPassword_givenPasswordFieldRequired_return400() throws Exception {
        UserVerifyReqDto requestDto = new UserVerifyReqDto();
        requestDto.setPassword("");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/user/password/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PASSWORD_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(PASSWORD_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("닉네임 업데이트_올바른 요청시_200반환")
    void updateNickname_givenValidReq_return200() throws Exception {
        UserNicknameUpdateReqDto requestDto = new UserNicknameUpdateReqDto();
        requestDto.setNickname("테스터");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(NICKNAME_UPDATED.getHttpStatus().value());
        assertThat(response).contains(NICKNAME_UPDATED.name());
    }

    @Test
    @DisplayName("닉네임 업데이트_닉네임 미입력시_400반환")
    void updateNickname_givenNicknameFieldRequired_return400() throws Exception {
        UserNicknameUpdateReqDto requestDto = new UserNicknameUpdateReqDto();
        requestDto.setNickname("");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(NICKNAME_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(NICKNAME_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("닉네임 업데이트_잘못된 닉네임 길이시_400반환")
    void updateNickname_givenNicknameLengthInvalid_return400() throws Exception {
        UserNicknameUpdateReqDto requestDto = new UserNicknameUpdateReqDto();
        requestDto.setNickname("가");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(NICKNAME_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(NICKNAME_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("닉네임 업데이트_잘못된 닉네임 포맷시_400반환")
    void updateNickname_givenNicknameFormatInvalid_return400() throws Exception {
        UserNicknameUpdateReqDto requestDto = new UserNicknameUpdateReqDto();
        requestDto.setNickname("tester");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(NICKNAME_FORMAT_INVALID.getHttpStatus().value());
        assertThat(response).contains(NICKNAME_FORMAT_INVALID.name());
    }

    @Test
    @DisplayName("비밀번호 업데이트_올바른 요청시_200반환")
    void updatePassword_givenValidReq_return200() throws Exception {
        UserUpdatePasswordReqDto requestDto = new UserUpdatePasswordReqDto();
        requestDto.setPassword("password123!");
        requestDto.setPasswordReEntered("password123!");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PASSWORD_UPDATED.getHttpStatus().value());
        assertThat(response).contains(PASSWORD_UPDATED.name());
    }

    @Test
    @DisplayName("비밀번호 업데이트_비밀번호 미입력시_400반환")
    void updatePassword_givenPasswordFieldRequired_return400() throws Exception {
        UserUpdatePasswordReqDto requestDto = new UserUpdatePasswordReqDto();
        requestDto.setPassword("");
        requestDto.setPasswordReEntered("password123!");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PASSWORD_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(PASSWORD_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("비밀번호 업데이트_잘못된 비밀번호 길이시_400반환")
    void updatePassword_givenPasswordLengthInvalid_return400() throws Exception {
        UserUpdatePasswordReqDto requestDto = new UserUpdatePasswordReqDto();
        requestDto.setPassword("pas123!");
        requestDto.setPasswordReEntered("password123!");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PASSWORD_LENGTH_INVALID.getHttpStatus().value());
        assertThat(response).contains(PASSWORD_LENGTH_INVALID.name());
    }

    @Test
    @DisplayName("비밀번호 업데이트_잘못된 비밀번호 포맷시_400반환")
    void updatePassword_givenPasswordFormatInvalid_return400() throws Exception {
        UserUpdatePasswordReqDto requestDto = new UserUpdatePasswordReqDto();
        requestDto.setPassword("password!@#$");
        requestDto.setPasswordReEntered("password!@#$");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PASSWORD_FORMAT_INVALID.getHttpStatus().value());
        assertThat(response).contains(PASSWORD_FORMAT_INVALID.name());
    }

    @Test
    @DisplayName("비밀번호 업데이트_비밀번호 재입력 미입력시_400반환")
    void updatePassword_givenPasswordReEnteredFieldRequired_return400() throws Exception {
        UserUpdatePasswordReqDto requestDto = new UserUpdatePasswordReqDto();
        requestDto.setPassword("password123!");
        requestDto.setPasswordReEntered("");
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(PASSWORD_RE_ENTERED_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(PASSWORD_RE_ENTERED_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("알림 업데이트_올바른 요청시_200반환")
    void updateIsNotified_givenValidReq_return200() throws Exception {
        UserIsNotifiedUpdateReqDto requestDto = new UserIsNotifiedUpdateReqDto();
        requestDto.setIsNotified(false);
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/notified")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(IS_NOTIFIED_UPDATED.getHttpStatus().value());
        assertThat(response).contains(IS_NOTIFIED_UPDATED.name());
    }

    @Test
    @DisplayName("알림 업데이트_알림 여부 미입력시_400반환")
    void updateIsNotified_givenIsNotifiedFieldRequired_return400() throws Exception {
        UserIsNotifiedUpdateReqDto requestDto = new UserIsNotifiedUpdateReqDto();
        String request = mapToJson(requestDto);

        MvcResult mvcResult = this.mockMvc.perform(patch("/api/v1/user/notified")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(IS_NOTIFIED_FIELD_REQUIRED.getHttpStatus().value());
        assertThat(response).contains(IS_NOTIFIED_FIELD_REQUIRED.name());
    }

    @Test
    @DisplayName("회원 탈퇴_올바른 요청시_200반환")
    void deactivate_givenValidReq_return200() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(delete("/api/v1/user"))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(status).isEqualTo(USER_DELETED.getHttpStatus().value());
        assertThat(response).contains(USER_DELETED.name());
    }
}