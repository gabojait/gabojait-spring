package com.gabojait.gabojaitspring.offer.dto.req;

import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.offer.domain.type.OfferedBy;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
@GroupSequence({OfferDefaultReqDto.class, ValidationSequence.Blank.class, ValidationSequence.Format.class})
@ApiModel(value = "제안 기본 요청")
public class OfferDefaultReqDto {

    @ApiModelProperty(position = 1, required = true, value = "포지션", example = "frontend",
            allowableValues = "designer, backend, frontend, manager")
    @NotBlank(message = "포지션은 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    @Pattern(regexp = "^(designer|backend|frontend|manager)",
            message = "포지션은 'designer', 'backend', 'frontend', 'manager', 또는 'none' 중 하나여야 됩니다.",
            groups = ValidationSequence.Format.class)
    private String position;

    public Offer toEntity(ObjectId userId, ObjectId teamId, OfferedBy offeredBy) {
        return Offer.builder()
                .userId(userId)
                .teamId(teamId)
                .offeredBy(offeredBy)
                .position(Position.fromString(this.position))
                .build();
    }
}
