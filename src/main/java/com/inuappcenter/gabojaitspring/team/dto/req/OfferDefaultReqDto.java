package com.inuappcenter.gabojaitspring.team.dto.req;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import com.inuappcenter.gabojaitspring.team.domain.Offer;
import com.inuappcenter.gabojaitspring.team.domain.Team;
import com.inuappcenter.gabojaitspring.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@GroupSequence({OfferDefaultReqDto.class, ValidationSequence.NotBlank.class})
@ApiModel(value = "Offer 기본 요청")
public class OfferDefaultReqDto {

    @ApiModelProperty(position = 1, required = true, value = "포지션", example = "backend",
            allowableValues = "designer, backend, frontend, pm")
    @NotBlank(message = "포지션을 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    private String position;

    public Offer userOfferToEntity(User user, Team team) {
        return Offer.builder()
                .applicant(user)
                .team(team)
                .isByApplicant(true)
                .build();
    }

    public Offer teamOfferToEntity(User user, Team team) {
        return Offer.builder()
                .team(team)
                .applicant(user)
                .isByApplicant(false)
                .build();
    }
}
