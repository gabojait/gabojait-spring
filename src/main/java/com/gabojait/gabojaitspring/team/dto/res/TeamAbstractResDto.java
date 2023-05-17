package com.gabojait.gabojaitspring.team.dto.res;

import com.gabojait.gabojaitspring.profile.dto.res.ProfileAbstractResDto;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@ApiModel(value = "팀 요약 응답")
public class TeamAbstractResDto {

    @ApiModelProperty(position = 1, required = true, value = "팀 식별자")
    private String teamId;

    @ApiModelProperty(position = 2, required = true, value = "프로젝트 이름")
    private String projectName;

    @ApiModelProperty(position = 3, required = true, value = "디자이너")
    private List<ProfileAbstractResDto> designers = new ArrayList<>();

    @ApiModelProperty(position = 4, required = true, value = "백엔드 개발자")
    private List<ProfileAbstractResDto> backends = new ArrayList<>();

    @ApiModelProperty(position = 5, required = true, value = "프론트엔드 개발자")
    private List<ProfileAbstractResDto> frontends = new ArrayList<>();

    @ApiModelProperty(position = 6, required = true, value = "매니저")
    private List<ProfileAbstractResDto> managers = new ArrayList<>();

    @ApiModelProperty(position = 7, required = true, value = "스키마 버전")
    private String schemaVersion;

    public TeamAbstractResDto(Team team) {
        this.teamId = team.getId().toString();
        this.projectName = team.getProjectName();
        this.schemaVersion = team.getSchemaVersion();

        if (!team.getDesigners().isEmpty())
            team.getDesigners().forEach(d -> this.designers.add(new ProfileAbstractResDto(d)));
        if (!team.getBackends().isEmpty())
            team.getBackends().forEach(b -> this.backends.add(new ProfileAbstractResDto(b)));
        if (!team.getFrontends().isEmpty())
            team.getFrontends().forEach(f -> this.frontends.add(new ProfileAbstractResDto(f)));
        if (!team.getManagers().isEmpty())
            team.getManagers().forEach(m -> this.managers.add(new ProfileAbstractResDto(m)));
    }
}
