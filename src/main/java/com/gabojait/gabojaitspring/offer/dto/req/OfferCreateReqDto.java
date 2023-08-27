package com.gabojait.gabojaitspring.offer.dto.req;

import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.offer.domain.type.OfferedBy;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@GroupSequence({OfferCreateReqDto.class, ValidationSequence.Blank.class, ValidationSequence.Format.class})
@ApiModel(value = "제안 기본 요청")
public class OfferCreateReqDto {

    @ApiModelProperty(position = 1, required = true, value = "포지션", example = "FRONTEND",
            allowableValues = "DESIGNER, BACKEND, FRONTEND, MANAGER")
    @NotBlank(message = "포지션은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Pattern(regexp = "^(DESIGNER|BACKEND|FRONTEND|MANAGER)",
            message = "포지션은 'DESIGNER', 'BACKEND', 'FRONTEND', 또는 'MANAGER' 중 하나여야 됩니다.",
            groups = ValidationSequence.Format.class)
    private String position;

    public Offer toEntity(User user, Team team, OfferedBy offeredBy) {
        return Offer.builder()
                .user(user)
                .team(team)
                .offeredBy(offeredBy)
                .position(Position.valueOf(this.position))
                .build();
    }

    @Builder
    private OfferCreateReqDto(String position) {
        this.position = position;
    }
}
