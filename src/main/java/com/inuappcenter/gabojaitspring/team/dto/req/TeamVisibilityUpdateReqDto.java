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
@GroupSequence({TeamVisibilityUpdateReqDto.class,
        ValidationSequence.NotNull.class})
@ApiModel(value = "Team 공개여부 수정 요청")
public class TeamVisibilityUpdateReqDto {

    @ApiModelProperty(position = 1, required = true, value = "공개 여부", example = "true",
            allowableValues = "true, false")
    @NotNull(message = "공개 여부를 입력해주세요.", groups = ValidationSequence.NotNull.class)
    private Boolean isPublic;
}
