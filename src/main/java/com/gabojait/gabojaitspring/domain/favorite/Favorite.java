package com.gabojait.gabojaitspring.domain.favorite;

import com.gabojait.gabojaitspring.domain.base.BaseEntity;
import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Favorite extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favorite_team_id")
    private Team favoriteTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favorite_user_id")
    private User favoriteUser;

    @Builder
    private Favorite(User user, Team favoriteTeam, User favoriteUser) {
        this.user = user;
        this.favoriteTeam = favoriteTeam;
        this.favoriteUser = favoriteUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Favorite)) return false;
        Favorite favorite = (Favorite) o;
        return Objects.equals(id, favorite.id)
                && Objects.equals(user, favorite.user)
                && Objects.equals(favoriteTeam, favorite.favoriteTeam)
                && Objects.equals(favoriteUser, favorite.favoriteUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, favoriteTeam, favoriteUser);
    }
}
