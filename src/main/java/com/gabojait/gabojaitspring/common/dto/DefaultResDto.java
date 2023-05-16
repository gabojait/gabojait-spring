package com.gabojait.gabojaitspring.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@ApiModel(value = "* 기본 응답")
public class DefaultResDto<T> {

    @ApiModelProperty(position = 1, dataType = "String", value = "코드", example = "RESPONSE_CODE")
    private String responseCode;

    @ApiModelProperty(position = 2, dataType = "Object", value = "메세지")
    private String responseMessage;

    @ApiModelProperty(position = 3, dataType = "Object", value = "데이터")
    private DataDto responseData;

    @Builder(builderMethodName = "noDataBuilder", builderClassName = "noDataBuilder")
    public DefaultResDto(final String responseCode, final String responseMessage) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.responseData = DataDto.noDataBuilder().build();
    }

    @Builder(builderMethodName = "singleDataBuilder", builderClassName = "singleDataBuilder")
    public DefaultResDto(final String responseCode,
                         final String responseMessage,
                         final T data) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.responseData = DataDto.singleDataBuilder()
                .data(data)
                .build();
    }

    @Builder(builderMethodName = "multiDataBuilder", builderClassName = "multiDataBuilder")
    public DefaultResDto(final String responseCode,
                         final String responseMessage,
                         final T data,
                         final Integer size) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.responseData = DataDto.multiDataBuilder()
                .data(data)
                .size(size)
                .build();
    }
}
