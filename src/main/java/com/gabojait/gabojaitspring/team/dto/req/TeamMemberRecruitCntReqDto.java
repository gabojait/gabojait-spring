package com.gabojait.gabojaitspring.team.dto.req;

import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
@ToString
@GroupSequence({TeamMemberRecruitCntReqDto.class, ValidationSequence.Blank.class, ValidationSequence.Format.class})
@ApiModel(value = "포지션별 팀원 수 요청")
public class TeamMemberRecruitCntReqDto {

    @ApiModelProperty(position = 1, required = true, value = "총 팀원 수")
    @NotNull(message = "팀원 수는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @PositiveOrZero(message = "팀원 수는 0 또는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
    private Byte totalRecruitCnt;

    @ApiModelProperty(position = 2, required = true, value = "포지션",
            allowableValues = "DESIGNER, BACKEND, FRONTEND, MANAGER")
    @NotBlank(message = "포지션은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Pattern(regexp = "^(DESIGNER|BACKEND|FRONTEND|MANAGER)",
            message = "포지션은 'DESIGNER', 'BACKEND', 'FRONTEND', 또는 'MANAGER' 중 하나여야 됩니다.",
            groups = ValidationSequence.Format.class)
    private String position;
}
