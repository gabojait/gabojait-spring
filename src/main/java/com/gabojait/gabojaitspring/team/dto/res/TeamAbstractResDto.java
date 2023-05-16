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

        for (User d : team.getDesigners())
            designers.add(new ProfileAbstractResDto(d));
        for (User b : team.getBackends())
            backends.add(new ProfileAbstractResDto(b));
        for (User f : team.getFrontends())
            frontends.add(new ProfileAbstractResDto(f));
        for (User p : team.getManagers())
            managers.add(new ProfileAbstractResDto(p));

        this.schemaVersion = team.getSchemaVersion();
    }
}
