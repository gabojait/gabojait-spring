package com.inuappcenter.gabojaitspring.user.controller;

import com.inuappcenter.gabojaitspring.auth.JwtProvider;
import com.inuappcenter.gabojaitspring.auth.JwtType;
import com.inuappcenter.gabojaitspring.common.DefaultResDto;
import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.dto.res.UserProfileDefaultResDto;
import com.inuappcenter.gabojaitspring.user.domain.Contact;
import com.inuappcenter.gabojaitspring.user.domain.type.Gender;
import com.inuappcenter.gabojaitspring.user.domain.type.Role;
import com.inuappcenter.gabojaitspring.user.domain.User;
import com.inuappcenter.gabojaitspring.user.dto.req.*;
import com.inuappcenter.gabojaitspring.user.dto.res.UserDefaultResDto;
import com.inuappcenter.gabojaitspring.user.service.ContactService;
import com.inuappcenter.gabojaitspring.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.inuappcenter.gabojaitspring.common.SuccessCode.*;
import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.TOKEN_AUTHENTICATION_FAIL;
import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.TOKEN_NOT_ALLOWED;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "회원")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final ContactService contactService;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "아이디 중복여부 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "NO_DUPLICATE_USERNAME",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400",
                    description = "USERNAME_LENGTH_INVALID / USERNAME_FORMAT_INVALID"),
            @ApiResponse(responseCode = "409", description = "EXISTING_USERNAME")
    })
    @GetMapping("/username/duplicate/{username}")
    public ResponseEntity<DefaultResDto<Object>> duplicateUsername(
            @PathVariable
            @Size(min = 5, max = 15, message = "아이디는 5~15자만 가능합니다.")
            @Pattern(regexp = "^(?=.*[A-z0-9])[A-z0-9]+$", message = "아이디는 영문과 숫자의 형식만 가능합니다.")
            String username) {

        userService.isExistingUsername(username);

        return ResponseEntity.status(USERNAME_NO_DUPLICATE.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(USERNAME_NO_DUPLICATE.name())
                        .responseMessage(USERNAME_NO_DUPLICATE.getMessage())
                        .build());
    }

    @ApiOperation(value = "가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "USER_REGISTERED",
                    content = @Content(schema = @Schema(implementation = UserDefaultResDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "FIELD_REQUIRED / *_LENGTH_INVALID / *_FORMAT_INVALID / PASSWORD_MATCH_INVALID"),
            @ApiResponse(responseCode = "409",
                    description = "EXISTING_USERNAME / EXISTING_NICKNAME / EMAIL_VERIFICATION_INVALID"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<DefaultResDto<Object>> create(@RequestBody @Valid UserSaveReqDto request) {

        userService.isExistingUsername(request.getUsername());
        userService.isExistingNickname(request.getNickname());
        Gender gender = Gender.fromString(request.getGender());
        String encodedPassword = userService.validatePwAndPwReEnterAndEncode(
                request.getPassword(),
                request.getPasswordReEntered()
        );


        Contact contact = contactService.findOneUnregisteredByEmail(request.getEmail());
        contactService.register(contact);

        User user = userService.create(request, encodedPassword, gender, contact);

        String newTokens = jwtProvider.generateJwt(String.valueOf(user.getId()), user.getRoles());

        UserDefaultResDto responseBody = new UserDefaultResDto(user);

        return ResponseEntity.status(USER_REGISTERED.getHttpStatus())
                .header(AUTHORIZATION, newTokens)
                .body(DefaultResDto.builder()
                        .responseCode(USER_REGISTERED.name())
                        .responseMessage(USER_REGISTERED.getMessage())
                        .data(responseBody)
                        .build());
    }

    @ApiOperation(value = "닉네임 중복여부 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "NO_DUPLICATE_NICKNAME",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400",
                    description = "NICKNAME_LENGTH_INVALID / NICKNAME_PATTERN_INVALID"),
            @ApiResponse(responseCode = "409", description = "EXISTING_NICKNAME")
    })
    @GetMapping("/nickname/duplicate/{nickname}")
    public ResponseEntity<DefaultResDto<Object>> duplicateNickname(
            @PathVariable
            @Size(min = 2, max = 8, message = "닉네임은 2~8자만 가능합니다.")
            @Pattern(regexp = "^[가-힣]+$", message = "닉네임은 한글 형식만 가능합니다.")
            String nickname
    ) {
        userService.isExistingUsername(nickname);

        return ResponseEntity.status(NICKNAME_NO_DUPLICATE.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(NICKNAME_NO_DUPLICATE.name())
                        .responseMessage(NICKNAME_NO_DUPLICATE.getMessage())
                        .build());
    }

    @ApiOperation(value = "닉네임 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "NICKNAME_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "NICKNAME_LENGTH_INVALID / " +
                    "NICKNAME_FORMAT_INVALID"),
            @ApiResponse(responseCode = "401", description = " TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "409", description = "EXISTING_NICKNAME"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PatchMapping("/nickname/update/{nickname}")
    public ResponseEntity<DefaultResDto<Object>> updateNickname(
            HttpServletRequest servletRequest,
            @PathVariable
            @Size(min = 2, max = 8, message = "닉네임은 2~8자만 가능합니다.")
            @Pattern(regexp = "^[가-힣]+$", message = "닉네임은 한글 형식만 가능합니다.")
            String nickname
    ) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));

        userService.isExistingNickname(nickname);

        userService.updateNickname(user, nickname);

        return ResponseEntity.status(NICKNAME_UPDATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(NICKNAME_UPDATED.name())
                        .responseMessage(NICKNAME_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "USER_LOGGED_IN / PASSWORD_FORCE_UPDATE",
                    content = @Content(schema = @Schema(implementation = UserDefaultResDto.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED"),
            @ApiResponse(responseCode = "401", description = "LOGIN_FAIL")
    })
    @PostMapping("/login")
    public ResponseEntity<DefaultResDto<Object>> login(@RequestBody @Valid UserLoginReqDto request) {

        User user = userService.login(request);

        List<String> authorities = new ArrayList<>(user.getRoles());

        String newTokens = jwtProvider.generateJwt(user.getId().toString(), authorities);

        UserDefaultResDto responseBody = new UserDefaultResDto(user);

        if (user.getIsTemporaryPassword()) {

            return ResponseEntity.status(PASSWORD_FORCE_UPDATE.getHttpStatus())
                    .body(DefaultResDto.builder()
                            .responseCode(PASSWORD_FORCE_UPDATE.name())
                            .responseMessage(PASSWORD_FORCE_UPDATE.getMessage())
                            .data(responseBody)
                            .build());
        } else {

            return ResponseEntity.status(USER_LOGGED_IN.getHttpStatus())
                    .header(AUTHORIZATION, newTokens)
                    .body(DefaultResDto.builder()
                            .responseCode(USER_LOGGED_IN.name())
                            .responseMessage(USER_LOGGED_IN.getMessage())
                            .data(responseBody)
                            .build());
        }
    }

    @ApiOperation(value = "본인 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "MY_USER_FOUND",
                    content = @Content(schema = @Schema(implementation = UserDefaultResDto.class))),
            @ApiResponse(responseCode = "401", description = " TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @GetMapping
    public ResponseEntity<DefaultResDto<Object>> findMyself(HttpServletRequest servletRequest) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User user = userService.findOneByUserId(token.get(0));

        UserDefaultResDto responseBody = new UserDefaultResDto(user);

        return ResponseEntity.status(MY_USER_FOUND.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(MY_USER_FOUND.name())
                        .responseMessage(MY_USER_FOUND.getMessage())
                        .data(responseBody)
                        .build());
    }

    @ApiOperation(value = "단건 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "USER_FOUND",
                    content = @Content(schema = @Schema(implementation = UserDefaultResDto.class))),
            @ApiResponse(responseCode = "401", description = " TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @GetMapping("/find/{user-id}")
    public ResponseEntity<DefaultResDto<Object>> findOne(HttpServletRequest servletRequest,
                                                         @PathVariable(value = "user-id") String userId) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        userService.findOneByUserId(token.get(0));

        User user = userService.findOneByUserId(userId);

        UserDefaultResDto responseBody = new UserDefaultResDto(user);

        return ResponseEntity.status(USER_FOUND.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(USER_FOUND.name())
                        .responseMessage(USER_FOUND.getMessage())
                        .data(responseBody)
                        .build());
    }

    @ApiOperation(value = "토큰 재발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TOKEN_RENEWED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PostMapping("/token/renew")
    public ResponseEntity<DefaultResDto<Object>> renewToken(HttpServletRequest servletRequest) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.REFRESH.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));

        List<String> authorities = new ArrayList<>(List.of(Role.USER.name()));
        String newTokens = jwtProvider.generateJwt(String.valueOf(user.getId()), authorities);

        return ResponseEntity.status(USER_TOKEN_RENEWED.getHttpStatus())
                .header(AUTHORIZATION, newTokens)
                .body(DefaultResDto.builder()
                        .responseCode(USER_TOKEN_RENEWED.name())
                        .responseMessage(USER_TOKEN_RENEWED.getMessage())
                        .build());
    }

    @ApiOperation(value = "아이디 찾기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "USERNAME_EMAIL_SENT",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / EMAIL_FORMAT_INVALID"),
            @ApiResponse(responseCode = "404", description = "EMAIL_NOT_FOUND / USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR / MAIL_SENDING_ERROR")
    })
    @PostMapping("/username/find")
    public ResponseEntity<DefaultResDto<Object>> forgotUsername(@RequestBody @Valid
                                                                UserForgotUsernameReqDto request) {

        Contact contact = contactService.findOneRegisteredByEmail(request.getEmail());
        User user = userService.findOneByContact(contact);

        userService.sendUsernameEmail(user);

        return ResponseEntity.status(USERNAME_EMAIL_SENT.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(USERNAME_EMAIL_SENT.name())
                        .responseMessage(USERNAME_EMAIL_SENT.getMessage())
                        .build());
    }

    @ApiOperation(value = "비밀번호 찾기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PASSWORD_EMAIL_SENT",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / EMAIL_FORMAT_INVALID"),
            @ApiResponse(responseCode = "401", description = "USERNAME_EMAIL_NO_MATCH"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / EMAIL_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR / MAIL_SENDING_ERROR")
    })
    @PostMapping("/password/find")
    public ResponseEntity<DefaultResDto<Object>> forgotPassword(@RequestBody @Valid
                                                                UserForgotPasswordReqDto request) {

        User user = userService.findOneByUsername(request.getUsername());
        Contact contact = contactService.findOneRegisteredByEmail(request.getEmail());

        userService.resetPasswordAndSendEmail(user, contact.getEmail());

        return ResponseEntity.status(PASSWORD_EMAIL_SENT.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(PASSWORD_EMAIL_SENT.name())
                        .responseMessage(PASSWORD_EMAIL_SENT.getMessage())
                        .build());
    }

    @ApiOperation(value = "비밀번호 검증")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PASSWORD_VERIFIED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PostMapping("/password/verify")
    public ResponseEntity<DefaultResDto<Object>> verifyPassword(
            HttpServletRequest servletRequest,
            @RequestBody @Valid UserVerifyPasswordReqDto request
    ) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));

        userService.validatePassword(user.getPassword(), request.getPassword());

        return ResponseEntity.status(PASSWORD_VERIFIED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(PASSWORD_VERIFIED.name())
                        .responseMessage(PASSWORD_VERIFIED.getMessage())
                        .build());
    }

    @ApiOperation(value = "비밀번호 업데이트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PASSWORD_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / PASSWORD_LENGTH_INVALID / " +
                    "PASSWORD_MATCH_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR / MAIL_SENDING_ERROR")
    })
    @PatchMapping("/password")
    public ResponseEntity<DefaultResDto<Object>> updatePassword(
            HttpServletRequest servletRequest,
            @RequestBody @Valid UserUpdatePasswordReqDto request
    ) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));

        String encodedPassword = userService.validatePwAndPwReEnterAndEncode(
                request.getNewPassword(),
                request.getNewPasswordReEntered()
        );

        userService.updatePassword(user, encodedPassword, request.getIsTemporaryPassword());

        return ResponseEntity.status(PASSWORD_UPDATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(PASSWORD_UPDATED.name())
                        .responseMessage(PASSWORD_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "탈퇴")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "USER_DEACTIVATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "401", description = " TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @DeleteMapping
    public ResponseEntity<DefaultResDto<Object>> deactivate(HttpServletRequest servletRequest) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));

        userService.deactivate(user);
        contactService.deactivate(user.getContact());
        // TODO: Must deactivate Profile, etc. after implementation.

        return ResponseEntity.status(USER_DEACTIVATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(USER_DEACTIVATED.name())
                        .responseMessage(USER_DEACTIVATED.getMessage())
                        .build());
    }


}
