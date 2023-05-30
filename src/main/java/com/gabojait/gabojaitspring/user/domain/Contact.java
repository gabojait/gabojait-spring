package com.gabojait.gabojaitspring.user.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@ToString
@Document(collection = "contact")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Contact extends BaseTimeEntity {

    private String email;

    @Field(name = "verification_code")
    private String verificationCode;

    @Field(name = "is_verified")
    private Boolean isVerified;

    @Field(name = "is_registered")
    private Boolean isRegistered;

    @Builder
    public Contact(String email, String verificationCode) {
        this.email = email;
        this.verificationCode = verificationCode;
        this.isVerified = false;
        this.isRegistered = false;
        this.isDeleted = false;
    }

    public void verified() {
        this.isVerified = true;
    }

    public void registered() {
        this.isRegistered = true;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
