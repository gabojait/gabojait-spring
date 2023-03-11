package com.inuappcenter.gabojaitspring.team.repository;

import com.inuappcenter.gabojaitspring.team.domain.Team;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends MongoRepository<Team, ObjectId> {

    Page<Team> findTeamsByIsPublicIsTrueAndIsDeletedIsFalseOrderByModifiedDateDesc(Pageable pageable);

    Optional<Team> findByIdAndIsDeletedIsFalse(ObjectId id);
}
