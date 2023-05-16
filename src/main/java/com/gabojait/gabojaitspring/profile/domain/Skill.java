package com.gabojait.gabojaitspring.profile.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.profile.domain.type.Level;
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
