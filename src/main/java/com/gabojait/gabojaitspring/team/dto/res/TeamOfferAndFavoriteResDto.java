package com.gabojait.gabojaitspring.team.dto.res;

import com.gabojait.gabojaitspring.team.domain.Team;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@ApiModel(value = "팀 찜 응답")
public class TeamFavoriteResDto extends TeamDefaultResDto {

    @ApiModelProperty(position = 11, required = true, value = "찜 여부")
    private Boolean isFavorite;

    public TeamFavoriteResDto(Team team, Boolean isFavorite) {
        super(team);

        this.isFavorite = isFavorite;
    }
}
