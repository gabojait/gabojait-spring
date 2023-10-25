package com.gabojait.gabojaitspring.repository.team;

import com.gabojait.gabojaitspring.domain.team.TeamMember;
import com.gabojait.gabojaitspring.domain.team.TeamMemberStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.gabojait.gabojaitspring.domain.review.QReview.review;
import static com.gabojait.gabojaitspring.domain.team.QTeam.team;
import static com.gabojait.gabojaitspring.domain.team.QTeamMember.teamMember;
import static com.gabojait.gabojaitspring.domain.user.QUser.user;
import static com.querydsl.jpa.JPAExpressions.select;
import static com.querydsl.jpa.JPAExpressions.selectDistinct;

@RequiredArgsConstructor
public class TeamMemberRepositoryImpl implements TeamMemberCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<TeamMember> findAllFetchTeam(long userId) {
        return queryFactory
                .selectFrom(teamMember)
                .leftJoin(teamMember.team, team)
                .fetchJoin()
                .leftJoin(teamMember.user, user)
                .where(
                        teamMember.user.id.eq(userId),
                        teamMember.teamMemberStatus.eq(TeamMemberStatus.COMPLETE)
                                .or(teamMember.teamMemberStatus.eq(TeamMemberStatus.PROGRESS))
                )
                .orderBy(teamMember.createdAt.desc())
                .fetch();
    }

    public List<TeamMember> findAll(long userId) {
        return queryFactory
                .selectFrom(teamMember)
                .leftJoin(teamMember.user, user)
                .where(teamMember.user.id.eq(userId))
                .orderBy(teamMember.createdAt.desc())
                .fetch();
    }

    @Override
    public Optional<TeamMember> findCurrentFetchTeam(long userId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(teamMember)
                        .leftJoin(teamMember.team, team)
                        .fetchJoin()
                        .leftJoin(teamMember.user, user)
                        .where(
                                teamMember.user.id.eq(userId),
                                teamMember.teamMemberStatus.eq(TeamMemberStatus.PROGRESS)
                        ).fetchFirst()
        );
    }

    @Override
    public Optional<TeamMember> find(long userId, long teamId, TeamMemberStatus teamMemberStatus) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(teamMember)
                        .leftJoin(teamMember.team, team)
                        .leftJoin(teamMember.user, user)
                        .where(
                                teamMember.user.id.eq(userId),
                                teamMember.team.id.eq(teamId),
                                teamMember.teamMemberStatus.eq(teamMemberStatus)
                        ).fetchFirst()
        );
    }

    @Override
    public Optional<TeamMember> findLeaderFetchUser(long teamId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(teamMember)
                        .leftJoin(teamMember.user, user)
                        .fetchJoin()
                        .leftJoin(teamMember.team, team)
                        .where(
                                teamMember.team.id.eq(teamId),
                                teamMember.isLeader.isTrue()
                        ).fetchFirst()
        );
    }

    @Override
    public List<TeamMember> findAllCompleteFetchTeam(long teamId) {
        return queryFactory
                .select(teamMember)
                .from(teamMember)
                .leftJoin(teamMember.team, team)
                .fetchJoin()
                .leftJoin(teamMember.user, user)
                .where(
                        teamMember.team.id.eq(teamId),
                        teamMember.teamMemberStatus.eq(TeamMemberStatus.COMPLETE)
                ).orderBy(teamMember.createdAt.desc())
                .fetch();
    }

    @Override
    public List<TeamMember> findAllCurrentFetchUser(long teamId) {
        return queryFactory
                .select(teamMember)
                .from(teamMember)
                .leftJoin(teamMember.user, user)
                .fetchJoin()
                .leftJoin(teamMember.team, team)
                .where(
                        teamMember.team.id.eq(teamId),
                        teamMember.teamMemberStatus.eq(TeamMemberStatus.PROGRESS),
                        teamMember.isDeleted.isFalse()
                ).fetch();
    }

    @Override
    public List<TeamMember> findAllFetchUser(long teamId) {
        return queryFactory
                .select(teamMember)
                .from(teamMember)
                .leftJoin(teamMember.user, user)
                .fetchJoin()
                .leftJoin(teamMember.team, team)
                .where(
                        teamMember.team.id.eq(teamId),
                        teamMember.teamMemberStatus.in(TeamMemberStatus.COMPLETE, TeamMemberStatus.PROGRESS),
                        teamMember.isDeleted.isFalse()
                ).fetch();
    }

    @Override
    public List<TeamMember> findAllExceptUserFetchUser(long teamId, long userId) {
        return queryFactory
                .select(teamMember)
                .from(teamMember)
                .leftJoin(teamMember.user, user)
                .fetchJoin()
                .leftJoin(teamMember.team, team)
                .where(
                        teamMember.user.id.ne(userId),
                        teamMember.team.id.eq(teamId),
                        teamMember.teamMemberStatus.in(TeamMemberStatus.COMPLETE, TeamMemberStatus.PROGRESS),
                        teamMember.isDeleted.isFalse()
                ).fetch();
    }

    @Override
    public Optional<TeamMember> findReviewableFetchTeam(long userId, long teamId, LocalDateTime now) {
        return Optional.ofNullable(
                queryFactory
                        .select(teamMember)
                        .from(teamMember)
                        .leftJoin(teamMember.team, team)
                        .fetchJoin()
                        .leftJoin(teamMember.user, user)
                        .where(
                                teamMember.team.id.eq(teamId),
                                teamMember.user.id.eq(userId),
                                teamMember.teamMemberStatus.eq(TeamMemberStatus.COMPLETE),
                                teamMember.team.completedAt.after(now.minusWeeks(4))
                        ).orderBy(teamMember.createdAt.desc())
                        .fetchFirst()
        );
    }

    public List<TeamMember> findAllReviewableFetchTeam(long userId, LocalDateTime now) {
        return queryFactory
                .select(teamMember)
                .from(teamMember)
                .leftJoin(teamMember.team, team)
                .fetchJoin()
                .leftJoin(teamMember.user, user)
                .where(
                        teamMember.id.notIn(
                                selectDistinct(review.reviewer.id)
                                        .from(review)
                                        .leftJoin(review.reviewer, teamMember)
                                        .where(
                                                teamMember.id.in(
                                                        select(teamMember.id)
                                                                .from(teamMember)
                                                                .leftJoin(teamMember.user, user)
                                                                .where(
                                                                        user.id.eq(userId),
                                                                        teamMember.teamMemberStatus.eq(TeamMemberStatus.COMPLETE)
                                                                )
                                                )
                                        )
                        ),
                        teamMember.user.id.eq(userId),
                        teamMember.teamMemberStatus.eq(TeamMemberStatus.COMPLETE),
                        teamMember.team.completedAt.after(now.minusWeeks(4))
                ).orderBy(teamMember.createdAt.desc())
                .fetch();
    }

    @Override
    public boolean existsCurrent(long userId) {
        Integer result = queryFactory
                .selectOne()
                .from(teamMember)
                .leftJoin(teamMember.team, team)
                .leftJoin(teamMember.user, user)
                .where(
                        teamMember.user.id.eq(userId),
                        teamMember.teamMemberStatus.eq(TeamMemberStatus.PROGRESS)
                ).fetchFirst();

        return result != null;
    }

    @Override
    public boolean exists(long userId, long teamId) {
        Integer result = queryFactory
                .selectOne()
                .from(teamMember)
                .leftJoin(teamMember.team, team)
                .leftJoin(teamMember.user, user)
                .where(
                        teamMember.user.id.eq(userId),
                        teamMember.team.id.eq(teamId),
                        teamMember.teamMemberStatus.eq(TeamMemberStatus.PROGRESS)
                                .or(teamMember.teamMemberStatus.eq(TeamMemberStatus.COMPLETE))
                ).fetchFirst();

        return result != null;
    }
}
