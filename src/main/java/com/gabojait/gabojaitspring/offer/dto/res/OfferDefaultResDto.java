package com.gabojait.gabojaitspring.offer.dto.res;

import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.profile.dto.res.ProfileAbstractResDto;
import com.gabojait.gabojaitspring.team.dto.res.TeamAbstractResDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@ApiModel(value = "제안 기본 응답")
public class OfferDefaultResDto extends OfferAbstractResDto {

    @ApiModelProperty(position = 7, required = true, value = "회원")
    private ProfileAbstractResDto user;

    @ApiModelProperty(position = 8, required = true, value = "팀")
    private TeamAbstractResDto team;

    public OfferDefaultResDto(Offer offer) {
        super(offer);

        this.user = new ProfileAbstractResDto(offer.getUser());
        this.team = new TeamAbstractResDto(offer.getTeam());
    }
}
