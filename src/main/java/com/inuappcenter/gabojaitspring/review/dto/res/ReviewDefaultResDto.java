package com.inuappcenter.gabojaitspring.review.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.inuappcenter.gabojaitspring.review.domain.Review;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@ApiModel(value = "Review 기본 응답")
public class ReviewDefaultResDto {

    @ApiModelProperty(position = 1, required = true, value = "리뷰 식별자")
    private String reviewId;

    @ApiModelProperty(position = 2, required = true, value = "리뷰 작성자 식별자")
    private String reviewerUserId;

    @ApiModelProperty(position = 3, required = true, value = "리뷰 대상자 식별자")
    private String revieweeUserId;

    @ApiModelProperty(position = 4, required = true, value = "질문")
    private String question;

    @ApiModelProperty(position = 5, required = true, value = "답변")
    private String answer;

    @ApiModelProperty(position = 6, required = true, value = "생성일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @ApiModelProperty(position = 7, required = true, value = "스키마 버전")
    private String schemaVersion;

    public ReviewDefaultResDto(Review review) {
        this.reviewId = review.getId().toString();
        this.reviewerUserId = review.getReviewerUserId().toString();
        this.revieweeUserId = review.getRevieweeUserId().toString();
        this.question = review.getQuestion().getContext();
        this.answer = review.getAnswer();
        this.createdDate = review.getCreatedDate();
        this.schemaVersion = review.getSchemaVersion();
    }
}
