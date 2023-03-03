package com.inuappcenter.gabojaitspring.review.repository;

import com.inuappcenter.gabojaitspring.review.domain.Question;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuestionRepository extends MongoRepository<Question, ObjectId> {
}
