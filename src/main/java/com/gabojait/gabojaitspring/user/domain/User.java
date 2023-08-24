package com.gabojait.gabojaitspring.user.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.fcm.domain.Fcm;
import com.gabojait.gabojaitspring.profile.domain.Education;
import com.gabojait.gabojaitspring.profile.domain.Portfolio;
import com.gabojait.gabojaitspring.profile.domain.Skill;
import com.gabojait.gabojaitspring.profile.domain.Work;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.review.domain.Review;
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
@Entity(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "contact_id")
    @ToString.Exclude
    private Contact contact;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private Set<UserRole> userRoles = new HashSet<>();

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
    @Column(nullable = false, length = 1)
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private Position position;
    private Boolean isSeekingTeam;
    private String imageUrl;

    private Float rating;
    private Long visitedCnt;
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
        this.gender = gender;
        this.birthdate = birthdate;
        this.nickname = nickname;
        this.contact = contact;

        this.position = Position.NONE;
        this.imageUrl = null;
        this.profileDescription = null;
        this.isSeekingTeam = true;

        this.rating = 0F;
        this.visitedCnt = 0L;
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
        this.gender = gender;
        this.birthdate = birthdate;
        this.nickname = legalName;

        this.lastRequestAt = LocalDateTime.now();
        this.isDeleted = true;
    }

    @Builder(builderMethodName = "masterBuilder", builderClassName = "masterBuilder")
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.gender = Gender.N;

        this.lastRequestAt = LocalDateTime.now();
        this.isDeleted = null;
    }

    @Builder(builderMethodName = "testOnlyBuilder", builderClassName = "testOnlyBuilder")
    public User(Long id, Role role) {
        this.id = id;
        this.username = "tester";
        this.nickname = "테스터";
        this.gender = Gender.N;
        this.position = Position.NONE;
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
                        new SimpleGrantedAuthority(userRole.getRole().name()))
                .collect(Collectors.toList());
    }

    public Set<String> getRoles() {
        Set<String> roles = new HashSet<>();
        for(UserRole userRole : this.userRoles)
            roles.add(userRole.getRole().name());

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

    /*
     * Profile related
     */

    public void updatePosition(Position position) {
        this.position = position;
    }

    public boolean hasPosition() {
        return !this.position.equals(Position.NONE);
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

    /*
     * Review related
     */

    public void rate(float rating) {
        if (this.reviewCnt == 0)
            this.rating = rating;
        else
            this.rating = (this.rating * ((float) this.reviewCnt / ((float) this.reviewCnt + 1)))
                    + (rating * (1 / ((float) this.reviewCnt + 1)));

        this.reviewCnt++;
    }
}
