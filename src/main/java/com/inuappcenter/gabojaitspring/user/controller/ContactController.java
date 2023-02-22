package com.inuappcenter.gabojaitspring.user.controller;

import com.inuappcenter.gabojaitspring.common.DefaultResDto;
import com.inuappcenter.gabojaitspring.user.dto.req.ContactSaveReqDto;
import com.inuappcenter.gabojaitspring.user.dto.req.ContactVerificationReqDto;
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

import static com.inuappcenter.gabojaitspring.common.SuccessCode.EMAIL_NO_DUPLICATE;
import static com.inuappcenter.gabojaitspring.common.SuccessCode.EMAIL_VERIFIED;

@Api(tags = "연락처")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactService contactService;

    @ApiOperation(value = "이메일 인증번호 요청")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "NO_DUPLICATE_EMAIL",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / EMAIL_FORMAT_INVALID"),
            @ApiResponse(responseCode = "409", description = "EXISTING_EMAIL"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<DefaultResDto<Object>> create(@RequestBody @Valid ContactSaveReqDto request) {

        contactService.isExistingEmail(request.getEmail());

        contactService.create(request);

        return ResponseEntity.status(EMAIL_NO_DUPLICATE.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(EMAIL_NO_DUPLICATE.name())
                        .responseMessage(EMAIL_NO_DUPLICATE.getMessage())
                        .build());
    }

    @ApiOperation(value = "인증번호 검증")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "EMAIL_VERIFIED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400",
                    description = "FIELD_REQUIRED / EMAIL_FORMAT_INVALID / VERIFICATIONCODE_INVALID"),
            @ApiResponse(responseCode = "404", description = "NOT_VERIFIED_EMAIL"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PatchMapping
    public ResponseEntity<DefaultResDto<Object>> verify(@RequestBody @Valid ContactVerificationReqDto request)
    {
        contactService.verification(request);

        return ResponseEntity.status(EMAIL_VERIFIED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(EMAIL_VERIFIED.name())
                        .responseMessage(EMAIL_VERIFIED.getMessage())
                        .build());
    }
}
