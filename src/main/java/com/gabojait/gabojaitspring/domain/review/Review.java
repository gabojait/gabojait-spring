package com.gabojait.gabojaitspring.domain.review;

import com.gabojait.gabojaitspring.domain.base.BaseEntity;
import com.gabojait.gabojaitspring.domain.team.TeamMember;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private TeamMember reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_id", nullable = false)
    private TeamMember reviewee;

    @Column(nullable = false)
    private Byte rating;
    @Column(nullable = false, length = 200)
    private String post;

    @Builder
    private Review(byte rating, String post, TeamMember reviewer, TeamMember reviewee) {
        this.rating = rating;
        this.post = post;
        this.reviewer = reviewer;
        this.reviewee = reviewee;

        reviewee.getUser().rate(rating);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Review)) return false;
        Review review = (Review) o;
        return Objects.equals(id, review.id)
                && Objects.equals(reviewer, review.reviewer)
                && Objects.equals(reviewee, review.reviewee)
                && Objects.equals(rating, review.rating)
                && Objects.equals(post, review.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reviewer, reviewee, rating, post);
    }
}
