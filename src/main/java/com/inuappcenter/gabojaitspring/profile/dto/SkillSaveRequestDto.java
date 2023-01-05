package com.inuappcenter.gabojaitspring.profile.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import com.inuappcenter.gabojaitspring.profile.domain.Level;
import com.inuappcenter.gabojaitspring.profile.domain.Profile;
import com.inuappcenter.gabojaitspring.profile.domain.Skill;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@GroupSequence({EducationSaveRequestDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.NotNull.class,
        ValidationSequence.Size.class})
@ApiModel(value = "Skill 생성 요청")
public class SkillSaveRequestDto {

    @ApiModelProperty(position = 1, required = true, dataType = "String", value = "기술명", example = "스프링")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 1, max = 20, message = "기술명은 1~20자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String skillName;

    @ApiModelProperty(position = 2,
            required = true,
            dataType = "String",
            allowableValues = "true, false",
            value = "경험 여부: true, false",
            example = "true")
    @NotNull(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    @JsonFormat(shape = JsonFormat.Shape.BOOLEAN)
    private Boolean isExperienced;

    @ApiModelProperty(position = 2,
            required = true,
            dataType = "Integer",
            allowableValues = "1, 2, 3",
            value = "레벨: 1, 2, 3",
            example = "3")
    @NotNull(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Integer level;

    @Builder
    public Skill toEntity(ObjectId profileId, Level level) {
        return Skill.builder()
                .skillName(this.skillName)
                .isExperienced(this.isExperienced)
                .level(level)
                .profileId(profileId)
                .build();
    }
}
