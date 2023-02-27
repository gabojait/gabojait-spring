package com.inuappcenter.gabojaitspring.team.repository;

import com.inuappcenter.gabojaitspring.team.domain.Offer;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferRepository extends MongoRepository<Offer, ObjectId> {
}
