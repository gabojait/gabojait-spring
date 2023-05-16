package com.gabojait.gabojaitspring.team.repository;

import com.gabojait.gabojaitspring.team.domain.Team;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends MongoRepository<Team, ObjectId> {

    Optional<Team> findByIdAndIsDeletedIsFalse(ObjectId teamId);

    Page<Team> findAllByIsRecruitingIsTrueAndIsDeletedIsFalseOrderByTotalRecruitCntDesc(Pageable pageable);

    Page<Team> findAllByIsDesignerFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByTotalRecruitCntDesc(
            Pageable pageable
    );

    Page<Team> findAllByIsBackendFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByTotalRecruitCntDesc(
            Pageable pageable
    );

    Page<Team> findAllByIsFrontendFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByTotalRecruitCntDesc(
            Pageable pageable
    );

    Page<Team> findAllByIsManagerFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByTotalRecruitCntDesc(
            Pageable pageable
    );

    Page<Team> findAllByIsRecruitingIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(Pageable pageable);

    Page<Team> findAllByIsDesignerFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(
            Pageable pageable
    );

    Page<Team> findAllByIsBackendFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(
            Pageable pageable
    );

    Page<Team> findAllByIsFrontendFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(
            Pageable pageable
    );

    Page<Team> findAllByIsManagerFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(
            Pageable pageable
    );

    Page<Team> findAllByIsRecruitingIsTrueAndIsDeletedIsFalseOrderByCreatedDateDesc(Pageable pageable);

    Page<Team> findAllByIsDesignerFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByCreatedDateDesc(
            Pageable pageable
    );

    Page<Team> findAllByIsBackendFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByCreatedDateDesc(
            Pageable pageable
    );

    Page<Team> findAllByIsFrontendFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByCreatedDateDesc(
            Pageable pageable
    );

    Page<Team> findAllByIsManagerFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByCreatedDateDesc(
            Pageable pageable
    );
}
