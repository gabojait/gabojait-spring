package com.gabojait.gabojaitspring.user.repository;

import com.gabojait.gabojaitspring.user.domain.Contact;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.domain.type.Role;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {

    Optional<User> findByIdAndIsDeletedIsFalse(ObjectId userId);

    Optional<User> findByUsernameAndIsDeletedIsFalse(String username);

    Optional<User> findByNicknameAndIsDeletedIsFalse(String nickname);

    Optional<User> findByContactAndIsDeletedIsFalse(Contact contact);

    Page<User> findAllByPositionAndIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByLastRequestDateDesc(Character position,
                                                                                               Pageable pageable);

    Page<User> findAllByIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByLastRequestDateDesc(Pageable pageable);

    Page<User> findAllByPositionAndIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(Character position,
                                                                                                 Pageable pageable);

    Page<User> findAllByIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByVisitedCntDesc(Pageable pageable);

    Page<User> findAllByPositionAndIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByRatingDesc(Character position,
                                                                                      Pageable pageable);

    Page<User> findAllByIsSeekingTeamIsTrueAndIsDeletedIsFalseOrderByRatingDesc(Pageable pageable);

    Optional<User> findByUsernameAndRolesIn(String username, List<Role> roles);
}
