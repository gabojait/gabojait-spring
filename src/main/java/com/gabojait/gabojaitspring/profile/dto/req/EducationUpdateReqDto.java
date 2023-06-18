package com.gabojait.gabojaitspring.profile.dto.req;

import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@ToString
@NoArgsConstructor
@GroupSequence({EducationUpdateReqDto.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class})
@ApiModel(value = "경력 업데이트 요청")
public class EducationUpdateReqDto extends EducationCreateReqDto {

    @ApiModelProperty(position = 4, required = true, value = "학력 식별자")
    @NotNull(message = "학력 식별자는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Positive(message = "학력 식별자는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
    private Long educationId;
}
