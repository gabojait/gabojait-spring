package com.inuappcenter.gabojaitspring.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@ApiModel(value = "기본 응답")
public class DefaultResDto<T> {

    @ApiModelProperty(position = 1, dataType = "String", value = "응답 코드", example = "RESPONSE_CODE")
    private String responseCode;

    @ApiModelProperty(position = 2, dataType = "String", value = "응답 메세지", example = "Response message.")
    private String responseMessage;

    @ApiModelProperty(position = 3, dataType = "Object", value = "데이터")
    private DefaultDataDto responseData;

    @Builder(builderClassName = "NoDataBuilder", builderMethodName = "NoDataBuilder")
    public DefaultResDto(final String responseCode, final String responseMessage) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.responseData = DefaultDataDto.NoDataBuilder().build();
    }

    @Builder(builderClassName = "SingleDataBuilder", builderMethodName = "SingleDataBuilder")
    public DefaultResDto(final String responseCode, final String responseMessage, final T data) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.responseData = DefaultDataDto.SingleDataBuilder()
                .data(data)
                .build();
    }

    @Builder(builderClassName = "MultiDataBuilder", builderMethodName = "MultiDataBuilder")
    public DefaultResDto(final String responseCode, final String responseMessage, final T data, final Integer size) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.responseData = DefaultDataDto.MultiDataBuilder()
                .data(data)
                .size(size)
                .build();
    }
}
