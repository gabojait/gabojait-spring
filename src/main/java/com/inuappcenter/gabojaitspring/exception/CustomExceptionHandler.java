package com.inuappcenter.gabojaitspring.exception;

import com.inuappcenter.gabojaitspring.exception.http.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * 400 Bad Request
     * 잘못된 응답 문법으로 인하여 서버가 요청하여 이해할 수 없습니다.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        String responseMessage = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();
        String responseCode;
        LocalDateTime timestamp = LocalDateTime.now();

        if (Objects.requireNonNull(responseMessage).contains("입력")) {
            responseCode = exception.getFieldError().getField().toUpperCase().concat("_REQUIRED");
        } else if (responseMessage.contains("~")) {
            responseCode = exception.getFieldError().getField().toUpperCase().concat("_LENGTH_INVALID");
        } else if (responseMessage.contains("형식")) {
            responseCode = exception.getFieldError().getField().toUpperCase().concat("_FORMAT_INVALID");
        } else {
            responseCode = exception.getFieldError().getField().toUpperCase();
        }

        log.error(responseMessage + " | " +
                exception.getFieldError().getField() + ": " +
                ((Objects.requireNonNull(exception.getFieldError().getRejectedValue()).toString() == null) ?
                        "null" : exception.getFieldError().getRejectedValue().toString()) +
                " At " + timestamp);

        return ResponseEntity.status(status).body(
                DefaultExceptionResponseDto.builder()
                        .responseCode(responseCode)
                        .responseMessage(responseMessage)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    /**
     * 401 Unauthorized
     * 미승인 또는 비인증이므로 클라이언트는 요청한 응답을 받기 위해서 반드시 스스로를 인증해야됩니다.
     */
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    protected ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException exception) {
        String responseMessage = exception.getMessage();
        String responseCode = "UNAUTHORIZED";
        LocalDateTime timestamp = LocalDateTime.now();

        log.error(responseMessage + " | " +
                exception.toString() +
                " At " + timestamp);

        return ResponseEntity.status(401).body(
                DefaultExceptionResponseDto.builder()
                        .responseCode(responseCode)
                        .responseMessage(responseMessage)
                        .timestamp(timestamp)
                        .build()
        );
    }

    /**
     * 403 Forbidden
     * 클라이언트가 권한이 없기 때문에 작업을 진행할 수 없습니다.
     */
    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleForbiddenException(ForbiddenException exception) {
        String responseMessage = exception.getMessage();
        String responseCode = "FORBIDDEN";
        LocalDateTime timestamp = LocalDateTime.now();

        log.error(responseMessage + " | " +
                exception.toString() +
                " At " + timestamp);

        return ResponseEntity.status(403).body(
                DefaultExceptionResponseDto.builder()
                        .responseCode(responseCode)
                        .responseMessage(responseMessage)
                        .timestamp(timestamp)
                        .build()
        );
    }

    /**
     * 404 Not Found
     * 클라이언트는 콘텐츠에 접근할 권리를 가지고 있지 않습니다.
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleForbiddenException(NotFoundException exception) {
        String responseMessage = exception.getMessage();
        String responseCode = "NOT_FOUND";
        LocalDateTime timestamp = LocalDateTime.now();

        log.error(responseMessage + " | " +
                exception.toString() +
                " At " + timestamp);

        return ResponseEntity.status(404).body(
                DefaultExceptionResponseDto.builder()
                        .responseCode(responseCode)
                        .responseMessage(responseMessage)
                        .timestamp(timestamp)
                        .build()
        );
    }

    /**
     * 405 Method Not Allowed
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

        log.error(responseMessage + " | " +
                exception.getMethod() +
                " At " + timestamp);

        return ResponseEntity.status(status).body(
                DefaultExceptionResponseDto.builder()
                        .responseCode(responseCode)
                        .responseMessage(responseMessage)
                        .timestamp(timestamp)
                        .build()
        );
    }

    /**
     * 406 Not Acceptable
     * 서버 주도 콘텐츠 협상을 수행한 후, 사용자 에이전트에서 정해준 규격에 따른 어떠한 콘텐츠도 찾지 않습니다.
     */
    @ExceptionHandler(NotAcceptableException.class)
    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
    public ResponseEntity<Object> handleNotAcceptableException(NotAcceptableException exception) {
        String responseMessage = exception.getMessage();
        String responseCode = "NOT_ACCEPTABLE";
        LocalDateTime timestamp = LocalDateTime.now();

        log.error(responseMessage + " | " +
                exception.getMessage() + ": " +
                exception.getCause() +
                " At " + timestamp);

        return ResponseEntity.status(406).body(
                DefaultExceptionResponseDto.builder()
                        .responseCode(responseCode)
                        .responseMessage(responseMessage)
                        .timestamp(timestamp)
                        .build()
        );
    }


    /**
     * 409 Conflict
     * 요청이 현재 서버의 상태와 충돌됩니다.
     */
    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleConflictException(ConflictException exception) {
        String responseMessage = exception.getMessage();
        String responseCode = "CONFLICT";
        LocalDateTime timestamp = LocalDateTime.now();

        log.error(responseMessage + " | " +
                exception.getMessage() + ": " +
                exception.getCause() +
                " At " + timestamp);

        return ResponseEntity.status(409).body(
                DefaultExceptionResponseDto.builder()
                        .responseCode(responseCode)
                        .responseMessage(responseMessage)
                        .timestamp(timestamp)
                        .build()
        );
    }


    /**
     * 500 Internal Server Error
     * 서버에 문제가 있음을 의미하지만 서버는 정확한 무제에 대해 더 구체적으로 설명할 수 없습니다.
     */
    @ExceptionHandler(InternalServerErrorException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleInternalServerException(InternalServerErrorException exception) {
        String responseMessage = "서버 에러가 발생했습니다";
        String responseCode = "INTERNAL_SERVER_ERROR";
        LocalDateTime timestamp = LocalDateTime.now();

        log.error(responseMessage + " | " +
                exception.getMessage() + ": " +
                exception.getCause() +
                " At " + timestamp);

        return ResponseEntity.status(500).body(
                DefaultExceptionResponseDto.builder()
                        .responseCode(responseCode)
                        .responseMessage(responseMessage)
                        .timestamp(timestamp)
                        .build()
        );
    }
}