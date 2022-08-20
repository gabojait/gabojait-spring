package com.inuappcenter.gabojaitspring.user.dto;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import com.inuappcenter.gabojaitspring.user.domain.Contact;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@GroupSequence({ContactSaveRequestDto.class, ValidationSequence.NotBlank.class, ValidationSequence.Email.class})
@ApiModel(value = "Contact 생성 요청")
public class ContactSaveRequestDto {

    @ApiModelProperty(position = 1, required = true, dataType = "String", value = "이메일", example = "email@domain.com")
    @NotBlank(message = "이메일을 입력해 주세요", groups = ValidationSequence.NotBlank.class)
    @Email(message = "올바른 이메일 형식이 아닙니다", groups = ValidationSequence.Email.class)
    private String email;

    public Contact toEntity(String verificationCode) {
        return Contact.ByContactBuilder()
                .email(email)
                .verificationCode(verificationCode)
                .build();
    }
}