package com.gabojait.gabojaitspring.profile.dto.req;

import com.gabojait.gabojaitspring.exception.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@NoArgsConstructor
@GroupSequence({
        PositionAndSkillDefaultReqDto.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class
})
@ApiModel(value = "포지션과 기술 기본 요청")
public class PositionAndSkillDefaultReqDto {

    @ApiModelProperty(position = 1, required = true, value = "포지션", example = "NONE",
            allowableValues = "DESIGNER, BACKEND, FRONTEND, PM, NONE")
    @NotBlank(message = "포지션을 입력해 주세요.", groups = ValidationSequence.Blank.class)
    @Pattern(regexp = "^(DESIGNER|BACKEND|FRONTEND|PM|NONE)",
            message = "포지션은 'DESIGNER', 'BACKEND', 'FRONTEND', 'PM', 또는 'NONE' 중 하나여야 됩니다.",
            groups = ValidationSequence.Format.class)
    private String position;

    @ApiModelProperty(position = 2, value = "생성 기술들")
    private List<SkillCreateReqDto> createSkills;

    @ApiModelProperty(position = 3, value = "수정 기술들")
    private List<SkillUpdateReqDto> updateSkills;

    @ApiModelProperty(position = 4, access = "삭제 기술 식별자들")
    private List<String> deleteSkillIds;
}
