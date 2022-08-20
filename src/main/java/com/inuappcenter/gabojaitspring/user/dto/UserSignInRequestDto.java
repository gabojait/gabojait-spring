package com.inuappcenter.gabojaitspring.user.dto;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@GroupSequence({UserSignInRequestDto.class})
@ApiModel(value = "User 로그인 요청")
public class UserSignInRequestDto {

    @ApiModelProperty(position = 1, required = true, dataType = "String", value = "아이디", example = "username")
    @NotBlank(message = "아이디 또는 비밀번호를 입력해 주세요")
    private String username;

    @ApiModelProperty(position = 2, required = true, dataType = "String", value = "비밀번호", example = "password")
    @NotBlank(message = "아이디 또는 비밀번호를 입력해 주세요")
    private String password;
}
