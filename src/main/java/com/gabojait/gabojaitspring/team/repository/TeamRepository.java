package com.gabojait.gabojaitspring.team.repository;

import com.gabojait.gabojaitspring.team.domain.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByIdAndIsDeletedIsFalse(Long id);

    Page<Team> findAllByIsRecruitingIsTrueAndIsDeletedIsFalseOrderByTeamOfferCntDesc(Pageable pageable);

    Page<Team> findAllByIsRecruitingIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(Pageable pageable);

    Page<Team> findAllByIsRecruitingIsTrueAndIsDeletedIsFalseOrderByCreatedAtDesc(Pageable pageable);

    Page<Team> findAllByIsDesignerFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByTeamOfferCntDesc(
            Pageable pageable
    );

    Page<Team> findAllByIsBackendFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByTeamOfferCntDesc(
            Pageable pageable
    );

    Page<Team> findAllByIsFrontendFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByTeamOfferCntDesc(
            Pageable pageable
    );

    Page<Team> findAllByIsManagerFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByTeamOfferCntDesc(
            Pageable pageable
    );

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

    Page<Team> findAllByIsDesignerFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByCreatedAtDesc(
            Pageable pageable
    );

    Page<Team> findAllByIsBackendFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByCreatedAtDesc(
            Pageable pageable
    );

    Page<Team> findAllByIsFrontendFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByCreatedAtDesc(
            Pageable pageable
    );

    Page<Team> findAllByIsManagerFullIsFalseAndIsRecruitingIsTrueAndIsDeletedIsFalseOrderByCreatedAtDesc(
            Pageable pageable
    );
}
