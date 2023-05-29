package com.gabojait.gabojaitspring.user.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@ApiModel(value = "회원 로그아웃 요청")
@NoArgsConstructor
public class UserLogoutReqDto {

    @ApiModelProperty(position = 1, required = true, value = "FCM 토큰")
    private String fcmToken;
}
