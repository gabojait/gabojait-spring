package com.inuappcenter.gabojaitspring.profile.dto.res;

import com.inuappcenter.gabojaitspring.profile.domain.Level;
import com.inuappcenter.gabojaitspring.profile.domain.Skill;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(value = "Skill 기본 응답")
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
            this.level = Level.toEnum(skill.getLevel()).name();
            this.schemaVersion = skill.getSchemaVersion();
        }
    }