package com.gabojait.gabojaitspring.profile.repository;

import com.gabojait.gabojaitspring.profile.domain.Work;
import com.gabojait.gabojaitspring.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkRepository extends JpaRepository<Work, Long> {

    Optional<Work> findByIdAndUserAndIsDeletedIsFalse(Long id, User user);
}
