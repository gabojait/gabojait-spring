package com.gabojait.gabojaitspring.team.repository;

import com.gabojait.gabojaitspring.team.domain.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long>, TeamCustomRepository {

    Optional<Team> findByIdAndIsDeletedIsFalse(Long id);
}
