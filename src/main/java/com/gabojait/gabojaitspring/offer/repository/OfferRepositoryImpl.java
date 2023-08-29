package com.gabojait.gabojaitspring.offer.repository;

import com.gabojait.gabojaitspring.common.util.PageProvider;
import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.offer.domain.type.OfferedBy;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.gabojait.gabojaitspring.offer.domain.QOffer.offer;

@Repository
@RequiredArgsConstructor
public class OfferRepositoryImpl implements OfferCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final PageProvider pageProvider;

    @Override
    public Page<Offer> searchByUserOrderByCreatedAt(long id, User user, OfferedBy offeredBy, Pageable pageable) {
        List<Offer> offers = queryFactory
                .selectFrom(offer)
                .where(
                        offer.id.lt(id),
                        offer.user.eq(user),
                        offer.offeredBy.eq(offeredBy),
                        offer.isDeleted.isFalse()
                )
                .orderBy(offer.createdAt.desc())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(offer.count())
                .from(offer)
                .where(
                        offer.user.eq(user),
                        offer.offeredBy.eq(offeredBy),
                        offer.isDeleted.isFalse()
                )
                .fetchOne();

        count = pageProvider.validateCount(count);

        return new PageImpl<>(offers, pageable, count);
    }

    @Override
    public Page<Offer> searchByTeamOrderByCreatedAt(long id,
                                                    Team team,
                                                    Position position,
                                                    OfferedBy offeredBy,
                                                    Pageable pageable) {
        List<Offer> offers = queryFactory
                .selectFrom(offer)
                .where(
                        offer.id.lt(id),
                        offer.team.eq(team),
                        offer.offeredBy.eq(offeredBy),
                        offer.isDeleted.isFalse()
                )
                .orderBy(offer.createdAt.desc())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(offer.count())
                .from(offer)
                .where(
                        offer.team.eq(team),
                        offer.offeredBy.eq(offeredBy),
                        offer.isDeleted.isFalse()
                )
                .fetchOne();

        count = pageProvider.validateCount(count);

        return new PageImpl<>(offers, pageable, count);
    }
}
