package com.inuappcenter.gabojaitspring.profile.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@ApiModel(value = "Profile 프로젝트 찾기 모드 요청")
public class ProfileFindProjectModeRequestDto {

    @ApiModelProperty(position = 1, required = true, value = "프로젝트 찾기 모드", example = "true")
    @NotNull(message = "프로젝트 찾기 모드 여부를 입력해주세요.")
    private Boolean isLookingForProject;
}
