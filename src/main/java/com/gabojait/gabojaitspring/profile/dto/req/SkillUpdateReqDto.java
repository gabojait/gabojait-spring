package com.gabojait.gabojaitspring.profile.dto.req;

import com.gabojait.gabojaitspring.exception.ValidationSequence;
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
@GroupSequence({SkillUpdateReqDto.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class})
@ApiModel(value = "기술 수정 요청")
public class SkillUpdateReqDto {

    @ApiModelProperty(position = 1, required = true, value = "기술 식별자")
    @NotBlank(message = "기술 식별자를 입력해 주세요.", groups = ValidationSequence.Blank.class)
    private String skillId;

    @ApiModelProperty(position = 2, required = true, value = "기술명", example = "스프링")
    @NotBlank(message = "기술명을 입력해 주세요.", groups = ValidationSequence.Blank.class)
    @Size(min = 1, max = 20, message = "기술명은 1~20자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String skillName;

    @ApiModelProperty(position = 3, required = true, value = "경험 여부", example = "true",
            allowableValues = "true, false")
    @NotNull(message = "경험 여부를 입력해 주세요.", groups = ValidationSequence.Blank.class)
    private Boolean isExperienced;

    @ApiModelProperty(position = 4, required = true, value = "레벨", example = "MID", allowableValues = "LOW, MID, HIGH")
    @NotBlank(message = "레벨을 입력해 주세요.", groups = ValidationSequence.Blank.class)
    @Pattern(regexp = "^(LOW|MID|HIGH)", message = "레벨은 'LOW', 'MID', 또는 'HIGH' 중 하나여야 됩니다.",
            groups = ValidationSequence.Format.class)
    private String level;
}