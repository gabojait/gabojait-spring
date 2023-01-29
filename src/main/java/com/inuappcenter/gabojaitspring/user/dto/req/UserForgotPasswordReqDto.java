package com.inuappcenter.gabojaitspring.user.dto.req;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@GroupSequence({UserForgotPasswordReqDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.Email.class})
@ApiModel(value = "User 비밀번호 찾기 요청")
public class UserForgotPasswordReqDto {

    @ApiModelProperty(position = 1, required = true, value = "아이디", example = "username")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    private String username;

    @ApiModelProperty(position = 2, required = true, value = "이메일", example = "email@domain.com")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Email(message = "올바른 이메일 형식이 아닙니다.", groups = ValidationSequence.Email.class)
    private String email;
}
