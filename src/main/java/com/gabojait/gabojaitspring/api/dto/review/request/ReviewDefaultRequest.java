package com.gabojait.gabojaitspring.api.dto.review.request;

import com.gabojait.gabojaitspring.api.dto.common.ValidationSequence;
import com.gabojait.gabojaitspring.domain.review.Review;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.GroupSequence;
import javax.validation.constraints.*;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@GroupSequence({ReviewDefaultRequest.class,
        ValidationSequence.Blank.class,
        ValidationSequence.Size.class,
        ValidationSequence.Format.class})
@ApiModel(value = "리뷰 기본 요청")
public class ReviewDefaultRequest {

    @ApiModelProperty(position = 1, required = true, value = "팀원 식별자")
    @NotNull(message = "팀원 식별자는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Positive(message = "팀원 식별자는 양수만 가능합니다.", groups = ValidationSequence.Format.class)
    private Long teamMemberId;

    @ApiModelProperty(position = 1, required = true, value = "평점", example = "1", allowableValues = "1, 2, 3, 4, 5")
    @NotNull(message = "평점은 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Min(value = 1, message = "평점은 1부터 5까지의 수만 가능합니다.", groups = ValidationSequence.Format.class)
    @Max(value = 5, message = "평점은 1부터 5까지의 수만 가능합니다.", groups = ValidationSequence.Format.class)
    private Byte rating;

    @ApiModelProperty(position = 3, required = true, value = "후기", example = "열정적으로 임하는 팀원이였습니다.")
    @NotBlank(message = "후기는 필수 입력입니다.", groups = ValidationSequence.Blank.class)
    @Size(min = 1, max = 200, message = "후기는 1~200자만 가능합니다.", groups = ValidationSequence.Size.class)
    private String post;

    public Review toEntity(TeamMember reviewer, TeamMember reviewee) {
        return Review.builder()
                .reviewer(reviewer)
                .reviewee(reviewee)
                .rating(this.rating)
                .post(this.post)
                .build();
    }

    @Builder
    private ReviewDefaultRequest(Long teamMemberId, Byte rating, String post) {
        this.teamMemberId = teamMemberId;
        this.rating = rating;
        this.post = post;
    }
}
