package com.gabojait.gabojaitspring.repository.profile;

import com.gabojait.gabojaitspring.domain.profile.Portfolio;

import java.util.List;

public interface PortfolioCustomRepository {

    List<Portfolio> findAll(long userId);
}
