package com.gabojait.gabojaitspring.profile.dto.req;

import com.gabojait.gabojaitspring.common.util.validator.ValidIfPresent;
import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.GroupSequence;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@ValidIfPresent
@GroupSequence({
        PositionAndSkillDefaultReqDto.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class
})
public class PositionAndSkillDefaultReqDto {

    @ApiModelProperty(position = 1, required = true, value = "포지션", example = "none",
            allowableValues = "designer, backend, frontend, manager, none")
    @NotBlank(message = "포지션은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Pattern(regexp = "^(designer|backend|frontend|manager|none)",
            message = "포지션은 'designer', 'backend', 'frontend', 'manager', 또는 'none' 중 하나여야 됩니다.",
            groups = ValidationSequence.Format.class)
    private String position;

    @ApiModelProperty(position = 2, value = "생성 기술들")
    @Valid
    private List<SkillCreateReqDto> createSkills;

    @ApiModelProperty(position = 3, value = "수정 기술들")
    @Valid
    private List<SkillUpdateReqDto> updateSkills;

    @ApiModelProperty(position = 4, access = "삭제 기술 식별자들")
    private List<Long> deleteSkillIds;
}
