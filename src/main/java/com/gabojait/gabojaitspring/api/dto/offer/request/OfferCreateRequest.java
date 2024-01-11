package com.gabojait.gabojaitspring.api.dto.offer.request;

import com.gabojait.gabojaitspring.domain.offer.Offer;
import com.gabojait.gabojaitspring.domain.offer.OfferedBy;
import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.domain.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Pattern;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(value = "제안 생성 요청")
public class OfferCreateRequest {

    @ApiModelProperty(position = 1, required = true, value = "포지션", example = "FRONTEND",
            allowableValues = "DESIGNER, BACKEND, FRONTEND, MANAGER")
    @Pattern(regexp = "^(DESIGNER|BACKEND|FRONTEND|MANAGER)",
            message = "제안할 포지션은 'DESIGNER', 'BACKEND', 'FRONTEND', 또는 'MANAGER' 중 하나여야 됩니다.")
    private String offerPosition;

    public Offer toEntity(User user, Team team, OfferedBy offeredBy) {
        return Offer.builder()
                .user(user)
                .team(team)
                .offeredBy(offeredBy)
                .position(Position.valueOf(this.offerPosition))
                .build();
    }

    @Builder
    private OfferCreateRequest(String position) {
        this.offerPosition = position;
    }
}
