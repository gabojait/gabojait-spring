package com.gabojait.gabojaitspring.api.dto.offer.response;

import com.gabojait.gabojaitspring.api.dto.profile.response.ProfileAbstractResponse;
import com.gabojait.gabojaitspring.api.dto.team.response.TeamAbstractResponse;
import com.gabojait.gabojaitspring.domain.offer.Offer;
import com.gabojait.gabojaitspring.domain.profile.Skill;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@ApiModel(value = "제안 기본 응답")
public class OfferDefaultResponse extends OfferAbstractResponse {

    @ApiModelProperty(position = 7, required = true, value = "회원")
    private ProfileAbstractResponse user;

    @ApiModelProperty(position = 8, required = true, value = "팀")
    private TeamAbstractResponse team;

    public OfferDefaultResponse(Offer offer, List<Skill> skills) {
        super(offer);

        this.user = new ProfileAbstractResponse(offer.getUser(), skills);
        this.team = new TeamAbstractResponse(offer.getTeam());
    }
}
