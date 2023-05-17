package com.gabojait.gabojaitspring.profile.dto.req;

import com.gabojait.gabojaitspring.exception.ValidationSequence;
import com.gabojait.gabojaitspring.profile.domain.Skill;
import com.gabojait.gabojaitspring.profile.domain.type.Level;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@GroupSequence({SkillCreateReqDto.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class})
@ApiModel(value = "기술 생성 요청")
public class SkillCreateReqDto {

    @ApiModelProperty(position = 1, required = true, value = "기술명", example = "스프링")
    @NotBlank(message = "기술명은 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 1, max = 20, message = "기술명은 1~20자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String skillName;

    @ApiModelProperty(position = 2, required = true, value = "경험 여부", example = "true",
            allowableValues = "true, false")
    @NotNull(message = "경험 여부는 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    private Boolean isExperienced;

    @ApiModelProperty(position = 3, required = true, value = "레벨", example = "low", allowableValues = "low, mid, high")
    @NotBlank(message = "레벨은 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    @Pattern(regexp = "^(low|mid|high)", message = "레벨은 'low', 'mid', 또는 'high' 중 하나여야 됩니다.",
            groups = ValidationSequence.Format.class)
    private String level;

    public Skill toEntity(ObjectId userId) {
        return Skill.builder()
                .userId(userId)
                .skillName(this.skillName)
                .isExperienced(this.isExperienced)
                .level(Level.fromString(this.level))
                .build();
    }
}
