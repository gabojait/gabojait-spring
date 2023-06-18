package com.gabojait.gabojaitspring.team.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.profile.domain.type.TeamMemberStatus;
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
    @JoinColumn(name = "member_id")
    @ToString.Exclude
    private User user;

    @ManyToOne
    @JoinColumn(name = "team_id")
    @ToString.Exclude
    private Team team;

    private Character position;
    private Character teamMemberStatus;

    @Builder
    public TeamMember(User user, Team team, Position position, TeamMemberStatus teamMemberStatus) {
        this.user = user;
        this.team = team;
        this.position = position.getType();
        this.teamMemberStatus = teamMemberStatus.getType();
        this.isDeleted = false;
    }

    public boolean isLeader() {
        if (this.team.getCompletedAt() != null)
            return false;

        return teamMemberStatus.equals(TeamMemberStatus.LEADER.getType());
    }

    public void complete() {
        this.isDeleted = true;
    }
}
