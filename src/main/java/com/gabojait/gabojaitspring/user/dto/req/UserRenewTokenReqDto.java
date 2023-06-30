package com.gabojait.gabojaitspring.user.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@ApiModel(value = "회원 토큰 재발급 요청")
public class UserRenewTokenReqDto {

    @ApiModelProperty(position = 1, value = "FCM 토큰")
    private String fcmToken;
}
