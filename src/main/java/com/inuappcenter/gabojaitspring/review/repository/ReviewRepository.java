package com.inuappcenter.gabojaitspring.review.repository;

import com.inuappcenter.gabojaitspring.review.domain.Review;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends MongoRepository<Review, ObjectId> {
}
