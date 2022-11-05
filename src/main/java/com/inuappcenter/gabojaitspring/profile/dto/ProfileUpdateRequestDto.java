package com.inuappcenter.gabojaitspring.profile.dto;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@GroupSequence({ProfileUpdateRequestDto.class, ValidationSequence.NotBlank.class, ValidationSequence.Size.class})
@ApiModel(value = "Profile 수정 요청")
public class ProfileUpdateRequestDto {

    @ApiModelProperty(position = 1, required = true, dataType = "String", value = "프로필 식별자", example = "profileId")
    @NotBlank(message = "모든 필수 정보를 입력해 주세요", groups = ValidationSequence.NotBlank.class)
    private String profileId;

    @ApiModelProperty(position = 2, dataType = "String", value = "소개글", example = "about")
    @Size(max = 100, message = "소개글은 100자 이하만 가능합니다", groups = ValidationSequence.Size.class)
    private String about;

    @ApiModelProperty(position = 3,
            dataType = "Character",
            allowableValues = "D, B, F, P",
            value = "포지션: D, B, F, P",
            example = "B")
    private Character position;
}

