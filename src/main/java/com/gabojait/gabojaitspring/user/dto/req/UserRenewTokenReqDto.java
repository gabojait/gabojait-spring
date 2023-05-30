package com.gabojait.gabojaitspring.user.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@ApiModel(value = "회원 토큰 재발급 요청")
public class UserRenewTokenReqDto {

    @ApiModelProperty(position = 1, required = true, value = "FCM 토큰")
    private String fcmToken;
}
