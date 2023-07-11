package com.gabojait.gabojaitspring.admin.dto.req;

import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@GroupSequence({AdminRegisterDecideReqDto.class, ValidationSequence.Blank.class})
@ApiModel(value = "관리자 가입 승인 요청")
public class AdminRegisterDecideReqDto {

    @ApiModelProperty(position = 1, required = true, value = "승인 여부", example = "true")
    @NotNull(message = "승인 여부는 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    private Boolean isApproved;
}
