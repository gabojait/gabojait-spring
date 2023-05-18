package com.gabojait.gabojaitspring.profile.repository;

import com.gabojait.gabojaitspring.profile.domain.Education;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EducationRepository extends MongoRepository<Education, ObjectId> {

    Optional<Education> findByIdAndIsDeletedIsFalse(ObjectId educationId);
}
