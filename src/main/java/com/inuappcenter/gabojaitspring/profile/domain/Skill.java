package com.inuappcenter.gabojaitspring.profile.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Getter
@Document(collection = "skill")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Skill {

    @Field(name = "skill_name")
    private String skillName;

    private Byte level;

    @Field(name = "is_experienced")
    private Boolean isExperienced;

    @Field(name = "started_date")
    private LocalDate startedDate;

    @Field(name = "ended_date")
    private LocalDate endedDate;

    @Field(name = "is_current")
    private Boolean isCurrent;

    @Field(name = "user_id")
    private String userId;

    @Field(name = "is_deleted")
    private Boolean isDeleted;

    @Builder(builderClassName = "BySkillBuilder", builderMethodName = "BySkillBuilder")
    public Skill(String skillName,
                 Byte level,
                 Boolean isExperienced,
                 LocalDate startedDate,
                 LocalDate endedDate,
                 Boolean isCurrent) {
        this.skillName = skillName;
        this.level = level;
        this.isExperienced = isExperienced;
        this.startedDate = startedDate;
        this.endedDate = endedDate;
        this.isCurrent = isCurrent;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void update(String skillName,
                       Byte level,
                       Boolean isExperienced,
                       LocalDate startedDate,
                       LocalDate endedDate,
                       Boolean isCurrent) {
        this.skillName = skillName;
        this.level = level;
        this.isExperienced = isExperienced;
        this.startedDate = startedDate;
        this.endedDate = endedDate;
        this.isCurrent = isCurrent;
    }
}
