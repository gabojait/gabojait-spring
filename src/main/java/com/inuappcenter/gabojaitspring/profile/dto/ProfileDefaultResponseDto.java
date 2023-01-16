package com.inuappcenter.gabojaitspring.profile.dto;

import com.inuappcenter.gabojaitspring.profile.domain.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@ApiModel(value = "Profile 응답")
public class ProfileDefaultResponseDto {

    @ApiModelProperty(position = 1, required = true, value = "프로필 식발자")
    private String profileId;

    @ApiModelProperty(position = 2, required = true, value = "자기소개")
    private String description;

    @ApiModelProperty(position = 3, required = true, value = "포지션")
    private Character position;

    @ApiModelProperty(position = 4, required = true, value = "프로필 사진")
    private String imageUrl;

    @ApiModelProperty(position = 5, required = true, value = "평점")
    private Float rating;

    @ApiModelProperty(position = 6, required = true, value = "학력")
    private List<EducationDefaultResponseDto> educations = new ArrayList<>();

    @ApiModelProperty(position = 7, required = true, value = "경력")
    private List<WorkDefaultResponseDto> works = new ArrayList<>();

    @ApiModelProperty(position = 8, required = true, value = "기술")
    private  List<SkillDefaultResponseDto> skills = new ArrayList<>();

    @ApiModelProperty(position = 9, required = true, value = "포트폴리오")
    private List<PortfolioDefaultResponseDto> portfolios = new ArrayList<>();

    @ApiModelProperty(position = 10, required = true, value = "스키마버전")
    private String schemaVersion;

    public ProfileDefaultResponseDto(Profile profile) {
        this.profileId = profile.getId().toString();
        this.description = profile.getDescription();
        this.position = profile.getPosition();
        this.imageUrl = profile.getImageUrl();
        this.rating = profile.getRating();

        for (Education education : profile.getEducations())
            if (!education.getIsDeleted())
                this.educations.add(new EducationDefaultResponseDto(education));

        for (Work work : profile.getWorks())
            if (!work.getIsDeleted())
                this.works.add(new WorkDefaultResponseDto(work));

        for (Skill skill : profile.getSkills())
            if (!skill.getIsDeleted())
                this.skills.add(new SkillDefaultResponseDto(skill));

        for (Portfolio portfolio: profile.getPortfolios())
            if (!portfolio.getIsDeleted())
                this.portfolios.add(new PortfolioDefaultResponseDto(portfolio));

        this.schemaVersion = profile.getSchemaVersion();
    }
}
