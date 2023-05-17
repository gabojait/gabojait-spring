package com.gabojait.gabojaitspring.team.dto.req;

import com.gabojait.gabojaitspring.exception.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@GroupSequence({TeamFavoriteUpdateReqDto.class, ValidationSequence.Blank.class})
@ApiModel(value = "팀의 회원 찜 업데이트 요청")
public class TeamFavoriteUpdateReqDto {

    @ApiModelProperty(position = 1, required = true, value = "찜 추가 여부", example = "true",
            allowableValues = "true, false")
    @NotNull(message = "찜 추가 여부는 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    Boolean isAddFavorite;
}