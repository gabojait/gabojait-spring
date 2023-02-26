package com.inuappcenter.gabojaitspring.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
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
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Objects;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;
import static org.springframework.http.HttpStatus.PAYLOAD_TOO_LARGE;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { CustomException.class })
    protected ResponseEntity<DefaultExceptionResDto> handleCustomException(CustomException exception) {
        log.error("[ERROR] {} - {}", exception.getExceptionCode().name(), exception.getCause());

        return DefaultExceptionResDto.exceptionResponse(exception.getExceptionCode());
    }

    /**
     * 400 Bad Request
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        String responseMessage = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();
        String responseCode = null;

        if (responseMessage.contains("입력")) {
            responseCode = "FIELD_REQUIRED";
        } else if (responseMessage.contains("~")) {
            responseCode = exception.getFieldError().getField().toUpperCase().concat("_LENGTH_INVALID");
        } else if (responseMessage.contains("형식")) {
            responseCode = exception.getFieldError().getField().toUpperCase().concat("_FORMAT_INVALID");
        } else if (responseMessage.contains("양수")) {
            responseCode = exception.getFieldError().getField().toUpperCase().concat("_POS_ZERO_ONLY");
        } else {
            responseCode = exception.getFieldError().getField().toUpperCase();
        }

        log.error("[ERROR] {} - {}", responseMessage, exception.getCause());

        return ResponseEntity.status(status).body(
                DefaultExceptionResDto.builder()
                        .responseCode(responseCode)
                        .responseMessage(responseMessage)
                        .build()
        );
    }

    @ExceptionHandler(value = { ConstraintViolationException.class })
    protected ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException exception) {
        String responseCode = null;
        String responseMessage = null;
        if (exception.getConstraintViolations().stream().findFirst().isPresent()) {
            ConstraintViolation<?> constraintViolation = exception.getConstraintViolations().stream().findFirst().get();

            int propertyPathSize = constraintViolation.getPropertyPath().toString().split("\\.").length;

            responseCode = constraintViolation.getPropertyPath()
                    .toString()
                    .split("\\.")[propertyPathSize - 1]
                    .toUpperCase();

            responseMessage = constraintViolation.getMessageTemplate();

            if (responseMessage.contains("입력")) {
                responseCode = "FIELD_REQUIRED";
            } else if (responseMessage.contains("~")) {
                responseCode = responseCode.concat("_LENGTH_INVALID");
            } else if (responseMessage.contains("형식")) {
                responseCode = responseCode.concat("_FORMAT_INVALID");
            } else if (responseMessage.contains("양수")) {
                responseCode = responseCode.concat("_NEGATIVEORZERO_INVALID");
            } else {
                responseCode = responseCode.toUpperCase();
            }
        } else {
            throw new CustomException(SERVER_ERROR);
        }

        log.error("[ERROR] {} - {}", responseMessage, exception.getCause());

        return ResponseEntity.status(400)
                .body(DefaultExceptionResDto.builder()
                        .responseCode(responseCode)
                        .responseMessage(responseMessage)
                        .build());
    }

    /**
     * 401 Unauthorized
     */
    @ExceptionHandler(value = { JWTVerificationException.class })
    protected ResponseEntity<DefaultExceptionResDto> handleJwtVerificationFail(JWTVerificationException exception) {

        log.error("[ERROR] {} - {}", TOKEN_AUTHENTICATION_FAIL.getMessage(), exception.getCause());

        return DefaultExceptionResDto.exceptionResponse(TOKEN_AUTHENTICATION_FAIL);
    }

    /**
     * 405 Method Not Allowed
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException exception,
                                                                         HttpHeaders headers,
                                                                         HttpStatus status,
                                                                         WebRequest request) {

        String responseCode = METHOD_NOT_ALLOWED.name();
        String responseMessage = METHOD_NOT_ALLOWED.getMessage();

        log.error("[ERROR] {} - {}", responseMessage, exception.getCause());

        return ResponseEntity.status(status)
                .body(DefaultExceptionResDto.builder()
                        .responseCode(responseCode)
                        .responseMessage(responseMessage)
                        .build());
    }

    /**
     * 415 Payload Too Large
     */
    @ExceptionHandler(value = { MaxUploadSizeExceededException.class })
    @ResponseStatus(PAYLOAD_TOO_LARGE)
    protected ResponseEntity<DefaultExceptionResDto> handleFileSizeLimitExceeded() {

        log.error("[ERROR] {} - {}", FILE_SIZE_EXCEED.name(), FILE_SIZE_EXCEED.getMessage());
        return DefaultExceptionResDto.exceptionResponse(FILE_SIZE_EXCEED);
    }
}
