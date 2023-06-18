package com.gabojait.gabojaitspring.team.repository;

import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.team.domain.TeamMember;
import com.gabojait.gabojaitspring.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    Optional<TeamMember> findByUserAndIsDeletedIsFalse(User user);

    Optional<TeamMember> findByUserAndTeamAndIsDeletedIsTrue(User user, Team team);
}
