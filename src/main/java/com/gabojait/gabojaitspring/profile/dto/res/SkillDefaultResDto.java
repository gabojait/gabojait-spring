package com.gabojait.gabojaitspring.profile.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.profile.domain.Skill;
import com.gabojait.gabojaitspring.profile.domain.type.Level;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@ApiModel(value = "기술 기본 응답")
public class SkillDefaultResDto {

    @ApiModelProperty(position = 1, required = true, value = "기술 식별자")
    private Long skillId;

    @ApiModelProperty(position = 2, required = true, value = "기술명")
    private String skillName;

    @ApiModelProperty(position = 3, required = true, value = "경험여부", allowableValues = "true, false")
    private Boolean isExperienced;

    @ApiModelProperty(position = 4, required = true, value = "레벨", allowableValues = "LOW, MID, HIGH")
    private Level level;

    @ApiModelProperty(position = 5, required = true, value = "생성일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @ApiModelProperty(position = 6, required = true, value = "수정일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public SkillDefaultResDto(Skill skill) {
        this.skillId = skill.getId();
        this.skillName = skill.getSkillName();
        this.isExperienced = skill.getIsExperienced();
        this.level = skill.getLevel();
        this.createdAt = skill.getCreatedAt();
        this.updatedAt = skill.getUpdatedAt();
    }
}
