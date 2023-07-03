package com.gabojait.gabojaitspring.profile.dto.res;

import com.gabojait.gabojaitspring.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@ApiModel(value = "프로필 찜 응답")
public class ProfileFavoriteResDto extends ProfileDefaultResDto {

    @ApiModelProperty(position = 19, required = true, value = "찜 여부", allowableValues = "true, false, null")
    private Boolean isFavorite;

    public ProfileFavoriteResDto(User user, Boolean isFavorite) {
        super(user);

        this.isFavorite = isFavorite;
    }
}