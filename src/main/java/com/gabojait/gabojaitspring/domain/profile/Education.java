package com.gabojait.gabojaitspring.domain.profile;

import com.gabojait.gabojaitspring.domain.base.BaseEntity;
import com.gabojait.gabojaitspring.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Education extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "education_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String institutionName;
    @Column(nullable = false)
    private LocalDate startedAt;
    private LocalDate endedAt;
    @Column(nullable = false)
    private Boolean isCurrent;

    @Builder
    private Education(String institutionName, LocalDate startedAt, LocalDate endedAt, boolean isCurrent, User user) {
        this.institutionName = institutionName;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.isCurrent = isCurrent;
        this.user = user;
    }

    public void update(String institutionName, LocalDate startedAt, LocalDate endedAt, boolean isCurrent) {
        this.institutionName = institutionName;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.isCurrent = isCurrent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Education)) return false;
        Education education = (Education) o;
        return Objects.equals(id, education.id)
                && Objects.equals(user, education.user)
                && Objects.equals(institutionName, education.institutionName)
                && Objects.equals(startedAt, education.startedAt)
                && Objects.equals(endedAt, education.endedAt)
                && Objects.equals(isCurrent, education.isCurrent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, institutionName, startedAt, endedAt, isCurrent);
    }
}
