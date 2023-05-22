package com.gabojait.gabojaitspring.review.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
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

    @Field(name = "reviewer_id")
    private ObjectId reviewerId;

    @Field(name = "reviewee_id")
    private ObjectId revieweeId;

    @Field(name = "team_id")
    private ObjectId teamId;

    private Integer rate;
    private String postscript;

    @Builder
    public Review(ObjectId reviewerId,
                  ObjectId revieweeId,
                  ObjectId teamId,
                  Integer rate,
                  String postscript) {
        this.reviewerId = reviewerId;
        this.revieweeId = revieweeId;
        this.teamId = teamId;
        this.rate = rate;
        this.postscript = postscript;

        this.isDeleted = false;
    }
}
