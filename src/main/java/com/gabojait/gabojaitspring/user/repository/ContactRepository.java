package com.gabojait.gabojaitspring.user.repository;

import com.gabojait.gabojaitspring.user.domain.Contact;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends MongoRepository<Contact, ObjectId> {

    Optional<Contact> findByEmailAndIsDeletedIsFalse(String email);

    Optional<Contact> findByEmailAndIsVerifiedIsFalseAndIsDeletedIsFalse(String email);

    Optional<Contact> findByEmailAndIsVerifiedIsTrueAndIsRegisteredIsTrueAndIsDeletedIsFalse(String email);

    Optional<Contact> findByEmailAndIsVerifiedIsTrueAndIsRegisteredIsFalseAndIsDeletedIsFalse(String email);
}
