package com.inuappcenter.gabojaitspring.review.dto.req;

import com.inuappcenter.gabojaitspring.common.ValidationSequence;
import com.inuappcenter.gabojaitspring.review.domain.Question;
import com.inuappcenter.gabojaitspring.review.domain.Review;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@GroupSequence({ReviewSaveOneReqDto.class, ValidationSequence.NotBlank.class, ValidationSequence.NotNull.class})
@ApiModel(value = "Review 단건 생성 요청")
public class ReviewSaveOneReqDto {

    @ApiModelProperty(position = 1, required = true, value = "질문 식별자들")
    @NotBlank(message = "모든 필수 정보를 입력해 주세요.", groups = ValidationSequence.NotBlank.class)
    private String questionId;

    @ApiModelProperty(position = 2, required = true, value = "평점")
    private Byte rate;

    @ApiModelProperty(position = 3, required = true, value = "응답")
    @Size(max = 200, message = "리뷰 응답은 0~200자만 가능합니다.")
    private String answer;

    public Review toEntity(ObjectId reviewerUserId, ObjectId revieweeUserId, ObjectId teamId, Question question) {
        return Review.builder()
                .reviewerUserId(reviewerUserId)
                .revieweeUserId(revieweeUserId)
                .teamId(teamId)
                .question(question)
                .rate(this.rate)
                .answer(this.answer)
                .build();
    }
}
