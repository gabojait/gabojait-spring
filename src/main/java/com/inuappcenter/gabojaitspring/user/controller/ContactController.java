package com.inuappcenter.gabojaitspring.user.controller;

import com.inuappcenter.gabojaitspring.common.DefaultResponseDto;
import com.inuappcenter.gabojaitspring.user.dto.ContactDefaultResponseDto;
import com.inuappcenter.gabojaitspring.user.dto.ContactSaveRequestDto;
import com.inuappcenter.gabojaitspring.user.dto.ContactVerificationRequestDto;
import com.inuappcenter.gabojaitspring.user.service.ContactService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "Contact")
@RestController
@RequiredArgsConstructor
@RequestMapping("/contact")
public class ContactController {

    private final ContactService contactService;

    @ApiOperation(value = "Contact 생성")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Contact 생성 완료"),
            @ApiResponse(code = 400, message = "Contact 정보 입력 에러"),
            @ApiResponse(code = 409, message = "Contact 이미 존재"),
            @ApiResponse(code = 500, message = "Contact 생성 중 서버 에러")
    })
    @PostMapping("/new")
    public ResponseEntity<Object> create(@RequestBody @Valid ContactSaveRequestDto request) {
        ContactDefaultResponseDto response = contactService.save(request);
        return ResponseEntity.status(201)
                .body(DefaultResponseDto.builder()
                        .responseCode("CREATED")
                        .responseMessage("이메일 중복여부 확인 완료")
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "Contact 인증번호 확인")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Contact 인증번호 확인 완료"),
            @ApiResponse(code = 400, message = "Contact 인증번호 입력 에러"),
            @ApiResponse(code = 401, message = "Contact 정보 존재 에러 || Contact 인증번호 불일치 에러"),
            @ApiResponse(code = 500, message = "Contact 인증번호 확인 중 서버 에러")
    })
    @PatchMapping
    public ResponseEntity<Object> verify(@RequestBody @Valid ContactVerificationRequestDto request) {
        ContactDefaultResponseDto response = contactService.update(request);
        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("OK")
                        .responseMessage("이메일 인증 완료")
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "Contact 전체 삭제")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Contact 전체 삭제 성공"),
            @ApiResponse(code = 500, message = "Contact 전체 삭제 중 서버 에러")
    })
    @DeleteMapping("/deleteAll")
    public ResponseEntity<Object> deleteAll() {
        contactService.deleteAll();
        return ResponseEntity.status(204)
                .body(DefaultResponseDto.builder()
                        .responseCode("NO_CONTENT")
                        .responseMessage("전체 삭제 완료")
                        .build());
    }
}
