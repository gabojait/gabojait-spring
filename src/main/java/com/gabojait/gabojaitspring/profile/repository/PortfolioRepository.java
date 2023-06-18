package com.gabojait.gabojaitspring.profile.repository;

import com.gabojait.gabojaitspring.profile.domain.Portfolio;
import com.gabojait.gabojaitspring.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    Optional<Portfolio> findByIdAndUserAndIsDeletedIsFalse(Long id, User user);
}
