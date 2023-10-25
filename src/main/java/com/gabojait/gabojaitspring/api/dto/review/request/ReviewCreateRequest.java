package com.gabojait.gabojaitspring.api.dto.review.request;

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
@GroupSequence({ReviewCreateRequest.class})
@ApiModel(value = "리뷰 생성 요청")
public class ReviewCreateRequest {

    @ApiModelProperty(position = 1, required = true, value = "리뷰들")
    @Valid
    List<ReviewDefaultRequest> reviews = new ArrayList<>();

    @Builder
    private ReviewCreateRequest(List<ReviewDefaultRequest> reviews) {
        this.reviews = reviews;
    }
}
