package com.inuappcenter.gabojaitspring.profile.dto;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@GroupSequence({EducationUpdateRequestDto.class, ValidationSequence.NotBlank.class})
@ApiModel(value = "Profile 삭제 요청")
public class EducationDeleteRequestDto {

    @ApiModelProperty(position = 1, required = true, dataType = "String", value = "프로필 식별자", example = "profileId")
    @NotBlank(message = "모든 필수 정보를 입력해 주세요", groups = ValidationSequence.NotBlank.class)
    private String profileId;

    @ApiModelProperty(position = 1, required = true, dataType = "String", value = "학력 식별자", example = "educationId")
    @NotBlank(message = "모든 필수 정보를 입력해 주세요", groups = ValidationSequence.NotBlank.class)
    private String educationId;
}
