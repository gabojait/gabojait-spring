package com.gabojait.gabojaitspring.api.dto.profile.response;

import com.gabojait.gabojaitspring.api.dto.offer.response.OfferAbstractResponse;
import com.gabojait.gabojaitspring.domain.offer.Offer;
import com.gabojait.gabojaitspring.domain.profile.Skill;
import com.gabojait.gabojaitspring.domain.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@ApiModel(value = "프로필 제안 응답")
public class ProfileOfferResponse extends ProfileAbstractResponse {

    @ApiModelProperty(position = 7, required = true, value = "제안들")
    private List<OfferAbstractResponse> offers;

    public ProfileOfferResponse(User user, List<Skill> skills, List<Offer> offers) {
        super(user, skills);

        this.offers = offers.stream()
                .map(OfferAbstractResponse::new)
                .collect(Collectors.toList());
    }
}
