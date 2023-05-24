package com.gabojait.gabojaitspring.admin.controller;

import com.gabojait.gabojaitspring.admin.dto.req.AdminLoginReqDto;
import com.gabojait.gabojaitspring.admin.dto.req.AdminRegisterReqDto;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.gabojait.gabojaitspring.common.code.SuccessCode.ADMIN_LOGIN;
import static com.gabojait.gabojaitspring.common.code.SuccessCode.ADMIN_REGISTERED;

@Api(tags = "관리자")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;
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
        AdminDefaultResDto response = new AdminDefaultResDto(admin);

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
                    content = @Content(schema = @Schema(implementation = Object.class)))
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
}
