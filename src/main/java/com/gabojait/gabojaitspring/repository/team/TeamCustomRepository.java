package com.gabojait.gabojaitspring.repository.team;

import com.gabojait.gabojaitspring.api.dto.common.response.PageData;
import com.gabojait.gabojaitspring.domain.team.Team;
import com.gabojait.gabojaitspring.domain.user.Position;

import java.util.List;

public interface TeamCustomRepository {

    PageData<List<Team>> findPage(Position position, long pageFrom, int pageSize);
}
