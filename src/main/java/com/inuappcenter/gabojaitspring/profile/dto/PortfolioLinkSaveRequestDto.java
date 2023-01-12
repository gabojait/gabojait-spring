package com.inuappcenter.gabojaitspring.profile.dto;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import com.inuappcenter.gabojaitspring.profile.domain.Portfolio;
import com.inuappcenter.gabojaitspring.profile.domain.PortfolioType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@GroupSequence({PortfolioLinkSaveRequestDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.Size.class})
@ApiModel(value = "Portfolio 링크 생성 요청")
public class PortfolioLinkSaveRequestDto {

    @ApiModelProperty(position = 1, required = true, value = "이름", example = "깃허브")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 1, max = 10, message = "이름은 1~10자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String name;

    @ApiModelProperty(position = 2, required = true, value = "링크", example = "github.com/gabojait")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    private String url;

    public Portfolio toEntity(ObjectId profileId, PortfolioType portfolioType) {
        return Portfolio.builder()
                .portfolioType(portfolioType)
                .name(this.name)
                .url(this.url)
                .profileId(profileId)
                .build();
    }
}
