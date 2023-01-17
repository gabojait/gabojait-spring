package com.inuappcenter.gabojaitspring.project.dto;

import com.inuappcenter.gabojaitspring.project.domain.Apply;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(value = "Apply 응답")
public class ApplyDefaultResponseDto {

    @ApiModelProperty(position = 1, required = true, value = "지원 식별자")
    private String applyId;

    @ApiModelProperty(position = 2, required = true, value = "지원자 프로필 식별자")
    private String userProfileId;

    @ApiModelProperty(position = 3, required = true, value = "프로젝트 식별자")
    private String projectId;

    @ApiModelProperty(position = 4, required = true, value = "승인 여부")
    private Boolean isAccepted;

    @ApiModelProperty(position = 5, required = true, value = "포지션")
    private Character position;

    public ApplyDefaultResponseDto(Apply apply) {
        this.applyId = apply.getId().toString();
        this.userProfileId = apply.getUserProfileId().toString();
        this.projectId = apply.getProjectId().toString();
        this.isAccepted = apply.getIsAccepted();
        this.position = apply.getPosition();
    }
}
