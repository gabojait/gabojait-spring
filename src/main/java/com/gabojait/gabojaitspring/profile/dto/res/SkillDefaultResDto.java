package com.gabojait.gabojaitspring.profile.dto.res;

import com.gabojait.gabojaitspring.profile.domain.Skill;
import com.gabojait.gabojaitspring.profile.domain.type.Level;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(value = "기술 기본 응답")
public class SkillDefaultResDto {

    @ApiModelProperty(position = 1, required = true, value = "기술 식별자")
    private String skillId;

    @ApiModelProperty(position = 2, required = true, value = "기술명")
    private String skillName;

    @ApiModelProperty(position = 3, required = true, value = "경험여부")
    private Boolean isExperienced;

    @ApiModelProperty(position = 4, required = true, value = "레벨")
    private String level;

    @ApiModelProperty(position = 5, required = true, value = "스키마 버전")
    private String schemaVersion;

    public SkillDefaultResDto(Skill skill) {
        this.skillId = skill.getId().toString();
        this.skillName = skill.getSkillName();
        this.isExperienced = skill.getIsExperienced();
        this.level = Level.fromChar(skill.getLevel()).name().toLowerCase();
        this.schemaVersion = skill.getSchemaVersion();
    }
}
