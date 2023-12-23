package com.gabojait.gabojaitspring.api.dto.user.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(value = "회원 알림 여부 업데이트 요청")
public class UserIsNotifiedUpdateRequest {

    @ApiModelProperty(position = 1, required = true, value = "알림 여부", example = "true",
            allowableValues = "true, false")
    @NotNull(message = "알림 여부는 필수 입력입니다.")
    private Boolean isNotified;

    @Builder
    private UserIsNotifiedUpdateRequest(Boolean isNotified) {
        this.isNotified = isNotified;
    }
}
