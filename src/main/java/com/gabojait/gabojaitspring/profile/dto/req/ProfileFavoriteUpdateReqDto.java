package com.gabojait.gabojaitspring.profile.dto.req;

import com.gabojait.gabojaitspring.exception.ValidationSequence;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@GroupSequence({ProfileFavoriteUpdateReqDto.class, ValidationSequence.Blank.class})
@ApiModel(value = "회원의 팀 찜 업데이트 요청")
public class ProfileFavoriteUpdateReqDto {

    @ApiModelProperty(position = 1, required = true, value = "찜 추가 여부", example = "true",
            allowableValues = "true, false")
    @NotNull(message = "찜 추가 여부를 입력해 주세요.", groups = ValidationSequence.Blank.class)
    private Boolean isAddFavorite;
}
