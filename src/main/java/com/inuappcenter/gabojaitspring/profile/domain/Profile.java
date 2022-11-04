package com.inuappcenter.gabojaitspring.profile.domain;

import com.inuappcenter.gabojaitspring.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Getter
@Document(collection = "profile")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile extends BaseTimeEntity {

    @Field(name = "user_id")
    private String userId;

    private String about;

    private Character position; // D = Designer, B = Backend Engineer, F = Frontend Engineer, P = Product Manager

    private List<Education> education = new ArrayList<>();

    private List<Work> work = new ArrayList<>();

    private List<Skill> skill = new ArrayList<>();

    private List<String> review = new ArrayList<>();

    @Builder(builderClassName = "ByProfileBuilder", builderMethodName = "ByProfileBuilder")
    public Profile(String userId, String about, Character position) {
        this.userId = userId;
        this.about = about;
        this.position = position;
    }

    public void addEducation(Education education) {
        this.education.add(education);
    }

    public void removeEducation(Education education) {
        this.education.remove(education);
    }

    public void addWork(Work work) {
        this.work.add(work);
    }

    public void removeWork(Work work) {
        this.work.remove(work);
    }

    public void addSkill(Skill skill) {
        this.skill.add(skill);
    }

    public void removeSkill(Skill skill) {
        this.skill.remove(skill);
    }

    public void addReview(String review) {
        this.review.add(review);
    }

    public void removeReview(String review) {
        this.review.remove(review);
    }

    public void update(String about, Character position) {
        this.about = about;
        this.position = position;
    }
}
