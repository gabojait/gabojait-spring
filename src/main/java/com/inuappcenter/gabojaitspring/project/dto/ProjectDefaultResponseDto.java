package com.inuappcenter.gabojaitspring.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.inuappcenter.gabojaitspring.profile.domain.Profile;
import com.inuappcenter.gabojaitspring.profile.dto.ProfileAbstractResponseDto;
import com.inuappcenter.gabojaitspring.project.domain.Project;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ApiModel(value = "Project 응답")
public class ProjectDefaultResponseDto {

    @ApiModelProperty(position = 1, required = true, value = "프로젝트 식별자")
    private String projectId;

    @ApiModelProperty(position = 2, required = true, value = "프로젝트 이름")
    private String projectName;

    @ApiModelProperty(position = 3, required = true, value = "리더")
    private ProfileAbstractResponseDto leader;

    @ApiModelProperty(position = 4, required = true, value = "채팅 링크")
    private String chatLink;

    @ApiModelProperty(position = 5, required = true, value = "백엔드개발자")
    private List<ProfileAbstractResponseDto> backends = new ArrayList<>();

    @ApiModelProperty(position = 6, required = true, value = "총 백엔드개발자 수")
    private Byte totalBackendCnt;

    @ApiModelProperty(position = 7, required = true, value = "프론트엔드개발자")
    private List<ProfileAbstractResponseDto> frontends = new ArrayList<>();

    @ApiModelProperty(position = 8, required = true, value = "총 프론트엔드개발자 수")
    private Byte totalFrontendCnt;

    @ApiModelProperty(position = 9, required = true, value = "디자이너")
    private List<ProfileAbstractResponseDto> designers = new ArrayList<>();

    @ApiModelProperty(position = 10, required = true, value = "총 디자이너 수")
    private Byte totalDesignerCnt;

    @ApiModelProperty(position = 11, required = true, value = "매니저")
    private List<ProfileAbstractResponseDto> managers = new ArrayList<>();

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

    @ApiModelProperty(position = 16, required = true, value = "스키마버전")
    private String schemaVersion;

    public ProjectDefaultResponseDto(Project project,
                                     Profile leader,
                                     List<Profile> backends,
                                     List<Profile> frontends,
                                     List<Profile> designers,
                                     List<Profile> managers) {
        this.projectId = project.getId().toString();
        this.projectName = project.getProjectName();
        this.leader = new ProfileAbstractResponseDto(leader);
        this.chatLink = project.getChatLink();
        this.totalBackendCnt = project.getTotalBackendCnt();
        this.totalFrontendCnt = project.getTotalFrontendCnt();
        this.totalDesignerCnt = project.getTotalDesignerCnt();
        this.totalManagerCnt = project.getTotalManagerCnt();

        for (Profile backend : backends)
            if(!backend.getIsDeleted())
                this.backends.add(new ProfileAbstractResponseDto(backend));

        for (Profile frontend : frontends)
            if(!frontend.getIsDeleted())
                this.frontends.add(new ProfileAbstractResponseDto(frontend));

        for (Profile designer : designers)
            if(!designer.getIsDeleted())
                this.designers.add(new ProfileAbstractResponseDto(designer));

        for (Profile manager : managers)
            if(!manager.getIsDeleted())
                this.managers.add(new ProfileAbstractResponseDto(manager));

        this.projectResultLink = project.getProjectResultLink();
        this.startedDate = project.getStartedDate();
        this.endedDate = project.getEndedDate();
        this.schemaVersion = project.getSchemaVersion();
    }
}
