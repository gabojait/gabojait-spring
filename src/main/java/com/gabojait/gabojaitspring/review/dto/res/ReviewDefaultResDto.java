package com.gabojait.gabojaitspring.review.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.review.domain.Review;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@ApiModel(value = "리뷰 기본 응답")
public class ReviewDefaultResDto {

    @ApiModelProperty(position = 1, required = true, value = "리뷰 식별자")
    private Long reviewId;

    @ApiModelProperty(position = 2, required = true, value = "리뷰 작성자 식별자")
    private Long reviewerId;

    @ApiModelProperty(position = 3, required = true, value = "리뷰 대상자 식별자")
    private Long revieweeId;

    @ApiModelProperty(position = 4, required = true, value = "팀 식별자")
    private Long teamId;

    @ApiModelProperty(position = 5, required = true, value = "평점")
    private Byte rating;

    @ApiModelProperty(position = 6, required = true, value = "후기 글")
    private String post;

    @ApiModelProperty(position = 7, required = true, value = "생성일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @ApiModelProperty(position = 8, required = true, value = "수정일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public ReviewDefaultResDto(Review review) {
        this.reviewId = review.getId();
        this.reviewerId = review.getReviewer().getId();
        this.revieweeId = review.getReviewee().getId();
        this.teamId = review.getTeam().getId();
        this.rating = review.getRate();
        this.post = review.getPost();
        this.createdAt = review.getCreatedAt();
        this.updatedAt = review.getUpdatedAt();
    }
}
