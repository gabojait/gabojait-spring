package com.gabojait.gabojaitspring.team.dto.res;

import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.profile.dto.res.ProfileAbstractResDto;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@ToString
@ApiModel(value = "팀 기본 응답")
public class TeamDefaultResDto extends TeamAbstractResDto {

    @ApiModelProperty(position = 12, required = true, value = "팀 리더 식별자")
    private String leaderUserId;

    @ApiModelProperty(position = 13, required = true, value = "프로젝트 설명")
    private String projectDescription;

    @ApiModelProperty(position = 14, required = true, value = "디자이너")
    private List<ProfileAbstractResDto> designers = new ArrayList<>();

    @ApiModelProperty(position = 15, required = true, value = "백엔드 개발자")
    private List<ProfileAbstractResDto> backends = new ArrayList<>();

    @ApiModelProperty(position = 16, required = true, value = "프론트엔드 개발자")
    private List<ProfileAbstractResDto> frontends = new ArrayList<>();

    @ApiModelProperty(position = 17, required = true, value = "매니저")
    private List<ProfileAbstractResDto> managers = new ArrayList<>();

    @ApiModelProperty(position = 18, required = true, value = "오픈 채팅 URL")
    private String openChatUrl;

    @ApiModelProperty(position = 19, required = true, value = "바라는 점")
    private String expectation;

    public TeamDefaultResDto(Team team,
                             Map<Character, List<User>> teamMembers) {
        super(team);

        this.leaderUserId = team.getLeaderUserId().toString();
        this.projectDescription = team.getProjectDescription();

        if (!teamMembers.get(Position.DESIGNER.getType()).isEmpty())
            teamMembers.get(Position.DESIGNER.getType())
                    .forEach(d ->
                            this.designers.add(new ProfileAbstractResDto(d))
                    );
        if (!teamMembers.get(Position.BACKEND.getType()).isEmpty())
            teamMembers.get(Position.BACKEND.getType())
                    .forEach(b ->
                            this.backends.add(new ProfileAbstractResDto(b))
                    );

        if (!teamMembers.get(Position.FRONTEND.getType()).isEmpty())
            teamMembers.get(Position.FRONTEND.getType())
                    .forEach(f ->
                            this.frontends.add(new ProfileAbstractResDto(f))
                    );

        if (!teamMembers.get(Position.MANAGER.getType()).isEmpty())
            teamMembers.get(Position.MANAGER.getType())
                    .forEach(m ->
                            this.managers.add(new ProfileAbstractResDto(m))
                    );
        this.openChatUrl = team.getOpenChatUrl();
        this.expectation = team.getExpectation();
    }
}
