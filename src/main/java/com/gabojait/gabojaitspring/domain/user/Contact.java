package com.gabojait.gabojaitspring.domain.user;

import com.gabojait.gabojaitspring.domain.base.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Contact extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 6)
    private String verificationCode;
    @Column(nullable = false)
    private Boolean isVerified;

    @Builder
    private Contact(String email, String verificationCode) {
        this.email = email;
        this.verificationCode = verificationCode;
        this.isVerified = false;
    }

    public void verified() {
        this.isVerified = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contact)) return false;
        Contact contact = (Contact) o;
        return Objects.equals(id, contact.id)
                && Objects.equals(email, contact.email)
                && Objects.equals(verificationCode, contact.verificationCode)
                && Objects.equals(isVerified, contact.isVerified);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, verificationCode, isVerified);
    }
}
