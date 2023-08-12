package com.gabojait.gabojaitspring.favorite.repository;

import com.gabojait.gabojaitspring.favorite.domain.FavoriteUser;
import com.gabojait.gabojaitspring.team.domain.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoriteUserCustomRepository {

    Page<FavoriteUser> searchByTeamOrderByCreatedAt(long id, Team team, Pageable pageable);
}
