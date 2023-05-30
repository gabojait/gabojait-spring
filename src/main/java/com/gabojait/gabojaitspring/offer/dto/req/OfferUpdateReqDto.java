package com.gabojait.gabojaitspring.offer.dto.req;

import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;

@Getter
@ToString
@NoArgsConstructor
@GroupSequence({OfferUpdateReqDto.class, ValidationSequence.Blank.class})
@ApiModel(value = "제안 업데이트 요청")
public class OfferUpdateReqDto {

    @ApiModelProperty(position = 1, required = true, value = "수락 여부", example = "true", allowableValues = "true, false")
    @NotNull(message = "수락 여부는 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    private Boolean isAccepted;
}
