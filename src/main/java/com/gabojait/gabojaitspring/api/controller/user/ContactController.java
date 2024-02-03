package com.gabojait.gabojaitspring.api.controller.user;

import com.gabojait.gabojaitspring.common.dto.response.DefaultNoResponse;
import com.gabojait.gabojaitspring.api.dto.user.request.ContactCreateRequest;
import com.gabojait.gabojaitspring.api.dto.user.request.ContactVerifyRequest;
import com.gabojait.gabojaitspring.api.service.user.ContactService;
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

import static com.gabojait.gabojaitspring.common.code.SuccessCode.EMAIL_VERIFIED;
import static com.gabojait.gabojaitspring.common.code.SuccessCode.VERIFICATION_CODE_SENT;

@Api(tags = "연락처")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/contact")
public class ContactController {

    private final ContactService contactService;

    @ApiOperation(value = "인증코드 전송",
            notes = "<응답 코드>\n" +
                    "- 201 = VERIFICATION_CODE_SENT\n" +
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
    public ResponseEntity<DefaultNoResponse> sendVerificationCode(@RequestBody @Valid ContactCreateRequest request) {
        contactService.validateDuplicateContact(request.getEmail());
        contactService.createContact(request);

        return ResponseEntity.status(VERIFICATION_CODE_SENT.getHttpStatus())
                .body(DefaultNoResponse.noDataBuilder()
                        .responseCode(VERIFICATION_CODE_SENT.name())
                        .responseMessage(VERIFICATION_CODE_SENT.getMessage())
                        .build());
    }

    @ApiOperation(value = "인증코드 확인",
            notes = "<응답 코드>\n" +
                    "- 200 = EMAIL_VERIFIED\n" +
                    "- 400 = EMAIL_FIELD_REQUIRED || VERIFICATION_CODE_FIELD_REQUIRED || EMAIL_FORMAT_INVALID || " +
                    "VERIFICATION_CODE_INVALID\n" +
                    "- 404 = EMAIL_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PatchMapping
    public ResponseEntity<DefaultNoResponse> verifyCode(@RequestBody @Valid ContactVerifyRequest request) {
        contactService.verifyContact(request);

        return ResponseEntity.status(EMAIL_VERIFIED.getHttpStatus())
                .body(DefaultNoResponse.noDataBuilder()
                        .responseCode(EMAIL_VERIFIED.name())
                        .responseMessage(EMAIL_VERIFIED.getMessage())
                        .build());
    }
}
