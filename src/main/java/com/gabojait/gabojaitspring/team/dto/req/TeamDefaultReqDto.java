package com.gabojait.gabojaitspring.team.dto.req;

import com.gabojait.gabojaitspring.common.util.validator.ValidIfPresent;
import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import com.gabojait.gabojaitspring.exception.CustomException;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.Valid;
import javax.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

import static com.gabojait.gabojaitspring.common.code.ErrorCode.NON_EXISTING_POSITION;

@Getter
@NoArgsConstructor
@GroupSequence({TeamDefaultReqDto.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class})
@ApiModel(value = "팀 기본 요청")
@ValidIfPresent
public class TeamDefaultReqDto {

    @ApiModelProperty(position = 1, required = true, value = "프로젝트명", example = "가보자잇")
    @NotBlank(message = "프로젝트명는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 1, max = 20, message = "프로젝트명은 1~20자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String projectName;

    @ApiModelProperty(position = 2, required = true, value = "프로젝트 설명", example = "가보자잇 프로젝트 설명입니다.")
    @NotBlank(message = "프로젝트 설명은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 1, max = 500, message = "프로젝트 설명은 1~500자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String projectDescription;

    @ApiModelProperty(position = 3, required = true, value = "팀원 수")
    @Valid
    private List<TeamMemberRecruitCntReqDto> teamMemberRecruitCnts = new ArrayList<>();

    @ApiModelProperty(position = 7, required = true, value = "바라는 점", example = "열정적인 팀원을 구합니다.")
    @NotBlank(message = "바라는 점은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 1, max = 200, message = "바라는 점은 1~200자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String expectation;

    @ApiModelProperty(position = 8, required = true, value = "오픈 채팅 링크", example = "https://open.kakao.com/o/test")
    @NotBlank(message = "오픈 채팅 URL은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 25, max = 100, message = "오픈 채팅 URL은 25~100자만 가능합니다.", groups = ValidationSequence.Size.class)
    @Pattern(regexp = "^https\\:\\/\\/open\\.kakao\\.com\\/.+$", message = "오픈 채팅 URL은 카카오 오픈 채팅 형식만 가능합니다.",
            groups = ValidationSequence.Format.class)
    private String openChatUrl;

    public Team toEntity(User user) {
        byte designerTotalRecruitCnt = 0;
        byte backendTotalRecruitCnt = 0;
        byte frontendTotalRecruitCnt = 0;
        byte managerTotalRecruitCnt = 0;

        switch (user.getPosition()) {
            case 'D':
                designerTotalRecruitCnt++;
                break;
            case 'B':
                backendTotalRecruitCnt++;
                break;
            case 'F':
                frontendTotalRecruitCnt++;
                break;
            case 'M':
                managerTotalRecruitCnt++;
                break;
            default:
                throw new CustomException(NON_EXISTING_POSITION);
        }

        for(TeamMemberRecruitCntReqDto recruit : teamMemberRecruitCnts) {
            Position position = Position.fromString(recruit.getPosition());

            switch (position.getType()) {
                case 'D':
                    designerTotalRecruitCnt += recruit.getTotalRecruitCnt();
                    break;
                case 'B':
                    backendTotalRecruitCnt += recruit.getTotalRecruitCnt();
                    break;
                case 'F':
                    frontendTotalRecruitCnt += recruit.getTotalRecruitCnt();
                    break;
                case 'M':
                    managerTotalRecruitCnt += recruit.getTotalRecruitCnt();
                    break;
            }
        }

        return Team.builder()
                .projectName(this.projectName)
                .projectDescription(this.projectDescription)
                .designerTotalRecruitCnt(designerTotalRecruitCnt)
                .backendTotalRecruitCnt(backendTotalRecruitCnt)
                .frontendTotalRecruitCnt(frontendTotalRecruitCnt)
                .managerTotalRecruitCnt(managerTotalRecruitCnt)
                .expectation(this.expectation)
                .openChatUrl(this.openChatUrl)
                .build();
    }
}
