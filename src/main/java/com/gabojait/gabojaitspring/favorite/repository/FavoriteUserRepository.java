package com.gabojait.gabojaitspring.favorite.repository;

import com.gabojait.gabojaitspring.favorite.domain.FavoriteUser;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteUserRepository extends JpaRepository<FavoriteUser, Long>, FavoriteUserCustomRepository {

    Optional<FavoriteUser> findByTeamAndUserAndIsDeletedIsFalse(Team team, User user);
}
