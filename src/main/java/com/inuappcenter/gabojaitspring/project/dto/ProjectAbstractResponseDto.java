package com.inuappcenter.gabojaitspring.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.inuappcenter.gabojaitspring.project.domain.Project;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@ApiModel(value = "Project 요약 응답")
public class ProjectAbstractResponseDto {

    @ApiModelProperty(position = 1, required = true, value = "프로젝트 식별자")
    private String projectId;

    @ApiModelProperty(position = 2, required = true, value = "리더 프로필 식별자")
    private String leaderProfileId;

    @ApiModelProperty(position = 3, required = true, value = "프로젝트 이름")
    private String projectName;

    @ApiModelProperty(position = 4, required = true, value = "채팅 링크")
    private String chatLink;

    @ApiModelProperty(position = 5, required = true, value = "현재 백엔드개발자 수")
    private Integer backendCnt;

    @ApiModelProperty(position = 6, required = true, value = "총 백엔드개발자 수")
    private Byte totalBackendCnt;

    @ApiModelProperty(position = 7, required = true, value = "현재 프론트엔드개발자 수")
    private Integer frontendCnt;

    @ApiModelProperty(position = 8, required = true, value = "총 프론트엔드개발자 수")
    private Byte totalFrontendCnt;

    @ApiModelProperty(position = 9, required = true, value = "현재 디자이너 수")
    private Integer designerCnt;

    @ApiModelProperty(position = 10, required = true, value = "총 디자이너 수")
    private Byte totalDesignerCnt;

    @ApiModelProperty(position = 11, required = true, value = "현재 매니저 수")
    private Integer managerCnt;

    @ApiModelProperty(position = 12, required = true, value = "총 매니저 수")
    private Byte totalManagerCnt;

    @ApiModelProperty(position = 13, required = true, value = "프로젝트 결과물 링크")
    private String projectResultLink;

    @ApiModelProperty(position = 14, required = true, value = "시작일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startedDate;

    @ApiModelProperty(position = 15, required = true, value = "종료일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endedDate;

    @ApiModelProperty(position = 16, required = true, value = "지원 수")
    private Integer applyCnt;

    @ApiModelProperty(position = 17, required = true, value = "영입 수")
    private Integer recruitCnt;

    @ApiModelProperty(position = 18, required = true, value = "스키마버전")
    private String schemaVersion;


    public ProjectAbstractResponseDto(Project project) {
        this.projectId = project.getId().toString();
        this.leaderProfileId = project.getLeaderProfileId().toString();
        this.projectName = project.getProjectName();
        this.chatLink = project.getChatLink();

        this.backendCnt = project.getBackendProfileIds().size();
        this.totalBackendCnt = project.getTotalBackendCnt();

        this.frontendCnt = project.getFrontendProfileIds().size();
        this.totalFrontendCnt = project.getTotalFrontendCnt();

        this.designerCnt = project.getDesignerProfileIds().size();
        this.totalDesignerCnt = project.getTotalDesignerCnt();

        this.managerCnt = project.getManagerProfileIds().size();
        this.totalManagerCnt = project.getTotalManagerCnt();

        this.projectResultLink = project.getProjectResultLink();
        this.startedDate = project.getStartedDate();
        this.endedDate = project.getEndedDate();

        this.applyCnt = project.getApplyIds().size();
        this.recruitCnt = project.getRecruitIds().size();

        this.schemaVersion = project.getSchemaVersion();
    }
}
