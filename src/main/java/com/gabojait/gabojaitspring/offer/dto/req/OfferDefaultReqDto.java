package com.gabojait.gabojaitspring.offer.dto.req;

import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
@GroupSequence({OfferDefaultReqDto.class, ValidationSequence.Blank.class})
@ApiModel(value = "제안 기본 요청")
public class OfferDefaultReqDto {

    @ApiModelProperty(position = 1, required = true, value = "제안할 포지션", example = "frontend",
            allowableValues = "designer, backend, frontend, manager")
    @NotBlank(message = "제안할 포지션은 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    @Pattern(regexp = "^(designer|backend|frontend|manager)", message = "제안할 포지션")
    private String offerPosition;
}
