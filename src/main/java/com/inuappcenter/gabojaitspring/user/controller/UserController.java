package com.inuappcenter.gabojaitspring.user.controller;

import com.inuappcenter.gabojaitspring.auth.JwtProvider;
import com.inuappcenter.gabojaitspring.common.DefaultResponseDto;
import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import com.inuappcenter.gabojaitspring.exception.http.UnauthorizedException;
import com.inuappcenter.gabojaitspring.user.dto.*;
import com.inuappcenter.gabojaitspring.user.service.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "User")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final String tokenPrefix = "Bearer ";

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
            @NotBlank(message = "모든 필수 정보를 입력해 주세요")
            @Size(min = 5, max = 15, message = "아이디는 5~15자만 가능합니다")
            @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디 형식은 영문과 숫자의 조합만 가능합니다")
            String username) {
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
            @ApiResponse(code = 401, message = "인증되지 않은 연락처"),
            @ApiResponse(code = 404, message = "연락처 조회 실패"),
            @ApiResponse(code = 409, message = "유저 아이디 또는 닉네임 중복 검사 실패"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PostMapping("/new")
    public ResponseEntity<Object> signUp(@RequestBody @Valid UserSaveRequestDto request) {
        UserDefaultResponseDto response = userService.save(request);
        return ResponseEntity.status(201)
                .body(DefaultResponseDto.builder()
                        .responseCode("CREATED")
                        .responseMessage("유저 가입 완료")
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "유저 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "유저 정보 조회 성공"),
            @ApiResponse(code = 403, message = "토큰 인증 실패"),
            @ApiResponse(code = 404, message = "유저 정보 조회 실패")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Object> findOneUser(@PathVariable String id) {
        UserDefaultResponseDto response = userService.findOneUser(id);
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
            @ApiResponse(code = 401, message = "유저 로그인 실패")
    })
    @PostMapping("/login")
    public ResponseEntity<Object> signIn(@RequestBody UserSignInRequestDto request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        if (authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();
            String[] token = jwtProvider.generateJwt(user);
            HttpHeaders responseHeader = new HttpHeaders();
            responseHeader.add("ACCESS-TOKEN", token[0]);
            responseHeader.add("REFRESH-TOKEN", token[1]);
            return ResponseEntity.status(200)
                    .headers(responseHeader)
                    .body(DefaultResponseDto.builder()
                            .responseCode("OK")
                            .responseMessage("유저 로그인 완료")
                            .build());
        } else {
            throw new UnauthorizedException("유저 로그인 실패");
        }
    }

    @ApiOperation(value = "유저 토큰 재발급")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "유저 토큰 재발급 성공"),
            @ApiResponse(code = 401, message = "유저 토큰 재발급 실패")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization",
                    value = "Refresh Token",
                    required = true,
                    paramType = "header",
                    dataTypeClass = String.class,
                    example = "Bearer access_token")
    })
    @GetMapping("/auth")
    public ResponseEntity<Object> refreshToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith(tokenPrefix)) {
            String refreshToken = authorizationHeader.substring(tokenPrefix.length());
            User user =  jwtProvider.verifyJwt(refreshToken);
            String[] token = jwtProvider.generateJwt(user);
            HttpHeaders responseHeader = new HttpHeaders();
            responseHeader.add("ACCESS-TOKEN", token[0]);
            responseHeader.add("REFRESH-TOKEN", token[1]);
            return ResponseEntity.status(200)
                    .headers(responseHeader)
                    .body(DefaultResponseDto.builder()
                            .responseCode("OK")
                            .responseMessage("유저 토큰 재발급 완료")
                            .build());
        } else {
            throw new UnauthorizedException("유저 토큰 재발급 실패");
        }
    }

    @ApiOperation(value = "유저 아이디 찾기")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "유저 아이디 찾기 성공"),
            @ApiResponse(code = 404, message = "유저 정보 존재하지 않음"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @GetMapping("/findId/{email}")
    public ResponseEntity<Object> forgotId(@PathVariable String email) {
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
    @GetMapping("/findPw/{username}/{email}")
    public ResponseEntity<Object> forgotPassword(@PathVariable String username, @PathVariable String email) {
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
            @ApiResponse(code = 401, message = "현재 비밀번호 틀림"),
            @ApiResponse(code = 403, message = "토큰 인증 실패"),
            @ApiResponse(code = 404, message = "유저 정보 존재하지 않음"),
            @ApiResponse(code = 406, message = "새 비밀번호와 새 비밀번호 재입력 불일치"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PatchMapping("/Pw")
    public ResponseEntity<Object> resetPassword(@RequestBody @Valid UserResetPasswordRequestDto request) {
        userService.resetPassword(request);
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
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PatchMapping("/Pw")
    public ResponseEntity<Object> updateNickname(@RequestBody @Valid UserUpdateNicknameRequestDto request) {
        userService.updateNickname(request);
        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("OK")
                        .responseMessage("유저 닉네임 업데이트 완료")
                        .build());
    }

    @ApiOperation(value = "유저 탈퇴")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "유저 탈퇴 성공"),
            @ApiResponse(code = 401, message = "현재 비밀번호 틀림"),
            @ApiResponse(code = 403, message = "토큰 인증 실패"),
            @ApiResponse(code = 404, message = "유저 정보 존재하지 않음"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PatchMapping("/deactivate")
    public ResponseEntity<Object> deactivate(@RequestBody @Valid UserDeactivateRequestDto request) {
        userService.deactivateUser(request);
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
