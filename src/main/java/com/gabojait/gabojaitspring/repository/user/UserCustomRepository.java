package com.gabojait.gabojaitspring.repository.user;

import com.gabojait.gabojaitspring.domain.user.Position;
import com.gabojait.gabojaitspring.domain.user.User;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface UserCustomRepository {

    Page<User> findPage(Position position, long pageFrom, int pageSize);

    Optional<User> findSeekingTeam(long userId);
}
