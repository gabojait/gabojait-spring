package com.gabojait.gabojaitspring.api.dto.review.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(value = "리뷰 다건 생성 요청")
public class ReviewCreateManyRequest {

    @ApiModelProperty(position = 1, required = true, value = "리뷰들")
    @Valid
    List<ReviewCreateOneRequest> reviews = new ArrayList<>();

    @Builder
    private ReviewCreateManyRequest(List<ReviewCreateOneRequest> reviews) {
        this.reviews = reviews;
    }
}
