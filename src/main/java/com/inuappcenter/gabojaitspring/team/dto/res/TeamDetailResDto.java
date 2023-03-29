package com.inuappcenter.gabojaitspring.team.dto.res;

import com.inuappcenter.gabojaitspring.team.domain.Team;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(value = "Team 상세 응답")
public class TeamDetailResDto extends TeamDefaultResDto {

    @ApiModelProperty(position = 16, required = true, value = "찜 여부")
    private Boolean isFavorite;

    public TeamDetailResDto(Team team, boolean isFavorite) {
        super(team);

        this.isFavorite = isFavorite;
    }
}
