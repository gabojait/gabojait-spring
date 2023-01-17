package com.inuappcenter.gabojaitspring.project.repository;

import com.inuappcenter.gabojaitspring.project.domain.Project;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProjectRepository extends MongoRepository<Project, ObjectId> {
}
