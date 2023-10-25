package com.gabojait.gabojaitspring.api.dto.profile.response;

import com.gabojait.gabojaitspring.api.dto.offer.response.OfferAbstractResponse;
import com.gabojait.gabojaitspring.api.vo.profile.ProfileVO;
import com.gabojait.gabojaitspring.domain.offer.Offer;
import com.gabojait.gabojaitspring.domain.profile.Education;
import com.gabojait.gabojaitspring.domain.profile.Portfolio;
import com.gabojait.gabojaitspring.domain.profile.Skill;
import com.gabojait.gabojaitspring.domain.profile.Work;
import com.gabojait.gabojaitspring.domain.review.Review;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
import com.gabojait.gabojaitspring.domain.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@ApiModel(value = "상세 프로필 응답")
public class ProfileDetailResponse extends ProfileDefaultResponse {

    @ApiModelProperty(position = 19, required = true, value = "제안들")
    private List<OfferAbstractResponse> offers;

    @ApiModelProperty(position = 20, required = true, value = "찜 여부", allowableValues = "true, false, null")
    private Boolean isFavorite;


    public ProfileDetailResponse(User user,
                                 List<Skill> skills,
                                 ProfileVO profile,
                                 List<Offer> offers,
                                 Boolean isFavorite) {
        super(user, skills, profile);
        this.isFavorite = isFavorite;

        this.offers = offers.stream()
                .map(OfferAbstractResponse::new)
                .collect(Collectors.toList());
    }
}
