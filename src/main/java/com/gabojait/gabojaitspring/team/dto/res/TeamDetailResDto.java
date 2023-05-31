package com.gabojait.gabojaitspring.team.dto.res;

import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@ToString
@ApiModel(value = "팀 상세 응답")
public class TeamDetailResDto extends TeamDefaultResDto {

    @ApiModelProperty(position = 20, required = true, value = "찜 여부")
    private Boolean isFavorite;

    public TeamDetailResDto(Team team,
                            Map<Character, List<User>> teamMembers,
                            Boolean isFavorite) {
        super(team, teamMembers);

        this.isFavorite = isFavorite;
    }
}
