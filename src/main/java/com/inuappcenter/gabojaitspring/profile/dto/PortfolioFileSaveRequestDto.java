package com.inuappcenter.gabojaitspring.profile.dto;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import com.inuappcenter.gabojaitspring.profile.domain.Portfolio;
import com.inuappcenter.gabojaitspring.profile.domain.PortfolioType;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@GroupSequence({PortfolioFileSaveRequestDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.Size.class})
@Schema(title = "Portfolio 파일 생성 요청")
public class PortfolioFileSaveRequestDto {

    @ApiModelProperty(position = 1, required = true)
    @NotBlank(message = "포트폴리오 이름을 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 1, max = 10, message = "이름은 1~10자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String name;

    @ApiModelProperty(position = 2, required = true)
    private MultipartFile file;

    public Portfolio toEntity(ObjectId profileId, PortfolioType portfolioType, String url) {
        return Portfolio.builder()
                .portfolioType(portfolioType)
                .name(this.name)
                .url(url)
                .profileId(profileId)
                .build();
    }
}
