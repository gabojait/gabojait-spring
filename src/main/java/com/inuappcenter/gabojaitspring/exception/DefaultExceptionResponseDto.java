package com.inuappcenter.gabojaitspring.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DefaultExceptionResponseDto {

    private final String responseCode;

    private final String responseMessage;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private final LocalDateTime timestamp;

    @Builder
    public DefaultExceptionResponseDto(String responseCode, String responseMessage, LocalDateTime timestamp) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.timestamp = timestamp;
    }
}
