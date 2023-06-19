package com.gabojait.gabojaitspring.offer.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.offer.domain.type.OfferedBy;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Offer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "offer_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    @ToString.Exclude
    private User user;

    @ManyToOne
    @JoinColumn(name = "team_id")
    @ToString.Exclude
    private Team team;

    private Boolean isAccepted;
    @Column(nullable = false)
    private Character offeredBy;
    @Column(nullable = false)
    private Character position;

    @Builder
    public Offer(User user, Team team, OfferedBy offeredBy, Position position) {
        this.user = user;
        this.team = team;
        this.isAccepted = null;
        this.offeredBy = offeredBy.getType();
        this.position = position.getType();
        this.isDeleted = false;
    }

    public void accept() {
        this.isAccepted = true;
        this.isDeleted = true;
    }

    public void decline() {
        this.isAccepted = false;
        this.isDeleted = true;
    }

    public void cancel() {
        this.isDeleted = true;
    }
}
