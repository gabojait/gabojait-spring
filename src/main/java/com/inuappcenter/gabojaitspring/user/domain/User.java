package com.inuappcenter.gabojaitspring.user.domain;

import com.inuappcenter.gabojaitspring.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@Getter
@Document(collection = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Indexed(unique = true)
    private String username;

    private String password;

    @Field(name = "legal_name")
    private String legalName;

    private String nickname;

    private Character gender;

    private LocalDate birthdate;

    private Collection<String> roles = new ArrayList<>();

    private Contact contact;

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

    public void addRole(String role) {
        this.roles.add(role);
    }
}
