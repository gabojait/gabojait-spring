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
@GroupSequence({PortfolioFileSaveRequestDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.Size.class})
@ApiModel(value = "Portfolio 파일 생성 요청")
public class PortfolioFileSaveRequestDto {

    @ApiModelProperty(position = 1, required = true, value = "이름", example = "깃허브")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 1, max = 10, message = "이름은 1~10자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String name;

    public Portfolio toEntity(ObjectId profileId, PortfolioType portfolioType, String url) {
        return Portfolio.builder()
                .portfolioType(portfolioType)
                .name(this.name)
                .url(url)
                .profileId(profileId)
                .build();
    }
}
