package com.gabojait.gabojaitspring.admin.controller;

import com.gabojait.gabojaitspring.admin.dto.req.AdminLoginReqDto;
import com.gabojait.gabojaitspring.admin.dto.req.AdminRegisterDecideReqDto;
import com.gabojait.gabojaitspring.admin.dto.req.AdminRegisterReqDto;
import com.gabojait.gabojaitspring.admin.dto.res.AdminAbstractResDto;
import com.gabojait.gabojaitspring.admin.dto.res.AdminDefaultResDto;
import com.gabojait.gabojaitspring.admin.service.AdminService;
import com.gabojait.gabojaitspring.admin.service.MasterService;
import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.dto.DefaultMultiResDto;
import com.gabojait.gabojaitspring.common.dto.DefaultNoResDto;
import com.gabojait.gabojaitspring.common.dto.DefaultSingleResDto;
import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.domain.type.Role;
import com.gabojait.gabojaitspring.user.dto.res.UserDefaultResDto;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.GroupSequence;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import java.util.ArrayList;
import java.util.List;

import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;

@Api(tags = "관리자")
@Validated
@GroupSequence({AdminController.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;
    private final MasterService masterService;
    private final JwtProvider jwtProvider;

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
    public ResponseEntity<DefaultNoResDto> register(@RequestBody @Valid AdminRegisterReqDto request) {
        adminService.register(request);

        return ResponseEntity.status(ADMIN_REGISTERED.getHttpStatus())
                .body(DefaultNoResDto.noDataBuilder()
                        .responseCode(ADMIN_REGISTERED.name())
                        .responseMessage(ADMIN_REGISTERED.getMessage())
                        .build());
    }

    @ApiOperation(value = "관리자 로그인",
            notes = "<응답 코드>\n" +
                    "- 200 = ADMIN_LOGIN\n" +
                    "- 400 = USERNAME_FIELD_REQUIRED || PASSWORD_FIELD_REQUIRED\n" +
                    "- 401 = LOGIN_UNAUTHENTICATED\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = AdminAbstractResDto.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @PostMapping("/login")
    public ResponseEntity<DefaultSingleResDto<Object>> login(@RequestBody @Valid AdminLoginReqDto request) {
        User admin = adminService.login(request);

        HttpHeaders headers;

        if (admin.getRoles().contains(Role.MASTER.name()))
            headers = jwtProvider.createMasterJwt(admin.getId(), admin.getRoles());
        else
            headers = jwtProvider.createJwt(admin.getId(), admin.getRoles());
        AdminAbstractResDto response = new AdminAbstractResDto(admin);

        return ResponseEntity.status(ADMIN_LOGIN.getHttpStatus())
                .headers(headers)
                .body(DefaultSingleResDto.singleDataBuilder()
                        .responseCode(ADMIN_LOGIN.name())
                        .responseMessage(ADMIN_LOGIN.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "관리자 가입 대기자 다건 조회",
            notes = "<검증>\n" +
                    "- page-from = NotNull && PositiveOrZero\n" +
                    "- page-size = Positive\n\n" +
                    "<응답 코드>\n" +
                    "- 200 = UNREGISTERED_ADMIN_FOUND\n" +
                    "- 400 = PAGE_FROM_FIELD_REQUIRED || PAGE_FROM_POSITIVE_OR_ZERO_ONLY || PAGE_SIZE_POSITIVE_ONLY\n" +
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
    public ResponseEntity<DefaultMultiResDto<Object>> findUnregisteredAdmin(
            @RequestParam(value = "page-from", required = false)
            @NotNull(message = "페이지 시작점은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
            @PositiveOrZero(message = "페이지 시작점은 0 또는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
            Integer pageFrom,
            @RequestParam(value = "page-size", required = false)
            @Positive(message = "페이지 사이즈는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
            Integer pageSize
    ) {
        Page<User> admins = masterService.findManyUnregisteredAdmin(pageFrom, pageSize);

        List<AdminDefaultResDto> responses = new ArrayList<>();
        for(User admin : admins)
            responses.add(new AdminDefaultResDto(admin));

        return ResponseEntity.status(UNREGISTERED_ADMIN_FOUND.getHttpStatus())
                .body(DefaultMultiResDto.multiDataBuilder()
                        .responseCode(UNREGISTERED_ADMIN_FOUND.name())
                        .responseMessage(UNREGISTERED_ADMIN_FOUND.getMessage())
                        .data(responses)
                        .size(admins.getTotalPages())
                        .build());
    }

    @ApiOperation(value = "관리자 가입 결정",
            notes = "<응답 코드>\n" +
                    "- 200 = ADMIN_REGISTER_DECIDED\n" +
                    "- 400 = IS_APPROVED_FIELD_REQUIRED || ADMIN_ID_FIELD_REQUIRED || ADMIN_ID_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = ADMIN_NOT_FOUND\n" +
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
    @PatchMapping("/{admin-id}/decide")
    public ResponseEntity<DefaultNoResDto> decideAdminRegistration(
            @PathVariable(value = "admin-id", required = false)
            @NotNull(message = "관리자 식별자는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
            @Positive(message = "관리자 식별자는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
            Long adminId,
            @RequestBody @Valid
            AdminRegisterDecideReqDto request
    ) {
        masterService.decideAdminRegistration(adminId, request.getIsApproved());

        return ResponseEntity.status(ADMIN_REGISTER_DECIDED.getHttpStatus())
                .body(DefaultNoResDto.noDataBuilder()
                        .responseCode(ADMIN_REGISTER_DECIDED.name())
                        .responseMessage(ADMIN_REGISTER_DECIDED.getMessage())
                        .build());
    }

    @ApiOperation(value = "회원 단건 조회",
            notes = "<응답 코드>\n" +
                    "- 200 = USER_FOUND\n" +
                    "- 400 = USER_ID_FIELD_REQUIRED || USER_ID_POSITIVE_ONLY\n" +
                    "- 401 = TOKEN_UNAUTHENTICATED\n" +
                    "- 403 = TOKEN_UNAUTHORIZED\n" +
                    "- 404 = USER_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = UserDefaultResDto.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED"),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/user/{user-id}")
    public ResponseEntity<DefaultSingleResDto<Object>> findOneUser(
            @PathVariable(value = "user-id", required = false)
            @NotNull(message = "회원 식별자는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
            @Positive(message = "회원 식별자는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
            Long userId
    ) {
        User otherUser = adminService.findOneUser(userId);

        UserDefaultResDto response = new UserDefaultResDto(otherUser);

        return ResponseEntity.status(USER_FOUND.getHttpStatus())
                .body(DefaultSingleResDto.singleDataBuilder()
                        .responseCode(USER_FOUND.name())
                        .responseMessage(USER_FOUND.getMessage())
                        .data(response)
                        .build());
    }
}
