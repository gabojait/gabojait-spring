package com.inuappcenter.gabojaitspring.team.dto.req;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@GroupSequence({OfferUpdateReqDto.class, ValidationSequence.NotNull.class})
@ApiModel(value = "Offer 수정 요청")
public class OfferUpdateReqDto {

    @ApiModelProperty(position = 1, required = true, value = "수락 여부", example = "true",
            allowableValues = "true, false")
    @NotNull(message = "수락 여부를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    private Boolean isAccepted;
}
