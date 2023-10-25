package com.gabojait.gabojaitspring.api.dto.offer.request;

import com.gabojait.gabojaitspring.api.dto.common.ValidationSequence;
import com.gabojait.gabojaitspring.domain.offer.Offer;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@GroupSequence({OfferDecideRequest.class, ValidationSequence.Blank.class})
@ApiModel(value = "제안 결정 요청")
public class OfferDecideRequest {

    @ApiModelProperty(position = 1, required = true, value = "수락 여부", example = "true", allowableValues = "true, false")
    @NotNull(message = "수락 여부는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    private Boolean isAccepted;

    public TeamMember toTeamMemberEntity(Offer offer) {
        return TeamMember.builder()
                .position(offer.getPosition())
                .isLeader(false)
                .user(offer.getUser())
                .team(offer.getTeam())
                .build();
    }

    @Builder
    private OfferDecideRequest(Boolean isAccepted) {
        this.isAccepted = isAccepted;
    }
}
