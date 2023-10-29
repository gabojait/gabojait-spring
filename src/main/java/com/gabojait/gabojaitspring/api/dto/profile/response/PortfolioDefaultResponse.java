package com.gabojait.gabojaitspring.api.dto.profile.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.domain.profile.Media;
import com.gabojait.gabojaitspring.domain.profile.Portfolio;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@ApiModel(value = "포트폴리오 기본 응답")
public class PortfolioDefaultResponse {

    @ApiModelProperty(position = 1, required = true, value = "포트폴리오 식별자")
    private Long portfolioId;

    @ApiModelProperty(position = 2, required = true, value = "포트폴리오명")
    private String portfolioName;

    @ApiModelProperty(position = 3, required = true, value = "포트폴리오 링크")
    private String portfolioUrl;

    @ApiModelProperty(position = 4, required = true, value = "미디어", allowableValues = "LINK, FILE")
    private Media media;

    @ApiModelProperty(position = 5, required = true, value = "생성일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @ApiModelProperty(position = 6, required = true, value = "수정일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public PortfolioDefaultResponse(Portfolio portfolio) {
        this.portfolioId = portfolio.getId();
        this.portfolioName = portfolio.getPortfolioName();
        this.portfolioUrl = portfolio.getPortfolioUrl();
        this.media = portfolio.getMedia();
        this.createdAt = portfolio.getCreatedAt();
        this.updatedAt = portfolio.getUpdatedAt();
    }
}
