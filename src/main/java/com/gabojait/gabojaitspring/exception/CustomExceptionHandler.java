package com.gabojait.gabojaitspring.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.gabojait.gabojaitspring.api.dto.common.response.ExceptionResponse;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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

import static com.gabojait.gabojaitspring.common.code.ErrorCode.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.PAYLOAD_TOO_LARGE;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ExceptionResponse> handleCustomException(CustomException exception) {
        return ExceptionResponse.exceptionResponse(exception.getErrorCode());
    }

    /**
     * 400 Bad Request
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        String responseCode = exception.getFieldError().getField()
                .replaceAll("[^\\p{Alnum}]+", "_")
                .replaceAll("(\\p{Lower})(\\p{Upper})", "$1_$2")
                .toUpperCase();
        String responseMessage = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();

        responseCode = formatIfInnerDto(responseCode);
        responseCode += formatResponseCode(responseMessage);
        return ResponseEntity.status(status)
                .body(ExceptionResponse.builder()
                        .responseCode(responseCode)
                        .responseMessage(responseMessage)
                        .build());
    }

    /**
     * 400 Bad Request
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException exception) {
        String responseCode;
        String responseMessage;

        if (exception.getConstraintViolations().stream().findFirst().isPresent()) {
            ConstraintViolation<?> constraintViolation = exception.getConstraintViolations().stream().findFirst().get();

            int propertyPathSize = constraintViolation.getPropertyPath().toString().split("\\.").length;

            responseCode = constraintViolation.getPropertyPath()
                    .toString().split("\\.")[propertyPathSize - 1];

            responseCode = responseCode.replaceAll("[^\\p{Alnum}]+", "_")
                    .replaceAll("(\\p{Lower})(\\p{Upper})", "$1_$2")
                    .toUpperCase();

            responseCode = formatIfInnerDto(responseCode);
            responseMessage = constraintViolation.getMessageTemplate();
            responseCode += formatResponseCode(responseMessage);
        } else {
            throw new CustomException(SERVER_ERROR);
        }

        return ResponseEntity.status(400)
                .body(ExceptionResponse.builder()
                        .responseCode(responseCode)
                        .responseMessage(responseMessage)
                        .build());
    }

    private String formatResponseCode(String responseMessage) {
        String responseCode = "";

        if (responseMessage.contains("필수 입력입니다.") || responseMessage.contains("첨부해 주세요")) {
            responseCode = "_FIELD_REQUIRED"; // @NotBlank, @NotNull
        } else if (responseMessage.contains("~")) {
            responseCode = "_LENGTH_INVALID"; // @Size
        } else if (responseMessage.contains("형식") || responseMessage.contains("조합")) {
            responseCode = "_FORMAT_INVALID"; // @Pattern, @Email - format
        } else if (responseMessage.contains("중 하나여야 됩니다.")) {
            responseCode = "_TYPE_INVALID"; // @Pattern - type
        } else if (responseMessage.contains("까지의 수만")) {
            responseCode = "_RANGE_INVALID"; // @Range
        } else if (responseMessage.contains("0 또는 양수")) {
            responseCode = "_POSITIVE_OR_ZERO_ONLY"; // @PositiveOrZero
        } else if (responseMessage.contains("양수")) {
            responseCode = "_POSITIVE_ONLY"; // @Positive
        } else if (responseMessage.contains("요청")) {
            responseCode = "REQUEST_INVALID";
        }

        return responseCode;
    }

    private String formatIfInnerDto(String responseCode) {
        if (!responseCode.matches(".*\\d+.*"))
            return responseCode;

        for (int i = responseCode.length() - 1; i >= 0; i--) {
            char c = responseCode.charAt(i);
            if (Character.isDigit(c))
                return responseCode.substring(i + 2);
        }

        return responseCode;
    }

    /**
     * 401 Unauthorized
     */
    @ExceptionHandler(JWTVerificationException.class)
    protected ResponseEntity<ExceptionResponse> handleJwtVerificationException(JWTVerificationException exception) {
        return ExceptionResponse.exceptionResponse(TOKEN_UNAUTHORIZED);
    }

    /**
     * 401 Unauthorized
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    protected ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException exception) {
        return ExceptionResponse.exceptionResponse(TOKEN_UNAUTHORIZED);
    }

    /**
     * 405 Method Not Allowed
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException exception,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        String responseCode = METHOD_DISABLED.name();
        String responseMessage = METHOD_DISABLED.getMessage();

        return ResponseEntity.status(status)
                .body(ExceptionResponse.builder()
                        .responseCode(responseCode)
                        .responseMessage(responseMessage)
                        .build());
    }

    /**
     * 413 Payload Too Large
     */
    @ResponseStatus(PAYLOAD_TOO_LARGE)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected ResponseEntity<ExceptionResponse> handleFileSizeLimitExceeded() {
        return ExceptionResponse.exceptionResponse(FILE_SIZE_EXCEED);
    }

    /**
     * 500 Internal Server Error
     */
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ExceptionResponse> handleException(Exception exception) {
        Sentry.captureException(exception);
        return ExceptionResponse.exceptionResponse(SERVER_ERROR);
    }

    /**
     * 500 Internal Server Error
     */
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InterruptedException.class)
    protected ResponseEntity<ExceptionResponse> handleInterruptedException(InterruptedException exception) {
        Sentry.captureException(exception);
        return ExceptionResponse.exceptionResponse(SERVER_ERROR);
    }
}
