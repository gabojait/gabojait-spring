package com.gabojait.gabojaitspring.offer.repository;

import com.gabojait.gabojaitspring.offer.domain.Offer;
import com.gabojait.gabojaitspring.offer.domain.type.OfferedBy;
import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    Optional<Offer> findByIdAndUserAndIsDeletedIsFalse(Long offerId, User user);

    Optional<Offer> findByIdAndTeamAndIsDeletedIsFalse(Long offerId, Team team);

    Optional<Offer> findByIdAndUserAndOfferedByAndIsDeletedIsFalse(Long offerId, User user, OfferedBy offeredBy);

    Optional<Offer> findByIdAndTeamAndOfferedByAndIsDeletedIsFalse(Long offerId, Team team, OfferedBy offeredBy);

    Page<Offer> findAllByUserAndIsDeletedIsFalse(User user, Pageable pageable);

    Page<Offer> findAllByTeamAndPositionAndIsDeletedIsFalse(Team team, Position position, Pageable pageable);

    List<Offer> findAllByUserAndTeamAndIsAcceptedIsNullAndIsDeletedIsFalse(User user, Team team);
}
