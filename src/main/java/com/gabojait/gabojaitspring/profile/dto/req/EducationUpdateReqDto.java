package com.gabojait.gabojaitspring.profile.dto.req;

import com.gabojait.gabojaitspring.exception.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@GroupSequence({EducationUpdateReqDto.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class})
@ApiModel(value = "경력 업데이트 요청")
public class EducationUpdateReqDto extends EducationCreateReqDto {

    @ApiModelProperty(position = 4, required = true, value = "학력 식별자")
    @NotBlank(message = "학력 식별자는 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    private String educationId;
}
