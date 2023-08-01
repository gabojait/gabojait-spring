package com.gabojait.gabojaitspring.user.repository;

import com.gabojait.gabojaitspring.profile.domain.type.Position;
import com.gabojait.gabojaitspring.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByIdAndIsDeletedIsFalse(Long id);

    Optional<User> findByUsernameAndIsDeletedIsFalse(String username);

    Optional<User> findByNicknameAndIsDeletedIsFalse(String nickname);

    Page<User> findAllByIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByRatingDesc(Pageable pageable);

    Page<User> findAllByIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(Pageable pageable);

    Page<User> findAllByIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByLastRequestAtDesc(Pageable pageable);

    Page<User> findAllByPositionAndIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByRatingDesc(Position position,
                                                                                           Pageable pageable);

    Page<User> findAllByPositionAndIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(Position position,
                                                                                               Pageable pageable);

    Page<User> findAllByPositionAndIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByLastRequestAtDesc(Position position,
                                                                                                  Pageable pageable);

    Page<User> findAllByUsernameEndsWithAndIsDeletedIsTrue(String username, Pageable pageable);

    Optional<User> findByIdAndIsDeletedIsTrue(Long id);

    Optional<User> findByIdAndIsDeletedIsNull(Long id);

    Optional<User> findByUsernameAndIsDeletedIsNull(String username);

    Optional<User> findByUsername(String username);
}
