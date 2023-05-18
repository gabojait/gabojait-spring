package com.gabojait.gabojaitspring.profile.dto.req;

import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@GroupSequence({PortfolioLinkUpdateReqDto.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class})
@ApiModel(value = "포트폴리오 링크 업데이트 요청")
public class PortfolioLinkUpdateReqDto extends PortfolioLinkCreateReqDto {

    @ApiModelProperty(position = 3, required = true, value = "포트폴리오 식별자")
    @NotBlank(message = "포트폴리오 식별자는 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    private String portfolioId;
}
