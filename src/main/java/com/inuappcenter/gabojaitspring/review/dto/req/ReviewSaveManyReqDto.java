package com.inuappcenter.gabojaitspring.review.dto.req;

import com.inuappcenter.gabojaitspring.review.domain.Question;
import com.inuappcenter.gabojaitspring.review.domain.Review;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import javax.validation.GroupSequence;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@GroupSequence({ReviewSaveManyReqDto.class})
@ApiModel(value = "Review 다건 생성 요청")
public class ReviewSaveManyReqDto {

    @ApiModelProperty(position = 1, required = true, value = "리뷰")
    List<ReviewSaveOneReqDto> reviews = new ArrayList<>();
}