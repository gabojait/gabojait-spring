package com.gabojait.gabojaitspring.user.dto.req;

import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@GroupSequence({ContactVerifyReqDto.class, ValidationSequence.Blank.class, ValidationSequence.Format.class})
@ApiModel(value = "연락처 인증코드 확인 요청")
public class ContactVerifyReqDto {

    @ApiModelProperty(position = 1, required = true, value = "이메일", example = "email@domain.com")
    @NotBlank(message = "이메일은 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    @Email(message = "올바른 이메일 형식을 입력해 주세요.", groups = ValidationSequence.Format.class)
    private String email;

    @ApiModelProperty(position = 2, required = true, value = "인증번호")
    @NotBlank(message = "인증코드는 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    private String verificationCode;
}
