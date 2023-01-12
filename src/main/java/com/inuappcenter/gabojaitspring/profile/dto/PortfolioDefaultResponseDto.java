package com.inuappcenter.gabojaitspring.profile.dto;

import com.inuappcenter.gabojaitspring.profile.domain.Portfolio;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(value = "Portfolio 응답")
public class PortfolioDefaultResponseDto {

    @ApiModelProperty(position = 1, required = true, value = "포트폴리오 식별자")
    private String portfolioId;

    @ApiModelProperty(position = 2, required = true, value = "포트폴리오 타입")
    private Character portfolioType;

    @ApiModelProperty(position = 3, required = true, value = "이름")
    private String name;

    @ApiModelProperty(position = 4, required = true, value = "링크")
    private String url;

    @ApiModelProperty(position = 5, required = true, value = "스키마버전")
    private String schemaVersion;

    public PortfolioDefaultResponseDto(Portfolio portfolio) {
        this.portfolioId = portfolio.getId().toString();
        this.portfolioType = portfolio.getPortfolioType();
        this.name = portfolio.getName();
        this.url = portfolio.getUrl();
        this.schemaVersion = portfolio.getSchemaVersion();
    }
}
