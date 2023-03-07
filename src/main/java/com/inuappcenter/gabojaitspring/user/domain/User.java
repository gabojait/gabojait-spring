package com.inuappcenter.gabojaitspring.user.domain;

import com.inuappcenter.gabojaitspring.common.BaseTimeEntity;
import com.inuappcenter.gabojaitspring.profile.domain.*;
import com.inuappcenter.gabojaitspring.profile.domain.type.Position;
import com.inuappcenter.gabojaitspring.review.domain.Review;
import com.inuappcenter.gabojaitspring.user.domain.type.Gender;
import com.inuappcenter.gabojaitspring.user.domain.type.Role;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Document(collection = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity implements UserDetails {

    private String username;

    @Field(name = "legal_name")
    private String legalName;

    @Field(name = "is_temporary_password")
    private Boolean isTemporaryPassword;

    @Field(name = "image_url")
    private String imageUrl;

    @Field(name = "current_team_id")
    private ObjectId currentTeamId;

    @Field(name = "completed_team_ids")
    private List<ObjectId> completedTeamIds = new ArrayList<>();

    @Field(name = "is_public")
    private Boolean isPublic;

    @Field(name = "recruit_ids")
    private List<ObjectId> recruitIds = new ArrayList<>();

    @Field(name = "application_ids")
    private List<ObjectId> applicationIds = new ArrayList<>();

    private String password;
    private Character gender;
    private LocalDate birthdate;
    private List<String> roles = new ArrayList<>();
    private Contact contact;
    private String nickname;
    private String description;
    private Character position;
    private Float rating;
    private List<Education> educations = new ArrayList<>();
    private List<Work> works = new ArrayList<>();
    private List<Skill> skills = new ArrayList<>();
    private List<Portfolio> portfolios = new ArrayList<>();
    private List<Review> reviews = new ArrayList<>();

    @Builder
    public User(String username,
                String legalName,
                String password,
                Gender gender,
                LocalDate birthdate,
                Contact contact,
                String nickname,
                List<Role> roles) {
        this.username = username;
        this.legalName = legalName;
        this.isTemporaryPassword = false;
        this.password = password;
        this.gender = gender.getType();
        this.birthdate = birthdate;
        this.contact = contact;
        this.rating = 0F;
        this.nickname = nickname;
        this.isPublic = false;
        this.imageUrl = null;
        this.currentTeamId = null;
        this.position = null;
        this.description = null;

        this.isDeleted = false;

        for (Role role : roles)
            this.roles.add(role.name());
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updatePosition(Position position) {
        this.position = position.getType();
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public void updateIsTemporaryPassword(boolean isTemporaryPassword) {
        this.isTemporaryPassword = isTemporaryPassword;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateCurrentTeamId(ObjectId teamId) {
        this.currentTeamId = teamId;
    }

    public void addEducation(Education education) {
        this.educations.add(education);
    }

    public void removeEducation(Education education) {
        this.educations.remove(education);
    }

    public void addPortfolio(Portfolio portfolio) {
        this.portfolios.add(portfolio);
    }

    public void removePortfolio(Portfolio portfolio) {
        this.portfolios.remove(portfolio);
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

    public void addRecruitId(ObjectId offerId) {
        this.recruitIds.add(offerId);
    }

    public void removeRecruitId(ObjectId offerId) {
        this.recruitIds.remove(offerId);
    }

    public void addApplicationId(ObjectId offerId) {
        this.applicationIds.add(offerId);
    }

    public void removeApplicationId(ObjectId offerId) {
        this.applicationIds.remove(offerId);
    }

    public void addCompletedTeamId(ObjectId teamId) {
        this.completedTeamIds.add(teamId);
    }

    public void addReview(Review review) {
        this.reviews.add(review);
    }

    public void delete() {
        this.isDeleted = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return !getIsDeleted();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !getIsDeleted();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !getIsDeleted();
    }

    @Override
    public boolean isEnabled() {
        return !getIsDeleted();
    }
}
