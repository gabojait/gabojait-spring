package com.gabojait.gabojaitspring.user.dto.req;

import com.gabojait.gabojaitspring.exception.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@GroupSequence({UserVerifyReqDto.class, ValidationSequence.Blank.class})
@ApiModel(value = "회원 비밀번호 검증 요청")
public class UserVerifyReqDto {

    @ApiModelProperty(position = 1, required = true, value = "비밀번호", example = "password1!")
    @NotBlank(message = "비밀번호를 입력해 주세요.", groups = ValidationSequence.Blank.class)
    private String password;
}
