package com.gabojait.gabojaitspring.user.dto.req;

import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;

@Getter
@ToString
@NoArgsConstructor
@GroupSequence({UserVerifyReqDto.class, ValidationSequence.Blank.class})
@ApiModel(value = "회원 비밀번호 검증 요청")
public class UserVerifyReqDto {

    @ApiModelProperty(position = 1, required = true, value = "비밀번호", example = "password1!")
    @NotBlank(message = "비밀번호는 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    private String password;
}
