package com.gabojait.gabojaitspring.profile.dto;

import com.gabojait.gabojaitspring.profile.domain.Education;
import com.gabojait.gabojaitspring.profile.domain.Portfolio;
import com.gabojait.gabojaitspring.profile.domain.Skill;
import com.gabojait.gabojaitspring.profile.domain.Work;
import com.gabojait.gabojaitspring.profile.dto.res.EducationDefaultResDto;
import com.gabojait.gabojaitspring.profile.dto.res.PortfolioDefaultResDto;
import com.gabojait.gabojaitspring.profile.dto.res.SkillDefaultResDto;
import com.gabojait.gabojaitspring.profile.dto.res.WorkDefaultResDto;
import com.gabojait.gabojaitspring.team.domain.TeamMember;
import com.gabojait.gabojaitspring.team.dto.res.TeamAbstractResDto;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@ToString
public class ProfileInfoDto {

    private final List<SkillDefaultResDto> skills = new ArrayList<>();
    private final List<EducationDefaultResDto> educations = new ArrayList<>();
    private final List<WorkDefaultResDto> works = new ArrayList<>();
    private final List<PortfolioDefaultResDto> portfolios = new ArrayList<>();
    private final List<TeamAbstractResDto> completedTeams = new ArrayList<>();
    private TeamAbstractResDto currentTeam;
    private Boolean isLeader;

    @Builder
    public ProfileInfoDto(List<Skill> skills,
                          List<Education> educations,
                          List<Work> works,
                          List<Portfolio> portfolios,
                          List<TeamMember> teamMembers) {
        skills.forEach(s -> this.skills.add(new SkillDefaultResDto(s)));
        educations.forEach(e -> this.educations.add(new EducationDefaultResDto(e)));
        works.forEach(w -> this.works.add(new WorkDefaultResDto(w)));
        portfolios.forEach(p -> this.portfolios.add(new PortfolioDefaultResDto(p)));
        teamMembers.forEach(teamMember -> {
            if (teamMember.getTeam().getIsDeleted()) {
                this.completedTeams.add(new TeamAbstractResDto(teamMember.getTeam()));
            } else {
                this.isLeader = teamMember.getIsLeader();
                this.currentTeam = new TeamAbstractResDto(teamMember.getTeam());
            }
        });
    }
}
