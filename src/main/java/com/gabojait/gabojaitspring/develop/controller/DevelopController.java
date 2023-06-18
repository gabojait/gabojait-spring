package com.gabojait.gabojaitspring.develop.controller;

import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.dto.DefaultResDto;
import com.gabojait.gabojaitspring.develop.service.DevelopService;
import com.gabojait.gabojaitspring.user.domain.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;

@Api(tags = "개발")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class DevelopController {

    private final DevelopService developService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "헬스 체크",
            notes = "<응답 코드>\n" +
                    "* 200 = SERVER_HEALTH_OK\n" +
                    "* 500 = SERVER_ERROR\n" +
                    "* 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/health")
    public ResponseEntity<DefaultResDto<Object>> healthCheck() {
        String serverName = developService.getServerName();

        return ResponseEntity.status(SERVER_OK.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(SERVER_OK.name())
                        .responseMessage(serverName.concat(" ").concat(SERVER_OK.getMessage()))
                        .build());
    }

    @ApiOperation(value = "데이터베이스 초기화 후 테스트 데이터 주입",
            notes = "<응답 코드>\n" +
                    "* 200 = TEST_DATA_INJECTED\n" +
                    "* 500 = SERVER_ERROR\n" +
                    "* 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @DeleteMapping("/test")
    public ResponseEntity<DefaultResDto<Object>> resetAndInjectTest() {
        developService.resetAndInjectData();

        return ResponseEntity.status(TEST_DATA_INJECTED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(TEST_DATA_INJECTED.name())
                        .responseMessage(TEST_DATA_INJECTED.getMessage())
                        .build());
    }

    @ApiOperation(value = "테스트 계정 토큰 발급",
            notes = "<값>\n" +
                    "- user-id = 1 || 2 || 3 || 4 || 5 || 6 || 7 || 8 || 9 || 10\n\n" +
                    "<응답 코드>\n" +
                    "- 200 = TOKEN_ISSUED\n" +
                    "- 400 = USER_ID_FIELD_REQUIRED || USER_ID_POSITIVE_ONLY\n" +
                    "- 404 = USER_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION\n")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/test/user/{user-id}")
    public ResponseEntity<DefaultResDto<Object>> testDataToken(@PathVariable(value = "user-id")
                                                               @NotNull(message = "회원 식별자는 필수 입력입니다.")
                                                               @Positive(message = "회원 식별자는 양수만 가능합니다.")
                                                               Integer userId) {
        User user = developService.findOneUser("test" + userId);

        HttpHeaders headers = jwtProvider.generateUserJwt(user.getId(), user.getRoles());

        return ResponseEntity.status(TOKEN_ISSUED.getHttpStatus())
                .headers(headers)
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(TOKEN_ISSUED.name())
                        .responseMessage(TOKEN_ISSUED.getMessage())
                        .build());
    }
}
