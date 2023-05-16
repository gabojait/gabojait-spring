package com.gabojait.gabojaitspring.profile.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@ApiModel(value = "포트폴리오 링크 기본 요청")
public class PortfolioLinkDefaultReqDto {

    @ApiModelProperty(position = 1, value = "생성 링크 포트폴리오들")
    private List<PortfolioLinkCreateReqDto> createLinkPortfolios;

    @ApiModelProperty(position = 2, value = "수정 링크 포트폴리오들")
    private List<PortfolioLinkUpdateReqDto> updateLinkPortfolios;

    @ApiModelProperty(position = 3, value = "삭제 포트폴리오 식별자들")
    private List<String> deletePortfolioIds;
}
