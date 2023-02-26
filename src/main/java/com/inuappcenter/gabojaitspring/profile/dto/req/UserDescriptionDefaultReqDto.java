package com.inuappcenter.gabojaitspring.profile.dto.req;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@GroupSequence({UserDescriptionDefaultReqDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.Size.class})
@ApiModel(value = "User 프로필 자기소개 기본 요청")
public class UserDescriptionDefaultReqDto {

    @ApiModelProperty(position = 1, required = true, value = "자기소개", example = "자기소개 예시입니다.")
    @Size(max = 120, message = "자기소개는 0~120자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String description;
}
