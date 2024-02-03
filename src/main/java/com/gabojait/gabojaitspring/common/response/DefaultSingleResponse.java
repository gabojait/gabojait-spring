package com.gabojait.gabojaitspring.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@ApiModel(value = "* 기본 단건 응답")
public class DefaultSingleResponse<T> extends DefaultNoResponse {

    @ApiModelProperty(position = 3, value = "데이터")
    private Object responseData;

    @Builder(builderMethodName = "singleDataBuilder", builderClassName = "singleDataBuilder")
    public DefaultSingleResponse(final String responseCode,
                               final String responseMessage,
                               final T data) {
        super(responseCode, responseMessage);

        this.responseData = data;
    }
}
