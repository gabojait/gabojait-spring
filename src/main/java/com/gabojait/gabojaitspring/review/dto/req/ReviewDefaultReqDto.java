package com.gabojait.gabojaitspring.review.dto.req;

import com.gabojait.gabojaitspring.common.util.validator.ValidationSequence;
import com.gabojait.gabojaitspring.review.domain.Review;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;

import javax.validation.GroupSequence;
import javax.validation.constraints.*;

@Getter
@ToString
@NoArgsConstructor
@GroupSequence({ReviewDefaultReqDto.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class})
@ApiModel(value = "리뷰 기본 요청")
public class ReviewDefaultReqDto {

    @ApiModelProperty(position = 1, required = true, value = "리뷰 대상자 식별자")
    @NotBlank(message = "리뷰 대상자 식별자는 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    private String revieweeId;

    @ApiModelProperty(position = 1, required = true, value = "평점", example = "1", allowableValues = "1, 2, 3, 4, 5")
    @NotNull(message = "평점은 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    @Min(value = 1, message = "평점은 1부터 5까지의 수만 가능합니다.", groups = ValidationSequence.Format.class)
    @Max(value = 5, message = "평점은 1부터 5까지의 수만 가능합니다.", groups = ValidationSequence.Format.class)
    private Integer rate;

    @ApiModelProperty(position = 3, required = true, value = "후기", example = "열정적으로 임하는 팀원이였습니다.")
    @NotBlank(message = "후기는 필수 입력란입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 1, max = 200, message = "후기 1~200자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String postscript;

    public Review toEntity(ObjectId reviewerId, ObjectId revieweeId, ObjectId teamId) {
        return Review.builder()
                .reviewerId(reviewerId)
                .revieweeId(revieweeId)
                .teamId(teamId)
                .rate(this.rate)
                .postscript(this.postscript)
                .build();
    }
}
