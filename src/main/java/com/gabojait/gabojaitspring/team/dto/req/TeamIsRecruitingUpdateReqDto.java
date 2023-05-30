package com.gabojait.gabojaitspring.team.dto.req;

import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;

@Getter
@ToString
@NoArgsConstructor
@GroupSequence({TeamIsRecruitingUpdateReqDto.class, ValidationSequence.Blank.class})
@ApiModel(value = "팀원 모집 여부 수정 요청")
public class TeamIsRecruitingUpdateReqDto {

    @ApiModelProperty(position = 1, required = true, value = "팀원 모집 여부", example = "true",
            allowableValues = "true, false")
    @NotNull(message = "팀원 모집 여부는 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    private Boolean isRecruiting;
}
