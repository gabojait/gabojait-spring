package com.gabojait.gabojaitspring.repository.favorite;

import com.gabojait.gabojaitspring.domain.favorite.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long>, FavoriteCustomRepository {
}
