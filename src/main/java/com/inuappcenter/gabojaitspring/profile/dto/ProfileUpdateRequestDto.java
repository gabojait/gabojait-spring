package com.inuappcenter.gabojaitspring.profile.dto;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@ApiModel(value = "Profile 수정 요청")
public class ProfileUpdateRequestDto {

    @ApiModelProperty(position = 1, required = true, dataType = "String", value = "자기소개", example = "저는 가보잇팀입니다.")
    @Size(max = 300, message = "자기소개는 0~300자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String description;

    @ApiModelProperty(position = 2,
            required = true,
            dataType = "Character",
            allowableValues = "D, B, F, M",
            value = "포지션: D, B, F, M",
            example = "B")
    private Character position;
}
