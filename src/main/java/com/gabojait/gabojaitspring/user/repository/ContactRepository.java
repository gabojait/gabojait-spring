package com.gabojait.gabojaitspring.user.repository;

import com.gabojait.gabojaitspring.user.domain.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findAllByEmailAndIsDeletedIsFalse(String email);

    Optional<Contact> findByEmailAndIsVerifiedIsFalseAndIsDeletedIsFalse(String email);

    Optional<Contact> findByEmailAndIsVerifiedIsTrueAndIsDeletedIsFalse(String email);
}
