package com.gabojait.gabojaitspring.review.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.GroupSequence;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@GroupSequence({ReviewCreateReqDto.class})
@ApiModel(value = "리뷰 생성 요청")
public class ReviewCreateReqDto {

    @ApiModelProperty(position = 1, required = true, value = "리뷰들")
    @Valid
    List<ReviewDefaultReqDto> reviews = new ArrayList<>();

    @Builder
    private ReviewCreateReqDto(List<ReviewDefaultReqDto> reviews) {
        this.reviews = reviews;
    }
}