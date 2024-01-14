package com.gabojait.gabojaitspring.api.controller.develop;

import com.gabojait.gabojaitspring.api.dto.common.response.DefaultNoResponse;
import com.gabojait.gabojaitspring.api.service.develop.DevelopService;
import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.exception.CustomException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.SERVER_ERROR;
import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;

@Api(tags = "개발")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class DevelopController {

    private final JwtProvider jwtProvider;
    private final DevelopService developService;

    @ApiOperation(value = "헬스 체크",
            notes = "<응답 코드>\n" +
                    "- 200 = SERVER_OK\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/health")
    public ResponseEntity<DefaultNoResponse> healthCheck() {
        String serverName = developService.getServerName();

        return ResponseEntity.status(SERVER_OK.getHttpStatus())
                .body(DefaultNoResponse.noDataBuilder()
                        .responseCode(SERVER_OK.name())
                        .responseMessage(serverName.concat(" ").concat(SERVER_OK.getMessage()))
                        .build());
    }

    @ApiOperation(value = "모니터링 체크",
            notes = "<응답 코드>\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/monitor")
    public ResponseEntity<DefaultNoResponse> monitorCheck() {
        throw new CustomException(SERVER_ERROR);
    }

    @ApiOperation(value = "데이터베이스 초기화",
            notes = "<응답 코드>\n" +
                    "- 200 = DATABASE_RESET\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @DeleteMapping("/test")
    public ResponseEntity<DefaultNoResponse> resetAndInjectTest() {
        developService.resetAndInject();

        return ResponseEntity.status(DATABASE_RESET.getHttpStatus())
                .body(DefaultNoResponse.noDataBuilder()
                        .responseCode(DATABASE_RESET.name())
                        .responseMessage(DATABASE_RESET.getMessage())
                        .build());
    }

    @ApiOperation(value = "테스트 계정 토큰 발급",
            notes = "<값>\n" +
                    "- tester-id = 1 ~ 100\n\n" +
                    "<응답 코드>\n" +
                    "- 200 = TESTER_TOKEN_ISSUED\n" +
                    "- 400 = TESTER_ID_POSITIVE_ONLY\n" +
                    "- 404 = TESTER_NOT_FOUND\n" +
                    "- 500 = SERVER_ERROR\n" +
                    "- 503 = ONGOING_INSPECTION\n")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR"),
            @ApiResponse(responseCode = "503", description = "SERVICE UNAVAILABLE")
    })
    @GetMapping("/test/user/{tester-id}")
    public ResponseEntity<DefaultNoResponse> testDataToken(
            @PathVariable(value = "tester-id")
            @Positive(message = "테스터 식별자는 양수만 가능합니다.")
            Long testerId
    ) {
        String username = developService.findTester(testerId);

        HttpHeaders headers = jwtProvider.createJwt(username);

        return ResponseEntity.status(TESTER_TOKEN_ISSUED.getHttpStatus())
                .headers(headers)
                .body(DefaultNoResponse.noDataBuilder()
                        .responseCode(TESTER_TOKEN_ISSUED.name())
                        .responseMessage(TESTER_TOKEN_ISSUED.getMessage())
                        .build());
    }
}
