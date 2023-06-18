package com.gabojait.gabojaitspring.favorite.dto.req;

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
@GroupSequence({FavoriteUpdateReqDto.class, ValidationSequence.Blank.class})
@ApiModel(value = "찜 업데이트 요청")
public class FavoriteUpdateReqDto {

    @ApiModelProperty(position = 1, required = true, value = "찜 추가 여부", example = "true",
            allowableValues = "true, false")
    @NotNull(message = "찜 추가 여부는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    private Boolean isAddFavorite;
}
