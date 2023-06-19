package com.gabojait.gabojaitspring.review.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    @ManyToOne
    @JoinColumn(name = "reviewee_id")
    private User reviewee;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(nullable = false)
    private Byte rate;
    @Column(nullable = false, length = 200)
    private String post;

    @Builder
    public Review(User reviewer, User reviewee, Team team, Byte rate, String post) {
        this.reviewer = reviewer;
        this.reviewee = reviewee;
        this.team = team;
        this.rate = rate;
        this.post = post;
        this.isDeleted = false;
    }
}
