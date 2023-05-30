package com.gabojait.gabojaitspring.profile.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.profile.domain.type.Level;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@ToString
@Document(collection = "skill")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Skill extends BaseTimeEntity {

    @Field(name = "user_id")
    private ObjectId userId;

    @Field(name = "skill_name")
    private String skillName;

    @Field(name = "is_experienced")
    private Boolean isExperienced;

    private Character level;

    @Builder
    public Skill(ObjectId userId, String skillName, boolean isExperienced, Level level) {
        this.userId = userId;
        this.skillName = skillName;
        this.isExperienced = isExperienced;
        this.level = level.getType();
        this.isDeleted = false;
    }

    public void update(String skillName, boolean isExperienced, Level level) {
        this.skillName = skillName;
        this.isExperienced = isExperienced;
        this.level = level.getType();
    }

    public void delete() {
        this.isDeleted = true;
    }
}
