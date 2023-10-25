package com.gabojait.gabojaitspring.api.dto.team.request;

import com.gabojait.gabojaitspring.api.dto.common.ValidationSequence;
import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
import com.gabojait.gabojaitspring.domain.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.GroupSequence;
import javax.validation.constraints.*;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@GroupSequence({TeamDefaultRequest.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class})
@ApiModel(value = "팀 기본 요청")
public class TeamDefaultRequest {

    @ApiModelProperty(position = 1, required = true, value = "프로젝트명", example = "가보자잇")
    @NotBlank(message = "프로젝트명는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 1, max = 20, message = "프로젝트명은 1~20자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String projectName;

    @ApiModelProperty(position = 2, required = true, value = "프로젝트 설명", example = "가보자잇 프로젝트 설명입니다.")
    @NotBlank(message = "프로젝트 설명은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 1, max = 500, message = "프로젝트 설명은 1~500자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String projectDescription;

    @ApiModelProperty(position = 3, required = true, value = "바라는 점", example = "열정적인 팀원을 구합니다.")
    @NotBlank(message = "바라는 점은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 1, max = 200, message = "바라는 점은 1~200자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String expectation;

    @ApiModelProperty(position = 4, required = true, value = "오픈 채팅 링크", example = "https://open.kakao.com/o/test")
    @NotBlank(message = "오픈 채팅 URL은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 25, max = 100, message = "오픈 채팅 URL은 25~100자만 가능합니다.", groups = ValidationSequence.Size.class)
    @Pattern(regexp = "^https\\:\\/\\/open\\.kakao\\.com\\/.+$", message = "오픈 채팅 URL은 카카오 오픈 채팅 형식만 가능합니다.",
            groups = ValidationSequence.Format.class)
    private String openChatUrl;

    @ApiModelProperty(position = 5, required = true, value = "디자이너 최대 수")
    @NotNull(message = "디자이너 최대 수는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @PositiveOrZero(message = "디자이너 최대 수는 0 또는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
    private Byte designerMaxCnt;

    @ApiModelProperty(position = 6, required = true, value = "백엔드 최대 수")
    @NotNull(message = "백엔드 최대 수는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @PositiveOrZero(message = "백엔드 최대 수는 0 또는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
    private Byte backendMaxCnt;

    @ApiModelProperty(position = 7, required = true, value = "프런트 최대 수")
    @NotNull(message = "프런트 최대 수는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @PositiveOrZero(message = "프런트 최대 수는 0 또는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
    private Byte frontendMaxCnt;

    @ApiModelProperty(position = 8, required = true, value = "매니저 최대 수")
    @NotNull(message = "매니저 최대 수는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @PositiveOrZero(message = "매니저 최대 수는 0 또는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
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
                .position(user.getPosition())
                .isLeader(true)
                .user(user)
                .team(team)
                .build();
    }

    @Builder
    private TeamDefaultRequest(String projectName,
                               String projectDescription,
                               String expectation,
                               String openChatUrl,
                               byte designerMaxCnt,
                               byte backendMaxCnt,
                               byte frontendMaxCnt,
                               byte managerMaxCnt) {
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.expectation = expectation;
        this.openChatUrl = openChatUrl;
        this.designerMaxCnt = designerMaxCnt;
        this.backendMaxCnt = backendMaxCnt;
        this.frontendMaxCnt = frontendMaxCnt;
        this.managerMaxCnt = managerMaxCnt;
    }

}
