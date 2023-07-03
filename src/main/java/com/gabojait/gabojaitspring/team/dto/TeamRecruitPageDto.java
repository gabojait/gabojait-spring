package com.gabojait.gabojaitspring.team.dto;

import com.gabojait.gabojaitspring.team.dto.res.TeamRecruitResDto;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class TeamRecruitPageDto {

    private final List<TeamRecruitResDto> teamOfferResDtos;
    private final int totalPages;

    public TeamRecruitPageDto(List<TeamRecruitResDto> teamOfferResDtos, int totalPages) {
        this.teamOfferResDtos = teamOfferResDtos;
        this.totalPages = totalPages;
    }
}
