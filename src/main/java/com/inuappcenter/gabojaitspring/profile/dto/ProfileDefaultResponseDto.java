package com.inuappcenter.gabojaitspring.profile.dto;

import com.inuappcenter.gabojaitspring.profile.domain.Education;
import com.inuappcenter.gabojaitspring.profile.domain.Profile;
import com.inuappcenter.gabojaitspring.profile.domain.Skill;
import com.inuappcenter.gabojaitspring.profile.domain.Work;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

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

    @ApiModelProperty(position = 4, required = true, value = "학력")
    private List<EducationDefaultResponseDto> educations;

    @ApiModelProperty(position = 5, required = true, value = "경력")
    private List<WorkDefaultResponseDto> works;

    @ApiModelProperty(position = 6, required = true, value = "기술")
    private  List<SkillDefaultResponseDto> skills;

    @ApiModelProperty(position = 7, required = true, value = "스키마버전")
    private String schemaVersion;

    public ProfileDefaultResponseDto(Profile profile) {
        this.profileId = profile.getId().toString();
        this.description = profile.getDescription();
        this.position = profile.getPosition();

        for (Education education : profile.getEducations())
            this.educations.add(new EducationDefaultResponseDto(education));

        for (Work work : profile.getWorks())
            this.works.add(new WorkDefaultResponseDto(work));

        for (Skill skill : profile.getSkills())
            this.skills.add(new SkillDefaultResponseDto(skill));

        this.schemaVersion = profile.getSchemaVersion();
    }
}
