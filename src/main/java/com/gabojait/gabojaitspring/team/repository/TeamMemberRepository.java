package com.gabojait.gabojaitspring.team.repository;

import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.team.domain.TeamMember;
import com.gabojait.gabojaitspring.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    Optional<TeamMember> findByUserAndIsQuitIsFalseAndIsDeletedIsFalse(User user);

    Optional<TeamMember> findByUserAndTeamAndIsQuitIsFalseAndIsDeletedIsFalse(User user, Team team);

    List<TeamMember> findAllByUserAndIsQuitIsFalseAndIsDeletedIsTrue(User user);

    Optional<TeamMember> findByUserAndTeamAndIsQuitIsFalseAndIsDeletedIsTrue(User user, Team team);

    List<TeamMember> findAllByUserAndIsQuitIsFalse(User user);
}
