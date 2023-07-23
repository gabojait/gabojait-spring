package com.gabojait.gabojaitspring.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@ApiModel(value = "* 기본 무건 응답")
public class DefaultNoResDto {

    @ApiModelProperty(position = 1, value = "코드", example = "RESPONSE_CODE")
    private String responseCode;

    @ApiModelProperty(position = 2, value = "메세지", example = "응답 메세지입니다.")
    private String responseMessage;

    @Builder(builderMethodName = "noDataBuilder", builderClassName = "noDataBuilder")
    public DefaultNoResDto(final String responseCode, final String responseMessage) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }
}
