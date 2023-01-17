package com.inuappcenter.gabojaitspring.project.domain;

import com.inuappcenter.gabojaitspring.common.BaseTimeEntity;
import com.inuappcenter.gabojaitspring.profile.domain.Position;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Document(collection = "recruit")
@NoArgsConstructor
public class Recruit extends BaseTimeEntity {

    @Field(name = "project_id")
    private ObjectId projectId;

    @Field(name = "user_profile_id")
    private ObjectId userProfileId;

    @Field(name = "is_accepted")
    private Boolean isAccepted;

    private Character position;

    @Builder
    public Recruit(ObjectId projectId, ObjectId userProfileId, Position position) {
        this.projectId = projectId;
        this.userProfileId = userProfileId;
        this.position = position.getType();
        this.isAccepted = null;
        this.isDeleted = false;
    }

    public void accept() {
        this.isAccepted = true;
    }

    public void decline() {
        this.isAccepted = false;
    }
}
