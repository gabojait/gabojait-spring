package com.inuappcenter.gabojaitspring.team.dto.req;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import com.inuappcenter.gabojaitspring.team.domain.Team;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@GroupSequence({TeamCreateReqDto.class})
@ApiModel(value = "Team 생성 요청")
public class TeamCreateReqDto {

    @ApiModelProperty(position = 1, required = true, value = "프로젝트 이름", example = "Gabojait")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 1, max = 20, message = "프로젝트 이름은 1~20자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String projectName;

    @ApiModelProperty(position = 2, required = true, value = "프로젝트 설명", example = "가보자잇 프로젝트 설명입니다.")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 1, max = 500, message = "프로젝트 설명은 1~500자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String projectDescription;

    @ApiModelProperty(position = 3, required = true, value = "원하는 디자이너 팀원 수", example = "1")
    @NotNull(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    @PositiveOrZero(message = "디자이너 수는 0 또는 양수만 가능합니다.", groups = ValidationSequence.PositiveOrZero.class)
    private Short designerTotalRecruitCnt;

    @ApiModelProperty(position = 4, required = true, value = "원하는 백엔드 개발자 팀원 수", example = "1")
    @NotNull(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    @PositiveOrZero(message = "디자이너 수는 0 또는 양수만 가능합니다.", groups = ValidationSequence.PositiveOrZero.class)
    private Short backendTotalRecruitCnt;

    @ApiModelProperty(position = 5, required = true, value = "원하는 프론트엔드 개발자 팀원 수", example = "1")
    @NotNull(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    @PositiveOrZero(message = "프론트엔드 개발자 수는 0 또는 양수만 가능합니다.", groups = ValidationSequence.PositiveOrZero.class)
    private Short frontendTotalRecruitCnt;

    @ApiModelProperty(position = 6, required = true, value = "원하는 프로젝트 매니저 팀원 팀원 수", example = "1")
    @NotNull(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    @PositiveOrZero(message = "매니저 수는 0 또는 양수만 가능합니다.", groups = ValidationSequence.PositiveOrZero.class)
    private Short projectManagerTotalRecruitCnt;

    @ApiModelProperty(position = 7, required = true, value = "바라는 점", example = "가보자잇 프로젝트에서 바라는 점입니다.")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 1, max = 200, message = "바라는 점은 1~200자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String expectation;

    @ApiModelProperty(position = 8, required = true, value = "오픈채팅 링크", example = "openKakaoURI")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 1, max = 100, message = "오픈채팅 링크는 1~100자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String openChatUrl;

    public Team toEntity(ObjectId userId) {
        return Team.builder()
                .leaderUserId(userId)
                .projectName(this.projectName)
                .projectDescription(this.projectDescription)
                .designerTotalRecruitCnt(this.designerTotalRecruitCnt)
                .backendTotalRecruitCnt(this.backendTotalRecruitCnt)
                .frontendTotalRecruitCnt(this.frontendTotalRecruitCnt)
                .projectManagerTotalRecruitCnt(this.projectManagerTotalRecruitCnt)
                .openChatUrl(this.openChatUrl)
                .expectation(this.expectation)
                .build();
    }
}
