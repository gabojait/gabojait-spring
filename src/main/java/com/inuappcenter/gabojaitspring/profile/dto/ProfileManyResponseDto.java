package com.inuappcenter.gabojaitspring.profile.dto;

import com.inuappcenter.gabojaitspring.profile.domain.Profile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@ApiModel(value = "Profile 다중 응답")
public class ProfileManyResponseDto {

    @ApiModelProperty(position = 1, required = true, value = "프로필")
    private List<ProfileDefaultResponseDto> profiles = new ArrayList<>();

    public ProfileManyResponseDto(List<Profile> profiles) {
        for (Profile profile : profiles)
            this.profiles.add(new ProfileDefaultResponseDto(profile));
    }
}
