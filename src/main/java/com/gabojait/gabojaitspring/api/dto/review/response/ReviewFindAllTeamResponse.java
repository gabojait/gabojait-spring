package com.gabojait.gabojaitspring.api.dto.review.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.domain.team.Team;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@ApiModel(value = "리뷰 가능한 전체 팀 응답")
public class ReviewFindAllTeamResponse {

    @ApiModelProperty(position = 1, required = true, value = "팀 식별자")
    private Long teamId;

    @ApiModelProperty(position = 2, required = true, value = "프로젝트 이름")
    private String projectName;

    @ApiModelProperty(position = 3, required = true, value = "디자이너 현재 수")
    private Byte designerCurrentCnt;

    @ApiModelProperty(position = 4, required = true, value = "백엔드 현재 수")
    private Byte backendCurrentCnt;

    @ApiModelProperty(position = 5, required = true, value = "프런트 현재 수")
    private Byte frontendCurrentCnt;

    @ApiModelProperty(position = 6, required = true, value = "매니저 현재 수")
    private Byte managerCurrentCnt;

    @ApiModelProperty(position = 7, required = true, value = "디자이너 최대 수")
    private Byte designerMaxCnt;

    @ApiModelProperty(position = 8, required = true, value = "백엔드 최대 수")
    private Byte backendMaxCnt;

    @ApiModelProperty(position = 9, required = true, value = "프런트 최대 수")
    private Byte frontendMaxCnt;

    @ApiModelProperty(position = 10, required = true, value = "매니저 최대 수")
    private Byte managerMaxCnt;

    @ApiModelProperty(position = 11, required = true, value = "생성일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @ApiModelProperty(position = 12, required = true, value = "수정일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public ReviewFindAllTeamResponse(Team team) {
        this.teamId = team.getId();
        this.projectName = team.getProjectName();
        this.designerCurrentCnt = team.getDesignerCurrentCnt();
        this.backendCurrentCnt = team.getBackendCurrentCnt();
        this.frontendCurrentCnt = team.getFrontendCurrentCnt();
        this.managerCurrentCnt = team.getManagerCurrentCnt();
        this.designerMaxCnt = team.getDesignerMaxCnt();
        this.backendMaxCnt = team.getBackendMaxCnt();
        this.frontendMaxCnt = team.getFrontendMaxCnt();
        this.managerMaxCnt = team.getManagerMaxCnt();
        this.createdAt = team.getCreatedAt();
        this.updatedAt = team.getUpdatedAt();
    }
}
