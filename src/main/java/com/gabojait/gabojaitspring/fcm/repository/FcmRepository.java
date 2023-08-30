package com.gabojait.gabojaitspring.fcm.repository;

import com.gabojait.gabojaitspring.fcm.domain.Fcm;
import com.gabojait.gabojaitspring.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FcmRepository extends JpaRepository<Fcm, Long> {

    Optional<Fcm> findByFcmTokenAndUserAndIsDeletedIsFalse(String FcmToken, User user);

    List<Fcm> findAllByUserAndIsDeletedIsFalse(User user);
}
