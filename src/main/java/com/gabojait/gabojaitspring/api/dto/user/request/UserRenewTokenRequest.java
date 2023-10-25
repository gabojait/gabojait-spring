package com.gabojait.gabojaitspring.api.dto.user.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(value = "회원 토큰 재발급 요청")
public class UserRenewTokenRequest {

    @ApiModelProperty(position = 1, value = "FCM 토큰")
    private String fcmToken;

    @Builder
    private UserRenewTokenRequest(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
