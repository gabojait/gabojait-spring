package com.gabojait.gabojaitspring.user.dto.req;

import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@GroupSequence({AdminRegisterDecideReqDto.class, ValidationSequence.Blank.class})
@ApiModel(value = "관리자 가입 승인 요청")
public class AdminRegisterDecideReqDto {

    @ApiModelProperty(position = 1, required = true, value = "승인 여부", example = "true", allowableValues = "true, false")
    @NotNull(message = "승인 여부는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    private Boolean isApproved;

    @Builder
    private AdminRegisterDecideReqDto(Boolean isApproved) {
        this.isApproved = isApproved;
    }
}