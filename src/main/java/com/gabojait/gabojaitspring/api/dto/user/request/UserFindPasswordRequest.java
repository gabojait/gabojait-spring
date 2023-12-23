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
@ApiModel(value = "회원 비밀번호 찾기 요청")
public class UserFindPasswordRequest {

    @ApiModelProperty(position = 1, required = true, value = "이메일", example = "email@domail.com")
    @NotBlank(message = "이메일은 필수 입력입니다.")
    @Email(message = "올바른 이메일 형식을 입력해 주세요.")
    private String email;

    @ApiModelProperty(position = 2, required = true, value = "아이디", example = "tester")
    @NotBlank(message = "아이디는 필수 입력입니다.")
    private String username;

    @Builder
    private UserFindPasswordRequest(String email, String username) {
        this.email = email;
        this.username = username;
    }
}
