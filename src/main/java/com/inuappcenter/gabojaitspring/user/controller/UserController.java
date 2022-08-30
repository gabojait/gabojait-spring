package com.inuappcenter.gabojaitspring.user.controller;

import com.inuappcenter.gabojaitspring.common.DefaultResponseDto;
import com.inuappcenter.gabojaitspring.user.dto.*;
import com.inuappcenter.gabojaitspring.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Api(tags = "User")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    @Autowired
    private final UserService userService;

    @ApiOperation(value = "User 아이디 중복 여부 확인")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User 아이디 중복 확인 성공"),
            @ApiResponse(code = 400, message = "User 아이디 입력 에러"),
            @ApiResponse(code = 409, message = "User 아이디 중복 확인 실패"),
            @ApiResponse(code = 500, message = "User 아이디 중복 여부 확인 중 에러")
    })
    @PostMapping("/duplicate")
    public ResponseEntity<Object> duplicateUsername(
            @RequestBody @Valid UserDuplicateRequestDto request) {
        userService.isExistingUsername(request);
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
    @GetMapping("")
    public ResponseEntity<Object> findOneUser(@RequestBody UserFindOneUserRequestDto request) {
        UserDefaultResponseDto response = userService.findOneUser(request);
        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("OK")
                        .responseMessage("User 정보 불러오기 성공")
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "User 전체 삭제")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "User 전체 삭제 성공"),
            @ApiResponse(code = 500, message = "User 전체 삭제 중 에러")
    })
    @DeleteMapping("/deleteAll")
    public ResponseEntity<Object> deleteAll() throws NoHandlerFoundException {
        userService.deleteAll();
        return ResponseEntity.status(204)
                .body(DefaultResponseDto.builder()
                        .responseCode("DELETE_ALL_SUCCESS")
                        .responseMessage("전체 삭제 완료")
                        .build());
    }
}
