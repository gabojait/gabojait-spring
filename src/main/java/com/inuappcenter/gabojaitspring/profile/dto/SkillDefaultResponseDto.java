package com.inuappcenter.gabojaitspring.profile.dto;

import com.inuappcenter.gabojaitspring.profile.domain.Skill;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@ApiModel(value = "Skill 응답")
public class SkillDefaultResponseDto {

    @ApiModelProperty(position = 1, required = true, value = "기술 식별자")
    private String skillId;

    @ApiModelProperty(position = 2, required = true, value = "기술명")
    private String skillName;

    @ApiModelProperty(position = 3, required = true, value = "경험여부")
    private Boolean isExperienced;

    @ApiModelProperty(position = 4, required = true, value = "레벨")
    private Byte level;

    @ApiModelProperty(position = 5, required = true, value = "스키마버전")
    private String schemaVersion;

    public SkillDefaultResponseDto(Skill skill) {
        this.skillId = skill.getId().toString();
        this.skillName = skill.getSkillName();
        this.isExperienced = skill.getIsExperienced();
        this.level = skill.getLevel();
        this.schemaVersion = skill.getSchemaVersion();
    }
}
