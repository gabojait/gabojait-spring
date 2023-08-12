package com.gabojait.gabojaitspring.user.repository;

import com.gabojait.gabojaitspring.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository {

    Optional<User> findByIdAndIsDeletedIsFalse(Long id);

    Optional<User> findByUsernameAndIsDeletedIsFalse(String username);

    Optional<User> findByNicknameAndIsDeletedIsFalse(String nickname);

    Optional<User> findByIdAndIsDeletedIsTrue(Long id);

    Optional<User> findByIdAndIsDeletedIsNull(Long id);

    Optional<User> findByUsernameAndIsDeletedIsNull(String username);

    Optional<User> findByUsername(String username);
}
