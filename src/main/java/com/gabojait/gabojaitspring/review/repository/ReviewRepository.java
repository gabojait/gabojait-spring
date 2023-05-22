package com.gabojait.gabojaitspring.review.repository;

import com.gabojait.gabojaitspring.review.domain.Review;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends MongoRepository<Review, ObjectId> {

    Optional<Review> findByReviewerIdAndRevieweeIdAndTeamIdAndIsDeletedIsFalse(ObjectId reviewerId,
                                                                               ObjectId revieweeId,
                                                                               ObjectId teamId);
}
