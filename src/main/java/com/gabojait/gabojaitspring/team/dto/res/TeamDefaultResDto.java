package com.gabojait.gabojaitspring.team.dto.res;

import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.team.domain.TeamMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@ApiModel(value = "팀 기본 응답")
public class TeamDefaultResDto extends TeamAbstractResDto {

    @ApiModelProperty(position = 7, required = true, value = "프로젝트 설명")
    private String projectDescription;

    @ApiModelProperty(position = 8, required = true, value = "오픈 채팅 URL")
    private String openChatUrl;

    @ApiModelProperty(position = 9, required = true, value = "바라는 점")
    private String expectation;

    @ApiModelProperty(position = 10, required = true, value = "팀원")
    private List<TeamMemberPositionResDto> teamMembers = new ArrayList<>();

    public TeamDefaultResDto(Team team) {
        super(team);

        this.projectDescription = team.getProjectDescription();
        this.openChatUrl = team.getOpenChatUrl();
        this.expectation = team.getExpectation();

        for(TeamMember teamMember : team.getTeamMembers())
            if (!teamMember.getIsDeleted())
                teamMembers.add(new TeamMemberPositionResDto(teamMember));
    }
}
