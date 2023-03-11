package com.inuappcenter.gabojaitspring.offer.dto.req;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import com.inuappcenter.gabojaitspring.profile.domain.type.Position;
import com.inuappcenter.gabojaitspring.offer.domain.Offer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@GroupSequence({OfferSaveReqDto.class, ValidationSequence.NotBlank.class})
@ApiModel(value = "Offer 생성 요청")
public class OfferSaveReqDto {

    @ApiModelProperty(position = 1, required = true, value = "포지션", example = "backend",
            allowableValues = "designer, backend, frontend, pm")
    @NotBlank(message = "포지션을 입력해주세요.", groups = ValidationSequence.NotBlank.class)
    private String position;

    public Offer userOfferToEntity(ObjectId applicantId, ObjectId teamId) {
        return Offer.builder()
                .applicantId(applicantId)
                .teamId(teamId)
                .position(Position.fromString(this.position).getType())
                .isByApplicant(true)
                .build();
    }

    public Offer teamOfferToEntity(ObjectId applicantId, ObjectId teamId) {
        return Offer.builder()
                .teamId(teamId)
                .applicantId(applicantId)
                .position(Position.fromString(this.position).getType())
                .isByApplicant(false)
                .build();
    }
}
