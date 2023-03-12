package com.inuappcenter.gabojaitspring.review.repository;

import com.inuappcenter.gabojaitspring.review.domain.Question;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends MongoRepository<Question, ObjectId> {

    List<Question> findQuestionsByIsDeletedIsFalseOrderByCreatedDateAsc();

    Optional<Question> findByIdAndIsDeletedIsFalse(ObjectId questionId);
}
