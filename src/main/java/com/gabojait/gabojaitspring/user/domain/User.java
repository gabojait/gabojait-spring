package com.gabojait.gabojaitspring.user.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.fcm.domain.Fcm;
import com.gabojait.gabojaitspring.profile.domain.Education;
import com.gabojait.gabojaitspring.profile.domain.Portfolio;
import com.gabojait.gabojaitspring.profile.domain.Skill;
import com.gabojait.gabojaitspring.profile.domain.Work;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.review.domain.Review;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.team.domain.TeamMember;
import com.gabojait.gabojaitspring.user.domain.type.Gender;
import com.gabojait.gabojaitspring.user.domain.type.Role;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@ToString
@Entity(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "contact_id")
    @ToString.Exclude
    private Contact contact;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<UserRole> userRoles = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Fcm> fcms = new ArrayList<>();

    @OneToMany(mappedBy = "reviewee")
    @ToString.Exclude
    private List<Review> receivedReviews = new ArrayList<>();

    @OneToMany(mappedBy = "reviewer")
    @ToString.Exclude
    private List<Review> givenReviews = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Education> educations = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Portfolio> portfolios = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Skill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Work> works = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<TeamMember> teamMembers = new ArrayList<>();

    @Column(nullable = false, length = 15)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(length = 8)
    private String nickname;
    @Column(length = 120)
    private String profileDescription;
    private LocalDate birthdate;
    @Column(nullable = false)
    private LocalDateTime lastRequestAt;
    @Column(nullable = false)
    private Character gender;
    private Character position;
    private Boolean isSeekingTeam;
    private String imageUrl;

    private Float rating;
    private Long userOfferCnt;
    private Long teamOfferCnt;
    private Long visitedCnt;
    private Short createTeamCnt;
    private Short joinTeamCnt;
    private Short firedTeamCnt;
    private Short quitTeamByLeaderCnt;
    private Short quitTeamByUserCnt;
    private Short completeTeamCnt;
    private Integer reviewCnt;
    private Boolean isTemporaryPassword;
    private Boolean isNotified;

    @Builder(builderMethodName = "userBuilder", builderClassName = "userBuilder")
    public User(String username,
                String password,
                Gender gender,
                LocalDate birthdate,
                String nickname,
                Contact contact) {
        this.username = username;
        this.password = password;
        this.gender = gender.getType();
        this.birthdate = birthdate;
        this.nickname = nickname;
        this.contact = contact;

        this.position = Position.NONE.getType();
        this.imageUrl = null;
        this.profileDescription = null;
        this.isSeekingTeam = true;

        this.rating = 0F;
        this.userOfferCnt = 0L;
        this.teamOfferCnt = 0L;
        this.visitedCnt = 0L;
        this.createTeamCnt = 0;
        this.joinTeamCnt = 0;
        this.firedTeamCnt = 0;
        this.quitTeamByLeaderCnt = 0;
        this.quitTeamByUserCnt = 0;
        this.completeTeamCnt = 0;
        this.reviewCnt = 0;
        this.isTemporaryPassword = false;
        this.isNotified = true;
        this.lastRequestAt = LocalDateTime.now();
        this.isDeleted = false;
    }

    @Builder(builderMethodName = "adminBuilder", builderClassName = "adminBuilder")
    public User(String username, String password, Gender gender, LocalDate birthdate, String legalName) {
        this.username = username;
        this.password = password;
        this.gender = gender.getType();
        this.birthdate = birthdate;
        this.nickname = legalName;

        this.lastRequestAt = LocalDateTime.now();
        this.isDeleted = true;
    }

    @Builder(builderMethodName = "masterBuilder", builderClassName = "masterBuilder")
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.gender = Gender.NONE.getType();

        this.lastRequestAt = LocalDateTime.now();
        this.isDeleted = null;
    }

    @Builder(builderMethodName = "testOnlyBuilder", builderClassName = "testOnlyBuilder")
    public User(Long id, Role role) {
        this.id = id;
        this.gender = Gender.NONE.getType();
        this.position = Position.NONE.getType();
        this.contact = Contact.builder()
                .email("tester@gabojait.com")
                .verificationCode("000000")
                .build();

        UserRole userRole = UserRole.builder()
                .user(this)
                .role(Role.USER)
                .build();

        this.userRoles.add(userRole);

        if (role == Role.ADMIN || role == Role.MASTER) {
            UserRole adminRole = UserRole.builder()
                    .user(this)
                    .role(Role.USER)
                    .build();

            this.userRoles.add(adminRole);

            if (role == Role.MASTER) {
                UserRole masterRole = UserRole.builder()
                        .user(this)
                        .role(Role.MASTER)
                        .build();

                this.userRoles.add(masterRole);
            }
        }
    }

    public void updatePassword(String password, boolean isTemporaryPassword) {
        this.password = password;
        this.isTemporaryPassword = isTemporaryPassword;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateIsNotified(boolean isNotified) {
        this.isNotified = isNotified;
    }

    public void updateLastRequestAt() {
        this.lastRequestAt = LocalDateTime.now();
    }

    public void decideAdminRegistration(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void deleteAccount() {
        this.username = null;
        this.isDeleted = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.userRoles.stream()
                .map(userRole ->
                        new SimpleGrantedAuthority(userRole.getRole()))
                .collect(Collectors.toList());
    }

    public List<String> getRoles() {
        List<String> roles = new ArrayList<>();
        for(UserRole userRole : this.userRoles)
            roles.add(userRole.getRole());

        return roles;
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

    /**
     * Profile related
     */

    public void updatePosition(Position position) {
        this.position = position.getType();
    }

    public boolean hasPosition() {
        return !this.position.equals(Position.NONE.getType());
    }

    public void updateProfileDescription(String profileDescription) {
        this.profileDescription = profileDescription;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateIsSeekingTeam(boolean isSeekingTeam) {
        this.isSeekingTeam = isSeekingTeam;
    }

    public void incrementVisitedCnt() {
        this.visitedCnt++;
    }

    /**
     * Team related
     */

    public boolean isLeader() {
        if (this.teamMembers.isEmpty())
            return false;

        TeamMember teamMember = this.teamMembers.get(this.teamMembers.size() - 1);

        if (teamMember.getTeam().getIsDeleted())
            return false;

        return teamMember.getIsLeader();
    }

    public boolean isTeamMember(Team team) {
        for(TeamMember teamMember : this.teamMembers)
            if (teamMember.getTeam().getId().equals(team.getId()))
                return true;

        return false;
    }

    public void incrementCreateTeamCnt() {
        this.createTeamCnt++;
    }

    public void incrementJoinTeamCnt() {
        this.joinTeamCnt++;
    }

    public void incrementFiredTeamCnt() {
        this.firedTeamCnt++;
    }

    public void incrementQuitTeamByLeaderCnt() {
        this.quitTeamByLeaderCnt++;
    }

    public void incrementQuitTeamByUserCnt() {
        this.quitTeamByUserCnt++;
    }

    public void incrementCompleteTeamCnt() {
        this.completeTeamCnt++;
    }

    /**
     * Review related
     */

    public void rate(byte rate) {
        this.rating = (this.rating / (this.reviewCnt + 1)) + (rate / (this.reviewCnt + 1));
        this.reviewCnt++;
    }

    /**
     * Offer related
     */

    public void incrementUserOfferCnt() {
        this.userOfferCnt++;
    }

    public void incrementTeamOfferCnt() {
        this.teamOfferCnt++;
    }
}
