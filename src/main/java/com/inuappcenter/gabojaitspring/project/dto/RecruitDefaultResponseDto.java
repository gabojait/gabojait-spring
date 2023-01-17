package com.inuappcenter.gabojaitspring.project.dto;

import com.inuappcenter.gabojaitspring.project.domain.Recruit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(value = "Recruit 응답")
public class RecruitDefaultResponseDto {

    @ApiModelProperty(position = 1, required = true, value = "영입 식별자")
    private String recruitId;

    @ApiModelProperty(position = 2, required = true, value = "프로젝트 식별자")
    private String projectId;

    @ApiModelProperty(position = 3, required = true, value = "영입 대상자 프로필 식별자")
    private String userProfileId;

    @ApiModelProperty(position = 4, required = true, value = "승인 여부")
    private Boolean isAccepted;

    @ApiModelProperty(position = 5, required = true, value = "포지션")
    private Character position;

    public RecruitDefaultResponseDto(Recruit recruit) {
        this.recruitId = recruit.getId().toString();
        this.projectId = recruit.getProjectId().toString();
        this.userProfileId = recruit.getUserProfileId().toString();
        this.isAccepted = recruit.getIsAccepted();
        this.position = recruit.getPosition();
    }
}
