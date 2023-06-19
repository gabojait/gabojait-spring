package com.gabojait.gabojaitspring.fcm.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.*;

import javax.persistence.*;

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
    @JoinColumn(name = "member_id")
    @ToString.Exclude
    private User user;

    @Column(nullable = false)
    private String fcmToken;

    @Builder
    public Fcm(User user, String fcmToken) {
        this.user = user;
        this.fcmToken = fcmToken;
        this.isDeleted = false;
    }
}
