package com.gabojait.gabojaitspring.common.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@ApiModel(value = "* 기본 무 응답")
public class DefaultNoResponse {

    @ApiModelProperty(position = 1, value = "코드", example = "RESPONSE_CODE")
    private String responseCode;

    @ApiModelProperty(position = 2, value = "메세지", example = "응답 메세지입니다.")
    private String responseMessage;

    @Builder(builderMethodName = "noDataBuilder", builderClassName = "noDataBuilder")
    public DefaultNoResponse(final String responseCode, final String responseMessage) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }
}
