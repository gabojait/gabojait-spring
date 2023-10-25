package com.gabojait.gabojaitspring.api.dto.user.request;

import com.gabojait.gabojaitspring.api.dto.common.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@GroupSequence({UserVerifyRequest.class, ValidationSequence.Blank.class})
@ApiModel(value = "회원 비밀번호 검증 요청")
public class UserVerifyRequest {

    @ApiModelProperty(position = 1, required = true, value = "비밀번호", example = "password1!")
    @NotBlank(message = "비밀번호는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    private String password;

    @Builder
    private UserVerifyRequest(String password) {
        this.password = password;
    }
}
