package com.gabojait.gabojaitspring.profile.dto.res;

import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.util.List;

@Getter
@ApiModel(value = "프로필 상세 응답")
public class ProfileDetailResDto extends ProfileDefaultResDto {

    @ApiModelProperty(position = 18, required = true, value = "찜 여부", allowableValues = "true, false, null")
    private Boolean isFavorite;

    public ProfileDetailResDto(User user, List<Team> completedTeams, Boolean isFavorite) {
        super(user, completedTeams);

        this.isFavorite = isFavorite;
    }
}