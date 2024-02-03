package com.gabojait.gabojaitspring.common.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DefaultMultiResponse<T> extends DefaultNoResponse {

    @ApiModelProperty(position = 3, value = "데이터")
    private PageData<T> responseData;

    @Builder(builderMethodName = "multiDataBuilder", builderClassName = "multiDataBuilder")
    public DefaultMultiResponse(String responseCode, String responseMessage, PageData responseData) {
        super(responseCode, responseMessage);

        this.responseData = responseData;
    }
}
