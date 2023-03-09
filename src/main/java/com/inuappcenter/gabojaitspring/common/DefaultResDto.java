package com.inuappcenter.gabojaitspring.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@ApiModel(value = "기본 응답")
public class DefaultResDto<T> {

    @ApiModelProperty(position = 1, dataType = "String", value = "응답 코드", example = "RESPONSE_CODE")
    private String responseCode;

    @ApiModelProperty(position = 2, dataType = "String", value = "응답 메세지", example = "Response message.")
    private String responseMessage;

    @ApiModelProperty(position = 3, dataType = "Object", value = "데이터")
    private T data;

    @ApiModelProperty(position = 4, dataType = "Integer", value = "총 페이지 수", example = "100")
    private Integer totalPageSize;

    public DefaultResDto(final String responseCode, final String responseMessage) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.data = null;
        this.totalPageSize = null;
    }

    public DefaultResDto(final String responseCode, final String responseMessage, final Integer totalPageNum) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.data = null;
        this.totalPageSize = totalPageNum;
    }

    public static <T> DefaultResDto<T> response(final String responseCode,
                                                final String responseMessage) {
        return response(responseCode, responseMessage, null, null);
    }

    public static <T> DefaultResDto<T> response(final String responseCode,
                                                final String responseMessage,
                                                final Integer totalPageNum) {
        return response(responseCode, responseMessage, null, totalPageNum);
    }

    public static <T> DefaultResDto<T> response(final String responseCode,
                                                final String responseMessage,
                                                final T data) {
        return DefaultResDto.<T>builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .data(data)
                .build();

    }

    public static <T> DefaultResDto<T> response(final String responseCode,
                                                final String responseMessage,
                                                final T data,
                                                final Integer totalPageSize) {
        return DefaultResDto.<T>builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .data(data)
                .totalPageSize(totalPageSize)
                .build();
    }
}
