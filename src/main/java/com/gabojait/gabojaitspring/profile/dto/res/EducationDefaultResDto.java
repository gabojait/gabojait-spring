package com.gabojait.gabojaitspring.profile.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.profile.domain.Education;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
@ApiModel(value = "학력 기본 응답")
public class EducationDefaultResDto {

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

    @ApiModelProperty(position = 5, required = true, value = "현재 여부")
    private Boolean isCurrent;

    @ApiModelProperty(position = 6, required = true, value = "스키마 버전")
    private String schemaVersion;

    public EducationDefaultResDto(Education education) {
        this.educationId = education.getId().toString();
        this.institutionName = education.getInstitutionName();
        this.startedDate = education.getStartedDate();
        this.endedDate = education.getEndedDate();
        this.isCurrent = education.getIsCurrent();
        this.schemaVersion = education.getSchemaVersion();
    }
}
