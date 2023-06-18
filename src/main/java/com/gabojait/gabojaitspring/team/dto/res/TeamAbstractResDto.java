package com.gabojait.gabojaitspring.team.dto.res;

import com.gabojait.gabojaitspring.team.domain.Team;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@ApiModel(value = "팀 요약 응답")
public class TeamAbstractResDto {

    @ApiModelProperty(position = 1, required = true, value = "팀 식별자")
    private Long teamId;

    @ApiModelProperty(position = 2, required = true, value = "프로젝트 이름")
    private String projectName;

    @ApiModelProperty(position = 3, required = true, value = "디자이너 총 팀원 수")
    private Byte designerTotalRecruitCnt;

    @ApiModelProperty(position = 4, required = true, value = "백엔드 개발자 총 팀원 수")
    private Byte backendTotalRecruitCnt;

    @ApiModelProperty(position = 5, required = true, value = "프론트엔드 개발자 총 팀원 수")
    private Byte frontendTotalRecruitCnt;

    @ApiModelProperty(position = 6, required = true, value = "매니저 총 팀원 수")
    private Byte managerTotalRecruitCnt;

    @ApiModelProperty(position = 7, required = true, value = "생성일")
    private LocalDateTime createdAt;

    @ApiModelProperty(position = 8, required = true, value = "수정일")
    private LocalDateTime updatedAt;

    public TeamAbstractResDto(Team team) {
        this.teamId = team.getId();
        this.projectName = team.getProjectName();
        this.designerTotalRecruitCnt = team.getDesignerTotalRecruitCnt();
        this.backendTotalRecruitCnt = team.getBackendTotalRecruitCnt();
        this.frontendTotalRecruitCnt = team.getFrontendTotalRecruitCnt();
        this.managerTotalRecruitCnt = team.getManagerTotalRecruitCnt();
        this.createdAt = team.getCreatedAt();
        this.updatedAt = team.getUpdatedAt();
    }
}
