package com.gabojait.gabojaitspring.profile.repository;

import com.gabojait.gabojaitspring.profile.domain.Portfolio;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortfolioRepository extends MongoRepository<Portfolio, ObjectId> {

    Optional<Portfolio> findByIdAndIsDeletedIsFalse(ObjectId portfolioId);
}
