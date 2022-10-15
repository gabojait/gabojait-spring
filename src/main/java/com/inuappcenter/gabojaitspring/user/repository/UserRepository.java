package com.inuappcenter.gabojaitspring.user.repository;

import com.inuappcenter.gabojaitspring.user.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(String username);

    Optional<User> findByNickname(String nickname);

    @Query(value = "{ 'contact.email':  ?0 }")
    Optional<User> findByContact(String email);
}
