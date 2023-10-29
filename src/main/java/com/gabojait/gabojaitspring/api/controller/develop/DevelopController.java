package com.gabojait.gabojaitspring.api.controller.develop;

import com.gabojait.gabojaitspring.api.dto.common.ValidationSequence;
import com.gabojait.gabojaitspring.api.dto.common.response.DefaultNoResponse;
import com.gabojait.gabojaitspring.api.service.develop.DevelopService;
import com.gabojait.gabojaitspring.exception.CustomException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.GroupSequence;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.SERVER_ERROR;
import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;

@Api(tags = "개발")
@Validated
@GroupSequence({DevelopController.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class DevelopController {
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
}
