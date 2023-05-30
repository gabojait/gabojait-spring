package com.gabojait.gabojaitspring.profile.dto.req;

import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;

@Getter
@ToString
@NoArgsConstructor
@GroupSequence({WorkUpdateReqDto.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class})
@ApiModel(value = "경력 업데이트 요청")
public class WorkUpdateReqDto extends WorkCreateReqDto {

    @ApiModelProperty(position = 6, required = true, value = "경력 식별자")
    @NotBlank(message = "경력 식별자는 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    private String workId;
}
