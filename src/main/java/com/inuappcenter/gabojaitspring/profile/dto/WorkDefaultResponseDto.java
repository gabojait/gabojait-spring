package com.inuappcenter.gabojaitspring.profile.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.inuappcenter.gabojaitspring.profile.domain.Work;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@ApiModel(value = "Work 응답")
public class WorkDefaultResponseDto {

    @ApiModelProperty(position = 1, required = true, value = "경력 식별자")
    private String workId;

    @ApiModelProperty(position = 2, required = true, value = "기관명")
    private String corporationName;

    @ApiModelProperty(position = 3, required = true, value = "시작일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startedDate;

    @ApiModelProperty(position = 4, required = true, value = "종료일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endedDate;

    @ApiModelProperty(position = 5, required = true, value = "현재여부")
    private Boolean isCurrent;

    @ApiModelProperty(position = 6, required = true, value = "설명")
    private String description;

    @ApiModelProperty(position = 6, required = true, value = "스키마버전")
    private String schemaVersion;

    public WorkDefaultResponseDto(Work work) {
        this.workId = work.getId().toString();
        this.corporationName = work.getCorporationName();
        this.startedDate = work.getStartedDate();
        this.endedDate = work.getEndedDate();
        this.isCurrent = work.getIsCurrent();
        this.description = work.getDescription();
        this.schemaVersion = work.getSchemaVersion();
    }
}
