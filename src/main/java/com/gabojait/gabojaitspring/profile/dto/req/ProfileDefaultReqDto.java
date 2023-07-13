package com.gabojait.gabojaitspring.profile.dto.req;

import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.GroupSequence;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@GroupSequence({
        ProfileDefaultReqDto.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class
})
@ApiModel(value = "프로필 기본 요청")
public class ProfileDefaultReqDto {

    @ApiModelProperty(position = 1, required = true, value = "포지션", example = "none",
            allowableValues = "designer, backend, frontend, manager, none")
    @NotBlank(message = "포지션은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Pattern(regexp = "^(designer|backend|frontend|manager|none)",
            message = "포지션은 'designer', 'backend', 'frontend', 'manager', 또는 'none' 중 하나여야 됩니다.",
            groups = ValidationSequence.Format.class)
    private String position;

    @ApiModelProperty(position = 2, value = "기술들")
    @Valid
    private List<SkillDefaultReqDto> skills = new ArrayList<>();

    @ApiModelProperty(position = 3, value = "학력들")
    @Valid
    private List<EducationDefaultReqDto> educations = new ArrayList<>();

    @ApiModelProperty(position = 4, value = "경력들")
    @Valid
    private List<WorkDefaultReqDto> works = new ArrayList<>();

    @ApiModelProperty(position = 5, value = "포트폴리오들")
    @Valid
    private List<PortfolioDefaultReqDto> portfolios = new ArrayList<>();
}
