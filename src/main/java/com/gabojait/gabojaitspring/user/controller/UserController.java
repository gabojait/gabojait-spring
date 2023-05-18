package com.gabojait.gabojaitspring.user.controller;

import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.dto.DefaultResDto;
import com.gabojait.gabojaitspring.user.domain.Contact;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.dto.req.*;
import com.gabojait.gabojaitspring.user.dto.res.UserDefaultResDto;
import com.gabojait.gabojaitspring.user.service.ContactService;
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
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "회원")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final ContactService contactService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "아이디 중복여부 확인",
            notes = "* username = NotBlank && Size(min = 5, max = 15) && Pattern(regex = ^(?=.*[a-z0-9])[a-z0-9]+$)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "USERNAME_AVAILABLE",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "USERNAME_LENGTH_INVALID / USERNAME_FORMAT_INVALID"),
            @ApiResponse(responseCode = "409", description = "UNAVAILABLE_USERNAME / EXISTING_USERNAME"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @GetMapping("/username")
    public ResponseEntity<DefaultResDto<Object>> duplicateUsername(
            @RequestParam(value = "username")
            @Size(min = 5, max = 15, message = "아이디는 5~15자만 가능합니다.")
            @Pattern(regexp = "^(?=.*[a-z0-9])[a-z0-9]+$", message = "아이디는 소문자 영어와 숫자의 조합으로 입력해 주세요.")
            String username
    ) {
        // main
        userService.validateUsername(username);

        return ResponseEntity.status(USERNAME_AVAILABLE.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(USERNAME_AVAILABLE.name())
                        .responseMessage(USERNAME_AVAILABLE.getMessage())
                        .build());
    }

    @ApiOperation(value = "닉네임 중복여부 확인",
            notes = "* nickname = NotBlank && Size(min = 2, max = 8) && Pattern(regex = ^[가-힣]+$)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "NICKNAME_AVAILABLE",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "NICKNAME_LENGTH_INVALID / NICKNAME_FORMAT_INVALID"),
            @ApiResponse(responseCode = "409", description = "UNAVAILABLE_NICKNAME / EXISTING_NICKNAME"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @GetMapping("/nickname")
    public ResponseEntity<DefaultResDto<Object>> duplicateNickname(
            @RequestParam(value = "nickname")
            @Size(min = 2, max = 8, message = "닉네임은 2~8자만 가능합니다.")
            @Pattern(regexp = "^[가-힣]+$", message = "닉닉네임은 한글 조합으로 입력해 주세요.")
            String nickname
    ) {
        // main
        userService.validateNickname(nickname);

        return ResponseEntity.status(NICKNAME_AVAILABLE.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(NICKNAME_AVAILABLE.name())
                        .responseMessage(NICKNAME_AVAILABLE.getMessage())
                        .build());
    }

    @ApiOperation(value = "가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "USER_REGISTERED",
                    content = @Content(schema = @Schema(implementation = UserDefaultResDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "*_FIELD_REQUIRED / *_LENGTH_INVALID / *_FORMAT_INVALID / GENDER_TYPE_INVALID / " +
                            "PASSWORD_MATCH_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "CONTACT_NOT_FOUND"),
            @ApiResponse(responseCode = "409",
                    description = "EXISTING_USERNAME / UNAVAILABLE_NICKNAME / EXISTING_NICKNAME"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<DefaultResDto<Object>> register(HttpServletRequest servletRequest,
                                                          @RequestBody @Valid UserRegisterReqDto request) {
        // auth
        jwtProvider.authorizeGuestAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // sub
        userService.validatePreRegister(request);
        // main
        Contact contact = contactService.registerContact(request.getEmail());
        User user = userService.register(request, contact);

        // response
        HttpHeaders headers = jwtProvider.generateUserJwt(user.getId(), user.getRoles());
        UserDefaultResDto response = new UserDefaultResDto(user);

        return ResponseEntity.status(USER_REGISTERED.getHttpStatus())
                .headers(headers)
                .body(DefaultResDto.singleDataBuilder()
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "USER_LOGIN",
                    content = @Content(schema = @Schema(implementation = UserDefaultResDto.class))),
            @ApiResponse(responseCode = "400", description = "*_FIELD_REQUIRED"),
            @ApiResponse(responseCode = "401", description = "LOGIN_FAIL"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @PostMapping("/login")
    public ResponseEntity<DefaultResDto<Object>> login(@RequestBody @Valid UserLoginReqDto request) {
        // auth & main
        User user = userService.login(request);
        // sub
        userService.updateLastRequestDate(user);

        // response
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

    @ApiOperation(value = "본인 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SELF_USER_FOUND",
                    content = @Content(schema = @Schema(implementation = UserDefaultResDto.class))),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @GetMapping
    public ResponseEntity<DefaultResDto<Object>> findMyself(HttpServletRequest servletRequest) {
        // auth & main
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // response
        UserDefaultResDto response = new UserDefaultResDto(user);

        return ResponseEntity.status(SELF_USER_FOUND.getHttpStatus())
                .body(DefaultResDto.singleDataBuilder()
                        .responseCode(SELF_USER_FOUND.name())
                        .responseMessage(SELF_USER_FOUND.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "단건 조회", notes = "* user-id = NotBlank")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "USER_FOUND",
                    content = @Content(schema = @Schema(implementation = UserDefaultResDto.class))),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @GetMapping("/{user-id}")
    public ResponseEntity<DefaultResDto<Object>> findOther(HttpServletRequest servletRequest,
                                                           @PathVariable(value = "user-id")
                                                           String userId) {
        // auth
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // main
        User otherUser = userService.findOneOther(user, userId);

        // response
        UserDefaultResDto response = new UserDefaultResDto(otherUser);

        return ResponseEntity.status(USER_FOUND.getHttpStatus())
                .body(DefaultResDto.singleDataBuilder()
                        .responseCode(USER_FOUND.name())
                        .responseMessage(USER_FOUND.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "토큰 재발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TOKEN_RENEWED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @GetMapping("/token")
    public ResponseEntity<DefaultResDto<Object>> renewToken(HttpServletRequest servletRequest) {
        // auth
        User user = jwtProvider.authorizeUserRefreshJwt(servletRequest.getHeader("Refresh-Token"));

        // sub
        userService.updateLastRequestDate(user);
        // main & response
        HttpHeaders headers = jwtProvider.generateUserJwt(user.getId(), user.getRoles());

        return ResponseEntity.status(TOKEN_RENEWED.getHttpStatus())
                .headers(headers)
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(TOKEN_RENEWED.name())
                        .responseMessage(TOKEN_RENEWED.getMessage())
                        .build());
    }

    @ApiOperation(value = "아이디 찾기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "USERNAME_EMAIL_SENT",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "EMAIL_FIELD_REQUIRED / EMAIL_FORMAT_INVALID"),
            @ApiResponse(responseCode = "404", description = "CONTACT_NOT_FOUND / USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR / EMAIL_SEND_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @PostMapping("/username")
    public ResponseEntity<DefaultResDto<Object>> findUsername(@RequestBody @Valid UserFindUsernameReqDto request) {
        // main
        Contact contact = contactService.findOneRegistered(request.getEmail());
        userService.sendUsernameEmail(contact);

        return ResponseEntity.status(USERNAME_EMAIL_SENT.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(USERNAME_EMAIL_SENT.name())
                        .responseMessage(USERNAME_EMAIL_SENT.getMessage())
                        .build());
    }

    @ApiOperation(value = "비밀번호 찾기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PASSWORD_EMAIL_SENT",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400",
                    description = "*_FIELD_REQUIRED / EMAIL_FORMAT_INVALID / USERNAME_EMAIL_MATCH_INVALID"),
            @ApiResponse(responseCode = "404", description = "CONTACT_NOT_FOUND / USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR / EMAIL_SEND_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @PostMapping("/password")
    public ResponseEntity<DefaultResDto<Object>> findPassword(@RequestBody @Valid UserFindPasswordReqDto request) {
        // main
        Contact contact = contactService.findOneRegistered(request.getEmail());
        userService.sendPasswordEmail(contact, request.getUsername());

        return ResponseEntity.status(PASSWORD_EMAIL_SENT.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(PASSWORD_EMAIL_SENT.name())
                        .responseMessage(PASSWORD_EMAIL_SENT.getMessage())
                        .build());
    }

    @ApiOperation(value = "비밀번호 검증")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PASSWORD_VERIFIED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "PASSWORD_FIELD_REQUIRED / PASSWORD_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @PostMapping("/password/verify")
    public ResponseEntity<DefaultResDto<Object>> verifyPassword(HttpServletRequest servletRequest,
                                                                @RequestBody @Valid UserVerifyReqDto request) {
        // auth
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // main
        userService.validatePassword(user, request.getPassword());

        return ResponseEntity.status(PASSWORD_VERIFIED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(PASSWORD_VERIFIED.name())
                        .responseMessage(PASSWORD_VERIFIED.getMessage())
                        .build());
    }

    @ApiOperation(value = "비밀번호 업데이트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PASSWORD_VERIFIED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400",
                    description = "*_FIELD_REQUIRED / PASSWORD_LENGTH_INVALID / PASSWORD_FORMAT_INVALID / " +
                            "PASSWORD_MATCH_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @PatchMapping("/password")
    public ResponseEntity<DefaultResDto<Object>> updatePassword(HttpServletRequest servletRequest,
                                                                @RequestBody @Valid UserUpdatePasswordReqDto request) {
        // auth
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // main
        userService.updatePassword(user, request.getPassword(), request.getPasswordReEntered(), false);

        return ResponseEntity.status(PASSWORD_VERIFIED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(PASSWORD_VERIFIED.name())
                        .responseMessage(PASSWORD_VERIFIED.getMessage())
                        .build());
    }

    @ApiOperation(value = "탈퇴")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "USER_DELETED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @DeleteMapping
    public ResponseEntity<DefaultResDto<Object>> delete(HttpServletRequest servletRequest) {
        // auth
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // main
        // TODO soft delete Education, Work, etc.
        userService.softDelete(user);
        contactService.softDelete(user.getContact());

        return ResponseEntity.status(USER_DELETED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(USER_DELETED.name())
                        .responseMessage(USER_DELETED.getMessage())
                        .build());
    }
}
