package com.gabojait.gabojaitspring.favorite.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@Entity(name = "favorite_member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteUser extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_member_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    @ToString.Exclude
    private User user;

    @ManyToOne
    @JoinColumn(name = "team_id")
    @ToString.Exclude
    private Team team;

    @Builder
    public FavoriteUser(User user, Team team) {
        this.user = user;
        this.team = team;
        this.isDeleted = false;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
