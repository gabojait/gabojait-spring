package com.gabojait.gabojaitspring.team.dto.res;

import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.offer.dto.res.OfferAbstractResDto;
import com.gabojait.gabojaitspring.team.domain.Team;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@ApiModel(value = "팀 제안과 찜 포함 응답")
public class TeamOfferAndFavoriteResDto extends TeamDefaultResDto {

    @ApiModelProperty(position = 11, required = true, value = "제안들")
    private List<OfferAbstractResDto> offers = new ArrayList<>();
    @ApiModelProperty(position = 12, required = true, value = "찜 여부")
    private Boolean isFavorite;

    public TeamOfferAndFavoriteResDto(Team team, List<Offer> offers, Boolean isFavorite) {
        super(team);

        for (Offer offer : offers)
            this.offers.add(new OfferAbstractResDto(offer));

        this.isFavorite = isFavorite;
    }
}
