package com.gabojait.gabojaitspring.offer.repository;

import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.offer.domain.type.OfferedBy;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OfferCustomRepository {

    Page<Offer> searchByUserOrderByCreatedAt(long id, User user, OfferedBy offeredBy, Pageable pageable);

    Page<Offer> searchByTeamOrderByCreatedAt(long id,
                                             Team team,
                                             Position position,
                                             OfferedBy offeredBy,
                                             Pageable pageable);
}
