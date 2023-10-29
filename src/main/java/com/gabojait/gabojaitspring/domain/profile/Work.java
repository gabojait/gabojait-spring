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
public class Work extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
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
                 boolean isCurrent,
                 User user) {
        this.corporationName = corporationName;
        this.workDescription = workDescription;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.isCurrent = isCurrent;
        this.user = user;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Work)) return false;
        Work work = (Work) o;
        return Objects.equals(id, work.id)
                && Objects.equals(user, work.user)
                && Objects.equals(corporationName, work.corporationName)
                && Objects.equals(workDescription, work.workDescription)
                && Objects.equals(startedAt, work.startedAt)
                && Objects.equals(endedAt, work.endedAt)
                && Objects.equals(isCurrent, work.isCurrent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, corporationName, workDescription, startedAt, endedAt, isCurrent);
    }
}
