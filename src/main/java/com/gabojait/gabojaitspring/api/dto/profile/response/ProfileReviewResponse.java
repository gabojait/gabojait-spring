package com.gabojait.gabojaitspring.api.dto.profile.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabojait.gabojaitspring.domain.review.Review;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@ApiModel(value = "프로필 리뷰 응답")
public class ProfileReviewResponse {

    @ApiModelProperty(position = 1, required = true, value = "리뷰 식별자")
    private Long reviewId;

    @ApiModelProperty(position = 2, required = true, value = "리뷰 작성자")
    private String reviewer;

    @ApiModelProperty(position = 3, required = true, value = "평점")
    private Byte rating;

    @ApiModelProperty(position = 4, required = true, value = "후기 글")
    private String post;

    @ApiModelProperty(position = 5, required = true, value = "생성일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @ApiModelProperty(position = 6, required = true, value = "수정일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public ProfileReviewResponse(Review review, int reviewerNum) {
        this.reviewId = review.getId();
        this.reviewer = "익명" + reviewerNum;
        this.rating = review.getRating();
        this.post = review.getPost();
        this.createdAt = review.getCreatedAt();
        this.updatedAt = review.getUpdatedAt();
    }
}
