package com.gabojait.gabojaitspring.offer.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.offer.domain.type.OfferedBy;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

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
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    @ToString.Exclude
    private Team team;

    private Boolean isAccepted;
    @Column(nullable = false, length = 4)
    @Enumerated(EnumType.STRING)
    private OfferedBy offeredBy;
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Position position;

    @Builder
    private Offer(User user, Team team, OfferedBy offeredBy, Position position) {
        this.user = user;
        this.team = team;
        this.isAccepted = null;
        this.offeredBy = offeredBy;
        this.position = position;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Offer)) return false;
        Offer offer = (Offer) o;
        return Objects.equals(id, offer.id)
                && user.equals(offer.user)
                && team.equals(offer.team)
                && Objects.equals(isAccepted, offer.isAccepted)
                && offeredBy == offer.offeredBy
                && position == offer.position;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, team, isAccepted, offeredBy, position);
    }
}
