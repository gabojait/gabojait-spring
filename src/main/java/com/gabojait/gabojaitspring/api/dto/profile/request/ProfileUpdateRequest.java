package com.gabojait.gabojaitspring.api.dto.profile.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(value = "프로필 업데이트 요청")
public class ProfileUpdateRequest {

    @ApiModelProperty(position = 1, required = true, value = "포지션", example = "NONE",
            allowableValues = "DESIGNER, BACKEND, FRONTEND, MANAGER, NONE")
    @Pattern(regexp = "^(DESIGNER|BACKEND|FRONTEND|MANAGER|NONE)",
            message = "포지션은 'DESIGNER', 'BACKEND', 'FRONTEND', 'MANAGER', 또는 'NONE' 중 하나여야 됩니다.")
    private String position;

    @ApiModelProperty(position = 2, value = "학력들")
    @Valid
    private List<EducationUpdateRequest> educations = new ArrayList<>();

    @ApiModelProperty(position = 3, value = "포트폴리오들")
    @Valid
    private List<PortfolioUpdateRequest> portfolios = new ArrayList<>();

    @ApiModelProperty(position = 4, value = "기술들")
    @Valid
    private List<SkillUpdateRequest> skills = new ArrayList<>();

    @ApiModelProperty(position = 5, value = "경력")
    @Valid
    private List<WorkUpdateRequest> works = new ArrayList<>();

    @Builder
    private ProfileUpdateRequest(String position,
                                 List<EducationUpdateRequest> educations,
                                 List<PortfolioUpdateRequest> portfolios,
                                 List<SkillUpdateRequest> skills,
                                 List<WorkUpdateRequest> works) {
        this.position = position;
        this.educations = educations;
        this.portfolios = portfolios;
        this.skills = skills;
        this.works = works;
    }
}
