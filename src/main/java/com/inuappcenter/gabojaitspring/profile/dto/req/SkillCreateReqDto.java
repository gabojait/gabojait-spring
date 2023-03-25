package com.inuappcenter.gabojaitspring.profile.dto.req;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import com.inuappcenter.gabojaitspring.profile.domain.type.Level;
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
@GroupSequence({SkillCreateReqDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.NotNull.class,
        ValidationSequence.Size.class,})
@ApiModel(value = "Skill 생성 요청")
public class SkillCreateReqDto {

    @ApiModelProperty(position = 1, required = true, value = "기술명", example = "스프링")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 1, max = 20, message = "기술명은 1~20자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String skillName;

    @ApiModelProperty(position = 2, required = true, value = "경험 여부", example = "true",
            allowableValues = "true, false")
    @NotNull(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    private Boolean isExperienced;

    @ApiModelProperty(position = 3, required = true, value = "레벨", example = "high", allowableValues = "low, mid, high")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    private String level;

    @Builder
    public Skill toEntity(ObjectId userId, Level level) {
        return Skill.builder()
                .userId(userId)
                .skillName(this.skillName)
                .isExperienced(this.isExperienced)
                .level(level)
                .build();
    }
}
