package com.gabojait.gabojaitspring.profile.dto;

import com.gabojait.gabojaitspring.profile.domain.Work;
import com.gabojait.gabojaitspring.profile.dto.req.WorkDefaultReqDto;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
public class WorkUpdateDto {

    private Work prevWork;
    private String corporationName;
    private String workDescription;
    private LocalDate startedAt;
    private LocalDate endedAt;
    private Boolean isCurrent;

    public WorkUpdateDto(Work prevWork, WorkDefaultReqDto newWork) {
        this.prevWork = prevWork;
        this.corporationName = newWork.getCorporationName().trim();
        this.workDescription = newWork.getWorkDescription().trim();
        this.startedAt = newWork.getStartedAt();
        this.endedAt = newWork.getEndedAt();
        this.isCurrent = newWork.getIsCurrent();
    }
}
