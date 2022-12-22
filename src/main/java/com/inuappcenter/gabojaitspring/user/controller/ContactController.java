package com.inuappcenter.gabojaitspring.user.controller;

import com.inuappcenter.gabojaitspring.common.DefaultResponseDto;
import com.inuappcenter.gabojaitspring.user.dto.ContactSaveRequestDto;
import com.inuappcenter.gabojaitspring.user.dto.ContactVerificationRequestDto;
import com.inuappcenter.gabojaitspring.user.service.ContactService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "Contact")
@RestController
@RequiredArgsConstructor
@RequestMapping("/contact")
public class ContactController {

    private final ContactService contactService;

    @ApiOperation(value = "연락처 이메일 중복여부 확인")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "연락처 이메일 중복확인 후 생성 성공"),
            @ApiResponse(code = 400, message = "연락처 정보 입력 에러"),
            @ApiResponse(code = 409, message = "이미 존재하는 이메일"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PostMapping("/new")
    public ResponseEntity<Object> create(@RequestBody @Valid ContactSaveRequestDto request) {
        contactService.save(request);
        return ResponseEntity.status(201)
                .body(DefaultResponseDto.builder()
                        .responseCode("CREATED")
                        .responseMessage("이메일 중복여부 확인 완료")
                        .build());
    }

    @ApiOperation(value = "연락처 인증번호 확인")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "연락처 인증번호 확인 성공"),
            @ApiResponse(code = 400, message = "연락처 인증번호 입력 에러"),
            @ApiResponse(code = 404, message = "연락처 정보 존재하지 않음"),
            @ApiResponse(code = 409, message = "인증번호 불일치"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PatchMapping
    public ResponseEntity<Object> verify(@RequestBody @Valid ContactVerificationRequestDto request) {
        contactService.verification(request);
        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("OK")
                        .responseMessage("이메일 인증번호 확인 완료")
                        .build());
    }

    @ApiOperation(value = "연락처 전체 삭제")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "연락처 전체 삭제 성공"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @DeleteMapping
    public ResponseEntity<Object> deleteAll() {
        contactService.deleteAll();
        return ResponseEntity.status(204)
                .body(DefaultResponseDto.builder()
                        .build());
    }
}
