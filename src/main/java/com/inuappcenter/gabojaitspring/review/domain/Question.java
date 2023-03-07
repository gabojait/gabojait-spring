package com.inuappcenter.gabojaitspring.review.domain;

import com.inuappcenter.gabojaitspring.common.BaseTimeEntity;
import com.inuappcenter.gabojaitspring.review.domain.type.ReviewType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Document(collection = "question")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question extends BaseTimeEntity {

    @Field(name = "used_cnt")
    private Long usedCnt;

    @Field(name = "review_type")
    private Character reviewType;

    private String context;

    @Builder
    public Question(String context, ReviewType reviewType) {
        this.context = context;
        this.reviewType = reviewType.getType();
        this.usedCnt = 0L;

        this.isDeleted = false;
    }

    public void incrementUsedCnt() {
        this.usedCnt++;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
