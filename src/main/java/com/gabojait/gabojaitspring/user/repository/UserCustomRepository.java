package com.gabojait.gabojaitspring.user.repository;

import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserCustomRepository {

    Page<User> searchOrderByCreatedAt(long id, Pageable pageable);

    Page<User> searchByPositionOrderByCreatedAt(long id, Position position, Pageable pageable);

    Page<User> searchAdmin(long id, String username, Pageable pageable);
}
