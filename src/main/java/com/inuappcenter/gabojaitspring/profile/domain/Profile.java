package com.inuappcenter.gabojaitspring.profile.domain;

import com.inuappcenter.gabojaitspring.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Getter
@Document
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile extends BaseTimeEntity {

    @Field(name = "user_id")
    private ObjectId userId;

    private String description;

    private Character position;

    private List<Education> educations = new ArrayList<>();

    private List<Work> works = new ArrayList<>();

    private List<Skill> skills = new ArrayList<>();

    private List<ObjectId> reviews = new ArrayList<>();

    @Builder
    public Profile(String description, Position position) {
        this.description = description;
        this.position = position.getType();
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public void updateProfile(String description, Position position) {
        this.description = description;
        this.position = position.getType();
    }

    public void addEducation(Education education) {
        this.educations.add(education);
    }

    public void removeEducation(Education education) {
        this.educations.remove(education);
    }

    public void addSkill(Skill skill) {
        this.skills.add(skill);
    }

    public void removeSkill(Skill skill) {
        this.skills.remove(skill);
    }

    public void addWork(Work work) {
        this.works.add(work);
    }

    public void removeWork(Work work) {
        this.works.remove(work);
    }
}
