package com.gabojait.gabojaitspring.user.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.profile.domain.Education;
import com.gabojait.gabojaitspring.profile.domain.Portfolio;
import com.gabojait.gabojaitspring.profile.domain.Skill;
import com.gabojait.gabojaitspring.profile.domain.Work;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.profile.domain.type.TeamMemberStatus;
import com.gabojait.gabojaitspring.review.domain.Review;
import com.gabojait.gabojaitspring.user.domain.type.Gender;
import com.gabojait.gabojaitspring.user.domain.type.Role;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Document(collection = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity implements UserDetails {

    @Field(name = "legal_name")
    private String legalName;

    @Field(name = "is_temporary_password")
    private Boolean isTemporaryPassword;

    @Field(name = "image_url")
    private String imageUrl;

    @Field(name = "last_request_date")
    private LocalDateTime lastRequestDate;

    @Field(name = "current_team_id")
    private ObjectId currentTeamId;

    @Field(name = "completed_team_ids")
    private List<ObjectId> completedTeamIds = new ArrayList<>();

    @Field(name = "team_member_status")
    private Character teamMemberStatus;

    @Field(name = "is_seeking_team")
    private Boolean isSeekingTeam;

    @Field(name = "recruit_ids")
    private List<ObjectId> recruitIds = new ArrayList<>();

    @Field(name = "application_ids")
    private List<ObjectId> applicationIds = new ArrayList<>();

    @Field(name = "profile_description")
    private String profileDescription;

    @Field(name = "visited_cnt")
    private Long visitedCnt;

    @Field(name = "join_team_cnt")
    private Short joinTeamCnt;

    @Field(name = "quit_incomplete_team_cnt")
    private Short quitIncompleteTeamCnt;

    @Field(name = "quit_complete_team_cnt")
    private Short quitCompleteTeamCnt;

    @Field(name = "rating_cnt")
    private Short ratingCnt;

    @Field(name = "favorite_team_ids")
    private List<ObjectId> favoriteTeamIds = new ArrayList<>();

    @Field(name = "user_fcm_id")
    private ObjectId userFcmId;

    private String username;
    private String password;
    private Character gender;
    private LocalDate birthdate;
    private Set<String> roles = new HashSet<>();
    private Contact contact;
    private String nickname;
    private Character position;
    private Float rating;
    private List<Education> educations = new ArrayList<>();
    private List<Portfolio> portfolios = new ArrayList<>();
    private List<Skill> skills = new ArrayList<>();
    private List<Work> works = new ArrayList<>();
    private List<Review> reviews = new ArrayList<>();

    @Builder
    public User(String username,
                String legalName,
                String password,
                Gender gender,
                LocalDate birthdate,
                Contact contact,
                String nickname) {
        this.username = username;
        this.nickname = nickname;
        this.legalName = legalName;
        this.password = password;
        this.gender = gender.getType();
        this.birthdate = birthdate;
        this.contact = contact;

        this.lastRequestDate = LocalDateTime.now();
        this.isTemporaryPassword = false;
        this.isSeekingTeam = true;
        this.imageUrl = null;
        this.teamMemberStatus = TeamMemberStatus.NULL.getType();
        this.currentTeamId = null;
        this.position = Position.NONE.getType();
        this.profileDescription = null;
        this.isDeleted = false;

        this.ratingCnt = 0;
        this.visitedCnt = 0L;
        this.joinTeamCnt = 0;
        this.quitCompleteTeamCnt = 0;
        this.quitIncompleteTeamCnt = 0;
        this.rating = 0F;

        updateRoles(List.of(Role.USER));
    }

    public void updatePosition(Position position) {
        this.position = position.getType();
    }
    public void updateProfileDescription(String profileDescription) {
        this.profileDescription = profileDescription;
    }

    public void updateIsSeekingTeam(Boolean isSeekingTeam) {
        this.isSeekingTeam = isSeekingTeam;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updatePassword(String password, boolean isTemporaryPassword) {
        this.password = password;
        this.isTemporaryPassword = isTemporaryPassword;
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

    public void incrementVisitedCnt() {
        this.visitedCnt++;
    }

    public void joinTeam(ObjectId currentTeamId) {
        this.currentTeamId = currentTeamId;
        this.joinTeamCnt++;
    }

    public void quitTeam(boolean isComplete) {
        if (isComplete) {
            this.quitCompleteTeamCnt++;
            this.completedTeamIds.set(0, this.currentTeamId);
        } else {
            this.quitIncompleteTeamCnt++;
        }

        this.currentTeamId = null;
    }

    public void updateRating(int rating) {
        this.rating = (this.rating * (float) (this.ratingCnt / (this.ratingCnt + 1))) +
                (rating * (float) (1 / (this.ratingCnt + 1)));

        this.ratingCnt++;
    }

    public void updateFavoriteTeamId(ObjectId teamId, boolean isAddFavorite) {
        if (isAddFavorite) {
            if (!this.favoriteTeamIds.contains(teamId))
                this.favoriteTeamIds.add(teamId);
        } else {
            this.favoriteTeamIds.remove(teamId);
        }
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void updateLastRequestDate() {
        this.lastRequestDate = LocalDateTime.now();
    }

    public void updateRoles(List<Role> roles) {
        for (Role role : roles)
            this.roles.add(role.name());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !this.isDeleted;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.isDeleted;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !this.isDeleted;
    }

    @Override
    public boolean isEnabled() {
        return !this.isDeleted;
    }
}
