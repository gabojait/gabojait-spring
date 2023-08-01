package com.gabojait.gabojaitspring.profile.dto;

import com.gabojait.gabojaitspring.profile.domain.Portfolio;
import com.gabojait.gabojaitspring.profile.domain.type.Media;
import com.gabojait.gabojaitspring.profile.dto.req.PortfolioDefaultReqDto;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PortfolioUpdateDto {

    private Portfolio prevPortfolio;
    private String portfolioName;
    private String portfolioUrl;
    private Media media;

    public PortfolioUpdateDto(Portfolio prevPortfolio, PortfolioDefaultReqDto newPortfolio) {
        this.prevPortfolio = prevPortfolio;
        this.portfolioName = newPortfolio.getPortfolioName();
        this.portfolioUrl = newPortfolio.getPortfolioUrl();
        this.media = Media.valueOf(newPortfolio.getMedia());
    }
}
