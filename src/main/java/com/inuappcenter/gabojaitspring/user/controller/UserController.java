package com.inuappcenter.gabojaitspring.user.controller;

import com.inuappcenter.gabojaitspring.auth.JwtProvider;
import com.inuappcenter.gabojaitspring.auth.JwtType;
import com.inuappcenter.gabojaitspring.common.DefaultResponseDto;
import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.user.domain.User;
import com.inuappcenter.gabojaitspring.user.dto.*;
import com.inuappcenter.gabojaitspring.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import java.util.List;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "회원")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "아이디 중복여부 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "아이디 중복 확인 완료"),
            @ApiResponse(responseCode = "400", description = "사용자 에러"),
            @ApiResponse(responseCode = "409", description = "이미 사용중인 아이디")
    })
    @GetMapping("/duplicate/{username}")
    public ResponseEntity<DefaultResponseDto<Object>> duplicateUsername(
            @PathVariable
            @NotBlank(message = "아이디를 입력해주세요.")
            @Size(min = 5, max = 15, message = "아이디는 5~15자만 가능합니다.")
            @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디 형식은 영문과 숫자의 조합만 가능합니다")
            String username
            ) {
        userService.isExistingUsername(username);

        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("NO_DUPLICATE_USERNAME")
                        .responseMessage("아이디 중복 확인 완료")
                        .build());
    }

    @ApiOperation(value = "닉네임 중복여부 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 중복 확인 완료"),
            @ApiResponse(responseCode = "400", description = "사용자 에러"),
            @ApiResponse(responseCode = "409", description = "이미 사용중인 아이디")
    })
    @GetMapping("/duplicate/{nickname}")
    public ResponseEntity<DefaultResponseDto<Object>> duplciateNickname(
            @PathVariable
            @NotBlank(message = "닉네임을 입력해주세요.")
            @Size(min = 3, max = 10, message = "닉네임은 3~10자만 가능합니다.")
            String nickname
    ) {
        userService.isExistingNickname(nickname);

        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("NO_DUPLICATE_NICKNAME")
                        .responseMessage("닉네임 중복 확인 완료")
                        .build());
    }

    @ApiOperation(value = "회원 가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원 가입 완료",
                    content = @Content(schema = @Schema(implementation = UserDefaultResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "사용자 에러"),
            @ApiResponse(responseCode = "409", description = "중복 여부확인 에러"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("/new")
    public ResponseEntity<DefaultResponseDto<Object>> create(@RequestBody @Valid UserSaveRequestDto request) {
        userService.isExistingUsername(request.getUsername());
        userService.isExistingNickname(request.getNickname());

        ObjectId userId = userService.save(request);
        User user = userService.findOne(userId);

        String[] tokens = jwtProvider.generateJwt(user);
        HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.add("ACCESS-TOKEN", tokens[0]);
        responseHeader.add("REFRESH-TOKEN", tokens[1]);

        UserDefaultResponseDto responseBody = new UserDefaultResponseDto(user);

        return ResponseEntity.status(201)
                .headers(responseHeader)
                .body(DefaultResponseDto.builder()
                        .responseCode("USER_REGISTERED")
                        .responseMessage("회원 가입 완료")
                        .data(responseBody)
                        .build());
    }

    @ApiOperation(value = "로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 완료",
                    content = @Content(schema = @Schema(implementation = UserDefaultResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "사용자 에러"),
            @ApiResponse(responseCode = "401", description = "로그인 실패")
    })
    @PostMapping("/login")
    public ResponseEntity<DefaultResponseDto<Object>> login(@RequestBody @Valid UserLoginRequestDto request) {
        User user = userService.login(request);

        HttpHeaders responseHeader = userService.generateJwtToken(user);
        UserDefaultResponseDto responseBody = new UserDefaultResponseDto(user);

        return ResponseEntity.status(200)
                .headers(responseHeader)
                .body(DefaultResponseDto.builder()
                        .responseCode("LOGIN_SUCCESS")
                        .responseMessage("로그인 완료")
                        .data(responseBody)
                        .build());
    }

    @ApiOperation(value = "단건 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단건 조회 및 토큰 재발급 완료",
                    content = @Content(schema = @Schema(implementation = UserDefaultResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "토큰 에러"),
            @ApiResponse(responseCode = "404", description = "존재하지 않은 회원")
    })
    @GetMapping
    public ResponseEntity<DefaultResponseDto<Object>> findOne(HttpServletRequest servletRequest) {
        List<String> tokenInfo = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION));

        if (!tokenInfo.get(1).equals(JwtType.ACCESS.name())) {
            throw new CustomException(TOKEN_AUTHORIZATION_FAIL);
        }

        User user = userService.findOneByUsername(tokenInfo.get(0));

        UserDefaultResponseDto response = new UserDefaultResponseDto(user);

        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("USER_INFO_FOUND")
                        .responseMessage("단건 조회 완료")
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "토큰 재발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 재발급 완료"),
            @ApiResponse(responseCode = "401", description = "토큰 에러"),
            @ApiResponse(responseCode = "404", description = "존재하지 않은 회원")
    })
    @GetMapping("/auth")
    public ResponseEntity<DefaultResponseDto<Object>> renewToken(HttpServletRequest servletRequest) {
        List<String> tokenInfo = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION));

        if (!tokenInfo.get(1).equals(JwtType.REFRESH.name())) {
            throw new CustomException(TOKEN_AUTHORIZATION_FAIL);
        }

        User user = userService.findOneByUsername(tokenInfo.get(0));

        String[] tokens = jwtProvider.generateJwt(user);
        HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.add("ACCESS-TOKEN", tokens[0]);
        responseHeader.add("REFRESH-TOKEN", tokens[1]);

        return ResponseEntity.status(200)
                .headers(responseHeader)
                .body(DefaultResponseDto.builder()
                        .responseMessage("TOKEN_RENEWED")
                        .responseMessage("토큰 재발급 완료")
                        .build());
    }

    @ApiOperation(value = "아이디 찾기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "아이디 찾기 완료"),
            @ApiResponse(responseCode = "400", description = "유저 에러"),
            @ApiResponse(responseCode = "404", description = "존재하지 않은 회원"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/findUsername/{email}")
    public ResponseEntity<DefaultResponseDto<Object>> forgotUsername(
            @PathVariable
            @NotBlank(message = "이메일을 입력해 주세요.")
            @Email(message = "올바른 이메일 형식이 아닙니다.")
            String email
    ) {
        userService.findForgotUsername(email);

        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("USERNAME_EMAIL_SENT")
                        .responseMessage("아이디를 이메일로 전송 완료")
                        .build());
    }

    @ApiOperation(value = "비밀번호 찾기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 찾기 완료"),
            @ApiResponse(responseCode = "400", description = "유저 에러"),
            @ApiResponse(responseCode = "404", description = "존재하지 않은 아이디 또는 이메일"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PatchMapping("/findPw/{username}/{email}")
    public ResponseEntity<DefaultResponseDto<Object>> forgotPw(
            @PathVariable
            @NotBlank(message = "아이디를 입력해주세요.")
            String username,

            @PathVariable
            @NotBlank(message = "이메일을 입력해주세요.")
            @Email(message = "올바른 이메일 형식이 아닙니다.")
            String email
    ) {
        userService.resetForgotPassword(username, email);

        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("PASSWORD_EMAIL_SENT")
                        .responseMessage("임시 비밀번호를 이메일로 전송 완료")
                        .build());
    }

    @ApiOperation(value = "비밀번호 업데이트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 업데이트 완료"),
            @ApiResponse(responseCode = "400", description = "유저 에러"),
            @ApiResponse(responseCode = "401", description = "토큰 에러"),
            @ApiResponse(responseCode = "404", description = "존재하지 않은 회원"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PatchMapping("/password")
    public ResponseEntity<DefaultResponseDto<Object>> updatePw(HttpServletRequest servletRequest,
                                                              @RequestBody @Valid UserUpdatePasswordRequestDto request)
    {
        List<String> tokenInfo = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION));

        if (!tokenInfo.get(1).equals(JwtType.ACCESS.name())) {
            throw new CustomException(TOKEN_AUTHORIZATION_FAIL);
        }

        User user = userService.findOneByUsername(tokenInfo.get(0));

        user = userService.updatePassword(user, request);

        UserDefaultResponseDto response = new UserDefaultResponseDto(user);

        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("PASSWORD_UPDATED")
                        .responseMessage("비밀번호 업데이트 완료")
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "닉네임 업데이트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 업데이트 완료"),
            @ApiResponse(responseCode = "400", description = "사용자 에러"),
            @ApiResponse(responseCode = "404", description = "존재하지 않은 회원"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PatchMapping("/{nickname}")
    public ResponseEntity<DefaultResponseDto<Object>> updateNickname(
            HttpServletRequest servletRequest,

            @PathVariable
            @NotBlank(message = "모든 필수 정보를 입력해 주세요.")
            @Size(min = 2, max = 8, message = "닉네임은 2~8자만 가능합니다.")
            String nickname
    ) {
        List<String> tokenInfo = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION));

        if (!tokenInfo.get(1).equals(JwtType.ACCESS.name())) {
            throw new CustomException(TOKEN_AUTHORIZATION_FAIL);
        }

        User user = userService.findOneByUsername(tokenInfo.get(0));

        userService.isExistingNickname(nickname);
        user = userService.updateNickname(user, nickname);

        UserDefaultResponseDto response = new UserDefaultResponseDto(user);

        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("NICKNAME_UPDATED")
                        .responseMessage("닉네임 업데이트 완료")
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "회원 탈퇴")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 완료"),
            @ApiResponse(responseCode = "401", description = "토큰 에러"),
            @ApiResponse(responseCode = "404", description = "존재하지 않은 회원"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PatchMapping("/deactivate")
    public ResponseEntity<DefaultResponseDto<Object>> deactivate(HttpServletRequest servletRequest,
                                                                 @RequestBody @Valid UserDeactivateRequestDto request) {
        List<String> tokenInfo = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION));

        if (!tokenInfo.get(1).equals(JwtType.ACCESS.name())) {
            throw new CustomException(TOKEN_AUTHORIZATION_FAIL);
        }

        User user = userService.findOneByUsername(tokenInfo.get(0));

        userService.deactivate(user);

        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("USER_DEACTIVATED")
                        .responseMessage("회원 탈퇴 완료")
                        .build());
    }

    // TODO: 배포 전 삭제 필요
    @ApiOperation(value = "전체 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "전체 삭제 완료"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @DeleteMapping("/all")
    public ResponseEntity<DefaultResponseDto<Object>> deleteAll() {
        userService.deleteAll();

        return ResponseEntity.status(204)
                .body(DefaultResponseDto.builder()
                        .build());
    }
}
