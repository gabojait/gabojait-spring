package com.gabojait.gabojaitspring.user.controller;

import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.dto.DefaultResDto;
import com.gabojait.gabojaitspring.user.dto.req.ContactSaveReqDto;
import com.gabojait.gabojaitspring.user.dto.req.ContactVerifyReqDto;
import com.gabojait.gabojaitspring.user.service.ContactService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static com.gabojait.gabojaitspring.common.code.SuccessCode.EMAIL_VERIFIED;
import static com.gabojait.gabojaitspring.common.code.SuccessCode.VERIFICATION_CODE_SENT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "연락처")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/contact")
public class ContactController {

    private final ContactService contactService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "인증코드 전송")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "VERIFICATION_CODE_SENT",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "EMAIL_FIELD_REQUIRED / EMAIL_FORMAT_INVALID"),
            @ApiResponse(responseCode = "409", description = "EXISTING_CONTACT"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR / EMAIL_SEND_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<DefaultResDto<Object>> sendVerificationCode(@RequestBody @Valid ContactSaveReqDto request) {
        // main
        contactService.sendRegisteredVerificationCode(request);

        // response
        HttpHeaders headers = jwtProvider.generateGuestJwt();

        return ResponseEntity.status(VERIFICATION_CODE_SENT.getHttpStatus())
                .headers(headers)
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(VERIFICATION_CODE_SENT.name())
                        .responseMessage(VERIFICATION_CODE_SENT.getMessage())
                        .build());
    }

    @ApiOperation(value = "인증코드 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "EMAIL_VERIFIED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400",
                    description = "*_FIELD_REQUIRED / EMAIL_FORMAT_INVALID / VERIFICATION_CODE_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN"),
            @ApiResponse(responseCode = "409", description = "EXISTING_CONTACT"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR / EMAIL_SEND_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @PatchMapping
    public ResponseEntity<DefaultResDto<Object>> verifyCode(HttpServletRequest servletRequest,
                                                            @RequestBody @Valid ContactVerifyReqDto request) {
        // auth
        jwtProvider.authorizeGuestAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // main
        contactService.verify(request);

        return ResponseEntity.status(EMAIL_VERIFIED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(EMAIL_VERIFIED.name())
                        .responseMessage(EMAIL_VERIFIED.getMessage())
                        .build());
    }
}
