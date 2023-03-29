package com.inuappcenter.gabojaitspring.profile.dto.res;

import com.inuappcenter.gabojaitspring.team.domain.Team;
import com.inuappcenter.gabojaitspring.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.util.List;

@Getter
@ApiModel(value = "User 프로필 상세 응답")
public class UserProfileDetailResDto extends UserProfileDefaultResDto {

    @ApiModelProperty(position = 18, required = true, value = "찜 여부")
    private Boolean isFavorite;

    public UserProfileDetailResDto(User user, List<Team> completedTeams, boolean isFavorite) {
        super(user, completedTeams);

        this.isFavorite = isFavorite;
    }
}
