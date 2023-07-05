package com.gabojait.gabojaitspring.user.controller;

import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.dto.DefaultResDto;
import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.dto.req.*;
import com.gabojait.gabojaitspring.user.dto.res.UserDefaultResDto;
import com.gabojait.gabojaitspring.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.GroupSequence;
import javax.validation.Valid;
import javax.validation.constraints.*;

import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "회원")
@Validated
@GroupSequence({UserController.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "아이디 중복여부 확인",
            notes = "<검증>\n" +
                    "- username = NotBlank && Size(min = 5, max = 15) && Pattern(regex = ^(?=.-[a-z0-9])[a-z0-9]+$)\n\n" +
                    "<응답 코드>\n" +
                    "- 200 = USERNAME_AVAILABLE\n" +
                    "- 400 = USERNAME_FIELD_REQUIRED || USERNAME_LENGTH_INVALID || USERNAME_FORMAT_INVALID\n" +
                    "- 409 = UNAVAILABLE_USERNAME || EXISTING_USERNAME\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "409", description = "CONFLICT"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/username")
    public ResponseEntity<DefaultResDto<Object>> duplicateUsername(
            @RequestParam(value = "username", required = false)
            @NotBlank(message = "아이디는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
            @Size(min = 5, max = 15, message = "아이디는 5~15자만 가능합니다.", groups = ValidationSequence.Size.class)
            @Pattern(regexp = "^(?=.*[a-z0-9])[a-z0-9]+$", message = "아이디는 소문자 영어와 숫자의 조합으로 입력해 주세요.",
                    groups = ValidationSequence.Format.class)
            String username
    ) {
        userService.validateUsername(username);

        return ResponseEntity.status(USERNAME_AVAILABLE.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(USERNAME_AVAILABLE.name())
                        .responseMessage(USERNAME_AVAILABLE.getMessage())
                        .build());
    }

    @ApiOperation(value = "닉네임 중복여부 확인",
            notes = "<검증>\n" +
                    "- nickname = NotBlank && Size(min = 2, max = 8) && Pattern(regex = ^[가-힣]+$)\n\n" +
                    "<응답 코드>\n" +
                    "- 200 = NICKNAME_AVAILABLE\n" +
                    "- 400 = NICKNAME_FIELD_REQUIRED || NICKNAME_LENGTH_INVALID || NICKNAME_FORMAT_INVALID\n" +
                    "- 409 = UNAVAILABLE_NICKNAME || EXISTING_NICKNAME\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "409", description = "CONFLICT"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/nickname")
    public ResponseEntity<DefaultResDto<Object>> duplicateNickname(
            @RequestParam(value = "nickname", required = false)
            @NotBlank(message = "닉네임은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
            @Size(min = 2, max = 8, message = "닉네임은 2~8자만 가능합니다.", groups = ValidationSequence.Size.class)
            @Pattern(regexp = "^[가-힣]+$", message = "닉닉네임은 한글 조합으로 입력해 주세요.",
                    groups = ValidationSequence.Format.class)
            String nickname
    ) {
        userService.validateNickname(nickname);

        return ResponseEntity.status(NICKNAME_AVAILABLE.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(NICKNAME_AVAILABLE.name())
                        .responseMessage(NICKNAME_AVAILABLE.getMessage())
                        .build());
    }

    @ApiOperation(value = "회원 가입",
            notes = "<응답 코드>\n" +
                    "- 201 = USER_REGISTERED\n" +
                    "- 400 = USERNAME_FIELD_REQUIRED || PASSWORD_FIELD_REQUIRED || PASSWORD_RE_ENTERED_FIELD_REQUIRED" +
                    " || NICKNAME_FIELD_REQUIRED || GENDER_FIELD_REQUIRED || EMAIL_FIELD_REQUIRED || " +
                    "USERNAME_LENGTH_INVALID || PASSWORD_LENGTH_INVALID || NICKNAME_LENGTH_INVALID || " +
                    "USERNAME_FORMAT_INVALID || PASSWORD_FORMAT_INVALID || NICKNAME_FORMAT_INVALID || " +
                    "GENDER_TYPE_INVALID || EMAIL_FORMAT_INVALID || PASSWORD_MATCH_INVALID\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = CONTACT_NOT_FOUND\n" +
                    "- 409 = UNAVAILABLE_USERNAME || EXISTING_USERNAME || UNAVAILABLE_NICKNAME || EXISTING_NICKNAME\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "CREATED",
                    content = @Content(schema = @Schema(implementation = UserDefaultResDto.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "409", description = "CONFLICT"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<DefaultResDto<Object>> register(HttpServletRequest servletRequest,
                                                          @RequestBody @Valid UserRegisterReqDto request) {
        jwtProvider.authorizeGuestAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        User user = userService.register(request);

        HttpHeaders headers = jwtProvider.generateUserJwt(user.getId(), user.getRoles());
        UserDefaultResDto response = new UserDefaultResDto(user);

        return ResponseEntity.status(USER_REGISTERED.getHttpStatus())
                .headers(headers)
                .body(DefaultResDto.singleDataBuilder()
                        .responseCode(USER_REGISTERED.name())
                        .responseMessage(USER_REGISTERED.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "로그인",
            notes = "<응답 코드>\n" +
                    "- 200 = USER_LOGIN\n" +
                    "- 400 = USERNAME_FIELD_REQUIRED || PASSWORD_FIELD_REQUIRED\n" +
                    "- 401 = LOGIN_UNAUTHENTICATED\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = UserDefaultResDto.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PostMapping("/login")
    public ResponseEntity<DefaultResDto<Object>> login(@RequestBody @Valid UserLoginReqDto request) {
        User user = userService.login(request);

        HttpHeaders headers = jwtProvider.generateUserJwt(user.getId(), user.getRoles());
        UserDefaultResDto response = new UserDefaultResDto(user);

        return ResponseEntity.status(USER_LOGIN.getHttpStatus())
                .headers(headers)
                .body(DefaultResDto.singleDataBuilder()
                        .responseCode(USER_LOGIN.name())
                        .responseMessage(USER_LOGIN.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "로그아웃",
            notes = "<응답 코드>\n" +
                    "- 200 = USER_LOGOUT\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PostMapping("/logout")
    public ResponseEntity<DefaultResDto<Object>> logout(HttpServletRequest servletRequest,
                                                        @RequestBody @Valid UserLogoutReqDto request) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        userService.logout(user, request.getFcmToken());

        return ResponseEntity.status(USER_LOGOUT.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(USER_LOGOUT.name())
                        .responseMessage(USER_LOGOUT.getMessage())
                        .build());
    }

    @ApiOperation(value = "본인 조회",
            notes = "<응답 코드>\n" +
                    "- 200 = SELF_USER_FOUND\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = UserDefaultResDto.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping
    public ResponseEntity<DefaultResDto<Object>> findMyself(HttpServletRequest servletRequest) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        UserDefaultResDto response = new UserDefaultResDto(user);

        return ResponseEntity.status(SELF_USER_FOUND.getHttpStatus())
                .body(DefaultResDto.singleDataBuilder()
                        .responseCode(SELF_USER_FOUND.name())
                        .responseMessage(SELF_USER_FOUND.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "토큰 재발급",
            notes = "<응답 코드>\n" +
                    "- 200 = TOKEN_RENEWED\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PostMapping("/token")
    public ResponseEntity<DefaultResDto<Object>> renewToken(HttpServletRequest servletRequest,
                                                            @RequestBody @Valid UserRenewTokenReqDto request) {
        User user = jwtProvider.authorizeUserRefreshJwt(servletRequest.getHeader("Refresh-Token"));

        userService.updateToken(user, request);

        HttpHeaders headers = jwtProvider.generateUserJwt(user.getId(), user.getRoles());

        return ResponseEntity.status(TOKEN_RENEWED.getHttpStatus())
                .headers(headers)
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(TOKEN_RENEWED.name())
                        .responseMessage(TOKEN_RENEWED.getMessage())
                        .build());
    }

    @ApiOperation(value = "아이디 찾기",
            notes = "<응답 코드>\n" +
                    "- 200 = USERNAME_EMAIL_SENT\n" +
                    "- 400 = EMAIL_FIELD_REQUIRED || EMAIL_FORMAT_INVALID\n" +
                    "- 404 = CONTACT_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR || EMAIL_SEND_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PostMapping("/username")
    public ResponseEntity<DefaultResDto<Object>> forgotUsername(@RequestBody @Valid UserFindUsernameReqDto request) {
        userService.sendUsernameToEmail(request.getEmail());

        return ResponseEntity.status(USERNAME_EMAIL_SENT.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(USERNAME_EMAIL_SENT.name())
                        .responseMessage(USERNAME_EMAIL_SENT.getMessage())
                        .build());
    }

    @ApiOperation(value = "비밀번호 찾기",
            notes = "<응답 코드>\n" +
                    "- 200 = PASSWORD_EMAIL_SENT\n" +
                    "- 400 = EMAIL_FIELD_REQUIRED || USERNAME_FIELD_REQUIRED || EMAIL_FORMAT_INVALID || " +
                    "USERNAME_EMAIL_MATCH_INVALID\n" +
                    "- 404 = CONTACT_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR || EMAIL_SEND_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PostMapping("/password")
    public ResponseEntity<DefaultResDto<Object>> findPassword(@RequestBody @Valid UserFindPasswordReqDto request) {
        userService.sendPasswordToEmail(request);

        return ResponseEntity.status(PASSWORD_EMAIL_SENT.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(PASSWORD_EMAIL_SENT.name())
                        .responseMessage(PASSWORD_EMAIL_SENT.getMessage())
                        .build());
    }

    @ApiOperation(value = "비밀번호 검증",
            notes = "<응답 코드>\n" +
                    "- 200 = PASSWORD_VERIFIED\n" +
                    "- 400 = PASSWORD_FIELD_REQUIRED\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED || PASSWORD_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PostMapping("/password/verify")
    public ResponseEntity<DefaultResDto<Object>> verifyPassword(HttpServletRequest servletRequest,
                                                                @RequestBody @Valid UserVerifyReqDto request) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        userService.verifyPassword(user, request.getPassword());

        return ResponseEntity.status(PASSWORD_VERIFIED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(PASSWORD_VERIFIED.name())
                        .responseMessage(PASSWORD_VERIFIED.getMessage())
                        .build());
    }

    @ApiOperation(value = "닉네임 업데이트",
            notes = "<응답 코드>\n" +
                    "- 200 = NICKNAME_UPDATED\n" +
                    "- 400 = NICKNAME_FIELD_REQUIRED || NICKNAME_LENGTH_INVALID || NICKNAME_FORMAT_INVALID\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 409 = UNAVAILABLE_NICKNAME || EXISTING_NICKNAME\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "409", description = "CONFLICT"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PatchMapping("/nickname")
    public ResponseEntity<DefaultResDto<Object>> updateNickname(HttpServletRequest servletRequest,
                                                                @RequestBody @Valid UserNicknameUpdateReqDto request) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        userService.updateNickname(user, request.getNickname());

        return ResponseEntity.status(NICKNAME_UPDATED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(NICKNAME_UPDATED.name())
                        .responseMessage(NICKNAME_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "비밀번호 업데이트",
            notes = "<응답 코드>\n" +
                    "- 200 = PASSWORD_UPDATED\n" +
                    "- 400 = PASSWORD_FIELD_REQUIRED || PASSWORD_RE_ENTERED_FIELD_REQUIRED || PASSWORD_LENGTH_INVALID" +
                    " || PASSWORD_FORMAT_INVALID || PASSWORD_MATCH_INVALID\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PatchMapping("/password")
    public ResponseEntity<DefaultResDto<Object>> updatePassword(HttpServletRequest servletRequest,
                                                                @RequestBody @Valid UserUpdatePasswordReqDto request) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        userService.updatePassword(user, request.getPassword(), request.getPassword(), false);

        return ResponseEntity.status(PASSWORD_UPDATED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(PASSWORD_UPDATED.name())
                        .responseMessage(PASSWORD_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "알림 업데이트",
            notes = "<응답 코드>" +
                    "- 200 = IS_NOTIFIED_UPDATED\n" +
                    "- 400 = IS_NOTIFIED_FIELD_REQUIRED\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PatchMapping("/notified")
    public ResponseEntity<DefaultResDto<Object>> updateIsNotified(HttpServletRequest servletRequest,
                                                                  @RequestBody @Valid
                                                                  UserIsNotifiedUpdateReqDto request) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        userService.updateIsNotified(user, request.getIsNotified());

        return ResponseEntity.status(IS_NOTIFIED_UPDATED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(IS_NOTIFIED_UPDATED.name())
                        .responseMessage(IS_NOTIFIED_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "회원 탈퇴",
            notes = "<응답 코드>\n" +
                    "- 200 = USER_DELETED\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @DeleteMapping
    public ResponseEntity<DefaultResDto<Object>> deactivate(HttpServletRequest servletRequest) {
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        userService.deleteAccount(user);

        return ResponseEntity.status(USER_DELETED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(USER_DELETED.name())
                        .responseMessage(USER_DELETED.getMessage())
                        .build());
    }
}
