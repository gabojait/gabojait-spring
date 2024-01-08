package com.gabojait.gabojaitspring.api.dto.review.response;

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
@ApiModel(value = "리뷰 가능한 팀 응답")
public class ReviewFindTeamResponse {

    @ApiModelProperty(position = 1, required = true, value = "팀 식별자")
    private Long teamId;

    @ApiModelProperty(position = 2, required = true, value = "프로젝트 이름")
    private String projectName;

    @ApiModelProperty(position = 3, required = true, value = "팀원들")
    private List<ReviewTeamMemberResponse> teamMembers;

    public ReviewFindTeamResponse(Team team, List<TeamMember> teamMembers) {
        this.teamId = team.getId();
        this.projectName = team.getProjectName();

        this. teamMembers = teamMembers.stream()
                .map(ReviewTeamMemberResponse::new)
                .collect(Collectors.toList());
    }
}
