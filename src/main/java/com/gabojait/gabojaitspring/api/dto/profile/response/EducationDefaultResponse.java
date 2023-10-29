package com.gabojait.gabojaitspring.api.dto.profile.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.domain.profile.Education;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@ToString
@ApiModel(value = "학력 기본 응답")
public class EducationDefaultResponse {

    @ApiModelProperty(position = 1, required = true, value = "학력 식별자")
    private Long educationId;

    @ApiModelProperty(position = 2, required = true, value = "학교명")
    private String institutionName;

    @ApiModelProperty(position = 3, required = true, value = "시작일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startedAt;

    @ApiModelProperty(position = 4, required = true, value = "종료일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endedAt;

    @ApiModelProperty(position = 5, required = true, value = "현재 여부", allowableValues = "true, false")
    private Boolean isCurrent;

    @ApiModelProperty(position = 6, required = true, value = "생성일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @ApiModelProperty(position = 7, required = true, value = "수정일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public EducationDefaultResponse(Education education) {
        this.educationId = education.getId();
        this.institutionName = education.getInstitutionName();
        this.startedAt = education.getStartedAt();
        this.endedAt = education.getEndedAt();
        this.isCurrent = education.getIsCurrent();
        this.createdAt = education.getCreatedAt();
        this.updatedAt = education.getUpdatedAt();
    }
}
