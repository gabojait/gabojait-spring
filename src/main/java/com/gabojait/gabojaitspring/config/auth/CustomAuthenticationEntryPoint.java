package com.gabojait.gabojaitspring.config.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabojait.gabojaitspring.common.response.ExceptionResponse;
import com.gabojait.gabojaitspring.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static com.gabojait.gabojaitspring.common.constant.code.ErrorCode.SERVER_ERROR;
import static com.gabojait.gabojaitspring.common.constant.code.ErrorCode.TOKEN_UNAUTHENTICATED;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        try {
            String responseBody = objectMapper.writeValueAsString(ExceptionResponse.builder()
                    .responseCode(TOKEN_UNAUTHENTICATED.name())
                    .responseMessage(TOKEN_UNAUTHENTICATED.getMessage())
                    .build());

            OutputStream outputStream = response.getOutputStream();
            outputStream.write(responseBody.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            throw new CustomException(SERVER_ERROR, e.getCause());
        }
    }
}