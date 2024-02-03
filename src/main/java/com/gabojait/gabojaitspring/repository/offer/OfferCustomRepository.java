package com.gabojait.gabojaitspring.repository.offer;

import com.gabojait.gabojaitspring.common.response.PageData;
import com.gabojait.gabojaitspring.domain.offer.Offer;
import com.gabojait.gabojaitspring.domain.offer.OfferedBy;
import com.gabojait.gabojaitspring.domain.user.Position;

import java.util.List;
import java.util.Optional;

public interface OfferCustomRepository {

    List<Offer> findAllByUserId(long userId, long offerorUserId);

    List<Offer> findAllInUserIds(List<Long> userIds, long offerorUserId);

    List<Offer> findAllByTeamId(long userId, long teamId);

    Optional<Offer> findFetchTeam(long userId, long offerId, OfferedBy offeredBy);

    Optional<Offer> findFetchUser(long teamId, long offerId, OfferedBy offeredBy);

    PageData<List<Offer>> findPageFetchUser(long userId, OfferedBy offeredBy, long pageFrom, int pageSize);

    PageData<List<Offer>> findPageFetchTeam(long teamId, Position position, OfferedBy offeredBy, long pageFrom, int pageSize);
}
