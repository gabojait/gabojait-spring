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
@GroupSequence({ContactVerificationReqDto.class, ValidationSequence.NotBlank.class, ValidationSequence.Email.class})
@ApiModel(value = "Contact 인증번호 확인 요청")
public class ContactVerificationReqDto {

    @ApiModelProperty(position = 1, required = true, value = "이메일", example = "email@domain.com")
    @NotBlank(message = "이메일 또는 인증번호를 입력해 주세요.", groups = ValidationSequence.NotBlank.class)
    @Email(message = "올바른 이메일 형식이 아닙니다.", groups = ValidationSequence.Email.class)
    private String email;

    @ApiModelProperty(position = 2, required = true, value = "인증번호")
    @NotBlank(message = "이메일 또는 인증번호를 입력해 주세요.", groups = ValidationSequence.NotBlank.class)
    private String verificationCode;
}