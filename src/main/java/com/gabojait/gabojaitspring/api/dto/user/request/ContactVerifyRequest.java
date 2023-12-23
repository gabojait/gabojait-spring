package com.gabojait.gabojaitspring.api.dto.user.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(value = "연락처 인증코드 확인 요청")
public class ContactVerifyRequest {

    @ApiModelProperty(position = 1, required = true, value = "이메일", example = "email@domain.com")
    @NotBlank(message = "이메일은 필수 입력입니다.")
    @Email(message = "올바른 이메일 형식을 입력해 주세요.")
    private String email;

    @ApiModelProperty(position = 2, required = true, value = "인증코드")
    @NotBlank(message = "인증코드는 필수 입력입니다.")
    private String verificationCode;

    @Builder
    private ContactVerifyRequest(String email, String verificationCode) {
        this.email = email;
        this.verificationCode = verificationCode;
    }
}
