package com.gabojait.gabojaitspring.user.repository;

import com.gabojait.gabojaitspring.user.domain.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByUsernameAndIsDeletedIsFalse(String username);

    Optional<Admin> findByUsernameAndIsApprovedIsTrueAndIsDeletedIsFalse(String username);

    Page<Admin> findAllByIdIsLessThanAndIsApprovedIsNullAndIsDeletedIsFalseOrderByCreatedAtDesc(Long id,
                                                                                                Pageable pageable);

    Optional<Admin> findByIdAndIsApprovedIsNullAndIsDeletedIsFalse(Long id);

    Optional<Admin> findByIdAndIsApprovedIsTrueAndIsDeletedIsFalse(Long id);
}
