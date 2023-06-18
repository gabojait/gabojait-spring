package com.gabojait.gabojaitspring.user.repository;

import com.gabojait.gabojaitspring.user.domain.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    Optional<Contact> findByEmailAndIsDeletedIsFalse(String email);

    Optional<Contact> findByEmailAndIsVerifiedIsFalseAndIsDeletedIsFalse(String email);

    Optional<Contact> findByEmailAndIsVerifiedIsTrueAndIsDeletedIsFalse(String email);
}
