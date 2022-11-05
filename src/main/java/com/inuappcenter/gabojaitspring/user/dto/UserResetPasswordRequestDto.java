package com.inuappcenter.gabojaitspring.user.dto;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.*;

@Getter
@NoArgsConstructor
@GroupSequence({UserResetPasswordRequestDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.Size.class,
})
@ApiModel(value = "User 비밀번호 재설정 요청")
public class UserResetPasswordRequestDto {

    @ApiModelProperty(position = 1, required = true, dataType = "String", value = "유저 식별자")
    @NotBlank(message = "모든 필수 정보를 입력해 주세요")
    private String userId;

    @ApiModelProperty(position = 2, required = true, dataType = "String", value = "현재 비밀번호", example = "password")
    @NotBlank(message = "모든 필수 정보를 입력해 주세요", groups = ValidationSequence.NotBlank.class)
    private String currentPassword;

    @ApiModelProperty(position = 3, required = true, dataType = "String", value = "새 비밀번호", example = "password")
    @NotBlank(message = "모든 필수 정보를 입력해 주세요", groups = ValidationSequence.NotBlank.class)
    @Size(min = 8, max = 30, message = "비밀번호는 8~30자만 가능합니다", groups = ValidationSequence.Size.class)
    private String newPassword;

    @ApiModelProperty(position = 4, required = true, dataType = "String", value = "새 비밀번호 재입력", example = "password")
    @NotBlank(message = "모든 필수 정보를 입력해 주세요", groups = ValidationSequence.NotBlank.class)
    @Size(min = 8, max = 30, message = "비밀번호는 8~30자만 가능합니다", groups = ValidationSequence.Size.class)
    private String newPasswordReEntered;
}


