package com.gabojait.gabojaitspring.favorite.repository;

import com.gabojait.gabojaitspring.favorite.domain.FavoriteTeam;
import com.gabojait.gabojaitspring.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoriteTeamCustomRepository {

    Page<FavoriteTeam> searchByUserOrderByCreatedAt(long id, User user, Pageable pageable);
}
