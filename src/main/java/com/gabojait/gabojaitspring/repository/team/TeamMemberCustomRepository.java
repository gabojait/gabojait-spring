package com.gabojait.gabojaitspring.repository.team;

import com.gabojait.gabojaitspring.domain.team.TeamMember;
import com.gabojait.gabojaitspring.domain.team.TeamMemberStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TeamMemberCustomRepository {

    List<TeamMember> findAllFetchTeam(long userId);

    List<TeamMember> findAll(long userId);

    Optional<TeamMember> findCurrentFetchTeam(long userId);

    Optional<TeamMember> find(long userId, long teamId, TeamMemberStatus teamMemberStatus);

    Optional<TeamMember> findLeaderFetchUser(long teamId);

    List<TeamMember> findAllCompleteFetchTeam(long teamId);

    List<TeamMember> findAllCurrentFetchUser(long teamId);

    List<TeamMember> findAllFetchUser(long teamId);

    List<TeamMember> findAllExceptUserFetchUser(long teamId, long userId);

    Optional<TeamMember> findReviewableFetchTeam(long userId, long teamId, LocalDateTime now);

    List<TeamMember> findAllReviewableFetchTeam(long userId, LocalDateTime now);

    boolean existsCurrent(long userId);

    boolean exists(long userId, long teamId);
}
