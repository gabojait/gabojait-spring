package com.gabojait.gabojaitspring.repository.user;

import com.gabojait.gabojaitspring.domain.user.User;
import com.gabojait.gabojaitspring.domain.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findAllByUser(User user);
}
