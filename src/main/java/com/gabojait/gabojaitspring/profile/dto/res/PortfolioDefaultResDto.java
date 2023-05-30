package com.gabojait.gabojaitspring.profile.dto.res;

import com.gabojait.gabojaitspring.profile.domain.Portfolio;
import com.gabojait.gabojaitspring.profile.domain.type.Media;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@ApiModel(value = "포트폴리오 기본 응답")
public class PortfolioDefaultResDto {

    @ApiModelProperty(position = 1, required = true, value = "포트폴리오 식별자")
    private String portfolioId;

    @ApiModelProperty(position = 2, required = true, value = "미디어")
    private String media;

    @ApiModelProperty(position = 3, required = true, value = "포트폴리오명")
    private String portfolioName;

    @ApiModelProperty(position = 4, required = true, value = "링크")
    private String url;

    @ApiModelProperty(position = 5, required = true, value = "스키마 버전")
    private String schemaVersion;

    public PortfolioDefaultResDto(Portfolio portfolio) {
        this.portfolioId = portfolio.getId().toString();
        this.media = Media.fromChar(portfolio.getMedia()).name().toLowerCase();
        this.portfolioName = portfolio.getPortfolioName();
        this.url = portfolio.getUrl();
        this.schemaVersion = portfolio.getSchemaVersion();
    }
}
