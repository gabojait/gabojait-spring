package com.inuappcenter.gabojaitspring.profile.dto;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@GroupSequence({PortfolioUpdateRequestDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.NotNull.class,
        ValidationSequence.Size.class})
@ApiModel(value = "Portfolio 수정 요청")
public class PortfolioUpdateRequestDto {

    @ApiModelProperty(position = 1, required = true, value = "포트폴리오 식별자")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    private String portfolioId;

    @ApiModelProperty(position = 1, required = true, value = "포트폴리오 타입: L, F", example = "L",
            allowableValues = "L, F")
    @NotNull(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    private Character portfolioType;

    @ApiModelProperty(position = 2, required = true, value = "이름", example = "깃허브")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 1, max = 10, message = "이름은 1~10자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String name;

    @ApiModelProperty(position = 3, required = true, value = "링크", example = "github.com/gabojait")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    private String url;
}
