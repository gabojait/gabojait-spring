package com.inuappcenter.gabojaitspring.user.dto.req;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@GroupSequence({UserTeamFavoriteDefaultReqDto.class, ValidationSequence.NotNull.class})
@ApiModel(value = "User 팀 찜하기 기본 요청")
public class UserTeamFavoriteDefaultReqDto {

    @ApiModelProperty(position = 1, required = true, value = "찜하기 여부", example = "true",
            allowableValues = "true, false")
    @NotNull(message = "찜하기 여부를 입력해 주세요.", groups = ValidationSequence.NotNull.class)
    private Boolean isAdd;
}
