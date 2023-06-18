package com.gabojait.gabojaitspring.user.repository;

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

    Page<User> findAllByPositionAndIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByRatingDesc(Character position,
                                                                                           Pageable pageable);

    Page<User> findAllByPositionAndIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(Character position,
                                                                                               Pageable pageable);

    Page<User> findAllByPositionAndIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByLastRequestAtDesc(Character position,
                                                                                                  Pageable pageable);
}
