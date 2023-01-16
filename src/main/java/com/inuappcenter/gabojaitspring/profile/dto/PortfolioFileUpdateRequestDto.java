package com.inuappcenter.gabojaitspring.profile.dto;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@GroupSequence({PortfolioFileUpdateRequestDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.Size.class})
@Schema(title = "Portfolio 파일 수정 요청")
public class PortfolioFileUpdateRequestDto {

    @ApiModelProperty(position = 1, required = true)
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    private String portfolioId;

    @ApiModelProperty(position = 2, required = true)
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 1, max = 10, message = "이름은 1~10자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String name;

    @ApiModelProperty(position = 3, required = true)
    private MultipartFile file;
}
