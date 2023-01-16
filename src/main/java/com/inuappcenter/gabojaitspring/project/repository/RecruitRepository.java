package com.inuappcenter.gabojaitspring.project.repository;

import com.inuappcenter.gabojaitspring.project.domain.Recruit;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruitRepository extends MongoRepository<Recruit, ObjectId> {
}
