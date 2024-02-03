package com.gabojait.gabojaitspring.api.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabojait.gabojaitspring.api.dto.user.request.*;
import com.gabojait.gabojaitspring.api.dto.user.response.UserLoginResponse;
import com.gabojait.gabojaitspring.api.dto.user.response.UserRegisterResponse;
import com.gabojait.gabojaitspring.api.service.user.UserService;
import com.gabojait.gabojaitspring.config.auth.CustomAuthenticationEntryPoint;
import com.gabojait.gabojaitspring.config.auth.JwtProvider;
import com.gabojait.gabojaitspring.domain.user.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;
import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    @MockBean private JwtProvider jwtProvider;
    @MockBean private UserService userService;

    @BeforeEach
    void setup() {
        when(jwtProvider.createJwt(anyLong()))
                .thenReturn(HttpHeaders.EMPTY);

        when(userService.register(any(), any()))
                .thenReturn(UserRegisterResponse.builder()
                        .id(1L)
                        .username("tester")
                        .nickname("테스터")
                        .gender(Gender.M)
                        .birthdate(LocalDate.of(1997, 2, 11))
                        .isNotified(true)
                        .now(LocalDateTime.now())
                        .email("tester@gabojait.com")
                        .build());

        when(userService.login(any(), any()))
                .thenReturn(UserLoginResponse.builder()
                        .id(1L)
                        .username("tester")
                        .nickname("테스터")
                        .gender(Gender.M)
                        .birthdate(LocalDate.of(1997, 2, 11))
                        .isNotified(true)
                        .now(LocalDateTime.now())
                        .email("tester@gabojait.com")
                        .build());
    }

    @Test
    @DisplayName("아이디 중복여부 확인을 하면 200을 반환한다.")
    void givenUsername_whenDuplicateUsername_thenReturn200() throws Exception {
        // given
        String username = "test1";

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/username")
                        .param("username", username)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(USERNAME_AVAILABLE.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(USERNAME_AVAILABLE.getMessage()));
    }

    @Test
    @DisplayName("아이디 5자 미만일시 아아디 중복여부 확인을 하면 400을 반환한다.")
    void givenLessThan5SizeUsername_whenDuplicateUsername_thenReturn400() throws Exception {
        // given
        String username = "test";

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/username")
                        .param("username", username)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(USERNAME_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(USERNAME_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("아이디 15자 초과일시 아아디 중복여부 확인을 하면 400을 반환한다.")
    void givenGreaterThan15SizeUsername_whenDuplicateUsername_thenReturn400() throws Exception {
        // given
        String username = "tester1234567890";

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/username")
                        .param("username", username)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(USERNAME_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(USERNAME_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("잘못된 아이디 포맷일시 아이디 중복여부 확인을 하면 400을 반환한다.")
    void givenFormatUsername__whenDuplicateUsername_thenReturn400() throws Exception {
        // given
        String username = "테스트이다";

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/username")
                        .param("username", username)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(USERNAME_FORMAT_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(USERNAME_FORMAT_INVALID.getMessage()));
    }

    @Test
    @DisplayName("닉네임 중복여부 확인을 하면 200을 반환한다.")
    void givenNickname_whenDuplicateNickname_thenReturn200() throws Exception {
        // given
        String nickname = "테스터";

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/nickname")
                        .param("nickname", nickname)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(NICKNAME_AVAILABLE.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(NICKNAME_AVAILABLE.getMessage()));
    }

    @Test
    @DisplayName("닉네임 2자 미만일시 닉네임 중복여부 확인을 하면 400을 반환한다.")
    void givenLessThan2SizeNickname_whenDuplicateNickname_thenReturn400() throws Exception {
        // given
        String nickname = "가";

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/nickname")
                        .param("nickname", nickname)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(NICKNAME_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(NICKNAME_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("닉네임 8자 초과일시 닉네임 중복여부 확인을 하면 400을 반환한다.")
    void givenGreaterThan8SizeNickname_whenDuplicateNickname_thenReturn400() throws Exception {
        // given
        String nickname = "가나다라마바사아자";

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/nickname")
                        .param("nickname", nickname)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(NICKNAME_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(NICKNAME_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("잘못된 닉네임 포맷일시 닉네임 중복여부 확인을 하면 400을 반환한다.")
    void givenFormatNickname_whenDuplicateNickname_thenReturn400() throws Exception {
        // given
        String nickname = "nickname";

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user/nickname")
                        .param("nickname", nickname)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(NICKNAME_FORMAT_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(NICKNAME_FORMAT_INVALID.getMessage()));
    }

    @Test
    @DisplayName("회원가입을 하면 201을 반환한다.")
    void givenUserRegisterRequest_whenRegister_thenReturn201() throws Exception {
        // given
        UserRegisterRequest request = createValidUserRegisterRequest();

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.responseCode")
                        .value(USER_REGISTERED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(USER_REGISTERED.getMessage()));
    }

    @Test
    @DisplayName("비밀번호 재입력을 미입력시 회원가입을 하면 400을 반환한다.")
    void givenBlankPasswordReEntered_whenRegister_thenReturn400() throws Exception {
        // given
        UserRegisterRequest request = createValidUserRegisterRequest();
        request.setPasswordReEntered("");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(PASSWORD_RE_ENTERED_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PASSWORD_RE_ENTERED_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("이메일 미입력시 회원가입을 하면 400을 반환한다.")
    void givenBlankEmail_whenRegister_thenReturn400() throws Exception {
        // given
        UserRegisterRequest request = createValidUserRegisterRequest();
        request.setEmail("");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(EMAIL_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(EMAIL_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("인증코드 미입력시 회원가입을 하면 400을 반환한다.")
    void givenBlankVerificationCode_whenRegister_thenReturn400() throws Exception {
        // given
        UserRegisterRequest request = createValidUserRegisterRequest();
        request.setVerificationCode("");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(VERIFICATION_CODE_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(VERIFICATION_CODE_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("아이디 5자 미만일시 회원가입을 하면 400을 반환한다.")
    void givenLessThan5SizeUsername_whenRegister_thenReturn400() throws Exception {
        // given
        UserRegisterRequest request = createValidUserRegisterRequest();
        request.setUsername("test");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(USERNAME_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(USERNAME_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("아이디 15자 초과일시 회원가입을 하면 400을 반환한다.")
    void givenGreaterThan15SizeUsername_whenRegister_thenReturn400() throws Exception {
        // given
        UserRegisterRequest request = createValidUserRegisterRequest();
        request.setUsername("tester1234567890");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(USERNAME_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(USERNAME_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("비밀번호 8자 미만일시 회원가입을 하면 400을 반환한다.")
    void givenLessThan8SizePassword_whenRegister_thenReturn400() throws Exception {
        // given
        UserRegisterRequest request = createValidUserRegisterRequest();
        request.setPassword("pass12!");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(PASSWORD_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PASSWORD_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("비밀번호 30자 초과일시 회원가입을 하면 400을 반환한다.")
    void givenGreaterThan30SizePassword_whenRegister_thenReturn400() throws Exception {
        // given
        UserRegisterRequest request = createValidUserRegisterRequest();
        request.setPassword("p".repeat(29) + "1!");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(PASSWORD_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PASSWORD_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("닉네임 2자 미만일시 회원가입을 하면 400을 반환한다.")
    void givenLessThan2SizeNickname_whenRegister_thenReturn400() throws Exception {
        // given
        UserRegisterRequest request = createValidUserRegisterRequest();
        request.setNickname("가");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(NICKNAME_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(NICKNAME_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("닉네임 8자 초과일시 회원가입을 하면 400을 반환한다.")
    void givenGreaterThan8SizeNickname_whenRegister_thenReturn400() throws Exception {
        // given
        UserRegisterRequest request = createValidUserRegisterRequest();
        request.setNickname("가나다라마바사아자");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(NICKNAME_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(NICKNAME_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("잘못된 아이디 포맷일시 회원가입을 하면 400을 반환한다.")
    void givenFormatUsername_whenRegister_thenReturn400() throws Exception {
        // given
        UserRegisterRequest request = createValidUserRegisterRequest();
        request.setUsername("테스터123");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(USERNAME_FORMAT_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(USERNAME_FORMAT_INVALID.getMessage()));
    }

    @Test
    @DisplayName("잘못된 비밀번호 포맷일시 회원가입을 하면 400을 반환한다.")
    void givenFormatPassword_whenRegister_thenReturn400() throws Exception {
        // given
        UserRegisterRequest request = createValidUserRegisterRequest();
        request.setPassword("password");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(PASSWORD_FORMAT_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PASSWORD_FORMAT_INVALID.getMessage()));
    }

    @Test
    @DisplayName("잘못된 닉네임 포맷일시 회원가입을 하면 400을 반환한다.")
    void givenFormatNickname_whenRegister_thenReturn400() throws Exception {
        // given
        UserRegisterRequest request = createValidUserRegisterRequest();
        request.setNickname("tester");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(NICKNAME_FORMAT_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(NICKNAME_FORMAT_INVALID.getMessage()));
    }

    @Test
    @DisplayName("잘못된 성별 타입일시 회원가입을 하면 400을 반환한다.")
    void givenFormatGender_whenRegister_thenReturn400() throws Exception {
        // given
        UserRegisterRequest request = createValidUserRegisterRequest();
        request.setGender("A");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(GENDER_TYPE_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(GENDER_TYPE_INVALID.getMessage()));
    }

    @Test
    @DisplayName("잘못된 이메일 포맷일시 회원가입을 하면 400을 반환한다.")
    void givenFormatEmail_whenRegister_thenReturn400() throws Exception {
        // given
        UserRegisterRequest request = createValidUserRegisterRequest();
        request.setEmail("testgabojait.com");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(EMAIL_FORMAT_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(EMAIL_FORMAT_INVALID.getMessage()));
    }

    @Test
    @DisplayName("로그인을 하면 200을 반환한다.")
    void givenUserLoginRequest_whenLogin_thenReturn200() throws Exception {
        // given
        UserLoginRequest request = createValidUserLoginRequest();

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(USER_LOGIN.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(USER_LOGIN.getMessage()));
    }

    @Test
    @DisplayName("아이디 미입력시 로그인을 하면 400을 반환한다.")
    void givenBlankUsername_whenLogin_thenReturn400() throws Exception {
        // given
        UserLoginRequest request = createValidUserLoginRequest();
        request.setUsername("");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(USERNAME_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(USERNAME_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("비밀번호 미입력시 로그인을 하면 400을 반환한다.")
    void givenBlankPassword_whenLogin_thenReturn400() throws Exception {
        // given
        UserLoginRequest request = createValidUserLoginRequest();
        request.setPassword("");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(PASSWORD_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PASSWORD_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("로그아웃을 하면 200을 반환한다.")
    void givenUserLogoutRequest_whenLogout_thenReturn200() throws Exception {
        // given
        UserLogoutRequest request = createValidUserLogoutRequest();

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/logout")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(USER_LOGOUT.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(USER_LOGOUT.getMessage()));
    }

    @Test
    @DisplayName("본인 조회를 하면 200을 반환한다.")
    void givenValid_whenFindMySelf_thenReturn200() throws Exception {
        // given
        String username = "tester";

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/user")
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(SELF_USER_FOUND.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(SELF_USER_FOUND.getMessage()));
    }

    @Test
    @DisplayName("토큰 재발급을 하면 200을 반환한다.")
    void givenUserRenewTokenRequest_whenRenewToken_thenReturn200() throws Exception {
        // given
        UserRenewTokenRequest request = createValidUserRenewTokenRequest();

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(TOKEN_RENEWED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(TOKEN_RENEWED.getMessage()));
    }

    @Test
    @DisplayName("아이디 찾기를 하면 200을 반환한다.")
    void givenUserFindUsernameRequest_whenForgotUsername_thenReturn200() throws Exception {
        // given
        UserFindUsernameRequest request = createValidUserFindUsernameRequest();

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/username")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(USERNAME_EMAIL_SENT.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(USERNAME_EMAIL_SENT.getMessage()));
    }

    @Test
    @DisplayName("이메일 미입력시 아이디 찾기를 하면 400을 반환한다..")
    void givenBlankEmail_whenForgotUsername_theReturn400() throws Exception {
        // given
        UserFindUsernameRequest request = createValidUserFindUsernameRequest();
        request.setEmail("");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/username")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(EMAIL_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(EMAIL_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("잘못된 이메일 포맷일시 아이디 찾기를 하면 400을 반환한다.")
    void givenFormatEmail_whenForgotUsername_thenReturn400() throws Exception {
        // given
        UserFindUsernameRequest request = createValidUserFindUsernameRequest();
        request.setEmail("testgabojait.com");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/username")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(EMAIL_FORMAT_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(EMAIL_FORMAT_INVALID.getMessage()));
    }

    @Test
    @DisplayName("비밀번호 찾기를 하면 200을 반환한다.")
    void givenUserFindPasswordRequest_whenForgotPassword_thenReturn200() throws Exception {
        // given
        UserFindPasswordRequest request = createValidUserFindPasswordRequest();

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/password")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(PASSWORD_EMAIL_SENT.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PASSWORD_EMAIL_SENT.getMessage()));
    }

    @Test
    @DisplayName("이메일 미입력시 비밀번호 찾기를 하면 400을 반환한다.")
    void givenBlankEmail_whenForgotPassword_thenReturn400() throws Exception {
        // given
        UserFindPasswordRequest request = createValidUserFindPasswordRequest();
        request.setEmail("");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/password")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(EMAIL_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(EMAIL_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("아이디 미입력시 비밀번호 찾기를 하면 400을 반환한다.")
    void givenBlankUsername_whenForgotPassword_thenReturn400() throws Exception {
        // given
        UserFindPasswordRequest request = createValidUserFindPasswordRequest();
        request.setUsername("");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/password")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(USERNAME_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(USERNAME_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("잘못된 이메일 포맷시 비밀번호 찾기를 하면 400을 반환한다.")
    void givenFormatEmail_whenForgotPassword_thenReturn400() throws Exception {
        // given
        UserFindPasswordRequest request = createValidUserFindPasswordRequest();
        request.setEmail("testergabojait.com");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/password")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(EMAIL_FORMAT_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(EMAIL_FORMAT_INVALID.getMessage()));
    }

    @Test
    @DisplayName("비밀번호 검증을 하면 200을 반환한다.")
    void givenValidUserVerifyRequest_whenVerifyPassword_thenReturn200() throws Exception {
        // given
        UserVerifyRequest request = createValidUserVerifyRequest();

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/password/verify")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(PASSWORD_VERIFIED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PASSWORD_VERIFIED.getMessage()));
    }

    @Test
    @DisplayName("비밀번호 검증을 하면 200을 반환한다.")
    void givenBlankPassword_whenVerifyPassword_thenReturn400() throws Exception {
        // given
        UserVerifyRequest request = createValidUserVerifyRequest();
        request.setPassword("");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/user/password/verify")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(PASSWORD_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PASSWORD_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("닉네임 업데이트를 하면 200을 반환한다.")
    void givenUserNicknameUpdateRequest_whenUpdateNickname_thenReturn200() throws Exception {
        // given
        UserNicknameUpdateRequest request = createValidUserNicknameUpdateRequest();

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/user/nickname")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(NICKNAME_UPDATED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(NICKNAME_UPDATED.getMessage()));
    }

    @Test
    @DisplayName("닉네임 2자 미만일시 닉네임 업데이트를 하면 400을 반환한다.")
    void givenLessThan2SizeNickname_whenUpdateNickname_thenReturn400() throws Exception {
        // given
        UserNicknameUpdateRequest request = createValidUserNicknameUpdateRequest();
        request.setNickname("가");

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/user/nickname")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(NICKNAME_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(NICKNAME_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("닉네임 8자 초과일시 닉네임 업데이트를 하면 400을 반환한다.")
    void givenGreaterThan8SizeNickname_whenUpdateNickname_thenReturn400() throws Exception {
        // given
        UserNicknameUpdateRequest request = createValidUserNicknameUpdateRequest();
        request.setNickname("가".repeat(9));

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/user/nickname")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(NICKNAME_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(NICKNAME_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("잘못된 닉네임 포맷일시 닉네임 업데이트를 하면 400을 반환한다.")
    void givenFormatNickname_whenUpdateNickname_thenReturn400() throws Exception {
        // given
        UserNicknameUpdateRequest request = createValidUserNicknameUpdateRequest();
        request.setNickname("tester");

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/user/nickname")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(NICKNAME_FORMAT_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(NICKNAME_FORMAT_INVALID.getMessage()));
    }

    @Test
    @DisplayName("비밀번호 업데이트를 하면 200을 반환한다.")
    void givenUserUpdatePasswordRequest_whenUpdatePassword_thenReturn200() throws Exception {
        // given
        UserUpdatePasswordRequest request = createValidUserUpdatePasswordRequest();

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/user/password")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(PASSWORD_UPDATED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PASSWORD_UPDATED.getMessage()));
    }

    @Test
    @DisplayName("비밀번호 재입력 미입력시 비밀번호 업데이트를 하면 400을 반환한다.")
    void givenBlankPasswordReEntered_whenUpdatePassword_thenReturn400() throws Exception {
        // given
        UserUpdatePasswordRequest request = createValidUserUpdatePasswordRequest();
        request.setPasswordReEntered("");

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/user/password")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(PASSWORD_RE_ENTERED_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PASSWORD_RE_ENTERED_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("비밀번호 8자 미만일시 비밀번호 업데이트를 하면 400을 반환한다.")
    void givenLessThan8SizePassword_whenUpdatePassword_thenReturn400() throws Exception {
        // given
        UserUpdatePasswordRequest request = createValidUserUpdatePasswordRequest();
        request.setPassword("passw1!");

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/user/password")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(PASSWORD_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PASSWORD_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("비밀번호 30자 초과일시 비밀번호 업데이트를 하면 400을 반환한다.")
    void givenGreaterThan30SizePassword_whenUpdatePassword_thenReturn400() throws Exception {
        // given
        UserUpdatePasswordRequest request = createValidUserUpdatePasswordRequest();
        request.setPassword("p".repeat(29) + "1!");

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/user/password")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(PASSWORD_LENGTH_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PASSWORD_LENGTH_INVALID.getMessage()));
    }

    @Test
    @DisplayName("잘못된 비밀번호 포맷일시 비밀번호 업데이트를 하면 400을 반환한다.")
    void givenFormatPassword_whenUpdatePassword_thenReturn400() throws Exception {
        // given
        UserUpdatePasswordRequest request = createValidUserUpdatePasswordRequest();
        request.setPassword("password1");

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/user/password")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(PASSWORD_FORMAT_INVALID.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(PASSWORD_FORMAT_INVALID.getMessage()));
    }

    @Test
    @DisplayName("알림 여부 업데이트를 하면 200을 반환한다.")
    void givenUserIsNotifiedUpdateRequest_whenUpdateIsNotified_thenReturn200() throws Exception {
        // given
        UserIsNotifiedUpdateRequest request = createValidUserIsNotifiedUpdateRequest();

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/user/notified")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(IS_NOTIFIED_UPDATED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(IS_NOTIFIED_UPDATED.getMessage()));
    }

    @Test
    @DisplayName("알림 여부 미입력시 알림 여부 업데이트를 하면 400을 반환한다.")
    void givenBlankIsNotified_whenUpdateIsNotified_thenReturn400() throws Exception {
        // given
        UserIsNotifiedUpdateRequest request = createValidUserIsNotifiedUpdateRequest();
        request.setIsNotified(null);

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/user/notified")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        );

        // then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseCode")
                        .value(IS_NOTIFIED_FIELD_REQUIRED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(IS_NOTIFIED_FIELD_REQUIRED.getMessage()));
    }

    @Test
    @DisplayName("회원 탈퇴퇴를 하면 200을 반환한다.")
    void givenValid_whenWithdrawal_thenReturn200() throws Exception {
        // when
        ResultActions actions = mockMvc.perform(
                delete("/api/v1/user")
        );

        // then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode")
                        .value(USER_DELETED.name()))
                .andExpect(jsonPath("$.responseMessage")
                        .value(USER_DELETED.getMessage()));
    }

    private UserIsNotifiedUpdateRequest createValidUserIsNotifiedUpdateRequest() {
        return UserIsNotifiedUpdateRequest.builder()
                .isNotified(false)
                .build();
    }

    private UserUpdatePasswordRequest createValidUserUpdatePasswordRequest() {
        return UserUpdatePasswordRequest.builder()
                .password("password1!")
                .passwordReEntered("password1!")
                .build();
    }

    private UserNicknameUpdateRequest createValidUserNicknameUpdateRequest() {
        return UserNicknameUpdateRequest.builder()
                .nickname("테스터")
                .build();
    }

    private UserVerifyRequest createValidUserVerifyRequest() {
        return UserVerifyRequest.builder()
                .password("password1!")
                .build();
    }

    private UserFindPasswordRequest createValidUserFindPasswordRequest() {
        return UserFindPasswordRequest.builder()
                .username("tester")
                .email("tester@gabojait.com")
                .build();
    }

    private UserFindUsernameRequest createValidUserFindUsernameRequest() {
        return UserFindUsernameRequest.builder()
                .email("tester@gabojait.com")
                .build();
    }

    private UserRenewTokenRequest createValidUserRenewTokenRequest() {
        return UserRenewTokenRequest
                .builder()
                .fcmToken("fcm-token")
                .build();
    }

    private UserLogoutRequest createValidUserLogoutRequest() {
        return UserLogoutRequest.builder()
                .fcmToken("fcm-token")
                .build();
    }

    private UserLoginRequest createValidUserLoginRequest() {
        return UserLoginRequest.builder()
                .username("tester")
                .password("password1!")
                .fcmToken("fcm-token")
                .build();
    }

    private UserRegisterRequest createValidUserRegisterRequest() {
        return UserRegisterRequest.builder()
                .username("tester")
                .password("password1!")
                .passwordReEntered("password1!")
                .nickname("테스터")
                .gender(Gender.M.name())
                .birthdate(LocalDate.of(1997, 2, 11))
                .email("tester@gabojait.com")
                .verificationCode("000000")
                .fcmToken("fcm-token")
                .build();
    }
}