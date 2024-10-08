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
@ApiModel(value = "회원 아이디 찾기 요청")
public class UserFindUsernameRequest {

    @ApiModelProperty(position = 1, required = true, value = "이메일", example = "email@domail.com")
    @NotBlank(message = "이메일은 필수 입력입니다.")
    @Email(message = "올바른 이메일 형식을 입력해 주세요.")
    private String email;

    @Builder
    private UserFindUsernameRequest(String email) {
        this.email = email;
    }
}
