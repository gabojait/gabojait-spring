package com.gabojait.gabojaitspring.team.dto.res;

import com.gabojait.gabojaitspring.profile.domain.type.TeamMemberStatus;
import com.gabojait.gabojaitspring.profile.dto.res.ProfileAbstractResDto;
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

    @ApiModelProperty(position = 9, required = true, value = "프로젝트 설명")
    private String projectDescription;

    @ApiModelProperty(position = 10, required = true, value = "오픈 채팅 URL")
    private String openChatUrl;

    @ApiModelProperty(position = 11, required = true, value = "바라는 점")
    private String expectation;

    @ApiModelProperty(position = 12, required = true, value = "리더")
    private ProfileAbstractResDto leader;

    @ApiModelProperty(position = 13, required = true, value = "디자이너")
    private List<ProfileAbstractResDto> designers = new ArrayList<>();

    @ApiModelProperty(position = 14, required = true, value = "백엔드 개발자")
    private List<ProfileAbstractResDto> backends = new ArrayList<>();

    @ApiModelProperty(position = 15, required = true, value = "프론트엔드 개발자")
    private List<ProfileAbstractResDto> frontends = new ArrayList<>();

    @ApiModelProperty(position = 16, required = true, value = "매니저")
    private List<ProfileAbstractResDto> managers = new ArrayList<>();

    public TeamDefaultResDto(Team team) {
        super(team);

        this.projectDescription = team.getProjectDescription();
        this.openChatUrl = team.getOpenChatUrl();
        this.expectation = team.getExpectation();

        for(TeamMember teamMember : team.getTeamMembers()) {
            if (teamMember.getTeamMemberStatus().equals(TeamMemberStatus.LEADER.getType()))
                leader = new ProfileAbstractResDto(teamMember.getUser());

            switch (teamMember.getPosition()) {
                case 'D':
                    designers.add(new ProfileAbstractResDto(teamMember.getUser()));
                    break;
                case 'B':
                    backends.add(new ProfileAbstractResDto(teamMember.getUser()));
                    break;
                case 'F':
                    frontends.add(new ProfileAbstractResDto(teamMember.getUser()));
                    break;
                case 'M':
                    managers.add(new ProfileAbstractResDto(teamMember.getUser()));
                    break;
            }
        }
    }
}
