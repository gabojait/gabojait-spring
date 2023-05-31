package com.gabojait.gabojaitspring.team.dto.res;

import com.gabojait.gabojaitspring.profile.dto.res.ProfileAbstractResDto;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@ApiModel(value = "팀 요약 응답")
public class TeamAbstractResDto {

    @ApiModelProperty(position = 1, required = true, value = "팀 식별자")
    private String teamId;

    @ApiModelProperty(position = 2, required = true, value = "프로젝트 이름")
    private String projectName;

    @ApiModelProperty(position = 3, required = true, value = "현재 디자이너 팀원 수")
    private Integer designerCurrentCnt;

    @ApiModelProperty(position = 4, required = true, value = "현재 백엔드 개발자 팀원 수")
    private Integer backendCurrentCnt;

    @ApiModelProperty(position = 5, required = true, value = "현재 프론트엔드 개발자 팀원 수")
    private Integer frontendCurrentCnt;

    @ApiModelProperty(position = 6, required = true, value = "현재 매니저 팀원 수")
    private Integer managerCurrentCnt;

    @ApiModelProperty(position = 7, required = true, value = "디자이너 총 팀원 수")
    private Short designerTotalRecruitCnt;

    @ApiModelProperty(position = 8, required = true, value = "백엔드 개발자 총 팀원 수")
    private Short backendTotalRecruitCnt;

    @ApiModelProperty(position = 9, required = true, value = "프론트엔드 개발자 총 팀원 수")
    private Short frontendTotalRecruitCnt;

    @ApiModelProperty(position = 10, required = true, value = "매니저 총 팀원 수")
    private Short managerTotalRecruitCnt;

    @ApiModelProperty(position = 11, required = true, value = "스키마 버전")
    private String schemaVersion;

    public TeamAbstractResDto(Team team) {
        this.teamId = team.getId().toString();
        this.projectName = team.getProjectName();
        this.designerCurrentCnt = team.getDesignerUserIds().size();
        this.backendCurrentCnt = team.getBackendUserIds().size();
        this.frontendCurrentCnt = team.getFrontendUserIds().size();
        this.managerCurrentCnt = team.getManagerUserIds().size();
        this.designerTotalRecruitCnt = team.getDesignerTotalRecruitCnt();
        this.backendTotalRecruitCnt = team.getBackendTotalRecruitCnt();
        this.frontendTotalRecruitCnt = team.getFrontendTotalRecruitCnt();
        this.managerTotalRecruitCnt = team.getManagerTotalRecruitCnt();
        this.schemaVersion = team.getSchemaVersion();
    }
}
