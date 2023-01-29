package com.inuappcenter.gabojaitspring.user.dto.req;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@GroupSequence({UserUpdatePasswordReqDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.NotNull.class,
        ValidationSequence.Size.class,
        ValidationSequence.Pattern.class
})
@ApiModel(value = "User 비밀번호 업데이트 요청")
public class UserUpdatePasswordReqDto {

    @ApiModelProperty(position = 1, required = true, value = "새 비밀번호", example = "password2!")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 8, max = 30, message = "비밀번호는 8~30자만 가능합니다.", groups = ValidationSequence.Size.class)
    @Pattern(regexp = "^(?=.*[A-z])(?=.*\\d)(?=.*[#$@!%&*?])[A-z\\d#$@!%&*?]+$",
            message = "비밀번호는 영문, 숫자, 특수문자(#$@!%&*?)의 조합의 형식만 가능합니다.", groups = ValidationSequence.Pattern.class)
    private String newPassword;

    @ApiModelProperty(position = 2, required = true, value = "새 비밀번호 재입력", example = "password2!")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    private String newPasswordReEntered;

    @ApiModelProperty(position = 3, required = true, value = "임시 비밀번호 여부", example = "false")
    @NotNull(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    private Boolean isTemporaryPassword;
}
