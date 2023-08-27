package com.gabojait.gabojaitspring.favorite.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteUser extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_user_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @ManyToOne
    @JoinColumn(name = "team_id")
    @ToString.Exclude
    private Team team;

    @Builder
    private FavoriteUser(User user, Team team) {
        this.user = user;
        this.team = team;
        this.isDeleted = false;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
