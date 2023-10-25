package com.gabojait.gabojaitspring.domain.notification;

import com.gabojait.gabojaitspring.domain.base.BaseEntity;
import com.gabojait.gabojaitspring.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Fcm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fcm_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String fcmToken;

    @Builder
    private Fcm(String fcmToken, User user) {
        this.fcmToken = fcmToken;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fcm)) return false;
        Fcm fcm = (Fcm) o;
        return Objects.equals(id, fcm.id)
                && Objects.equals(user, fcm.user)
                && Objects.equals(fcmToken, fcm.fcmToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, fcmToken);
    }
}
