package com.gabojait.gabojaitspring.api.dto.team.response;

import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@ApiModel(value = "팀 기본 응답")
public class TeamDefaultResponse extends TeamAbstractResponse {

    @ApiModelProperty(position = 13, required = true, value = "프로젝트 설명")
    private String projectDescription;

    @ApiModelProperty(position = 14, required = true, value = "오픈 채팅 URL")
    private String openChatUrl;

    @ApiModelProperty(position = 15, required = true, value = "바라는 점")
    private String expectation;

    @ApiModelProperty(position = 16, required = true, value = "팀원")
    private List<TeamMemberDefaultResponse> teamMembers;

    public TeamDefaultResponse(Team team, List<TeamMember> teamMembers) {
        super(team);

        this.projectDescription = team.getProjectDescription();
        this.openChatUrl = team.getOpenChatUrl();
        this.expectation = team.getExpectation();
        this.teamMembers = teamMembers.stream()
                .map(TeamMemberDefaultResponse::new)
                .collect(Collectors.toList());
    }
}
