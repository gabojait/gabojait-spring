package com.inuappcenter.gabojaitspring.user.dto;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@GroupSequence({UserUpdatePasswordRequestDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Pattern.class
})
@ApiModel(value = "User 비밀번호 업데이트 요청")
public class UserUpdatePasswordRequestDto {

    @ApiModelProperty(position = 1, required = true, dataType = "String", value = "현재 비밀번호", example = "password")
    @NotBlank(message = "모든 필수 정보를 입력해주세요", groups = ValidationSequence.NotBlank.class)
    private String currentPassword;

    @ApiModelProperty(position = 2, required = true, dataType = "String", value = "새 비밀번호", example = "newPassword")
    @NotBlank(message = "모든 필수 정보를 입력해주세요", groups = ValidationSequence.NotBlank.class)
    @Size(min = 8, max = 30, message = "비밀번호는 8~30자만 가능합니다", groups = ValidationSequence.Size.class)
    @Pattern(regexp = "^[a-zA-Z0-9]+$",
            message = "비밀번호는 영문과 숫자의 조합만 가능합니다.",
            groups = ValidationSequence.Pattern.class)
    private String newPassword;

    @ApiModelProperty(position = 3, required = true, dataType = "String", value = "새 비밀번호 재입력", example = "newPassword")
    @NotBlank(message = "모든 필수 정보를 입력해주세요", groups = ValidationSequence.NotBlank.class)
    private String newPasswordReEntered;
}
