package com.gabojait.gabojaitspring.api.dto.profile.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.domain.profile.Work;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@ToString
@ApiModel(value = "경력 기본 응답")
public class WorkDefaultResponse {

    @ApiModelProperty(position = 1, required = true, value = "경력 식별자")
    private Long workId;

    @ApiModelProperty(position = 2, required = true, value = "기관명")
    private String corporationName;

    @ApiModelProperty(position = 3, required = true, value = "경력 설명")
    private String workDescription;

    @ApiModelProperty(position = 4, required = true, value = "시작일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startedAt;

    @ApiModelProperty(position = 5, required = true, value = "종료일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endedAt;

    @ApiModelProperty(position = 6, required = true, value = "현재 여부", allowableValues = "true, false")
    private Boolean isCurrent;

    @ApiModelProperty(position = 7, required = true, value = "생성일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @ApiModelProperty(position = 8, required = true, value = "수정일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public WorkDefaultResponse(Work work) {
        this.workId = work.getId();
        this.corporationName = work.getCorporationName();
        this.workDescription = work.getWorkDescription();
        this.startedAt = work.getStartedAt();
        this.endedAt = work.getEndedAt();
        this.isCurrent = work.getIsCurrent();
        this.createdAt = work.getCreatedAt();
        this.updatedAt = work.getUpdatedAt();
    }
}
