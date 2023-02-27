package com.inuappcenter.gabojaitspring.profile.dto.res;

import com.inuappcenter.gabojaitspring.profile.domain.type.Position;
import com.inuappcenter.gabojaitspring.team.domain.Team;
import com.inuappcenter.gabojaitspring.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(value = "User 팀 요약 응답")
public class UserTeamAbstractResDto {

    @ApiModelProperty(position = 1, required = true, value = "팀 식별자")
    private String teamId;

    @ApiModelProperty(position = 2, required = true, value = "프로젝트 이름")
    private String projectName;

    @ApiModelProperty(position = 3, required = true, value = "포지션")
    private String position;

    public UserTeamAbstractResDto(Team team, User user) {

        this.teamId = team.getId().toString();
        this.projectName = team.getProjectName();

        if (team.getDesigners().contains(user))
            this.position = Position.DESIGNER.name();
        else if (team.getBackends().contains(user))
            this.position = Position.BACKEND.name();
        else if (team.getFrontends().contains(user))
            this.position = Position.FRONTEND.name();
        else
            this.position = Position.PM.name();
    }
}
