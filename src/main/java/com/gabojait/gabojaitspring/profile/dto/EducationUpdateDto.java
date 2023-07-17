package com.gabojait.gabojaitspring.profile.dto;

import com.gabojait.gabojaitspring.profile.domain.Education;
import com.gabojait.gabojaitspring.profile.dto.req.EducationDefaultReqDto;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
public class EducationUpdateDto {

    private Education prevEducation;
    private String institutionName;
    private LocalDate startedAt;
    private LocalDate endedAt;
    private Boolean isCurrent;


    public EducationUpdateDto(Education prevEducation, EducationDefaultReqDto newEducation) {
        this.prevEducation = prevEducation;
        this.institutionName = newEducation.getInstitutionName().trim();
        this.startedAt = newEducation.getStartedAt();
        this.endedAt = newEducation.getEndedAt();
        this.isCurrent = newEducation.getIsCurrent();
    }

}
