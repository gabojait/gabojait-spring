package com.inuappcenter.gabojaitspring.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@ApiModel(value = "User 탈퇴 요청")
public class UserDeactivateRequestDto {

    @ApiModelProperty(position = 1, required = true, dataType = "String", value = "식별자")
    @NotBlank(message = "모든 필수 정보를 입력해 주세요")
    private String id;

    @ApiModelProperty(position = 2, required = true, dataType = "String", value = "비밀번호", example = "password")
    @NotBlank(message = "모든 필수 정보를 입력해 주세요")
    private String password;
}
