package com.gabojait.gabojaitspring.profile.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.profile.domain.Work;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
@ApiModel(value = "경력 기본 응답")
public class WorkDefaultResDto {

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

    @ApiModelProperty(position = 5, required = true, value = "현재 여부")
    private Boolean isCurrent;

    @ApiModelProperty(position = 6, required = true, value = "경력 설명")
    private String workDescription;

    @ApiModelProperty(position = 6, required = true, value = "스키마 버전")
    private String schemaVersion;

    public WorkDefaultResDto(Work work) {
        this.workId = work.getId().toString();
        this.corporationName = work.getCorporationName();
        this.startedDate = work.getStartedDate();
        this.endedDate = work.getEndedDate();
        this.isCurrent = work.getIsCurrent();
        this.workDescription = work.getWorkDescription();
        this.schemaVersion = work.getSchemaVersion();
    }
}
