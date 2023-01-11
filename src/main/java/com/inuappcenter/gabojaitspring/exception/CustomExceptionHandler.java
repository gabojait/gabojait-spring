package com.inuappcenter.gabojaitspring.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { CustomException.class })
    protected ResponseEntity<DefaultExceptionResponseDto> handleCustomException(CustomException exception) {
        log.error("ERROR | " + exception.getExceptionCode().name() + " | " + exception.getCause());

        return DefaultExceptionResponseDto.exceptionResponse(exception.getExceptionCode());
    }

    /**
     * 400 Bad Request |
     * 잘못된 응답 문법으로 인하여 서버가 요청하여 이해할 수 없습니다.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        String responseMessage = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();
        String responseCode;

        if (responseMessage.contains("입력")) {
            responseCode = "FIELD_REQUIRED";
        } else if (responseMessage.contains("~")) {
            responseCode = exception.getFieldError().getField().toUpperCase().concat("_LENGTH_INVALID");
        } else if (responseMessage.contains("형식")) {
            responseCode = exception.getFieldError().getField().toUpperCase().concat("_FORMAT_INVALID");
        } else if (responseMessage.contains("한글")) {
            responseCode = exception.getFieldError().getField().toUpperCase().concat("_NOT_KOREAN");
        } else if (responseMessage.contains("하나")) {
            responseCode = exception.getFieldError().getField().toUpperCase().concat("_INCORRECT_TYPE");
        } else {
            responseCode = exception.getFieldError().getField().toUpperCase();
        }

        log.error("ERROR | " + responseMessage + " | " + exception.getCause());

        return ResponseEntity.status(status).body(
                DefaultExceptionResponseDto.builder()
                        .responseCode(responseCode)
                        .responseMessage(responseMessage)
                        .build()
        );
    }

    /**
     * 405 Method Not Allowed |
     * 요청한 메소드는 서머에서 알고 있지만, 제거되었고 사용할 수 없습니다.
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException exception,
                                                                         HttpHeaders headers,
                                                                         HttpStatus status,
                                                                         WebRequest request) {
        String responseMessage = "사용할 수 없는 메소드입니다";
        String responseCode = "METHOD_NOT_ALLOWED";
        LocalDateTime timestamp = LocalDateTime.now();

        log.error("ERROR | " + responseMessage + " | " + timestamp + " | " + exception.getCause());

        return ResponseEntity.status(status).body(
                DefaultExceptionResponseDto.builder()
                        .responseCode(responseCode)
                        .responseMessage(responseMessage)
                        .build()
        );
    }
}
