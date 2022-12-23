package com.inuappcenter.gabojaitspring.user.controller;

import com.inuappcenter.gabojaitspring.common.DefaultResponseDto;
import com.inuappcenter.gabojaitspring.user.dto.*;
import com.inuappcenter.gabojaitspring.user.service.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "User")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @ApiOperation(value = "유저 아이디 중복 여부 확인")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "유저 아이디 중복 확인 성공"),
            @ApiResponse(code = 400, message = "유저 아이디 입력 에러"),
            @ApiResponse(code = 409, message = "유저 아이디 중복 확인 실패"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @GetMapping("/duplicate/{username}")
    public ResponseEntity<Object> duplicateUsername(
            @PathVariable
            @NotBlank(message = "아이디를 입력해 주세요")
            @Size(min = 5, max = 15, message = "아이디는 5~15자만 가능합니다")
            @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디 형식은 영문과 숫자의 조합만 가능합니다")
            String username
    ) {
        userService.isExistingUsername(username);

        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("OK")
                        .responseMessage("유저 아이디 중복 확인 완료")
                        .build());
    }

    @ApiOperation(value = "유저 가입")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "유저 가입 성공"),
            @ApiResponse(code = 400, message = "유저 정보 입력 에러"),
            @ApiResponse(code = 401, message = "유저 비밀번호 에러"),
            @ApiResponse(code = 404, message = "존재하지 않은 유저"),
            @ApiResponse(code = 409, message = "유저 아이디 또는 닉네임 중복 검사 실패"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PostMapping("/new")
    public ResponseEntity<Object> signUp(@RequestBody @Valid UserSaveRequestDto request) {
        userService.save(request);

        String[] token = userService.generateToken(request.getUsername(), request.getPassword());
        HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.add("ACCESS-TOKEN", token[0]);
        responseHeader.add("REFRESH-TOKEN", token[1]);

        return ResponseEntity.status(201)
                .headers(responseHeader)
                .body(DefaultResponseDto.builder()
                        .responseCode("CREATED")
                        .responseMessage("유저 가입 완료")
                        .build());
    }

    @ApiOperation(value = "유저 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "유저 정보 조회 성공"),
            @ApiResponse(code = 401, message = "토큰 인증 실패"),
            @ApiResponse(code = 404, message = "존재하지 않은 유저"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @GetMapping()
    public ResponseEntity<Object> findOneUser(HttpServletRequest servletRequest) {
        String token = servletRequest.getHeader(AUTHORIZATION);
        UserDefaultResponseDto response = userService.findOneUserByToken(token);

        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("OK")
                        .responseMessage("유저 정보 불러오기 완료")
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "유저 로그인")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "유저 로그인 성공"),
            @ApiResponse(code = 401, message = "유저 로그인 실패"),
            @ApiResponse(code = 404, message = "존재하지 않은 유저")
    })
    @PostMapping("/login")
    public ResponseEntity<Object> signIn(@RequestBody @Valid UserSignInRequestDto request) {
        String[] token = userService.generateToken(request.getUsername(), request.getPassword());
        HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.add("ACCESS-TOKEN", token[0]);
        responseHeader.add("REFRESH-TOKEN", token[1]);

        return ResponseEntity.status(200)
                .headers(responseHeader)
                .body(DefaultResponseDto.builder()
                        .responseCode("OK")
                        .responseMessage("유저 로그인 완료")
                        .build());
    }

    @ApiOperation(value = "유저 토큰 재발급")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "유저 토큰 재발급 성공"),
            @ApiResponse(code = 401, message = "유저 토큰 재발급 실패")
    })
    @GetMapping("/auth")
    public ResponseEntity<Object> refreshToken(HttpServletRequest servletRequest) {
        String[] token = userService.regenerateToken(servletRequest.getHeader(AUTHORIZATION));
        HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.add("ACCESS-TOKEN", token[0]);
        responseHeader.add("REFRESH-TOKEN", token[1]);

        return ResponseEntity.status(200)
                .headers(responseHeader)
                .body(DefaultResponseDto.builder()
                        .responseCode("OK")
                        .responseMessage("유저 토큰 재발급 완료")
                        .build());
    }

    @ApiOperation(value = "유저 아이디 찾기")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "유저 아이디 찾기 성공"),
            @ApiResponse(code = 404, message = "유저 정보 존재하지 않음"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @GetMapping("/findId/{email}")
    public ResponseEntity<Object> forgotId(
            @PathVariable
            @NotBlank(message = "이메일을 입력해 주세요")
            @Email(message = "올바른 이메일 형식이 아닙니다")
            String email
    ) {
        userService.findForgotUsernameByEmail(email);

        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("OK")
                        .responseMessage("유저 아이디 찾기 완료")
                        .build());
    }

    @ApiOperation(value = "유저 비밀번호 찾기")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "유저 비밀번호 찾기 성공"),
            @ApiResponse(code = 404, message = "유저 정보 존재하지 않음"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PatchMapping("/findPw/{username}/{email}")
    public ResponseEntity<Object> forgotPassword(
            @PathVariable
            @NotBlank(message = "아이디를 입력해 주세요")
            @Size(min = 5, max = 15, message = "아이디는 5~15자만 가능합니다")
            @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디 형식은 영문과 숫자의 조합만 가능합니다")
            String username,

            @PathVariable
            @NotBlank(message = "이메일을 입력해 주세요")
            @Email(message = "올바른 이메일 형식이 아닙니다")
            String email
    ) {
        userService.resetPasswordByEmailAndUsername(username, email);

        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("OK")
                        .responseMessage("유저 비밀번호 찾기 완료")
                        .build());
    }

    @ApiOperation(value = "유저 비밀번호 재설정")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "유저 비밀번호 재설정 성공"),
            @ApiResponse(code = 401, message = "토큰 인증 실패"),
            @ApiResponse(code = 404, message = "유저 정보 존재하지 않음"),
            @ApiResponse(code = 406, message = "새 비밀번호와 새 비밀번호 재입력 불일치"),
            @ApiResponse(code = 409, message = "현재 비밀번호 틀림"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PatchMapping("/pw")
    public ResponseEntity<Object> resetPassword(HttpServletRequest servletRequest,
                                                @RequestBody @Valid UserResetPasswordRequestDto request) {
        String token = servletRequest.getHeader(AUTHORIZATION);
        userService.resetPassword(token, request);

        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("OK")
                        .responseMessage("유저 비밀번호 재설정 완료")
                        .build());
    }

    @ApiOperation(value = "유저 닉네임 업데이트")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "유저 닉네임 업데이트 성공"),
            @ApiResponse(code = 400, message = "유저 정보 입력 에러"),
            @ApiResponse(code = 401, message = "토큰 인증 실패"),
            @ApiResponse(code = 404, message = "유저 정보 존재하지 않음"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PatchMapping("/nickname")
    public ResponseEntity<Object> updateNickname(HttpServletRequest servletRequest,
                                                 @RequestBody @Valid UserUpdateNicknameRequestDto request) {
        String token = servletRequest.getHeader(AUTHORIZATION);
        userService.updateNickname(token, request);

        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("OK")
                        .responseMessage("유저 닉네임 업데이트 완료")
                        .build());
    }

    @ApiOperation(value = "유저 탈퇴")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "유저 탈퇴 성공"),
            @ApiResponse(code = 401, message = "토큰 인증 실패"),
            @ApiResponse(code = 404, message = "유저 정보 존재하지 않음"),
            @ApiResponse(code = 409, message = "현재 비밀번호 틀림"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PatchMapping("/deactivate")
    public ResponseEntity<Object> deactivate(HttpServletRequest servletRequest,
                                             @RequestBody @Valid UserDeactivateRequestDto request) {
        String token = servletRequest.getHeader(AUTHORIZATION);

        userService.deactivateUser(token, request);
        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("OK")
                        .responseMessage("유저 탈퇴 성공")
                        .build());
    }

    @ApiOperation(value = "유저 전체 삭제")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "유저 전체 삭제 성공"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @DeleteMapping
    public ResponseEntity<Object> deleteAll() {
        userService.deleteAll();
        return ResponseEntity.status(204)
                .body(DefaultResponseDto.builder()
                        .build());
    }
}
