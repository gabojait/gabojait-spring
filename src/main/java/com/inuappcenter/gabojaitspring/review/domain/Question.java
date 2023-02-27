package com.inuappcenter.gabojaitspring.review.domain;

import com.inuappcenter.gabojaitspring.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "question")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question extends BaseTimeEntity {

    private String context;

    @Builder
    public Question(String context) {
        this.context = context;

        this.isDeleted = false;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
