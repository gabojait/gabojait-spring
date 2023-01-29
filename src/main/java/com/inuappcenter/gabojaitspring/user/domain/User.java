package com.inuappcenter.gabojaitspring.user.domain;

import com.inuappcenter.gabojaitspring.common.BaseTimeEntity;
import com.inuappcenter.gabojaitspring.review.domain.ReviewComment;
import com.inuappcenter.gabojaitspring.profile.domain.Education;
import com.inuappcenter.gabojaitspring.profile.domain.Portfolio;
import com.inuappcenter.gabojaitspring.profile.domain.Skill;
import com.inuappcenter.gabojaitspring.profile.domain.Work;
import com.inuappcenter.gabojaitspring.user.domain.type.Gender;
import com.inuappcenter.gabojaitspring.user.domain.type.Role;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
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

    @Indexed(unique = true)
    private String username;

    @Field(name = "legal_name")
    private String legalName;

    @Field(name = "is_temporary_password")
    private Boolean isTemporaryPassword;

    @Field(name = "image_url")
    private String imageUrl;

    @Field(name = "current_project_id")
    private ObjectId currentProjectId;

    @Field(name = "completed_project_ids")
    private List<ObjectId> completedProjectIds = new ArrayList<>();

    private String password;
    private Character gender;
    private LocalDate birthdate;
    private Set<String> roles = new HashSet<>();
    private Contact contact;
    private String nickname;
    private String description;
    private Character position;
    private Float rating;
    private Boolean isPublic;
    private List<Education> educations = new ArrayList<>();
    private List<Work> works = new ArrayList<>();
    private List<Skill> skills = new ArrayList<>();
    private List<Portfolio> portfolios = new ArrayList<>();
    private List<ReviewComment> reviewComments = new ArrayList<>();

    @Builder
    public User(String username,
                String legalName,
                String password,
                Gender gender,
                LocalDate birthdate,
                Contact contact,
                String nickname,
                Set<Role> roles) {
        this.username = username;
        this.legalName = legalName;
        this.isTemporaryPassword = false;
        this.imageUrl = null;
        this.currentProjectId = null;
        this.password = password;
        this.gender = gender.getType();
        this.birthdate = birthdate;
        this.contact = contact;
        this.nickname = nickname;
        this.description = null;
        this.position = null;
        this.rating = null;
        this.isPublic = false;
        this.isDeleted = false;

        updateRoles(roles);
    }

    public void updateRoles(Set<Role> roles) {
        for (Role role : roles)
            this.roles.add(role.name());
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

    public void delete() {
        this.isDeleted = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
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
