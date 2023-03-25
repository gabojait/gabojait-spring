package com.inuappcenter.gabojaitspring.profile.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@ApiModel(value = "Portfolio 링크/파일 기본 요청")
public class PortfolioDefaultReqDto {

    @ApiModelProperty(position = 1, value = "생성 링크 포트폴리오들")
    private List<PortfolioLinkCreateReqDto> createLinkPortfolios;

    @ApiModelProperty(position = 2, value = "수정 링크 포트폴리오들")
    private List<PortfolioLinkUpdateReqDto> updateLinkPortfolios;

    @ApiModelProperty(position = 3, value = "생성 파일 포트폴리오들")
    private List<PortfolioFileSaveReqDto> createFilePortfolios;

    @ApiModelProperty(position = 4, value = "수정 파일 포트폴리오들")
    private List<PortfolioFileUpdateReqDto> updateFilePortfolios;

    @ApiModelProperty(position = 5, value = "삭제 포트폴리오들")
    private List<String> deletePortfolios;
}
