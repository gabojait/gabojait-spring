package com.inuappcenter.gabojaitspring.profile.repository;

import com.inuappcenter.gabojaitspring.profile.domain.Work;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WorkRepository extends MongoRepository<Work, String> {
}
