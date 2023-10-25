package com.gabojait.gabojaitspring.api.dto.user.request;

import com.gabojait.gabojaitspring.api.dto.common.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.GroupSequence;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@GroupSequence({ContactVerifyRequest.class, ValidationSequence.Blank.class, ValidationSequence.Format.class})
@ApiModel(value = "연락처 인증코드 확인 요청")
public class ContactVerifyRequest {

    @ApiModelProperty(position = 1, required = true, value = "이메일", example = "email@domain.com")
    @NotBlank(message = "이메일은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Email(message = "올바른 이메일 형식을 입력해 주세요.", groups = ValidationSequence.Format.class)
    private String email;

    @ApiModelProperty(position = 2, required = true, value = "인증코드")
    @NotBlank(message = "인증코드는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    private String verificationCode;

    @Builder
    private ContactVerifyRequest(String email, String verificationCode) {
        this.email = email;
        this.verificationCode = verificationCode;
    }
}
