package com.gabojait.gabojaitspring.team.dto.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@ApiModel(value = "포지션별 팀원 수 응답")
public class TeamMemberRecruitCntResDto {

    @ApiModelProperty(position = 1, required = true, value = "총 팀원 수")
    private Byte totalRecruitCnt;

    @ApiModelProperty(position = 2, required = true, value = "포지션",
            allowableValues = "designer, backend, frontend, manager")
    private String position;

    public TeamMemberRecruitCntResDto(Byte totalRecruitCnt, String position) {
        this.totalRecruitCnt = totalRecruitCnt;
        this.position = position;
    }
}
