package com.gabojait.gabojaitspring.api.dto.user.request;

import com.gabojait.gabojaitspring.api.dto.common.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@GroupSequence({UserUpdatePasswordRequest.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class})
@ApiModel(value = "회원 비밀번호 업데이트 요청")
public class UserUpdatePasswordRequest {

    @ApiModelProperty(position = 1, required = true, value = "비밀번호", example = "password2!")
    @NotBlank(message = "비밀번호는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 8, max = 30, message = "비밀번호는 8~30자만 가능합니다.", groups = ValidationSequence.Size.class)
    @Pattern(regexp = "^(?=.*[A-z])(?=.*\\d)(?=.*[#$@!%&*?])[A-z\\d#$@!%&*?]+$",
            message = "비밀번호는 영어, 숫자, 특수문자(#$@!%&*?)의 조합으로 입력해 주세요.", groups = ValidationSequence.Format.class)
    private String password;

    @ApiModelProperty(position = 2, required = true, value = "비밀번호 재입력", example = "password2!")
    @NotBlank(message = "비밀번호 재입력은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    private String passwordReEntered;

    @Builder
    private UserUpdatePasswordRequest(String password, String passwordReEntered) {
        this.password = password;
        this.passwordReEntered = passwordReEntered;
    }
}