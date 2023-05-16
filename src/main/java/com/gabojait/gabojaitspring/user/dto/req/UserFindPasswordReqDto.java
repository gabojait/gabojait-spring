package com.gabojait.gabojaitspring.user.dto.req;

import com.gabojait.gabojaitspring.exception.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@ApiModel(value = "회원 비밀번호 찾기 요청")
@NoArgsConstructor
@GroupSequence({UserFindPasswordReqDto.class, ValidationSequence.Blank.class, ValidationSequence.Format.class})
public class UserFindPasswordReqDto {

    @ApiModelProperty(position = 1, required = true, value = "이메일", example = "email@domail.com")
    @NotBlank(message = "이메일을 입력해 주세요.", groups = ValidationSequence.Blank.class)
    @Email(message = "올바른 이메일 형식을 입력해주세요.", groups = ValidationSequence.Format.class)
    private String email;

    @ApiModelProperty(position = 2, required = true, value = "아이디", example = "username")
    @NotBlank(message = "아이디를 입력해 주세요.", groups = ValidationSequence.Blank.class)
    private String username;
}
