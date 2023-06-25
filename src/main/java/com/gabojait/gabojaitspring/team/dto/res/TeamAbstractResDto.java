package com.gabojait.gabojaitspring.team.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.team.domain.TeamMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@ApiModel(value = "팀 요약 응답")
public class TeamAbstractResDto {

    @ApiModelProperty(position = 1, required = true, value = "팀 식별자")
    private Long teamId;

    @ApiModelProperty(position = 2, required = true, value = "프로젝트 이름")
    private String projectName;

    @ApiModelProperty(position = 3, required = true, value = "총 모집 팀원 수")
    private List<TeamMemberRecruitCntResDto> teamMemberRecruitCnts = new ArrayList<>();

    @ApiModelProperty(position = 4, required = true, value = "현재 팀원")
    private List<TeamMemberPositionResDto> teamMembers = new ArrayList<>();

    @ApiModelProperty(position = 5, required = true, value = "생성일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;

    @ApiModelProperty(position = 6, required = true, value = "수정일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime updatedAt;

    public TeamAbstractResDto(Team team) {
        this.teamId = team.getId();
        this.projectName = team.getProjectName();

        if (team.getDesignerTotalRecruitCnt() != 0)
            this.teamMemberRecruitCnts.add(
                    new TeamMemberRecruitCntResDto(
                        team.getDesignerTotalRecruitCnt(),
                        Position.DESIGNER.name()
                        )
            );

        if (team.getBackendTotalRecruitCnt() != 0)
            this.teamMemberRecruitCnts.add(
                    new TeamMemberRecruitCntResDto(
                            team.getBackendTotalRecruitCnt(),
                            Position.BACKEND.name()
                    )
            );

        if (team.getFrontendTotalRecruitCnt() != 0)
            this.teamMemberRecruitCnts.add(
                    new TeamMemberRecruitCntResDto(
                            team.getFrontendTotalRecruitCnt(),
                            Position.FRONTEND.name()
                    )
            );

        if (team.getManagerTotalRecruitCnt() != 0)
            this.teamMemberRecruitCnts.add(
                    new TeamMemberRecruitCntResDto(
                            team.getManagerTotalRecruitCnt(),
                            Position.MANAGER.name()
                    )
            );

        for(TeamMember teamMember : team.getTeamMembers())
            this.teamMembers.add(new TeamMemberPositionResDto(teamMember));

        this.createdAt = team.getCreatedAt();
        this.updatedAt = team.getUpdatedAt();
    }
}
