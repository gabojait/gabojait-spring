package com.gabojait.gabojaitspring.repository.favorite;

import com.gabojait.gabojaitspring.domain.favorite.Favorite;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface FavoriteCustomRepository {

    boolean existsUser(long userId, long targetUserId);

    boolean existsTeam(long userId, long teamId);

    Optional<Favorite> findUser(long userId, long targetUserId);

    Optional<Favorite> findTeam(long userId, long teamId);

    Page<Favorite> findPageUser(long userId, long pageFrom, int pageSize);

    Page<Favorite> findPageTeam(long userId, long pageFrom, int pageSize);
}
