package com.gabojait.gabojaitspring.profile.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.profile.domain.type.Media;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Portfolio extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    @ToString.Exclude
    private User user;

    @Column(nullable = false, length = 10)
    private String portfolioName;
    @Column(nullable = false, length = 1000)
    private String portfolioUrl;
    @Column(nullable = false, length = 4)
    @Enumerated(EnumType.STRING)
    private Media media;

    @Builder
    public Portfolio(String portfolioName, String portfolioUrl, Media media, User user) {
        this.portfolioName = portfolioName;
        this.portfolioUrl = portfolioUrl;
        this.media = media;
        this.user = user;
        this.isDeleted = false;

        user.getPortfolios().add(this);
    }

    public void update(String portfolioName, String portfolioUrl, Media media) {
        this.portfolioName = portfolioName;
        this.portfolioUrl = portfolioUrl;
        this.media = media;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
