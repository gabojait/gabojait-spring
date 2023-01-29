package com.inuappcenter.gabojaitspring.review.domain;

import com.inuappcenter.gabojaitspring.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "review_question")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewQuestion extends BaseTimeEntity {
}
