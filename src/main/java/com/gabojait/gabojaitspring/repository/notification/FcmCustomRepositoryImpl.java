package com.gabojait.gabojaitspring.repository.notification;

import com.gabojait.gabojaitspring.domain.team.TeamMemberStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.gabojait.gabojaitspring.domain.notification.QFcm.fcm;
import static com.gabojait.gabojaitspring.domain.team.QTeam.team;
import static com.gabojait.gabojaitspring.domain.team.QTeamMember.teamMember;
import static com.gabojait.gabojaitspring.domain.user.QUser.user;
import static com.querydsl.jpa.JPAExpressions.select;

@RequiredArgsConstructor
public class FcmCustomRepositoryImpl implements FcmCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findAllTeam(long teamId) {
        return queryFactory
                .select(fcm.fcmToken)
                .from(fcm)
                .leftJoin(fcm.user, user)
                .where(
                        fcm.user.id.in(
                                select(teamMember.user.id)
                                        .from(teamMember)
                                        .leftJoin(teamMember.user, user)
                                        .leftJoin(teamMember.team, team)
                                        .where(
                                                teamMember.team.id.eq(teamId),
                                                teamMember.teamMemberStatus.eq(TeamMemberStatus.PROGRESS)
                                                        .or(teamMember.teamMemberStatus.eq(TeamMemberStatus.COMPLETE)),
                                                teamMember.isDeleted.isFalse()
                                        )
                        )
                ).fetch();
    }

    @Override
    public List<String> findAllUser(long userId) {
        return queryFactory
                .select(fcm.fcmToken)
                .from(fcm)
                .leftJoin(fcm.user, user)
                .where(
                        fcm.user.id.in(userId)
                ).fetch();
    }

    @Override
    public List<String> findAllTeamExceptUser(long teamId, long userId) {
        return queryFactory
                .select(fcm.fcmToken)
                .from(fcm)
                .leftJoin(fcm.user, user)
                .where(
                        fcm.user.id.in(
                                select(teamMember.user.id)
                                        .from(teamMember)
                                        .leftJoin(teamMember.user, user)
                                        .leftJoin(teamMember.team, team)
                                        .where(
                                                teamMember.user.id.ne(userId),
                                                teamMember.team.id.eq(teamId),
                                                teamMember.teamMemberStatus.eq(TeamMemberStatus.PROGRESS).or(
                                                                teamMember.teamMemberStatus.eq(TeamMemberStatus.COMPLETE)),
                                                teamMember.isDeleted.isFalse()

                                        )
                        )
                ).fetch();
    }
}
