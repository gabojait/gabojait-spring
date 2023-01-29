package com.inuappcenter.gabojaitspring.profile.domain;

import com.inuappcenter.gabojaitspring.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Document(collection = "skill")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Skill extends BaseTimeEntity {

    @Field(name = "user_id")
    private ObjectId userId;

    @Field(name = "skill_name")
    private String skillName;

    @Field(name = "is_experienced")
    private Boolean isExperienced;

    private Byte level;

    @Builder
    public Skill(ObjectId userId, String skillName, boolean isExperienced, Level level) {
        this.userId = userId;
        this.skillName = skillName;
        this.isExperienced = isExperienced;
        this.level = level.getType();
        this.isDeleted = false;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void update(String skillName, boolean isExperienced, Level level) {
        this.skillName = skillName;
        this.isExperienced = isExperienced;
        this.level = level.getType();
    }
}
