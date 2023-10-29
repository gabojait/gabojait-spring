package com.gabojait.gabojaitspring.api.dto.favorite.response;

import com.gabojait.gabojaitspring.api.dto.team.response.TeamAbstractResponse;
import com.gabojait.gabojaitspring.domain.favorite.Favorite;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@ApiModel(value = "찜한 팀 응답")
public class FavoriteTeamResponse extends TeamAbstractResponse {

    @ApiModelProperty(position = 13, required = true, value = "찜 식별자")
    private Long favoriteId;

    public FavoriteTeamResponse(Favorite favorite) {
        super(favorite.getFavoriteTeam());

        this.favoriteId = favorite.getId();
    }
}
