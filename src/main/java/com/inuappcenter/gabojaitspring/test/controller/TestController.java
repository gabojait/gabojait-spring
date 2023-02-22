package com.inuappcenter.gabojaitspring.test.controller;

import com.inuappcenter.gabojaitspring.auth.JwtProvider;
import com.inuappcenter.gabojaitspring.common.DefaultResDto;
import com.inuappcenter.gabojaitspring.test.service.TestService;
import com.inuappcenter.gabojaitspring.user.domain.User;
import com.inuappcenter.gabojaitspring.user.service.UserService;
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

import static com.inuappcenter.gabojaitspring.common.SuccessCode.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

// TODO: Must remove before service deployment
@Api(tags = "개발")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dev")
public class TestController {

    private final TestService testService;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "데이터베이스 초기화")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @DeleteMapping
    public ResponseEntity<DefaultResDto<Object>> deleteAll() {

        testService.resetDatabase();

        return ResponseEntity.status(204)
                .body(DefaultResDto.builder()
                        .build());
    }

    @ApiOperation(value = "테스트 계정 토큰 재발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TOKEN_RENEWED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND")
    })
    @GetMapping("/token/renew/{test-id}")
    public ResponseEntity<DefaultResDto<Object>> renewToken(@PathVariable(value = "test-id") Integer testId) {


        User user = userService.findOneByUsername("test" + testId);

        String newTokens = jwtProvider.generateJwt(String.valueOf(user.getId()), user.getRoles());

        return ResponseEntity.status(USER_TOKEN_RENEWED.getHttpStatus())
                .header(AUTHORIZATION, newTokens)
                .body(DefaultResDto.builder()
                        .responseCode(USER_TOKEN_RENEWED.name())
                        .responseMessage(USER_TOKEN_RENEWED.getMessage())
                        .build());
    }
}
