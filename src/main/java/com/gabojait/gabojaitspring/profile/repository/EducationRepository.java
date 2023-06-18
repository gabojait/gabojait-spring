package com.gabojait.gabojaitspring.profile.repository;

import com.gabojait.gabojaitspring.profile.domain.Education;
import com.gabojait.gabojaitspring.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EducationRepository extends JpaRepository<Education, Long> {

    Optional<Education> findByIdAndUserAndIsDeletedIsFalse(Long id, User user);
}
