package com.gabojait.gabojaitspring.repository.offer;

import com.gabojait.gabojaitspring.domain.offer.Offer;
import com.gabojait.gabojaitspring.domain.offer.OfferedBy;
import com.gabojait.gabojaitspring.domain.team.TeamMemberStatus;
import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.domain.user.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.gabojait.gabojaitspring.domain.offer.QOffer.offer;
import static com.gabojait.gabojaitspring.domain.team.QTeam.team;
import static com.gabojait.gabojaitspring.domain.team.QTeamMember.teamMember;
import static com.gabojait.gabojaitspring.domain.user.QUser.user;
import static com.querydsl.jpa.JPAExpressions.*;

@Repository
@RequiredArgsConstructor
public class OfferRepositoryImpl implements OfferCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Offer> findAllByUserId(long userId, long offerorUserId) {
        QUser offerorUser = new QUser("offerorUser");
        return queryFactory
                .selectFrom(offer)
                .where(
                        offer.team.id.eq(
                                select(teamMember.team.id)
                                        .from(teamMember)
                                        .leftJoin(teamMember.team, team)
                                        .leftJoin(teamMember.user, offerorUser)
                                        .where(
                                                offerorUser.id.eq(offerorUserId),
                                                teamMember.isLeader.isTrue(),
                                                teamMember.teamMemberStatus.eq(TeamMemberStatus.PROGRESS),
                                                teamMember.team.completedAt.isNull(),
                                                teamMember.isDeleted.isFalse()
                                        )
                        ),
                        offer.user.id.eq(userId),
                        offer.isAccepted.isNull(),
                        offer.isDeleted.isFalse()
                ).orderBy(offer.createdAt.desc())
                .fetch();
    }

    @Override
    public List<Offer> findAllInUserIds(List<Long> userIds, long offerorUserId) {
        QUser offerorUser = new QUser("offerorUser");
        return queryFactory
                .select(offer)
                .from(offer)
                .leftJoin(offer.user, user)
                .fetchJoin()
                .where(
                        offer.user.id.in(userIds),
                        offer.team.id.eq(
                                select(teamMember.team.id)
                                        .from(teamMember)
                                        .leftJoin(teamMember.team, team)
                                        .leftJoin(teamMember.user, offerorUser)
                                        .where(
                                                offerorUser.id.eq(offerorUserId),
                                                teamMember.isLeader.isTrue(),
                                                teamMember.teamMemberStatus.eq(TeamMemberStatus.PROGRESS),
                                                teamMember.team.completedAt.isNull(),
                                                teamMember.team.isDeleted.isFalse()
                                        )
                        ),
                        offer.isAccepted.isNull(),
                        offer.isDeleted.isFalse()
                ).orderBy(offer.createdAt.desc())
                .fetch();
    }

    @Override
    public List<Offer> findAllByTeamId(long userId, long teamId) {
        return queryFactory.selectFrom(offer)
                .leftJoin(offer.user, user)
                .leftJoin(offer.team, team)
                .where(
                        offer.user.id.eq(userId),
                        offer.team.id.eq(teamId),
                        offer.isAccepted.isNull(),
                        offer.team.completedAt.isNull()
                ).fetch();
    }

    @Override
    public Optional<Offer> findFetchTeam(long userId, long offerId, OfferedBy offeredBy) {
        return Optional.ofNullable(
                queryFactory.selectFrom(offer)
                        .leftJoin(offer.team, team)
                        .fetchJoin()
                        .leftJoin(offer.user, user)
                        .where(
                                offer.id.eq(offerId),
                                offer.isAccepted.isNull(),
                                offer.offeredBy.eq(offeredBy),
                                offer.isDeleted.isFalse(),
                                offer.user.id.eq(userId),
                                offer.team.completedAt.isNull()
                ).fetchFirst()
        );
    }

    @Override
    public Optional<Offer> findFetchUser(long teamId, long offerId, OfferedBy offeredBy) {
        return Optional.ofNullable(
                queryFactory.selectFrom(offer)
                        .leftJoin(offer.user, user)
                        .fetchJoin()
                        .leftJoin(offer.team, team)
                        .where(
                                offer.id.eq(offerId),
                                offer.isAccepted.isNull(),
                                offer.offeredBy.eq(offeredBy),
                                offer.isDeleted.isFalse(),
                                offer.team.id.eq(teamId),
                                offer.team.completedAt.isNull()
                        ).fetchFirst()
        );
    }

    @Override
    public Page<Offer> findPageFetchUser(long userId, OfferedBy offeredBy, long pageFrom, int pageSize) {
        Long count = queryFactory.select(offer.count())
                .from(offer)
                .leftJoin(offer.user, user)
                .leftJoin(offer.team, team)
                .where(
                        offer.user.id.eq(userId),
                        offer.offeredBy.eq(offeredBy),
                        offer.isAccepted.isNull(),
                        offer.team.completedAt.isNull()
                ).fetchOne();

        Pageable pageable = Pageable.ofSize(pageSize);

        if (count == null || count == 0)
            return new PageImpl<>(List.of(), pageable, 0);

        List<Offer> offers = queryFactory.select(offer)
                .from(offer)
                .leftJoin(offer.team, team)
                .fetchJoin()
                .leftJoin(offer.user, user)
                .where(
                        offer.id.lt(pageFrom),
                        offer.offeredBy.eq(offeredBy),
                        offer.user.id.eq(userId),
                        offer.isAccepted.isNull(),
                        offer.team.completedAt.isNull()
                ).orderBy(offer.createdAt.desc())
                .limit(pageSize)
                .fetch();

        return new PageImpl<>(offers, pageable, count);
    }

    @Override
    public Page<Offer> findPageFetchTeam(long teamId,
                                         Position position,
                                         OfferedBy offeredBy,
                                         long pageFrom,
                                         int pageSize) {
        Long count = queryFactory.select(offer.count())
                .from(offer)
                .leftJoin(offer.user, user)
                .leftJoin(offer.team, team)
                .where(
                        offer.team.id.eq(teamId),
                        offer.offeredBy.eq(offeredBy),
                        offer.position.eq(position),
                        offer.isAccepted.isNull(),
                        offer.team.completedAt.isNull()
                ).fetchOne();

        Pageable pageable = Pageable.ofSize(pageSize);

        if (count == null || count == 0)
            return new PageImpl<>(List.of(), pageable, 0);

        List<Offer> offers = queryFactory.select(offer)
                .from(offer)
                .leftJoin(offer.user, user)
                .fetchJoin()
                .leftJoin(offer.team, team)
                .where(
                        offer.id.lt(pageFrom),
                        offer.offeredBy.eq(offeredBy),
                        offer.team.id.eq(teamId),
                        offer.position.eq(position),
                        offer.isAccepted.isNull(),
                        offer.team.completedAt.isNull()
                ).orderBy(offer.createdAt.desc())
                .limit(pageSize)
                .fetch();

        return new PageImpl<>(offers, pageable, count);
    }
}
