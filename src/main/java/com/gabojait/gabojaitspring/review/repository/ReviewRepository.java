package com.gabojait.gabojaitspring.review.repository;

import com.gabojait.gabojaitspring.review.domain.Review;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewCustomRepository {

    List<Review> findAllByReviewerAndTeamAndIsDeletedIsFalse(User reviewer, Team team);

    List<Review> findAllByRevieweeAndIsDeletedIsFalseOrderByCreatedAtAsc(User reviewee);
}
