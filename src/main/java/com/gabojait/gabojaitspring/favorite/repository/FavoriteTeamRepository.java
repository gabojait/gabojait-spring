package com.gabojait.gabojaitspring.favorite.repository;

import com.gabojait.gabojaitspring.favorite.domain.FavoriteTeam;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteTeamRepository extends JpaRepository<FavoriteTeam, Long> {

    Optional<FavoriteTeam> findByUserAndTeamAndIsDeletedIsFalse(User user, Team team);

    Page<FavoriteTeam> findAllByUserAndIsDeletedIsFalse(User user, Pageable pageable);
}
