package com.gabojait.gabojaitspring.profile.repository;

import com.gabojait.gabojaitspring.profile.domain.Skill;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkillRepository extends MongoRepository<Skill, ObjectId> {

    Optional<Skill> findByIdAndIsDeletedIsFalse(ObjectId skillId);
}
