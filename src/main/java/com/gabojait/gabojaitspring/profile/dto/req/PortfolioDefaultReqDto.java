package com.gabojait.gabojaitspring.profile.dto.req;

import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import com.gabojait.gabojaitspring.profile.domain.Portfolio;
import com.gabojait.gabojaitspring.profile.domain.type.Media;
import com.gabojait.gabojaitspring.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@GroupSequence({PortfolioDefaultReqDto.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class})
@ApiModel(value = "포트폴리오 기본 요청")
public class PortfolioDefaultReqDto {

    @ApiModelProperty(position = 1, required = true, value = "포트폴리오 식별자")
    private Long portfolioId;

    @ApiModelProperty(position = 1, required = true, value = "포트폴리오명", example = "깃허브")
    @NotBlank(message = "포트폴리오명는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 1, max = 10, message = "포트폴리오명은 1~10자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String portfolioName;

    @ApiModelProperty(position = 2, required = true, value = "포트폴리오 URL", example = "github.com/gabojait")
    @NotBlank(message = "포트폴리오 URL은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 1, max = 1000, message = "URL은 1~1000자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String portfolioUrl;

    @ApiModelProperty(position = 3, required = true, value = "미디어", example = "LINK")
    @NotBlank(message = "미디어는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Pattern(regexp = "^(LINK|FILE)", message = "미디어는 'LINK' 또는 'FILE'  중 하나여야 됩니다.",
            groups = ValidationSequence.Format.class)
    private String media;

    public Portfolio toEntity(User user) {
        return Portfolio.builder()
                .user(user)
                .portfolioName(this.portfolioName.trim())
                .portfolioUrl(this.portfolioUrl.trim())
                .media(Media.valueOf(this.media))
                .build();
    }
}
