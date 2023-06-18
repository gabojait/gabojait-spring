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

import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "연락처")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/contact")
public class ContactController {

    private final ContactService contactService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "인증코드 전송",
            notes = "<응답 코드>\n" +
                    "- 201 = VERIFICATION\n" +
                    "- 400 = EMAIL_FIELD_REQUIRED || EMAIL_FORMAT_INVALID\n" +
                    "- 409 = EXISTING_CONTACT\n" +
                    "- 500 = SERVER_ERROR || EMAIL_SEND_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "CREATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "409", description = "CONFLICT"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<DefaultResDto<Object>> sendVerificationCode(@RequestBody @Valid ContactSaveReqDto request) {
        contactService.sendRegisterVerificationCode(request);

        HttpHeaders headers = jwtProvider.generateGuestJwt();

        return ResponseEntity.status(VERIFICATION_CODE_SENT.getHttpStatus())
                .headers(headers)
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(VERIFICATION_CODE_SENT.name())
                        .responseMessage(VERIFICATION_CODE_SENT.getMessage())
                        .build());
    }

    @ApiOperation(value = "인증코드 확인",
            notes = "<응답 코드>\n" +
                    "- 200 = EMAIL_VERIFIED\n" +
                    "- 400 = EMAIL_FIELD_REQUIRED || VERIFICATION_FIELD_REQUIRED || EMAIL_FORMAT_INVALID || " +
                    "VERIFICATION_CODE_INVALID\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 404 = EMAIL_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PatchMapping
    public ResponseEntity<DefaultResDto<Object>> verifyCode(HttpServletRequest servletRequest,
                                                            @RequestBody @Valid ContactVerifyReqDto request) {
        jwtProvider.authorizeGuestAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        contactService.verify(request);

        return ResponseEntity.status(EMAIL_VERIFIED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(EMAIL_VERIFIED.name())
                        .responseMessage(EMAIL_VERIFIED.getMessage())
                        .build());
    }
}
