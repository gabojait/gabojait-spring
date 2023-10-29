package com.gabojait.gabojaitspring.domain.offer;

import com.gabojait.gabojaitspring.domain.base.BasePermanentEntity;
import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Offer extends BasePermanentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "offer_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(nullable = false, length = 6)
    @Enumerated(EnumType.STRING)
    private OfferedBy offeredBy;
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Position position;
    private Boolean isAccepted;

    @Builder
    private Offer(OfferedBy offeredBy, Position position, User user, Team team) {
        this.offeredBy = offeredBy;
        this.position = position;
        this.user = user;
        this.team = team;
        this.isAccepted = null;
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
                && Objects.equals(user, offer.user)
                && Objects.equals(team, offer.team)
                && offeredBy == offer.offeredBy
                && position == offer.position
                && Objects.equals(isAccepted, offer.isAccepted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, team, offeredBy, position, isAccepted);
    }
}
