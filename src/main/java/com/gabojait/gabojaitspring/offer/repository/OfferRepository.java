package com.gabojait.gabojaitspring.offer.repository;

import com.gabojait.gabojaitspring.offer.domain.Offer;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OfferRepository extends MongoRepository<Offer, ObjectId> {

    Optional<Offer> findByIdAndIsDeletedIsFalse(ObjectId offerId);

    Page<Offer> findAllByUserIdAndIsDeletedIsFalse(ObjectId userId, Pageable pageable);

    Page<Offer> findAllByTeamIdAndIsDeletedIsFalse(ObjectId teamId, Pageable pageable);
}
