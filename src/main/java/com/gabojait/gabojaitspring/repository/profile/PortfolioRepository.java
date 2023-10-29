package com.gabojait.gabojaitspring.repository.profile;

import com.gabojait.gabojaitspring.domain.profile.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long>, PortfolioCustomRepository {
}
