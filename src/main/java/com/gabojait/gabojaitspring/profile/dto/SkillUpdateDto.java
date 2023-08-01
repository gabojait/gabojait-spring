package com.gabojait.gabojaitspring.profile.dto;

import com.gabojait.gabojaitspring.profile.domain.Skill;
import com.gabojait.gabojaitspring.profile.domain.type.Level;
import com.gabojait.gabojaitspring.profile.dto.req.SkillDefaultReqDto;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SkillUpdateDto {

    private Skill prevSkill;
    private String skillName;
    private Boolean isExperienced;
    private Level level;

    public SkillUpdateDto(Skill prevSkill, SkillDefaultReqDto newSkill) {
        this.prevSkill = prevSkill;
        this.skillName = newSkill.getSkillName().trim();
        this.isExperienced = newSkill.getIsExperienced();
        this.level = Level.valueOf(newSkill.getLevel());
    }
}
