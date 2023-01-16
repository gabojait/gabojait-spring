package com.inuappcenter.gabojaitspring.profile.domain;

import com.inuappcenter.gabojaitspring.common.BaseTimeEntity;
import com.inuappcenter.gabojaitspring.project.domain.Project;
import com.inuappcenter.gabojaitspring.user.domain.User;
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

    @Field(name = "image_url")
    private String imageUrl;

    @Field(name = "current_project")
    private Project currentProject;

    @Field(name = "project_ids")
    private List<ObjectId> projectIds = new ArrayList<>();

    private String nickname;
    private String description;
    private Character position;
    private List<Education> educations = new ArrayList<>();
    private List<Work> works = new ArrayList<>();
    private List<Skill> skills = new ArrayList<>();
    private List<Portfolio> portfolios = new ArrayList<>();

    private List<ObjectId> reviews = new ArrayList<>();
    private Float rating;

    @Builder
    public Profile(String description, Position position) {
        this.description = description;
        this.position = position.getType();
        this.imageUrl = null;
        this.currentProject = null;
        this.isDeleted = false;
        this.rating = 0F;
    }

    public void setUser(User user) {
        this.userId = user.getId();
        this.nickname = user.getNickname();
    }

    public void updateProfile(String description, Position position) {
        this.description = description;
        this.position = position.getType();
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void startProject(Project project) {
        this.currentProject = project;
        this.projectIds.add(project.getId());
    }

    public void endProject() {
        this.currentProject = null;
    }

    public void setRating (Float rating) {
        this.rating = rating;
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

    public void addPortfolio(Portfolio portfolio) {
        this.portfolios.add(portfolio);
    }

    public void removePortfolio(Portfolio portfolio) {
        this.portfolios.remove(portfolio);
    }
}
