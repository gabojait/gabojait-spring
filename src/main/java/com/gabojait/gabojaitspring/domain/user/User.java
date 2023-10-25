package com.gabojait.gabojaitspring.domain.user;

import com.gabojait.gabojaitspring.domain.base.BaseEntity;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

@Getter
@Entity(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact contact;

    @Column(nullable = false, length = 15, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, length = 8, unique = true)
    private String nickname;
    @Column(length = 120)
    private String profileDescription;
    private String imageUrl;

    private LocalDate birthdate;
    @Column(nullable = false)
    private LocalDateTime lastRequestAt;

    @Column(nullable = false, length = 1)
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Position position;

    @Column(nullable = false)
    private Float rating;
    @Column(nullable = false)
    private Long visitedCnt;
    @Column(nullable = false)
    private Integer reviewCnt;
    @Column(nullable = false)
    private Boolean isSeekingTeam;
    @Column(nullable = false)
    private Boolean isTemporaryPassword;
    @Column(nullable = false)
    private Boolean isNotified;

    @Builder
    private User(String username,
                 String password,
                 String nickname,
                 Gender gender,
                 LocalDate birthdate,
                 LocalDateTime lastRequestAt,
                 Contact contact) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.gender = gender;
        this.birthdate = birthdate;
        this.contact = contact;

        this.profileDescription = null;
        this.imageUrl = null;
        this.lastRequestAt = lastRequestAt;
        this.position = Position.NONE;
        this.rating = 0F;
        this.visitedCnt = 0L;
        this.reviewCnt = 0;
        this.isSeekingTeam = true;
        this.isTemporaryPassword = false;
        this.isNotified = true;
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

    public void updateLastRequestAt(LocalDateTime lastRequestAt) {
        this.lastRequestAt = lastRequestAt;
    }

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

    public void visit() {
        this.visitedCnt++;
    }

    public void rate(float rating) {
        if (this.reviewCnt == 0)
            this.rating = rating;
        else
            this.rating = (this.rating * ((float) this.reviewCnt / ((float) this.reviewCnt + 1)))
                    + (rating * (1 / ((float) this.reviewCnt + 1)));

        this.reviewCnt++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id)
                && Objects.equals(contact, user.contact)
                && Objects.equals(username, user.username)
                && Objects.equals(password, user.password)
                && Objects.equals(nickname, user.nickname)
                && Objects.equals(profileDescription, user.profileDescription)
                && Objects.equals(imageUrl, user.imageUrl)
                && Objects.equals(birthdate, user.birthdate)
                && Objects.equals(lastRequestAt, user.lastRequestAt)
                && gender == user.gender
                && position == user.position
                && Objects.equals(rating, user.rating)
                && Objects.equals(visitedCnt, user.visitedCnt)
                && Objects.equals(reviewCnt, user.reviewCnt)
                && Objects.equals(isSeekingTeam, user.isSeekingTeam)
                && Objects.equals(isTemporaryPassword, user.isTemporaryPassword)
                && Objects.equals(isNotified, user.isNotified);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, contact, username, password, nickname, profileDescription, imageUrl, birthdate,
                lastRequestAt, gender, position, rating, visitedCnt, reviewCnt, isSeekingTeam, isTemporaryPassword,
                isNotified);
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
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
