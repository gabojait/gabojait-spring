package com.gabojait.gabojaitspring.user.dto.req;

import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;

@Getter
@ToString
@NoArgsConstructor
@GroupSequence({UserIsNotifiedUpdateReqDto.class, ValidationSequence.Blank.class})
@ApiModel(value = "회원 알림 여부 업데이트 요청")
public class UserIsNotifiedUpdateReqDto {

    @ApiModelProperty(position = 1, required = true, value = "알림 여부", example = "true",
            allowableValues = "true, false")
    @NotNull(message = "알림 여부는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    private Boolean isNotified;
}
