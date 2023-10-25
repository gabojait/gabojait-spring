package com.gabojait.gabojaitspring.api.dto.user.request;

import com.gabojait.gabojaitspring.api.dto.common.ValidationSequence;
import com.gabojait.gabojaitspring.domain.user.Contact;
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
@GroupSequence({ContactCreateRequest.class, ValidationSequence.Blank.class, ValidationSequence.Format.class})
@ApiModel(value = "연락처 생성 요청")
public class ContactCreateRequest {

    @ApiModelProperty(position = 1, required = true, value = "이메일", example = "email@domain.com")
    @NotBlank(message = "이메일은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Email(message = "올바른 이메일 형식을 입력해 주세요.", groups = ValidationSequence.Format.class)
    private String email;

    public Contact toEntity(String verificationCode) {
        return Contact.builder()
                .email(this.email)
                .verificationCode(verificationCode)
                .build();
    }

    @Builder
    private ContactCreateRequest(String email) {
        this.email = email;
    }
}