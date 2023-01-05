package com.inuappcenter.gabojaitspring.profile.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.inuappcenter.gabojaitspring.profile.domain.Education;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@ApiModel(value = "Education 응답")
public class EducationDefaultResponseDto {

    @ApiModelProperty(position = 1, required = true, value = "학력 식별자")
    private String educationId;

    @ApiModelProperty(position = 2, required = true, value = "학교명")
    private String institutionName;

    @ApiModelProperty(position = 3, required = true, value = "시작일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startedDate;

    @ApiModelProperty(position = 4, required = true, value = "종료일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endedDate;

    @ApiModelProperty(position = 5, required = true, value = "현재여부")
    private Boolean isCurrent;

    @ApiModelProperty(position = 6, required = true, value = "스키마버전")
    private String schemaVersion;

    public EducationDefaultResponseDto(Education education) {
        this.educationId = education.getId().toString();
        this.institutionName = education.getInstitutionName();
        this.startedDate = education.getStartedDate();
        this.endedDate = education.getEndedDate();
        this.isCurrent = education.getIsCurrent();
        this.schemaVersion = education.getSchemaVersion();
    }
}
