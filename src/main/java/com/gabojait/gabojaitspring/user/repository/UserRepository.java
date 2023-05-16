package com.gabojait.gabojaitspring.user.repository;

import com.gabojait.gabojaitspring.user.domain.Contact;
import com.gabojait.gabojaitspring.user.domain.User;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {

    Optional<User> findByIdAndIsDeletedIsFalse(ObjectId userId);

    Optional<User> findByUsernameAndIsDeletedIsFalse(String username);

    Optional<User> findByNicknameAndIsDeletedIsFalse(String nickname);

    Optional<User> findByContactAndIsDeletedIsFalse(Contact contact);

    Page<User> findAllByPositionAndIsPublicIsTrueAndIsDeletedIsFalseOrderByLastRequestDateDesc(Character position,
                                                                                               Pageable pageable);

    Page<User> findAllByIsPublicIsTrueAndIsDeletedIsFalseOrderByLastRequestDateDesc(Pageable pageable);

    Page<User> findAllByPositionAndIsPublicIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(Character position,
                                                                                                 Pageable pageable);

    Page<User> findAllByIsPublicIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(Pageable pageable);

    Page<User> findAllByPositionAndIsPublicIsTrueAndIsDeletedIsFalseOrderByRatingDesc(Character position,
                                                                                      Pageable pageable);

    Page<User> findAllByIsPublicIsTrueAndIsDeletedIsFalseOrderByRatingDesc(Pageable pageable);
}
