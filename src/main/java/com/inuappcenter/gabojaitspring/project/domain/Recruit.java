package com.inuappcenter.gabojaitspring.project.domain;

import com.inuappcenter.gabojaitspring.common.BaseTimeEntity;
import com.inuappcenter.gabojaitspring.profile.domain.Position;
import com.inuappcenter.gabojaitspring.profile.domain.Profile;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Document(collection = "offer")
@NoArgsConstructor
public class Recruit extends BaseTimeEntity {

    @Field(name = "is_accepted")
    private Boolean isAccepted;

    private ObjectId projectId;
    private Profile user;
    private Character position;

    @Builder
    public Recruit(ObjectId projectId, Profile user, Position position) {
        this.projectId = projectId;
        this.user = user;
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
