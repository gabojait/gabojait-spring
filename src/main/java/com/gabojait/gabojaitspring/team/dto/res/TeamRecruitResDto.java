package com.gabojait.gabojaitspring.team.dto.res;

import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.offer.dto.res.OfferAbstractResDto;
import com.gabojait.gabojaitspring.team.domain.Team;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@ApiModel(value = "팀 제안 응답")
public class TeamRecruitResDto extends TeamAbstractResDto {

    @ApiModelProperty(position = 7, required = true, value = "제안들")
    private List<OfferAbstractResDto> offers = new ArrayList<>();

    public TeamRecruitResDto(Team team, List<Offer> offers) {
        super(team);

        for (Offer offer : offers)
            this.offers.add(new OfferAbstractResDto(offer));
    }
}
