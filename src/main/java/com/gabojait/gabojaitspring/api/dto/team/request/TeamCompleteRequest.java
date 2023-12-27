package com.gabojait.gabojaitspring.api.dto.team.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(value = "프로젝트 완료 요청")
public class TeamCompleteRequest {

    @ApiModelProperty(position = 1, required = true, value = "완료한 프로젝트 URL", example = "github.com/gabojait")
    @NotBlank(message = "완료된 프로젝트 URL은 필수 입력입니다.")
    private String projectUrl;

    @Builder
    private TeamCompleteRequest(String projectUrl) {
        this.projectUrl = projectUrl;
    }
}
