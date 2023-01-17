package com.inuappcenter.gabojaitspring.profile.repository;

import com.inuappcenter.gabojaitspring.profile.domain.Profile;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends MongoRepository<Profile, ObjectId> {

    Page<Profile> findAllByIsLookingForProjectIsTrueOrderByModifiedDateDesc(Pageable pageable);

    Page<Profile> findAllByIsLookingForProjectIsTrueAndPositionEqualsOrderByModifiedDate(Pageable pageable, Character position);
}
