package com.gabojait.gabojaitspring.fcm.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@ToString
@Entity(name = "fcm")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Fcm extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fcm_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @Column(nullable = false)
    private String fcmToken;

    @Builder
    private Fcm(User user, String fcmToken) {
        this.user = user;
        this.fcmToken = fcmToken;
        this.isDeleted = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fcm)) return false;
        Fcm fcm = (Fcm) o;
        return Objects.equals(id, fcm.id) && user.equals(fcm.user) && fcmToken.equals(fcm.fcmToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, fcmToken);
    }
}
