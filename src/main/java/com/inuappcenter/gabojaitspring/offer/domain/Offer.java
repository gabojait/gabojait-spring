package com.inuappcenter.gabojaitspring.offer.domain;

import com.inuappcenter.gabojaitspring.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Document(collection = "offer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Offer extends BaseTimeEntity {

    @Field(name = "applicant_id")
    private ObjectId applicantId;

    @Field(name = "team_id")
    private ObjectId teamId;

    @Field(name = "is_accepted")
    private Boolean isAccepted;

    @Field(name = "is_by_applicant")
    private Boolean isByApplicant;

    private Character position;

    @Builder
    public Offer(ObjectId applicantId, ObjectId teamId, char position, boolean isByApplicant) {
        this.applicantId = applicantId;
        this.teamId = teamId;
        this.isByApplicant = isByApplicant;
        this.position = position;
        this.isDeleted = false;
    }

    public void accept() {
        this.isAccepted = true;
        this.isDeleted = true;
    }

    public void decline() {
        this.isAccepted = false;
        this.isDeleted = true;
    }
}
