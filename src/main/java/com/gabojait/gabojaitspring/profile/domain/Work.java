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
public class Work extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @Column(nullable = false, length = 20)
    private String corporationName;
    @Column(length = 100)
    private String workDescription;
    @Column(nullable = false)
    private LocalDate startedAt;
    private LocalDate endedAt;
    @Column(nullable = false)
    private Boolean isCurrent;

    @Builder
    private Work(String corporationName,
                String workDescription,
                LocalDate startedAt,
                LocalDate endedAt,
                Boolean isCurrent,
                User user) {
        this.corporationName = corporationName;
        this.workDescription = workDescription;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.isCurrent = isCurrent;
        this.user = user;
        this.isDeleted = false;
    }

    public void update(String corporationName,
                       String workDescription,
                       LocalDate startedAt,
                       LocalDate endedAt,
                       boolean isCurrent) {
        this.corporationName = corporationName;
        this.workDescription = workDescription;
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
        Work work = (Work) o;
        return id.equals(work.id)
                && user.equals(work.user)
                && corporationName.equals(work.corporationName)
                && workDescription.equals(work.workDescription)
                && startedAt.equals(work.startedAt)
                && Objects.equals(endedAt, work.endedAt)
                && isCurrent.equals(work.isCurrent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, corporationName, workDescription, startedAt, endedAt, isCurrent);
    }
}
