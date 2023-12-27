package com.gabojait.gabojaitspring.api.dto.team.response;

import com.gabojait.gabojaitspring.domain.team.TeamMember;
import com.gabojait.gabojaitspring.domain.user.Position;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@ApiModel(value = "팀원 응답")
public class TeamMemberResponse {

    @ApiModelProperty(position = 1, required = true, value = "회원 식별자")
    private Long userId;

    @ApiModelProperty(position = 2, required = true, value = "아이디")
    private String username;

    @ApiModelProperty(position = 3, required = true, value = "닉네임")
    private String nickname;

    @ApiModelProperty(position = 4, required = true, value = "포지션",
            allowableValues = "DESIGNER, FRONTEND, BACKEND, MANAGER")
    private Position position;

    @ApiModelProperty(position = 5, required = true, value = "리더 여부")
    private Boolean isLeader;

    public TeamMemberResponse(TeamMember teamMember) {
        this.userId = teamMember.getUser().getId();
        this.username = teamMember.getUser().getUsername();
        this.nickname = teamMember.getUser().getNickname();
        this.position = teamMember.getPosition();
        this.isLeader = teamMember.getIsLeader();
    }
}
