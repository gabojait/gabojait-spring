package com.gabojait.gabojaitspring.profile.dto.res;

import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.offer.dto.res.OfferAbstractResDto;
import com.gabojait.gabojaitspring.profile.domain.Education;
import com.gabojait.gabojaitspring.profile.domain.Portfolio;
import com.gabojait.gabojaitspring.profile.domain.Skill;
import com.gabojait.gabojaitspring.profile.domain.Work;
import com.gabojait.gabojaitspring.profile.dto.ProfileInfoDto;
import com.gabojait.gabojaitspring.team.domain.TeamMember;
import com.gabojait.gabojaitspring.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@ApiModel(value = "프로필 제안과 찜 포함 응답")
public class ProfileOfferAndFavoriteResDto extends ProfileDefaultResDto {

    @ApiModelProperty(position = 19, required = true, value = "제안들")
    private List<OfferAbstractResDto> offers = new ArrayList<>();

    @ApiModelProperty(position = 20, required = true, value = "찜 여부", allowableValues = "true, false, null")
    private Boolean isFavorite;

    public ProfileOfferAndFavoriteResDto(User user,
                                         ProfileInfoDto profileInfo,
                                         List<Offer> offers,
                                         Boolean isFavorite) {
        super(user, profileInfo);

        for (Offer offer : offers)
            this.offers.add(new OfferAbstractResDto(offer));

        this.isFavorite = isFavorite;
    }
}
