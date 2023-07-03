package com.gabojait.gabojaitspring.profile.dto.res;

import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.offer.dto.res.OfferAbstractResDto;
import com.gabojait.gabojaitspring.user.domain.User;
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
@ApiModel(value = "프로필 제안 응답")
public class ProfileSeekResDto extends ProfileAbstractResDto {

    @ApiModelProperty(position = 7, required = true, value = "현재 제안들")
    private List<OfferAbstractResDto> offers = new ArrayList<>();

    public ProfileSeekResDto(User user, List<Offer> offers) {
        super(user);

        for (Offer offer : offers)
            this.offers.add(new OfferAbstractResDto(offer));
    }
}
