package com.gabojait.gabojaitspring.user.repository;

import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    Optional<UserRole> findByUserAndRole(User user, String role);
}
