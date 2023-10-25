package com.gabojait.gabojaitspring.api.dto.common.response;


import com.gabojait.gabojaitspring.common.code.ErrorCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.ResponseEntity;

@Getter
@ToString
@Builder
@ApiModel(value = "* 에러 응답")
public class ExceptionResponse {

    @ApiModelProperty(position = 1, dataType = "String", value = "에러 코드", example = "EXCEPTION_CODE")
    private String responseCode;

    @ApiModelProperty(position = 2, dataType = "Object", value = "메세지")
    private String responseMessage;

    public static ResponseEntity<ExceptionResponse> exceptionResponse(final ErrorCode exceptionCode) {
        return ResponseEntity
                .status(exceptionCode.getHttpStatus())
                .body(ExceptionResponse.builder()
                        .responseCode(exceptionCode.name())
                        .responseMessage(exceptionCode.getMessage())
                        .build());
    }
}
