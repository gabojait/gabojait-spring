package com.gabojait.gabojaitspring.domain.profile;

import com.gabojait.gabojaitspring.domain.base.BaseEntity;
import com.gabojait.gabojaitspring.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Portfolio extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 10)
    private String portfolioName;
    @Column(nullable = false, length = 1000)
    private String portfolioUrl;
    @Column(nullable = false, length = 4)
    @Enumerated(EnumType.STRING)
    private Media media;

    @Builder
    private Portfolio(String portfolioName, String portfolioUrl, Media media, User user) {
        this.portfolioName = portfolioName;
        this.portfolioUrl = portfolioUrl;
        this.media = media;
        this.user = user;
    }

    public void update(String portfolioName, String portfolioUrl, Media media) {
        this.portfolioName = portfolioName;
        this.portfolioUrl = portfolioUrl;
        this.media = media;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Portfolio)) return false;
        Portfolio portfolio = (Portfolio) o;
        return Objects.equals(id, portfolio.id)
                && Objects.equals(user, portfolio.user)
                && Objects.equals(portfolioName, portfolio.portfolioName)
                && Objects.equals(portfolioUrl, portfolio.portfolioUrl)
                && media == portfolio.media;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, portfolioName, portfolioUrl, media);
    }
}
