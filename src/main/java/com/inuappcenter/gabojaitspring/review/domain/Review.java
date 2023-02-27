package com.inuappcenter.gabojaitspring.review.domain;

import com.inuappcenter.gabojaitspring.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Document(collection = "review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    @Field(name = "reviewer_user_id")
    private ObjectId reviewerUserId;

    @Field(name = "reviewee_user_id")
    private ObjectId revieweeUserId;

    @Field(name = "team_id")
    private ObjectId teamId;

    private Question question;
    private Byte rate;
    private String answer;

    @Builder
    public Review(ObjectId reviewerUserId,
                  ObjectId revieweeUserId,
                  ObjectId teamId,
                  Question question,
                  Byte rate,
                  String answer) {
        this.reviewerUserId = reviewerUserId;
        this.revieweeUserId = revieweeUserId;
        this.teamId = teamId;
        this.question = question;
        this.rate = rate;
        this.answer = answer;

        this.isDeleted = false;
    }
}
