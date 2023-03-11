package com.inuappcenter.gabojaitspring.offer.repository;

import com.inuappcenter.gabojaitspring.offer.domain.Offer;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OfferRepository extends MongoRepository<Offer, ObjectId> {

    Optional<Offer> findByApplicantIdAndTeamId(ObjectId applicantId, ObjectId teamId);

    Page<Offer> findOffersByTeamIdAndPositionAndIsByApplicantAndIsDeletedIsFalseOrderByModifiedDateDesc(ObjectId teamId,
                                                                                                        char position,
                                                                                                        boolean isByApplicant,
                                                                                                        Pageable pageable);

    Page<Offer> findOffersByApplicantIdAndIsByApplicantAndIsDeletedIsFalseOrderByModifiedDateDesc(ObjectId userId,
                                                                                                  boolean isByApplicant,
                                                                                                  Pageable pageable);
}
