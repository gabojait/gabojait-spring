package com.inuappcenter.gabojaitspring.user.domain;

import com.inuappcenter.gabojaitspring.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Document(collection = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity implements UserDetails {

    @Indexed(unique = true)
    private String username;

    @Field(name = "legal_name")
    private String legalName;

    private String password;
    private String nickname;
    private Character gender;
    private LocalDate birthdate;
    private String role;
    private Contact contact;
    private Float rating;

    @Builder
    public User(String username, String legalName, String password, String nickname, Gender gender, LocalDate birthdate, Role role, Contact contact) {
        this.username = username;
        this.legalName = legalName;
        this.password = password;
        this.nickname = nickname;
        this.gender = gender.getType();
        this.birthdate = birthdate;
        this.role = role.name();
        this.contact = contact;
        this.rating = 0F;
        this.isDeleted = false;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<String> roles = new HashSet<>();
        roles.add(role);
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
