package com.gabojait.gabojaitspring.team.repository;

import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.team.domain.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TeamCustomRepository {

    Page<Team> searchByIsPositionFullOrderByCreatedAt(long id, Position position, Pageable pageable);
}
