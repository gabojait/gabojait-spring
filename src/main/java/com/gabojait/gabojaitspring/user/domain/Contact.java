package com.gabojait.gabojaitspring.user.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Contact extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_id")
    private Long id;

    @OneToOne(mappedBy = "contact")
    @ToString.Exclude
    private User user;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String verificationCode;

    @Column(nullable = false)
    private Boolean isVerified;

    @Builder
    private Contact(String email, String verificationCode) {
        this.email = email;
        this.verificationCode = verificationCode;
        this.isVerified = false;
        this.isDeleted = false;
    }

    public void verified() {
        this.isVerified = true;
    }

    public void delete() {
        this.email = null;
        this.isDeleted = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contact)) return false;
        Contact contact = (Contact) o;
        return Objects.equals(id, contact.id)
                && email.equals(contact.email)
                && verificationCode.equals(contact.verificationCode)
                && Objects.equals(isVerified, contact.isVerified);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, verificationCode, isVerified);
    }
}
