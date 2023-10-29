package com.gabojait.gabojaitspring.api.dto.profile.request;

import com.gabojait.gabojaitspring.api.dto.common.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.GroupSequence;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@GroupSequence({
        ProfileDefaultRequest.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class
})
@ApiModel(value = "프로필 기본 요청")
public class ProfileDefaultRequest {

    @ApiModelProperty(position = 1, required = true, value = "포지션", example = "NONE",
            allowableValues = "DESIGNER, BACKEND, FRONTEND, MANAGER, NONE")
    @NotBlank(message = "포지션은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Pattern(regexp = "^(DESIGNER|BACKEND|FRONTEND|MANAGER|NONE)",
            message = "포지션은 'DESIGNER', 'BACKEND', 'FRONTEND', 'MANAGER', 또는 'NONE' 중 하나여야 됩니다.",
            groups = ValidationSequence.Format.class)
    private String position;

    @ApiModelProperty(position = 2, value = "학력들")
    @Valid
    private List<EducationDefaultRequest> educations = new ArrayList<>();

    @ApiModelProperty(position = 3, value = "포트폴리오들")
    @Valid
    private List<PortfolioDefaultRequest> portfolios = new ArrayList<>();

    @ApiModelProperty(position = 4, value = "기술들")
    @Valid
    private List<SkillDefaultRequest> skills = new ArrayList<>();

    @ApiModelProperty(position = 5, value = "경력들")
    @Valid
    private List<WorkDefaultRequest> works = new ArrayList<>();

    @Builder
    private ProfileDefaultRequest(String position,
                                  List<EducationDefaultRequest> educations,
                                  List<PortfolioDefaultRequest> portfolios,
                                  List<SkillDefaultRequest> skills,
                                  List<WorkDefaultRequest> works) {
        this.position = position;
        this.educations = educations;
        this.portfolios = portfolios;
        this.skills = skills;
        this.works = works;
    }
}
