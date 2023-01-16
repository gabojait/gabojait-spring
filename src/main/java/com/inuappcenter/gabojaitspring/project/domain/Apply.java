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
@Document(collection = "apply")
@NoArgsConstructor
public class Apply extends BaseTimeEntity {

    @Field(name = "is_accepted")
    private Boolean isAccepted;

    private Profile applicant;
    private ObjectId projectId;
    private Character position;

    @Builder
    public Apply(Profile applicant, ObjectId projectId, Position position) {
        this.applicant = applicant;
        this.projectId = projectId;
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
