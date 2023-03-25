package com.inuappcenter.gabojaitspring.profile.dto.req;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@GroupSequence({PortfolioFileUpdateReqDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.NotNull.class,
        ValidationSequence.Size.class})
@Schema(title = "Portfolio 파일 수정 요청")
public class PortfolioFileUpdateReqDto {

    @ApiModelProperty(position = 1, required = true, value = "포트폴리오 식별자", example = "1")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    private String portfolioId;

    @ApiModelProperty(position = 1, required = true, value = "이름", example = "이력서")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 1, max = 10, message = "포트폴리오명은 1~10자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String portfolioName;

    @ApiModelProperty(position = 2, required = true, value = "파일")
    @NotNull(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    private MultipartFile file;
}
