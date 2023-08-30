package com.gabojait.gabojaitspring.user.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.fcm.domain.Fcm;
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

    @Column(nullable = false, length = 15)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(length = 8, nullable = false)
    private String nickname;
    @Column(length = 120)
    private String profileDescription;
    private LocalDate birthdate;
    @Column(nullable = false)
    private LocalDateTime lastRequestAt;
    @Column(nullable = false, length = 1)
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private Position position;
    @Column(nullable = false)
    private Boolean isSeekingTeam;
    private String imageUrl;

    @Column(nullable = false)
    private Float rating;
    @Column(nullable = false)
    private Long visitedCnt;
    @Column(nullable = false)
    private Integer reviewCnt;
    @Column(nullable = false)
    private Boolean isTemporaryPassword;
    @Column(nullable = false)
    private Boolean isNotified;

    @Builder
    private User(String username,
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

    @Builder(builderMethodName = "testBuilder", builderClassName = "testBuilder")
    private User(Long id) {
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

    public void delete() {
        this.username = null;
        this.isDeleted = true;

        this.contact.delete();
    }

    public Set<String> getRoles() {
        Set<String> roles = new HashSet<>();
        for(UserRole userRole : this.userRoles)
            roles.add(userRole.getRole().name());

        return roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id)
                && username.equals(user.username)
                && password.equals(user.password)
                && nickname.equals(user.nickname)
                && Objects.equals(profileDescription, user.profileDescription)
                && Objects.equals(birthdate, user.birthdate)
                && lastRequestAt.equals(user.lastRequestAt)
                && gender == user.gender
                && position == user.position
                && isSeekingTeam.equals(user.isSeekingTeam)
                && Objects.equals(imageUrl, user.imageUrl)
                && rating.equals(user.rating)
                && visitedCnt.equals(user.visitedCnt)
                && reviewCnt.equals(user.reviewCnt)
                && isTemporaryPassword.equals(user.isTemporaryPassword)
                && isNotified.equals(user.isNotified);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, nickname, profileDescription, birthdate, lastRequestAt, gender,
                position, isSeekingTeam, imageUrl, rating, visitedCnt, reviewCnt, isTemporaryPassword, isNotified);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.userRoles.stream()
                .map(userRole ->
                        new SimpleGrantedAuthority(userRole.getRole().name()))
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
