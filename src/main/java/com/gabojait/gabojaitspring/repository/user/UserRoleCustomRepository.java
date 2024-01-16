package com.gabojait.gabojaitspring.repository.user;

import com.gabojait.gabojaitspring.domain.user.UserRole;

import java.util.List;

public interface UserRoleCustomRepository {

    List<UserRole> findAll(String username);

    List<UserRole> findAll(long userId);
}
