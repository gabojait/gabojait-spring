package com.inuappcenter.gabojaitspring.review.repository;

import com.inuappcenter.gabojaitspring.review.domain.Question;
import com.inuappcenter.gabojaitspring.review.domain.Review;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends MongoRepository<Review, ObjectId> {

    List<Review> findReviewsByRevieweeUserIdAndIsDeletedIsFalseOrderByCreatedDate(ObjectId revieweeUserId);

    Optional<Review> findByReviewerUserIdAndRevieweeUserIdAndTeamIdAndQuestionAndIsDeletedIsFalse(ObjectId reviewerUserId,
                                                                                                  ObjectId revieweeUserId,
                                                                                                  ObjectId teamId,
                                                                                                  Question question);
}
