package com.inuappcenter.gabojaitspring.profile.repository;

import com.inuappcenter.gabojaitspring.profile.domain.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository  extends MongoRepository<Profile, String> {
}
