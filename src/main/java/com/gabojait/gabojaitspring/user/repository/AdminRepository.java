package com.gabojait.gabojaitspring.user.repository;

import com.gabojait.gabojaitspring.user.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long>, AdminCustomRepository {

    Optional<Admin> findByUsernameAndIsDeletedIsFalse(String username);

    Optional<Admin> findByUsernameAndIsApprovedIsTrueAndIsDeletedIsFalse(String username);

    Optional<Admin> findByIdAndIsApprovedIsNullAndIsDeletedIsFalse(Long id);

    Optional<Admin> findByIdAndIsApprovedIsTrueAndIsDeletedIsFalse(Long id);
}
