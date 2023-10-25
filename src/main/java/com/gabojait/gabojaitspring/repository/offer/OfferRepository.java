package com.gabojait.gabojaitspring.repository.offer;

import com.gabojait.gabojaitspring.domain.offer.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferRepository extends JpaRepository<Offer, Long>, OfferCustomRepository {
}
