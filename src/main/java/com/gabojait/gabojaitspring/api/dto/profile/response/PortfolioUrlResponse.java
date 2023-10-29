package com.gabojait.gabojaitspring.api.dto.profile.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@ApiModel(value = "포트폴리오 URL 응답")
public class PortfolioUrlResponse {

    @ApiModelProperty(position = 1, required = true, value = "포트폴리오 URL")
    private String portfolioUrl;

    public PortfolioUrlResponse(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }
}
