package com.inuappcenter.gabojaitspring.profile.repository;

import com.inuappcenter.gabojaitspring.profile.domain.Education;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EducationRepository extends MongoRepository<Education, ObjectId> {
}
