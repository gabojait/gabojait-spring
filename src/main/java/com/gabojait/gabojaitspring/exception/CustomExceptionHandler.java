package com.gabojait.gabojaitspring.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.gabojait.gabojaitspring.common.dto.ExceptionResDto;
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
import static org.springframework.http.HttpStatus.PAYLOAD_TOO_LARGE;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { CustomException.class })
    protected ResponseEntity<ExceptionResDto> handleCustomException(CustomException exception) {
//        if (exception.getExceptionCode().getHttpStatus().equals(INTERNAL_SERVER_ERROR))
//            Sentry.captureException(exception);

        return ExceptionResDto.exceptionResponse(exception.getExceptionCode());
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

        responseCode = responseCode.concat(formatResponseCode(responseMessage));

        return ResponseEntity.status(status)
                .body(ExceptionResDto.builder()
                        .responseCode(responseCode)
                        .responseMessage(responseMessage)
                        .build());
    }

    @ExceptionHandler(value = { ConstraintViolationException.class })
    protected ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException exception) {
        String responseCode;
        String responseMessage;

        if (exception.getConstraintViolations().stream().findFirst().isPresent()) {
            ConstraintViolation<?> constraintViolation = exception.getConstraintViolations().stream().findFirst().get();

            int propertyPathSize = constraintViolation.getPropertyPath().toString().split("\\.").length;

            responseCode = constraintViolation.getPropertyPath()
                    .toString().split("\\.")[propertyPathSize - 1]
                    .toUpperCase();

            responseMessage = constraintViolation.getMessageTemplate();
            responseMessage.concat(formatResponseCode(responseMessage));
        } else {
            throw new CustomException(null, SERVER_ERROR);
        }

        return ResponseEntity.status(400)
                .body(ExceptionResDto.builder()
                        .responseCode(responseCode)
                        .responseMessage(responseMessage)
                        .build());
    }

    private String formatResponseCode(String responseMessage) {
        String responseCode = "";

        if (responseMessage.contains("입력해 주세요") || responseMessage.contains("첨부해 주세요")) {
            responseCode = responseCode.concat("_FIELD_REQUIRED"); // @NotBlank, @NotNull
        } else if (responseMessage.contains("~")) {
            responseCode = responseCode.concat("_LENGTH_INVALID"); // @Size
        } else if (responseMessage.contains("형식") || responseMessage.contains("조합")) {
            responseCode = responseCode.concat("_FORMAT_INVALID"); // @Pattern, @Email - format
        } else if (responseMessage.contains("중 하나여야 됩니다.")) {
            responseCode = responseCode.concat("_TYPE_INVALID"); // @Pattern - type
        } else if (responseMessage.contains("양수 또는 0")) {
            responseCode = responseCode.concat("_POSITIVE_OR_ZERO_ONLY"); // @PositiveOrZero
        } else if (responseMessage.contains("양수")) {
            responseCode = responseCode.concat("_POSITIVE_ONLY"); // @Positive
        } else if (responseMessage.contains("요청")) {
            responseCode = responseCode.concat("REQUEST_INVALID");
        }

        return responseCode;
    }

    /**
     * 401 Unauthorized
     */
    @ExceptionHandler(value = JWTVerificationException.class)
    protected ResponseEntity<ExceptionResDto> handleJwtVerificationException(JWTVerificationException exception) {
        return ExceptionResDto.exceptionResponse(TOKEN_UNAUTHORIZED);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    protected ResponseEntity<ExceptionResDto> handleAccessDeniedException(AccessDeniedException exception) {
        return ExceptionResDto.exceptionResponse(TOKEN_UNAUTHORIZED);
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
                .body(ExceptionResDto.builder()
                        .responseCode(responseCode)
                        .responseMessage(responseMessage)
                        .build());
    }

    /**
     * 413 Payload Too Large
     */
    @ExceptionHandler(value = { MaxUploadSizeExceededException.class })
    @ResponseStatus(PAYLOAD_TOO_LARGE)
    protected ResponseEntity<ExceptionResDto> handleFileSizeLimitExceeded() {
        return ExceptionResDto.exceptionResponse(FILE_SIZE_EXCEED);
    }
}
