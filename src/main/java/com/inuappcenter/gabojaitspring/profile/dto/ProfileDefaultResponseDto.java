package com.inuappcenter.gabojaitspring.profile.dto;

import com.inuappcenter.gabojaitspring.profile.domain.Profile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@ApiModel(value = "Profile 응답")
public class ProfileDefaultResponseDto {

    @ApiModelProperty(position = 1, required = true, value = "프로필 식별자")
    private String profileId;

    @ApiModelProperty(position = 2, required = true, value = "소개글")
    private String about;

    @ApiModelProperty(position = 3, required = true, value = "포지션")
    private Character position;

    @ApiModelProperty(position = 4, required = true, value = "학력 리스트")
    private List<EducationListResponseDto> educationList = new ArrayList<>();

    public ProfileDefaultResponseDto(Profile profile, List<EducationListResponseDto> educationList) {
        this.profileId = profile.getId();
        this.about = profile.getAbout();
        this.position = profile.getPosition();
        this.educationList = educationList;
    }
}
