package com.inuappcenter.gabojaitspring.profile.repository;

import com.inuappcenter.gabojaitspring.profile.domain.Skill;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepository  extends MongoRepository<Skill, String> {
}
