package com.gabojait.gabojaitspring.repository.user;

import com.gabojait.gabojaitspring.api.dto.common.response.PageData;
import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.domain.user.User;

import java.util.List;
import java.util.Optional;

public interface UserCustomRepository {

    PageData<List<User>> findPage(Position position, long pageFrom, int pageSize);

    Optional<User> findSeekingTeam(long userId);

    Optional<User> find(String email);
}
