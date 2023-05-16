package com.gabojait.gabojaitspring.review.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.review.domain.Review;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@ApiModel(value = "리뷰 기본 응답")
public class ReviewDefaultResDto {

    @ApiModelProperty(position = 1, required = true, value = "리뷰 식별자")
    private String reviewId;

    @ApiModelProperty(position = 2, required = true, value = "리뷰 작성자 식별자")
    private String reviewerId;

    @ApiModelProperty(position = 3, required = true, value = "리뷰 대상자 식별자")
    private String revieweeId;

    @ApiModelProperty(position = 4, required = true, value = "평점")
    private Byte rating;

    @ApiModelProperty(position = 5, required = true, value = "글")
    private String writing;

    @ApiModelProperty(position = 6, required = true, value = "생성일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @ApiModelProperty(position = 7, required = true, value = "스키마 버전")
    private String schemaVersion;

    public ReviewDefaultResDto(Review review) {
        this.reviewId = review.getId().toString();
        this.reviewerId = review.getReviewerId().toString();
        this.revieweeId = review.getRevieweeId().toString();
        this.rating = review.getRate();
        this.writing = review.getAnswer();
        this.createdDate = review.getCreatedDate();
        this.schemaVersion = review.getSchemaVersion();
    }
}
