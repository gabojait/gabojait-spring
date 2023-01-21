package com.inuappcenter.gabojaitspring.review.domain;

import com.inuappcenter.gabojaitspring.common.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Document(collection = "review")
@NoArgsConstructor
public class Review extends BaseTimeEntity {

    @Field(name = "user_profile_id")
    private ObjectId userProfileId;

    @Field(name = "project_id")
    private ObjectId projectId;

    private String question;

    private Byte rating;

    @Builder
    public Review(ObjectId userProfileId, ObjectId projectId, String question, Rating rating) {
        this.userProfileId = userProfileId;
        this.projectId = projectId;
        this.question = question;
        this.rating = rating.getType();
        this.isDeleted = false;
    }
}
