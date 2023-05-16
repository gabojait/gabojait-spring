package com.gabojait.gabojaitspring.profile.repository;

import com.gabojait.gabojaitspring.profile.domain.Work;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkRepository extends MongoRepository<Work, ObjectId> {
}
