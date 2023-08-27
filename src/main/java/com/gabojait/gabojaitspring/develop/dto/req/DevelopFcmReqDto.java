package com.gabojait.gabojaitspring.develop.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(value = "개발 FCM 요청")
public class DevelopFcmReqDto {

    @ApiModelProperty(position = 1, required = true, value = "FCM 제목", example = "알림 테스트")
    @NotBlank(message = "FCM 제목은 필수 입력입니다.")
    private String fcmTitle;

    @ApiModelProperty(position = 2, required = true, value = "FCM 메세지", example = "알림 테스트입니다.")
    @NotBlank(message = "FCM 메세지는 필수 입력입니다.")
    private String fcmMessage;

    @Builder
    private DevelopFcmReqDto(String fcmTitle, String fcmMessage) {
        this.fcmTitle = fcmTitle;
        this.fcmMessage = fcmMessage;
    }
}
