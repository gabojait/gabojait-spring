package com.gabojait.gabojaitspring.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DefaultMultiResDto<T> extends DefaultNoResDto {

    @ApiModelProperty(position = 3, value = "데이터")
    private PageDataDto responseData;

    @Builder(builderMethodName = "multiDataBuilder", builderClassName = "multiDataBuilder")
    public DefaultMultiResDto(String responseCode, String responseMessage, final T data, final long size) {
        super(responseCode, responseMessage);

        this.responseData = PageDataDto.builder()
                .data(data)
                .size(size)
                .build();
    }
}
