package com.inuappcenter.gabojaitspring.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@ApiModel(value = "User 정보 불러오기 요청")
public class UserFindOneUserRequestDto {

    @ApiModelProperty(position = 1, required = true, dataType = "String", value = "아이디", example = "username")
    private String username;
}
