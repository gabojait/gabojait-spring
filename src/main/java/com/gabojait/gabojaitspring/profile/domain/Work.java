package com.gabojait.gabojaitspring.profile.domain;

import com.gabojait.gabojaitspring.common.entity.BaseTimeEntity;
import com.gabojait.gabojaitspring.user.domain.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

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
    @JoinColumn(name = "member_id")
    @ToString.Exclude
    private User user;

    private String corporationName;
    private String workDescription;
    private LocalDate startedAt;
    private LocalDate endedAt;
    private Boolean isCurrent;

    @Builder
    public Work(String corporationName,
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
}
