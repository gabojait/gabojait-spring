package com.inuappcenter.gabojaitspring.profile.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@GroupSequence({SkillUpdateRequestDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.NotNull.class,
        ValidationSequence.Size.class,
        ValidationSequence.Pattern.class})
@ApiModel(value = "Skill 수정 요청")
public class SkillUpdateRequestDto {

    @ApiModelProperty(position = 1, required = true, value = "기술 식별자", allowableValues = "Restriction: [NotBlank]")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    private String skillId;

    @ApiModelProperty(position = 2, required = true, value = "기술명", example = "스프링",
            allowableValues = "Restriction: [NotBlank > Size]")
    @NotBlank(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 1, max = 20, message = "기술명은 1~20자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String skillName;

    @ApiModelProperty(position = 3, required = true, value = "경험 여부: true, false", example = "true",
            allowableValues = "Input: [true | false], Restriction: [NotNull]")
    @NotNull(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    @JsonFormat(shape = JsonFormat.Shape.BOOLEAN)
    private Boolean isExperienced;

    @ApiModelProperty(position = 4, required = true, value = "레벨: 1, 2, 3", example = "3",
            allowableValues = "Input: [1 | 2 | 3], Restriction: [NotNull > Pattern]")
    @NotNull(message = "모든 필수 정보를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    @Pattern(regexp = "^[1-3]+$]", message = "레벨은 1, 2, 3 중 하나입니다.", groups = ValidationSequence.Pattern.class)
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Integer level;
}
