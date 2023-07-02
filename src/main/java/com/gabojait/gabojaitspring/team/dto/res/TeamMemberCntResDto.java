package com.gabojait.gabojaitspring.team.dto.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@ApiModel(value = "포지션별 팀원 수 응답")
public class TeamMemberCntResDto {

    @ApiModelProperty(position = 1, required = true, value = "포지션",
            allowableValues = "designer, backend, frontend, manager")
    private String position;

    @ApiModelProperty(position = 2, required = true, value = "모집 팀원 수")
    private Byte recruitCnt;

    @ApiModelProperty(position = 3, required = true, value = "현재 팀원 수")
    private Byte currentCnt;

    public TeamMemberCntResDto(String position, Byte recruitCnt, Byte currentCnt) {
        this.position = position;
        this.recruitCnt = recruitCnt;
        this.currentCnt = currentCnt;
    }
}
