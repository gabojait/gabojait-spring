package com.gabojait.gabojaitspring.repository.notification;

import com.gabojait.gabojaitspring.domain.notification.Fcm;
import com.gabojait.gabojaitspring.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FcmRepository extends JpaRepository<Fcm, Long>, FcmCustomRepository {

    Optional<Fcm> findByUserAndFcmToken(User user, String fcmToken);

    List<Fcm> findAllByUser(User user);
}
