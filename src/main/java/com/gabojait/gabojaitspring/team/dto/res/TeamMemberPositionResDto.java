package com.gabojait.gabojaitspring.team.dto.res;

import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.profile.domain.type.TeamMemberStatus;
import com.gabojait.gabojaitspring.team.domain.TeamMember;
import com.gabojait.gabojaitspring.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@ApiModel(value = "팀원 포지션 응답")
public class TeamMemberPositionResDto {

    @ApiModelProperty(position = 1, required = true, value = "회원 식별자")
    private Long userId;

    @ApiModelProperty(position = 2, required = true, value = "닉네임")
    private String nickname;

    @ApiModelProperty(position = 3, required = true, value = "포지션",
            allowableValues = "designer, frontend, backend, manager")
    private String position;

    @ApiModelProperty(position = 4, required = true, value = "팀원 상태")
    private String teamMemberStatus;

    public TeamMemberPositionResDto(TeamMember teamMember) {
        User user = teamMember.getUser();

        this.userId = user.getId();
        this.nickname = user.getNickname();
        this.position = Position.fromChar(teamMember.getPosition()).name().toLowerCase();
        this.teamMemberStatus = TeamMemberStatus.fromChar(teamMember.getTeamMemberStatus()).name().toLowerCase();
    }

}
