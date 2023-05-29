package com.gabojait.gabojaitspring.admin.controller;

import com.gabojait.gabojaitspring.admin.dto.req.AdminLoginReqDto;
import com.gabojait.gabojaitspring.admin.dto.req.AdminRegisterApprovalReqDto;
import com.gabojait.gabojaitspring.admin.dto.req.AdminRegisterReqDto;
import com.gabojait.gabojaitspring.admin.dto.res.AdminAbstractResDto;
import com.gabojait.gabojaitspring.admin.dto.res.AdminDefaultResDto;
import com.gabojait.gabojaitspring.admin.service.AdminService;
import com.gabojait.gabojaitspring.admin.service.MasterService;
import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.dto.DefaultResDto;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.domain.type.Role;
import com.gabojait.gabojaitspring.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import java.util.ArrayList;
import java.util.List;

import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "관리자")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;
    private final MasterService masterService;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "로그인",
            notes = "<응답 코드>\n" +
                    "- 200 = ADMIN_LOGIN\n" +
                    "- 400 = USERNAME_FIELD_REQUIRED || PASSWORD_FIELD_REQUIRED\n" +
                    "- 401 = LOGIN_UNAUTHENTICATED\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = AdminDefaultResDto.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PostMapping("/login")
    public ResponseEntity<DefaultResDto<Object>> login(@RequestBody @Valid AdminLoginReqDto request) {
        // auth && main
        User admin = adminService.login(request);
        // sub
        userService.updateLastRequestDate(admin);

        // response
        HttpHeaders headers;
        if (admin.getRoles().contains(Role.MASTER.name()))
            headers = jwtProvider.generateMasterJwt(admin.getId(), admin.getRoles());
        else
            headers = jwtProvider.generateAdminJwt(admin.getId(), admin.getRoles());
        AdminAbstractResDto response = new AdminAbstractResDto(admin);

        return ResponseEntity.status(ADMIN_LOGIN.getHttpStatus())
                .headers(headers)
                .body(DefaultResDto.singleDataBuilder()
                        .responseCode(ADMIN_LOGIN.name())
                        .responseMessage(ADMIN_LOGIN.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "관리자 가입",
            notes = "<응답 코드>\n" +
                    "- 201 = ADMIN_REGISTERED\n" +
                    "- 400 = ADMIN_NAME_FIELD_REQUIRED || PASSWORD_FIELD_REQUIRED || " +
                    "PASSWORD_RE_ENTERED_FIELD_REQUIRED || LEGAL_NAME_FIELD_REQUIRED || GENDER_FIELD_REQUIRED || " +
                    "BIRTHDATE_FIELD_REQUIRED || ADMIN_NAME_LENGTH_INVALID || PASSWORD_LENGTH_INVALID || " +
                    "LEGAL_NAME_LENGTH_INVALID || ADMIN_NAME_FORMAT_INVALID || PASSWORD_FORMAT_INVALID || " +
                    "LEGAL_NAME_FORMAT_INVALID || GENDER_TYPE_INVALID || PASSWORD_MATCH_INVALID\n" +
                    "- 409 = EXISTING_USERNAME\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "CREATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "409", description = "CONFLICT"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PostMapping
    public ResponseEntity<DefaultResDto<Object>> register(@RequestBody @Valid AdminRegisterReqDto request) {
        // main
        adminService.register(request);

        return ResponseEntity.status(ADMIN_REGISTERED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(ADMIN_REGISTERED.name())
                        .responseMessage(ADMIN_REGISTERED.getMessage())
                        .build());
    }

    @ApiOperation(value = "관리자 가입 대기자 다건 조회",
            notes = "<검증>\n" +
                    "- page-from = NotNull && PositiveOrZero\n" +
                    "- page-size = Positive\n\n" +
                    "<응답 코드>\n" +
                    "- 200 = ADMIN_UNREGISTERED_FOUND\n" +
                    "- 400 = PAGE_FROM_FIELD_REQUIRED || PAGE_FROM_POS_OR_ZERO_ONLY || PAGE_SIZE_POS_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = AdminDefaultResDto.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping
    public ResponseEntity<DefaultResDto<Object>> findUnregisteredAdmin(
            HttpServletRequest servletRequest,
            @RequestParam(value = "page-from")
            @NotNull(message = "페이지 시작점은 필수 입력란입니다.")
            @PositiveOrZero(message = "페이지 시작점은 0 또는 양수만 가능합니다.")
            Integer pageFrom,
            @RequestParam(value = "page-size", required = false)
            @Positive(message = "페이지 사이즈는 양수만 가능합니다.")
            Integer pageSize
    ) {
        // auth
        jwtProvider.authorizeMasterAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // main
        Page<User> admins = masterService.findPageUnregisteredAdmin(pageFrom, pageSize);

        // response
        List<AdminDefaultResDto> responses = new ArrayList<>();
        for (User admin : admins)
            responses.add(new AdminDefaultResDto(admin));

        return ResponseEntity.status(ADMIN_UNREGISTERED_FOUND.getHttpStatus())
                .body(DefaultResDto.multiDataBuilder()
                        .responseCode(ADMIN_UNREGISTERED_FOUND.name())
                        .responseMessage(ADMIN_UNREGISTERED_FOUND.getMessage())
                        .data(responses)
                        .size(admins.getTotalPages())
                        .build());
    }

    @ApiOperation(value = "관리자 가입 결정",
            notes = "<응답 코드>\n" +
                    "- 200 = ADMIN_REGISTER_DECIDED\n" +
                    "- 400 = IS_APPROVED_FIELD_REQUIRED\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PatchMapping("/{user-id}/decide")
    public ResponseEntity<DefaultResDto<Object>> decideAdminRegistration(HttpServletRequest servletRequest,
                                                                          @PathVariable(value = "user-id")
                                                                          String userId,
                                                                          @RequestBody @Valid
                                                                          AdminRegisterApprovalReqDto request) {
        // auth
        jwtProvider.authorizeMasterAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // main
        masterService.decideAdminRegistration(userId, request.getIsApproved());

        return ResponseEntity.status(ADMIN_REGISTER_DECIDED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(ADMIN_REGISTER_DECIDED.name())
                        .responseMessage(ADMIN_REGISTER_DECIDED.getMessage())
                        .build());
    }
}