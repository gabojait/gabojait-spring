package com.gabojait.gabojaitspring.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@ApiModel(value = "* 기본 단건 응답")
public class DefaultSingleResDto<T> extends DefaultNoResDto {

    @ApiModelProperty(position = 3, value = "데이터")
    private Object responseData;

    @Builder(builderMethodName = "singleDataBuilder", builderClassName = "singleDataBuilder")
    public DefaultSingleResDto(final String responseCode,
                               final String responseMessage,
                               final T data) {
        super(responseCode, responseMessage);

        this.responseData = data;
    }
}
