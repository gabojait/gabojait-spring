package com.gabojait.gabojaitspring.repository.user;

import com.gabojait.gabojaitspring.domain.user.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    Optional<Contact> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Contact> findByEmailAndIsVerified(String email, boolean isVerified);

    void deleteByEmail(String email);
}
