package com.inuappcenter.gabojaitspring.team.dto.res;

import com.inuappcenter.gabojaitspring.profile.dto.res.UserProfileAbstractResDto;
import com.inuappcenter.gabojaitspring.team.domain.Team;
import com.inuappcenter.gabojaitspring.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@ApiModel(value = "Team 기본 응답")
public class TeamDefaultResDto extends TeamAbstractResDto {

    @ApiModelProperty(position = 8, required = true, value = "팀 리더 식별자")
    private String leaderUserId;

    @ApiModelProperty(position = 9, required = true, value = "프로젝트 설명")
    private String projectDescription;

    @ApiModelProperty(position = 10, required = true, value = "원하는 디자이너 팀원 수")
    private Short designerTotalRecruitCnt;

    @ApiModelProperty(position = 11, required = true, value = "원하는 백엔드 개발자 팀원 수")
    private Short backendTotalRecruitCnt;

    @ApiModelProperty(position = 12, required = true, value = "원하는 프론트엔드 개발자 팀원 수")
    private Short frontendTotalRecruitCnt;

    @ApiModelProperty(position = 13, required = true, value = "원하는 프로젝트 매니저 팀원 수")
    private Short projectManagerTotalRecruitCnt;

    @ApiModelProperty(position = 14, required = true, value = "오픈채팅 링크")
    private String openChatUrl;

    @ApiModelProperty(position = 15, required = true, value = "바라는 점")
    private String expectation;

    public TeamDefaultResDto(Team team) {
        super(team);

        this.leaderUserId = team.getLeaderUserId().toString();
        this.projectDescription = team.getProjectDescription();
        this.designerTotalRecruitCnt = team.getDesignerTotalRecruitCnt();
        this.backendTotalRecruitCnt = team.getBackendTotalRecruitCnt();
        this.frontendTotalRecruitCnt = team.getFrontendTotalRecruitCnt();
        this.projectManagerTotalRecruitCnt = team.getProjectManagerTotalRecruitCnt();
        this.openChatUrl = team.getOpenChatUrl();
        this.expectation = team.getExpectation();
    }
}
