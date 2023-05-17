package com.gabojait.gabojaitspring.user.dto.req;

import com.gabojait.gabojaitspring.exception.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;

@Getter
@ApiModel(value = "회원 로그인 요청")
@NoArgsConstructor
@GroupSequence({UserLoginReqDto.class, ValidationSequence.Blank.class})
public class UserLoginReqDto {

    @ApiModelProperty(position = 1, required = true, value = "아이디", example = "test1")
    @NotBlank(message = "아이디를 입력해 주세요.", groups = ValidationSequence.Blank.class)
    private String username;

    @ApiModelProperty(position = 2, required = true, value = "비밀번호", example = "password1!")
    @NotBlank(message = "비밀번호를 입력해 주세요.", groups = ValidationSequence.Blank.class)
    private String password;
}
