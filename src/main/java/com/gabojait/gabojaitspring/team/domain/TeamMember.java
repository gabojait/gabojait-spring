package com.gabojait.gabojaitspring.team.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

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
    @Column(nullable = false)
    private Boolean isQuit;

    @Builder
    private TeamMember(User user, Team team, Position position, boolean isLeader) {
        this.user = user;
        this.team = team;
        this.position = position;
        this.isLeader = isLeader;
        this.isQuit = false;
        this.isDeleted = false;
    }

    public void delete(boolean isQuit) {
        this.isQuit = isQuit;
        this.isDeleted = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TeamMember)) return false;
        TeamMember that = (TeamMember) o;
        return Objects.equals(id, that.id)
                && user.equals(that.user)
                && team.equals(that.team)
                && position == that.position
                && isLeader.equals(that.isLeader)
                && isQuit.equals(that.isQuit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, team, position, isLeader, isQuit);
    }
}
