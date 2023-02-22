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
@GroupSequence({UserForgotUsernameReqDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.Email.class})
@ApiModel(value = "User 아이디 찾기 요청")
public class UserForgotUsernameReqDto {

    @ApiModelProperty(position = 1, required = true, value = "이메일", example = "email@domain.com")
    @NotBlank(message = "이메일을 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Email(message = "올바른 이메일 형식이 아닙니다.", groups = ValidationSequence.Email.class)
    private String email;
}