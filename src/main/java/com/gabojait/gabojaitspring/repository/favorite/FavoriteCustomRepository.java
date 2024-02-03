package com.gabojait.gabojaitspring.repository.favorite;

import com.gabojait.gabojaitspring.common.response.PageData;
import com.gabojait.gabojaitspring.domain.favorite.Favorite;

import java.util.List;
import java.util.Optional;

public interface FavoriteCustomRepository {

    boolean existsUser(long userId, long targetUserId);

    boolean existsTeam(long userId, long teamId);

    Optional<Favorite> findUser(long userId, long targetUserId);

    Optional<Favorite> findTeam(long userId, long teamId);

    PageData<List<Favorite>> findPageUser(long userId, long pageFrom, int pageSize);

    PageData<List<Favorite>> findPageTeam(long userId, long pageFrom, int pageSize);
}
