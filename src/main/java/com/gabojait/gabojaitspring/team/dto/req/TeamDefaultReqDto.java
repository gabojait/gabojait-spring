package com.gabojait.gabojaitspring.team.dto.req;

import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.exception.ValidationSequence;
import com.gabojait.gabojaitspring.team.domain.Team;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import javax.validation.GroupSequence;
import javax.validation.constraints.*;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.SERVER_ERROR;

@Getter
@NoArgsConstructor
@GroupSequence({TeamDefaultReqDto.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class})
@ApiModel(value = "팀 기본 요청")
public class TeamDefaultReqDto {

    @ApiModelProperty(position = 1, required = true, value = "프로젝트명", example = "가보자잇")
    @NotBlank(message = "프로젝트명는 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 1, max = 20, message = "프로젝트명은 1~20자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String projectName;

    @ApiModelProperty(position = 2, required = true, value = "프로젝트 설명", example = "가보자잇 프로젝트 설명입니다.")
    @NotBlank(message = "프로젝트 설명은 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 1, max = 500, message = "프로젝트 설명은 1~500자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String projectDescription;

    @ApiModelProperty(position = 3, required = true, value = "디자이너 총 팀원 수", example = "2")
    @NotNull(message = "디자이너 총 팀원 수는 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    @PositiveOrZero(message = "디자이너 총 팀원 수는 0 또는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
    private Short designerTotalRecruitCnt;

    @ApiModelProperty(position = 4, required = true, value = "백엔드 개발자 총 팀원 수", example = "2")
    @NotNull(message = "백엔드 개발자 총 팀원 수는 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    @PositiveOrZero(message = "백엔드 개발자 총 팀원 수는 0 또는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
    private Short backendTotalRecruitCnt;

    @ApiModelProperty(position = 5, required = true, value = "프론트엔드 개발자 총 팀원 수", example = "2")
    @NotNull(message = "프론트엔드 개발자 총 팀원 수는 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    @PositiveOrZero(message = "프론트엔드 개발자 총 팀원 수는 0 또는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
    private Short frontendTotalRecruitCnt;

    @ApiModelProperty(position = 6, required = true, value = "매니저 총 팀원 수", example = "2")
    @NotNull(message = "매니저 총 팀원 수는 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    @PositiveOrZero(message = "매니저 총 팀원 수는 0 또는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
    private Short managerTotalRecruitCnt;

    @ApiModelProperty(position = 7, required = true, value = "바라는 점", example = "열정적인 팀원을 구합니다.")
    @NotBlank(message = "바라는 점은 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 1, max = 200, message = "바라는 점은 1~200자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String expectation;

    @ApiModelProperty(position = 8, required = true, value = "오픈 채팅 링크", example = "https://open.kakao.com/o/test")
    @NotBlank(message = "오픈 채팅 URL은 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 25, max = 100, message = "오픈 채팅 URL은 25~100자만 가능합니다.", groups = ValidationSequence.Size.class)
    @Pattern(regexp = "^https\\:\\/\\/open\\.kakao\\.com\\/.+$", message = "오픈 채팅 URL은 카카오 오픈 채팅 형식만 가능합니다.",
            groups = ValidationSequence.Format.class)
    private String openChatUrl;

    public Team toEntity(ObjectId userId, char position) {
        switch (position) {
            case 'D':
                this.designerTotalRecruitCnt++;
                break;
            case 'B':
                this.backendTotalRecruitCnt++;
                break;
            case 'F':
                this.frontendTotalRecruitCnt++;
                break;
            case 'M':
                this.managerTotalRecruitCnt++;
                break;
            default:
                throw new CustomException(null, SERVER_ERROR);
        }

        return Team.builder()
                .leaderUserId(userId)
                .projectName(this.projectName)
                .projectDescription(this.projectDescription)
                .designerTotalRecruitCnt(this.designerTotalRecruitCnt)
                .backendTotalRecruitCnt(this.backendTotalRecruitCnt)
                .frontendTotalRecruitCnt(this.frontendTotalRecruitCnt)
                .managerTotalRecruitCnt(this.managerTotalRecruitCnt)
                .openChatUrl(this.openChatUrl)
                .expectation(this.expectation)
                .build();
    }
}
