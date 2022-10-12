package com.inuappcenter.gabojaitspring.user.controller;

import com.inuappcenter.gabojaitspring.auth.JwtProvider;
import com.inuappcenter.gabojaitspring.common.DefaultResponseDto;
import com.inuappcenter.gabojaitspring.exception.http.UnauthorizedException;
import com.inuappcenter.gabojaitspring.user.dto.*;
import com.inuappcenter.gabojaitspring.user.service.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

    @ApiOperation(value = "User 아이디 중복 여부 확인")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User 아이디 중복 확인 성공"),
            @ApiResponse(code = 400, message = "User 아이디 입력 에러"),
            @ApiResponse(code = 409, message = "User 아이디 중복 확인 실패"),
            @ApiResponse(code = 500, message = "User 아이디 중복 여부 확인 중 에러")
    })
    @GetMapping("/duplicate/{username}")
    public ResponseEntity<Object> duplicateUsername(
            @PathVariable String username) {
        userService.isExistingUsername(username);
        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("OK")
                        .responseMessage("아이디 중복 확인 완료")
                        .build());
    }

    @ApiOperation(value = "User 생성")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "User 가입 성공"),
            @ApiResponse(code = 400, message = "User 정보 입력 에러"),
            @ApiResponse(code = 404, message = "Contact 정보 조회 실패"),
            @ApiResponse(code = 409, message = "인증되지 않은 Contact"),
            @ApiResponse(code = 500, message = "User 가입 중 에러")
    })
    @PostMapping("/new")
    public ResponseEntity<Object> signUp(@RequestBody @Valid UserSaveRequestDto request) {
        UserDefaultResponseDto response = userService.save(request);
        return ResponseEntity.status(201)
                .body(DefaultResponseDto.builder()
                        .responseCode("CREATED")
                        .responseMessage("회원가입 완료")
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "User 정보 불러오기")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User 정보 불러오기 성공"),
            @ApiResponse(code = 401, message = "User 정보 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Object> findOneUser(@PathVariable String id) {
        UserDefaultResponseDto response = userService.findOneUser(id);
        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("OK")
                        .responseMessage("User 정보 불러오기 성공")
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "User 로그인")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User 로그인 성공"),
            @ApiResponse(code = 401, message = "User 로그인 실패")
    })
    @PostMapping("/auth")
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
                            .responseMessage("User 로그인 성공")
                            .build());
        } else {
            throw new UnauthorizedException();
        }
    }

    @ApiOperation(value = "User 토큰 재발급")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User 토큰 재발급 성공"),
            @ApiResponse(code = 401, message = "User 토큰 재발급 실패")
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
                            .responseMessage("User 토큰 재발급 성공")
                            .build());
        } else {
            throw new UnauthorizedException("Refresh 토큰이 없습니다");
        }
    }

    @ApiOperation(value = "User 아이디 찾기")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User 아이디 찾기 성공")
    })
    @GetMapping("/forgotid/{email}")
    public ResponseEntity<Object> forgotId(@PathVariable String email) {
        userService.findUserByEmail(email);
        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("OK")
                        .responseMessage("User 아이디 찾기 성공")
                        .build());
    }


    @ApiOperation(value = "User 전체 삭제")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "User 전체 삭제 성공"),
            @ApiResponse(code = 500, message = "User 전체 삭제 중 에러")
    })
    @DeleteMapping
    public ResponseEntity<Object> deleteAll() {
        userService.deleteAll();
        return ResponseEntity.status(204)
                .body(DefaultResponseDto.builder()
                        .responseCode("DELETE_ALL_SUCCESS")
                        .responseMessage("전체 삭제 완료")
                        .build());
    }
}
