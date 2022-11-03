package com.inuappcenter.gabojaitspring.user.domain;

import com.inuappcenter.gabojaitspring.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Getter
@Document(collection = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity implements UserDetails {

    @Indexed(unique = true)
    private String username;

    private String password;

    @Field(name = "legal_name")
    private String legalName;

    private String nickname;

    private Character gender;

    private LocalDate birthdate;

    @Field(name = "is_deactivated")
    private Boolean isDeactivated;

    private Collection<String> roles = new ArrayList<>();

    private Contact contact;

    @Field(name = "profile_id")
    private String profileId;

    @Transient
    private Double rating;

    @Builder(builderClassName = "ByUserBuilder", builderMethodName = "ByUserBuilder")
    public User(String username,
                String password,
                String legalName,
                String nickname,
                Character gender,
                LocalDate birthdate,
                Contact contact) {
        this.username = username;
        this.password = password;
        this.legalName = legalName;
        this.nickname = nickname;
        this.gender = gender;
        this.birthdate = birthdate;
        this.contact = contact;
        this.isDeactivated = false;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setGender(Character gender) {
        this.gender = gender;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public void setIsDeactivated(Boolean isDeactivated) {
        this.isDeactivated = isDeactivated;
    }

    public void addRole(String role) {
        this.roles.add(role);
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
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
