package com.gabojait.gabojaitspring.develop.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@ToString
@NoArgsConstructor
@ApiModel(value = "개발 알림 요청")
public class DevelopNotificationReqDto {

    @ApiModelProperty(position = 1, required = true, value = "알림 제목", example = "알림 테스트")
    @NotBlank(message = "알림 제목은 필수 입력란입니다.")
    private String notificationTitle;

    @ApiModelProperty(position = 2, required = true, value = "알림 메세지", example = "알림 테스트입니다.")
    @NotBlank(message = "알림 메세지는 필수 입력란입니다.")
    private String notificationMessage;
}
