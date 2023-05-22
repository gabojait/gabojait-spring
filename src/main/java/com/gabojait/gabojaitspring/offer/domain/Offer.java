package com.gabojait.gabojaitspring.offer.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.offer.domain.type.OfferedBy;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
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

    @Field(name = "user_id")
    private ObjectId userId;

    @Field(name = "team_id")
    private ObjectId teamId;

    @Field(name = "is_accepted")
    private Boolean isAccepted;

    @Field(name = "offered_by")
    private Character offeredBy;

    private Character position;

    @Builder
    public Offer(ObjectId userId, ObjectId teamId, OfferedBy offeredBy, Position position) {
        this.userId = userId;
        this.teamId = teamId;
        this.offeredBy = offeredBy.getType();
        this.position = position.getType();

        this.isDeleted = false;
    }

    public void accept() {
        this.isAccepted = true;

        this.isDeleted = true;
    }

    public void decline() {
        this.isAccepted = true;

        this.isDeleted = true;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
