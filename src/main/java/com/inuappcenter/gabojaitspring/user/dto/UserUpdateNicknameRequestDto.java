package com.inuappcenter.gabojaitspring.user.dto;

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
@GroupSequence({UserUpdateNicknameRequestDto.class, ValidationSequence.NotBlank.class, ValidationSequence.Size.class})
@ApiModel(value = "User 닉네임 업데이트 요청")
public class UserUpdateNicknameRequestDto {

    @ApiModelProperty(position = 1, required = true, dataType = "String", value = "식별자")
    @NotBlank(message = "모든 필수 정보를 입력해 주세요")
    private String id;

    @ApiModelProperty(position = 4, required = true, dataType = "String", value = "닉네임", example = "닉네임")
    @NotBlank(message = "모든 필수 정보를 입력해 주세요", groups = ValidationSequence.NotBlank.class)
    @Size(min = 2, max = 8, message = "닉네임은 2~8자만 가능합니다", groups = ValidationSequence.Size.class)
    private String nickname;
}
