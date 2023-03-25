package com.inuappcenter.gabojaitspring.profile.dto.req;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import com.inuappcenter.gabojaitspring.profile.domain.Portfolio;
import com.inuappcenter.gabojaitspring.profile.domain.type.PortfolioType;
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
@GroupSequence({PortfolioLinkCreateReqDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.Size.class})
@ApiModel(value = "Portfolio 링크 생성 요청")
public class PortfolioLinkCreateReqDto {

    @ApiModelProperty(position = 1, required = true, value = "이름", example = "깃허브")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 1, max = 10, message = "포트폴리오명은 1~10자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String portfolioName;

    @ApiModelProperty(position = 2, required = true, value = "링크", example = "github.com/gabojait")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 1, max = 1000, message = "URL은 1~1000자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String url;

    public Portfolio toEntity(ObjectId userId) {
        return Portfolio.builder()
                .userId(userId)
                .portfolioType(PortfolioType.LINK)
                .name(this.portfolioName)
                .url(this.url)
                .build();
    }
}
