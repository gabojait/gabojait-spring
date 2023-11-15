package com.gabojait.gabojaitspring.api.dto.team.response;

import com.gabojait.gabojaitspring.api.dto.offer.response.OfferAbstractResponse;
import com.gabojait.gabojaitspring.domain.offer.Offer;
import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@ApiModel(value = "팀과 제안 및 찜 응답")
public class TeamOfferFavoriteResponse extends TeamDefaultResponse {

    @ApiModelProperty(position = 10, required = true, value = "제안들")
    private List<OfferAbstractResponse> offers;

    @ApiModelProperty(position = 11, required = true, value = "찜 여부")
    private Boolean isFavorite;

    public TeamOfferFavoriteResponse(Team team, List<TeamMember> teamMembers, List<Offer> offers, boolean isFavorite) {
        super(team, teamMembers);

        this.offers = offers.stream()
                .map(OfferAbstractResponse::new)
                .collect(Collectors.toList());

        this.isFavorite = isFavorite;
    }
}
