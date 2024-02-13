package com.gabojait.gabojaitspring.domain.team;

import com.gabojait.gabojaitspring.domain.base.BasePermanentEntity;
import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.domain.user.User;
import com.gabojait.gabojaitspring.common.exception.CustomException;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.gabojait.gabojaitspring.common.constant.code.ErrorCode.TEAM_LEADER_UNAVAILABLE;
import static com.gabojait.gabojaitspring.common.constant.code.ErrorCode.UNREGISTER_UNAVAILABLE;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamMember extends BasePermanentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Position position;
    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private TeamMemberStatus teamMemberStatus;
    private Boolean isLeader;

    @Builder
    private TeamMember(Position position, boolean isLeader, User user, Team team) {
        this.position = position;
        this.isLeader = isLeader;
        this.user = user;
        this.team = team;
        this.teamMemberStatus = TeamMemberStatus.PROGRESS;
        this.isDeleted = false;

        team.join(position);
        user.updateIsSeekingTeam(false);
    }

    public void updatePosition(Position position) {
        team.leave(this.position);

        this.position = position;

        team.join(position);
    }

    public void complete(String projectUrl, LocalDateTime completedAt) {
        this.teamMemberStatus = TeamMemberStatus.COMPLETE;

        this.user.updateIsSeekingTeam(true);
        if (this.isLeader) this.team.complete(projectUrl, completedAt);
    }

    public void incomplete() {
        this.teamMemberStatus = TeamMemberStatus.INCOMPLETE;
        this.isDeleted = true;

        this.team.incomplete();
        this.user.updateIsSeekingTeam(true);
    }

    public void fire() {
        if (this.isLeader) throw new CustomException(TEAM_LEADER_UNAVAILABLE);

        this.teamMemberStatus = TeamMemberStatus.FIRED;
        this.isDeleted = true;

        this.team.leave(this.position);
        this.user.updateIsSeekingTeam(true);
    }

    public void quit() {
        if (this.isLeader) throw new CustomException(TEAM_LEADER_UNAVAILABLE);

        this.teamMemberStatus = TeamMemberStatus.QUIT;
        this.isDeleted = true;

        this.team.leave(this.position);
        this.user.updateIsSeekingTeam(true);
    }

    public void disconnectUser() {
        if (this.teamMemberStatus.equals(TeamMemberStatus.PROGRESS)) {
            if (!this.isLeader) quit();
            else throw new CustomException(UNREGISTER_UNAVAILABLE);
        }

        this.user = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TeamMember)) return false;
        TeamMember that = (TeamMember) o;
        return isLeader == that.isLeader
                && teamMemberStatus == that.teamMemberStatus
                && Objects.equals(id, that.id)
                && Objects.equals(user, that.user)
                && Objects.equals(team, that.team)
                && position == that.position;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, team, position, isLeader, teamMemberStatus);
    }
}
