package com.inuappcenter.gabojaitspring.profile.dto;

import com.inuappcenter.gabojaitspring.profile.domain.Education;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@ApiModel(value = "Education 응답")
public class EducationListResponseDto {

    @ApiModelProperty(position = 1, required = true, value = "학력 식별자")
    private String educationId;

    @ApiModelProperty(position = 2, required = true, value = "시작일")
    private LocalDate startedDate;

    @ApiModelProperty(position = 3, required = true, value = "종료일")
    private LocalDate endedDate;

    @ApiModelProperty(position = 4, required = true, value = "현재 진행 여부")
    private Boolean isCurrent;

    public EducationListResponseDto(Education education) {
        this.educationId = education.getId();
        this.startedDate = education.getStartedDate();
        this.endedDate = education.getEndedDate();
        this.isCurrent = education.getIsCurrent();
    }
}
