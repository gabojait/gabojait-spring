package com.inuappcenter.gabojaitspring.profile.domain;

import com.inuappcenter.gabojaitspring.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Getter
@Document(collection = "skill")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Skill extends BaseTimeEntity {

    @Field(name = "skill_name")
    private String skillName;

    @Field(name = "is_experienced")
    private Boolean isExperienced;

    @Field(name = "profile_id")
    private ObjectId profileId;

    private Byte level;

    @Builder
    public Skill(String skillName,
                 Boolean isExperienced,
                 Level level,
                 ObjectId profileId) {
        this.skillName = skillName;
        this.isExperienced = isExperienced;
        this.profileId = profileId;
        this.level = level.getType();
        this.isDeleted = false;
    }

    public void deleteSkill() {
        this.isDeleted = true;
    }

    public void updateSkill(String skillName,
                            Boolean isExperienced,
                            Level level) {
        this.skillName = skillName;
        this.isExperienced = isExperienced;
        this.level = level.getType();
    }
}
