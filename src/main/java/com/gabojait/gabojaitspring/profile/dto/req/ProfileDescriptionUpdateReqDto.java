package com.gabojait.gabojaitspring.profile.dto.req;

import com.gabojait.gabojaitspring.exception.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@GroupSequence({ProfileDescriptionUpdateReqDto.class,
        ValidationSequence.Size.class,})
@ApiModel(value = "프로필 자기소개 수정 요청")
public class ProfileDescriptionUpdateReqDto {

    @ApiModelProperty(position = 1, required = true, value = "자기소개", example = "김가보자잇 자기소개 입니다.")
    @Size(max = 120, message = "자기소개는 0~120자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String profileDescription;
}
