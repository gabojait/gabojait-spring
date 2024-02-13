package com.gabojait.gabojaitspring.api.dto.team.request;

import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.domain.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(value = "팀 생성 요청")
public class TeamCreateRequest {

    @ApiModelProperty(position = 1, required = true, value = "프로젝트명", example = "가보자잇")
    @Size(min = 1, max = 20, message = "프로젝트명은 1~20자만 가능합니다.")
    private String projectName;

    @ApiModelProperty(position = 2, required = true, value = "프로젝트 설명", example = "가보자잇 프로젝트 설명입니다.")
    @Size(min = 1, max = 500, message = "프로젝트 설명은 1~500자만 가능합니다.")
    private String projectDescription;

    @ApiModelProperty(position = 3, required = true, value = "바라는 점", example = "열정적인 팀원을 구합니다.")
    @Size(min = 1, max = 200, message = "바라는 점은 1~200자만 가능합니다.")
    private String expectation;

    @ApiModelProperty(position = 4, required = true, value = "오픈 채팅 링크", example = "https://open.kakao.com/o/test")
    @Size(min = 26, max = 100, message = "오픈 채팅 URL은 26~100자만 가능합니다.")
    @Pattern(regexp = "^https\\:\\/\\/open\\.kakao\\.com\\/.+$", message = "오픈 채팅 URL은 카카오 오픈 채팅 형식만 가능합니다.")
    private String openChatUrl;

    @ApiModelProperty(position = 5, required = true, value = "포지션", example = "MANAGER",
            allowableValues = "DESIGNER, BACKEND, FRONTEND, MANAGER")
    @Pattern(regexp = "^(DESIGNER|BACKEND|FRONTEND|MANAGER|NONE)",
            message = "포지션은 'DESIGNER', 'BACKEND', 'FRONTEND', 'MANAGER', 또는 'NONE' 중 하나여야 됩니다.")
    private String position;

    @ApiModelProperty(position = 6, required = true, value = "디자이너 최대 수")
    @NotNull(message = "디자이너 최대 수는 필수 입력입니다.")
    @PositiveOrZero(message = "디자이너 최대 수는 0 또는 양수만 가능합니다.")
    private Byte designerMaxCnt;

    @ApiModelProperty(position = 7, required = true, value = "백엔드 최대 수")
    @NotNull(message = "백엔드 최대 수는 필수 입력입니다.")
    @PositiveOrZero(message = "백엔드 최대 수는 0 또는 양수만 가능합니다.")
    private Byte backendMaxCnt;

    @ApiModelProperty(position = 8, required = true, value = "프런트 최대 수")
    @NotNull(message = "프런트 최대 수는 필수 입력입니다.")
    @PositiveOrZero(message = "프런트 최대 수는 0 또는 양수만 가능합니다.")
    private Byte frontendMaxCnt;

    @ApiModelProperty(position = 9, required = true, value = "매니저 최대 수")
    @NotNull(message = "매니저 최대 수는 필수 입력입니다.")
    @PositiveOrZero(message = "매니저 최대 수는 0 또는 양수만 가능합니다.")
    private Byte managerMaxCnt;

    public Team toTeamEntity() {
        return Team.builder()
                .projectName(this.projectName)
                .projectDescription(this.projectDescription)
                .expectation(this.expectation)
                .openChatUrl(this.openChatUrl)
                .designerMaxCnt(this.designerMaxCnt)
                .backendMaxCnt(this.backendMaxCnt)
                .frontendMaxCnt(this.frontendMaxCnt)
                .managerMaxCnt(this.managerMaxCnt)
                .build();
    }

    public TeamMember toTeamMemberEntity(User user, Team team) {
        return TeamMember.builder()
                .position(Position.valueOf(this.position))
                .isLeader(true)
                .user(user)
                .team(team)
                .build();
    }

    @Builder
    private TeamCreateRequest(String projectName,
                              String projectDescription,
                              String expectation,
                              String openChatUrl,
                              String position,
                              byte designerMaxCnt,
                              byte backendMaxCnt,
                              byte frontendMaxCnt,
                              byte managerMaxCnt) {
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.expectation = expectation;
        this.openChatUrl = openChatUrl;
        this.position = position;
        this.designerMaxCnt = designerMaxCnt;
        this.backendMaxCnt = backendMaxCnt;
        this.frontendMaxCnt = frontendMaxCnt;
        this.managerMaxCnt = managerMaxCnt;
    }
}
