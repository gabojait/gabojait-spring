package com.inuappcenter.gabojaitspring.profile.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@ApiModel(value = "Profile 다건 조회 요청")
public class ProfileManyRequestDto {

    @ApiModelProperty(position = 1, required = true, value = "프로필 식별자")
    @NotEmpty(message = "하나 이상의 프로필 식별자를 입력해주세요.")
    private List<String> profileIds = new ArrayList<>();
}
