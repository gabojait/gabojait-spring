package com.gabojait.gabojaitspring.repository.team;

import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.user.Position;
import org.springframework.data.domain.Page;

public interface TeamCustomRepository {

    Page<Team> findPage(Position position, long pageFrom, int pageSize);
}
