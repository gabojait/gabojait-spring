package com.gabojait.gabojaitspring.user.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

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

    private String email;
    private String verificationCode;
    private Boolean isVerified;

    @Builder
    public Contact(String email, String verificationCode) {
        this.email = email;
        this.verificationCode = verificationCode;
        this.isVerified = false;
        this.isDeleted = false;
    }

    public void verified() {
        this.isVerified = true;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void deleteAccount() {
        this.email = null;
        this.isDeleted = true;
    }
}
