package com.gabojait.gabojaitspring.repository.team;

import com.gabojait.gabojaitspring.domain.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long>, TeamCustomRepository {
}
