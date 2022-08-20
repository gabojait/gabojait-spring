package com.inuappcenter.gabojaitspring.user.repository;

import com.inuappcenter.gabojaitspring.user.domain.Contact;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends MongoRepository<Contact, String> {

    Optional<Contact> findByEmail(String email);
}