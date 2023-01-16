package com.inuappcenter.gabojaitspring.project.repository;

import com.inuappcenter.gabojaitspring.project.domain.Apply;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplyRepository extends MongoRepository<Apply, ObjectId> {
}
