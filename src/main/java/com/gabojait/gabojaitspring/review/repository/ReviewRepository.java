package com.gabojait.gabojaitspring.review.repository;

import com.gabojait.gabojaitspring.review.domain.Review;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends MongoRepository<Review, ObjectId> {
}
