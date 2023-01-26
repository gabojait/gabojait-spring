package com.inuappcenter.gabojaitspring.user.controller;

import com.inuappcenter.gabojaitspring.common.DefaultResponseDto;
import com.inuappcenter.gabojaitspring.user.dto.ContactSaveRequestDto;
import com.inuappcenter.gabojaitspring.user.dto.ContactVerificationRequestDto;
import com.inuappcenter.gabojaitspring.user.service.ContactService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "연락처")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/contact")
public class ContactController {

    private final ContactService contactService;

    @ApiOperation(value = "이메일 중복 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "NO_DUPLICATE_EMAIL",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / EMAIL_FORMAT_INVALID"),
            @ApiResponse(responseCode = "409", description = "EXISTING_EMAIL"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<DefaultResponseDto<Object>> create(@RequestBody @Valid ContactSaveRequestDto request) {

        contactService.isExistingEmail(request.getEmail());

        contactService.save(request);

        return ResponseEntity.status(201).body(
                DefaultResponseDto.builder()
                        .responseCode("NO_DUPLICATE_EMAIL")
                        .responseMessage("이메일 중복 확인 완료")
                        .build());
    }

    @ApiOperation(value = "인증번호 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "EMAIL_VERIFIED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400",
                    description = "FIELD_REQUIRED / EMAIL_FORMAT_INVALID / VERIFICATIONCODE_INVALID"),
            @ApiResponse(responseCode = "404", description = "NOT_VERIFIED_EMAIL"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PatchMapping
    public ResponseEntity<DefaultResponseDto<Object>> verify(@RequestBody @Valid ContactVerificationRequestDto request)
    {
        contactService.verification(request);

        return ResponseEntity.status(200).body(
                DefaultResponseDto.builder()
                        .responseCode("EMAIL_VERIFIED")
                        .responseMessage("이메일 인증번호 확인 완료")
                        .build());
    }

    // TODO: 배포 전 삭제 필요
    @ApiOperation(value = "전체 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @DeleteMapping("/all")
    public ResponseEntity<DefaultResponseDto<Object>> deleteAll() {
        contactService.deleteAll();

        return ResponseEntity.status(204)
                .body(DefaultResponseDto.builder()
                        .build());
    }
}
