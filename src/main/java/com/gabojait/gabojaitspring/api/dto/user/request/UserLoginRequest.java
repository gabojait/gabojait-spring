package com.gabojait.gabojaitspring.api.dto.user.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(value = "회원 로그인 요청")
public class UserLoginRequest {

    @ApiModelProperty(position = 1, required = true, value = "아이디", example = "tester")
    @NotBlank(message = "아이디는 필수 입력입니다.")
    private String username;

    @ApiModelProperty(position = 2, required = true, value = "비밀번호", example = "password1!")
    @NotBlank(message = "비밀번호는 필수 입력입니다.")
    private String password;

    @ApiModelProperty(position = 3, value = "FCM 토큰")
    private String fcmToken;

    @Builder
    private UserLoginRequest(String username, String password, String fcmToken) {
        this.username = username;
        this.password = password;
        this.fcmToken = fcmToken;
    }
}
