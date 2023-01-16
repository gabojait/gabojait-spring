package com.inuappcenter.gabojaitspring.project.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@ApiModel(value = "Project 종료 요청")
public class ProjectEndRequestDto {

    @ApiModelProperty(position = 1, required = true, value = "프로젝트 식별자")
    @NotBlank(message = "프로젝트 식별자를 입력해주세요.")
    private String projectId;
}
