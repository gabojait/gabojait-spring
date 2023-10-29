package com.gabojait.gabojaitspring.api.dto.favorite.response;

import com.gabojait.gabojaitspring.api.dto.profile.response.ProfileAbstractResponse;
import com.gabojait.gabojaitspring.domain.favorite.Favorite;
import com.gabojait.gabojaitspring.domain.profile.Skill;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@ApiModel(value = "찜한 회원 응답")
public class FavoriteUserResponse extends ProfileAbstractResponse {

    @ApiModelProperty(position = 8, required = true, value = "찜 식별자")
    private Long favoriteId;

    public FavoriteUserResponse(Favorite favorite, List<Skill> skills) {
        super(favorite.getFavoriteUser(), skills);

        this.favoriteId = favorite.getId();
    }
}
