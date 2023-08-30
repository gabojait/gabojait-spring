package com.gabojait.gabojaitspring.profile.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Education extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "education_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
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
        this.isDeleted = false;
    }

    public void update(String institutionName, LocalDate startedAt, LocalDate endedAt, boolean isCurrent) {
        this.institutionName = institutionName;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.isCurrent = isCurrent;
    }

    public void delete() {
        this.isDeleted = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Education education = (Education) o;
        return id.equals(education.id)
                && user.equals(education.user)
                && institutionName.equals(education.institutionName)
                && startedAt.equals(education.startedAt)
                && Objects.equals(endedAt, education.endedAt)
                && isCurrent.equals(education.isCurrent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, institutionName, startedAt, endedAt, isCurrent);
    }
}