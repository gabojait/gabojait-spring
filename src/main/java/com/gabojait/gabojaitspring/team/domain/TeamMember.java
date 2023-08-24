package com.gabojait.gabojaitspring.team.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_member_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    @ToString.Exclude
    private Team team;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Position position;
    @Column(nullable = false)
    private Boolean isLeader;

    @Builder
    public TeamMember(User user, Team team, Position position, Boolean isLeader) {
        this.user = user;
        this.team = team;
        this.position = position;
        this.isLeader = isLeader;
        this.isDeleted = false;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
